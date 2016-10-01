package br.com.metricminer2.scm.subversion;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.ModificationType;
import br.com.metricminer2.scm.RepositoryFile;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.SubversionRepository;

/**
 * @author Juliano Silva
 *
 */
public class SubversionRepositoryTest {

	private SubversionRepository svn;
	private static String path;

	@BeforeClass
	public static void readPath() throws SVNException, IOException {
		String file = SubversionRepositoryTest.class.getResource("/").getPath() + "../../test-repos/svn-1";
		path = "file:///" + new File(file).getCanonicalPath();
	}

	@Before
	public void setUp() {
		svn = new SubversionRepository(path, "julianosilva", "secret");
	}

	@Test
	public void shouldGetInfoFromARepo() {
		SCMRepository info = svn.info();

		Assert.assertEquals(path, svn.getPath());
		Assert.assertEquals("0", info.getFirstCommit());
		Assert.assertEquals("4", info.getHeadCommit());
	}

	@Test
	public void shouldMakeUnauthenticatedBlame() {
		svn = new SubversionRepository(path);

		String revision = svn.blame("Arquivo.java", "2", 3);

		Assert.assertEquals("2", revision);
	}

	@Test
	public void shouldMakeUnauthenticatedCheckout() {
		svn = new SubversionRepository(path);

		svn.checkout("2");
		List<RepositoryFile> files2 = svn.files();
		Assert.assertEquals(4, files2.size());
	}

	@Test
	public void shouldMakeUnauthenticatedGetHead() {
		svn = new SubversionRepository(path);

		ChangeSet head = svn.getHead();

		Assert.assertEquals("4", head.getId());
	}

	@Test
	public void shouldMakeUnauthenticatedGetAllCommits() {
		svn = new SubversionRepository(path);

		List<ChangeSet> cs = svn.getChangeSets();

		Assert.assertEquals(5, cs.size());
		Assert.assertEquals("1", cs.get(1).getId());
		Assert.assertEquals("2", cs.get(2).getId());
	}

	@Test
	public void shouldMakeUnauthenticatedGetCommit() {
		svn = new SubversionRepository(path);

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
	
	@Test 
	public void testMaxNumberOfFilesInACommit() {
		Integer expectedDefaultValue = 50;
		Assert.assertEquals(expectedDefaultValue, svn.getMaxNumberFilesInACommit());
		
		Integer newExpectedMaxNumber = 2000;
		SubversionRepository subversionRepository = new SubversionRepository(path, newExpectedMaxNumber);
		Assert.assertEquals(newExpectedMaxNumber, subversionRepository.getMaxNumberFilesInACommit());
	}

	@Test 
	public void invalidMaxNumberOfFilesInACommit() {
		SubversionRepository subversionRepository = null;
		try {
			subversionRepository = new SubversionRepository(path, 0);
			Assert.fail("Should not init repository with invalid maxNumberOfFilesInACommit");
		} catch (IllegalArgumentException e) {
			Assert.assertNull(subversionRepository);
		}

		try {
			subversionRepository = new SubversionRepository(path, -1);
			Assert.fail("Should not init repository with invalid maxNumberOfFilesInACommit");
		} catch (IllegalArgumentException e) {
			Assert.assertNull(subversionRepository);
		}
	}
	
}