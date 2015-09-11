package br.com.metricminer2.scm.subversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.ModificationType;
import br.com.metricminer2.scm.RepositoryFile;
import br.com.metricminer2.scm.SubversionAuthenticationConfig;
import br.com.metricminer2.scm.SubversionConfig;
import br.com.metricminer2.scm.SubversionRepository;

public class SubversionRepositoryTest {

	private SubversionRepository svn;
	private static String repositoryPath;
	private static String workingCopyPath;
	private static String filesLocation;

	@BeforeClass
	public static void readPath() throws FileNotFoundException, SVNException {
		String cfgFile = SubversionRepositoryTest.class.getResource("/repo-2.txt").getPath();
		Scanner sc = new java.util.Scanner(new File(cfgFile));
		filesLocation = sc.nextLine();
		workingCopyPath = new File(new File(filesLocation).getParent(), "repo-2-wc").getPath();
		sc.close();

		repositoryPath = "file:///"+ filesLocation;
	}

	@AfterClass
	public static void removeWorkingCopyAndDestroyRepository() throws IOException {
		File workingCopy = new File(workingCopyPath);
		delete(workingCopy);
	}

	@Before
	public void setUp() {
		svn = new SubversionRepository(
				new SubversionConfig(repositoryPath, workingCopyPath, new SubversionAuthenticationConfig("julianosilva", "secret")));
	}

	@Test
	public void shouldMakeUnauthenticatedBlame() {
		svn = new SubversionRepository(new SubversionConfig(repositoryPath, workingCopyPath));

		String revision = svn.blame("Arquivo.java", "2", 3);

		Assert.assertEquals("2", revision);
	}

	@Test
	public void shouldMakeUnauthenticatedCheckout() {
		svn = new SubversionRepository(new SubversionConfig(repositoryPath, workingCopyPath));

		svn.checkout("2");
		List<RepositoryFile> files2 = svn.files();
		Assert.assertEquals(4, files2.size());
	}

	@Test
	public void shouldMakeUnauthenticatedGetHead() {
		svn = new SubversionRepository(new SubversionConfig(repositoryPath, workingCopyPath));

		ChangeSet head = svn.getHead();

		Assert.assertEquals("4", head.getId());
	}
	
	@Test
	public void shouldMakeUnauthenticatedGetAllCommits() {
		svn = new SubversionRepository(new SubversionConfig(repositoryPath, workingCopyPath));
		
		List<ChangeSet> cs = svn.getChangeSets();

		Assert.assertEquals(5, cs.size());
		Assert.assertEquals("1", cs.get(1).getId());
		Assert.assertEquals("2", cs.get(2).getId());
	}
	
	@Test
	public void shouldMakeUnauthenticatedGetCommit() {
		svn = new SubversionRepository(new SubversionConfig(repositoryPath, workingCopyPath));

		Commit commit = svn.getCommit("4");
		Assert.assertEquals(ModificationType.MODIFY, commit.getModifications().get(0).getType());
	}

	@Test
	public void blame() {
		String revision = svn.blame("Arquivo.java", "2", 3);

		Assert.assertEquals("2", revision);
	}

	@Test
	public void shouldListAllFilesInACommit() {
		svn.checkout("2");
		List<RepositoryFile> files2 = svn.files();
		Assert.assertEquals(4, files2.size());
		svn.reset();

		svn.checkout("1");
		List<RepositoryFile> files1 = svn.files();
		Assert.assertEquals(1, files1.size());
		svn.reset();
	}

	@Test
	public void shouldGetHead() {
		ChangeSet head = svn.getHead();

		Assert.assertEquals("4", head.getId());
	}

	@Test
	public void shouldGetAllCommits() {
		List<ChangeSet> cs = svn.getChangeSets();

		Assert.assertEquals(5, cs.size());
		Assert.assertEquals("1", cs.get(1).getId());
		Assert.assertEquals("2", cs.get(2).getId());
	}

	@Test
	public void shouldDetailACommit() {
		Commit commit = svn.getCommit("4");

		Assert.assertEquals("julianosilva", commit.getCommitter().getName());
		Assert.assertNull(commit.getCommitter().getEmail());

		Assert.assertEquals("Alterando conteúdo do arquivo Matricula.java", commit.getMsg());
		Assert.assertEquals(1, commit.getModifications().size());

		Assert.assertEquals("/Matricula.java", commit.getModifications().get(0).getNewPath());
		Assert.assertTrue(commit.getModifications().get(0).getDiff().contains("-if(x) a++;"));
		Assert.assertTrue(commit.getModifications().get(0).getDiff().contains("+if(a) a++;"));
		Assert.assertTrue(commit.getModifications().get(0).getSourceCode().startsWith("package model;"));

	}

	@Test
	public void shouldGetModificationStatus() {
		Commit commit = svn.getCommit("2");
		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(0).getType());
		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(1).getType());
		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(2).getType());

		commit = svn.getCommit("3");
		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(0).getType());
		Assert.assertEquals(ModificationType.DELETE, commit.getModifications().get(1).getType());

		commit = svn.getCommit("4");
		Assert.assertEquals(ModificationType.MODIFY, commit.getModifications().get(0).getType());
	}

	@Test
	public void shouldDetailARename() {
		Commit commit = svn.getCommit("3");

		Assert.assertEquals("julianosilva", commit.getCommitter().getName());

		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(0).getType());
		Assert.assertEquals(ModificationType.DELETE, commit.getModifications().get(1).getType());
		Assert.assertEquals("/Matricula.java", commit.getModifications().get(0).getNewPath());
		Assert.assertEquals("/Matricula.javax", commit.getModifications().get(1).getNewPath());

	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);

					delete(fileDelete);
				}
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			file.delete();
		}
	}

}