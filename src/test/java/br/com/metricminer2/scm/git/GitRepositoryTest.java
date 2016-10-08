/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.metricminer2.scm.git;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.ModificationType;
import br.com.metricminer2.scm.BlamedLine;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.RepositoryFile;
import br.com.metricminer2.scm.SCMRepository;

public class GitRepositoryTest {

	private GitRepository git1;
	private GitRepository git2;
	private GitRepository git3;
	private static String path1;
	private static String path2;
	private static String path3;

	@BeforeClass
	public static void readPath() throws FileNotFoundException {
		path1 = GitRepositoryTest.class.getResource("/").getPath() + "../../test-repos/git-1";
		path2 = GitRepositoryTest.class.getResource("/").getPath() + "../../test-repos/git-2";
		path3 = GitRepositoryTest.class.getResource("/").getPath() + "../../test-repos/git-3";
	}
	
	@Before
	public void setUp() {
		git1 = new GitRepository(path1);
		git2 = new GitRepository(path2, 2000);
		git3 = new GitRepository(path3);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void deprecatedBlame() {
		String hash = git1.blame("Arquivo.java", "e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", 3);
		
		Assert.assertEquals("a4ece0762e797d2e2dcbd471115108dd6e05ff58", hash);
	}

	@Test
	public void blameFullFile() {
		List<BlamedLine> blame = git3.blame("Arquivo.java", "112fd6787a9b0ffedab5e36fbbd6f014859a9d6d", false);
		
		Assert.assertEquals(
				new BlamedLine(0, "class Arquivo {", "Maurício Aniche", "Maurício Aniche", "a4ece0762e797d2e2dcbd471115108dd6e05ff58"),
				blame.get(0));
		Assert.assertEquals(
				new BlamedLine(1, "  void a() {", "Maurício Aniche", "Maurício Aniche", "a4ece0762e797d2e2dcbd471115108dd6e05ff58"),
				blame.get(1));
		Assert.assertEquals(
				new BlamedLine(2, "   b();", "Maurício Aniche", "Maurício Aniche", "e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2"),
				blame.get(2));
		Assert.assertEquals(
				new BlamedLine(3, "   c();", "John Doe", "Maurício Aniche", "112fd6787a9b0ffedab5e36fbbd6f014859a9d6d"),
				blame.get(3));
		Assert.assertEquals(
				new BlamedLine(4, "   d();", "John Doe", "Maurício Aniche", "112fd6787a9b0ffedab5e36fbbd6f014859a9d6d"),
				blame.get(4));
		Assert.assertEquals(
				new BlamedLine(5, "  }", "Maurício Aniche", "Maurício Aniche", "a4ece0762e797d2e2dcbd471115108dd6e05ff58"),
				blame.get(5));
		Assert.assertEquals(
				new BlamedLine(6, "   int a;", "John Doe", "Maurício Aniche", "112fd6787a9b0ffedab5e36fbbd6f014859a9d6d"),
				blame.get(6));
		Assert.assertEquals(
				new BlamedLine(7, "", "Maurício Aniche", "Maurício Aniche", "a4ece0762e797d2e2dcbd471115108dd6e05ff58"),
				blame.get(7));
		Assert.assertEquals(
				new BlamedLine(8, "}", "Maurício Aniche", "Maurício Aniche", "a4ece0762e797d2e2dcbd471115108dd6e05ff58"),
				blame.get(8));
				

		List<BlamedLine> blame2 = git3.blame("Arquivo.java", "e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", false);
		List<BlamedLine> blame3 = git3.blame("Arquivo.java", "112fd6787a9b0ffedab5e36fbbd6f014859a9d6d", true);
		
		Assert.assertEquals(blame2, blame3);
		
	}
	
	@Test 
	public void shouldListAllFilesInACommit() {
		git1.checkout("a7053a4dcd627f5f4f213dc9aa002eb1caf926f8");
		List<RepositoryFile> files1 = git1.files();
		Assert.assertEquals(3, files1.size());
		git1.reset();
		
		git1.checkout("f0dd1308bd904a9b108a6a40865166ee962af3d4");
		List<RepositoryFile> files2 = git1.files();
		Assert.assertEquals(2, files2.size());
		git1.reset();
		
		git1.checkout("9e71dd5726d775fb4a5f08506a539216e878adbb");
		List<RepositoryFile> files3 = git1.files();
		Assert.assertEquals(3, files3.size());
		git1.reset();
		
	}
	
	@Test 
	public void shouldGetHead() {
		ChangeSet head = git1.getHead();
		
		Assert.assertEquals("e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", head.getId());
	}
	
	@Test 
	public void shouldGetAllCommits() {
		List<ChangeSet> cs = git1.getChangeSets();
		
		Assert.assertEquals(14, cs.size());
		Assert.assertEquals("a997e9d400f742003dea601bb05a9315d14d1124", cs.get(0).getId());
		Assert.assertEquals("866e997a9e44cb4ddd9e00efe49361420aff2559", cs.get(13).getId());
	}
	
	@Test
	public void getBranchesFromCommit() {
		Commit commit = git1.getCommit("a997e9d400f742003dea601bb05a9315d14d1124");
		Assert.assertEquals(1, commit.getBranches().size());
		Assert.assertTrue(commit.getBranches().contains("b2"));

		commit = git1.getCommit("866e997a9e44cb4ddd9e00efe49361420aff2559");
		Assert.assertEquals(2, commit.getBranches().size());
		Assert.assertTrue(commit.getBranches().contains("master"));
		Assert.assertTrue(commit.getBranches().contains("b2"));
	}
	
	@Test
	public void isCommitInMasterBranch() {
		Assert.assertEquals("29e929fbc5dc6a2e9c620069b24e2a143af4285f", git2.getHead().getId());

		git2.checkout("8986af2a679759e5a15794f6d56e6d46c3f302f1");
		GitRepository gitToChangeHead = new GitRepository(path2);

		Commit commit = gitToChangeHead.getCommit("8169f76a3d7add54b4fc7bca7160d1f1eede6eda");
		Assert.assertFalse(commit.isInMainBranch());

		commit = gitToChangeHead.getCommit("168b3aab057ed61a769acf336a4ef5e64f76c9fd");
		Assert.assertTrue(commit.isInMainBranch());
		
		git2.reset();
		Assert.assertEquals("29e929fbc5dc6a2e9c620069b24e2a143af4285f", git2.getHead().getId());
	}
	
	@Test 
	public void shouldDetailACommit() {
		
		Commit commit = git1.getCommit("866e997a9e44cb4ddd9e00efe49361420aff2559");
		
		Assert.assertEquals("Maurício Aniche", commit.getAuthor().getName());
		Assert.assertEquals("mauricioaniche@gmail.com", commit.getAuthor().getEmail());
		
		Assert.assertEquals("Matricula adicionada", commit.getMsg());
		Assert.assertEquals(1, commit.getModifications().size());
		
		Assert.assertEquals("Matricula.java", commit.getModifications().get(0).getNewPath());
		Assert.assertTrue(commit.getModifications().get(0).getDiff().startsWith("diff --git a/Matricula.java b/Matricula.java"));
		Assert.assertTrue(commit.getModifications().get(0).getSourceCode().startsWith("package model;"));
		
	}

	
	@Test 
	public void mergeCommits() {
		Commit commit = git2.getCommit("168b3aab057ed61a769acf336a4ef5e64f76c9fd");
		Assert.assertFalse(commit.isMerge());

		commit = git2.getCommit("8169f76a3d7add54b4fc7bca7160d1f1eede6eda");
		Assert.assertFalse(commit.isMerge());

		commit = git2.getCommit("29e929fbc5dc6a2e9c620069b24e2a143af4285f");
		Assert.assertTrue(commit.isMerge());
	}

	@Test 
	public void shouldGetNumberOfModifications() {
		
		Commit commit = git1.getCommit("866e997a9e44cb4ddd9e00efe49361420aff2559");
		
		Assert.assertEquals(62, commit.getModifications().get(0).getAdded());
		Assert.assertEquals(0, commit.getModifications().get(0).getRemoved());

		commit = git1.getCommit("d11dd6734ff4e60cac3a7b58d9267f138c9e05c7");
		
		Assert.assertEquals(1, commit.getModifications().get(0).getAdded());
		Assert.assertEquals(1, commit.getModifications().get(0).getRemoved());
		
	}
	
	@Test 
	public void shouldGetModificationStatus() {
		
		Commit commit = git1.getCommit("866e997a9e44cb4ddd9e00efe49361420aff2559");
		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(0).getType());
		
		commit = git1.getCommit("57dbd017d1a744b949e7ca0b1c1a3b3dd4c1cbc1");
		Assert.assertEquals(ModificationType.MODIFY, commit.getModifications().get(0).getType());
		
		commit = git1.getCommit("ffccf1e7497eb8136fd66ed5e42bef29677c4b71");
		Assert.assertEquals(ModificationType.DELETE, commit.getModifications().get(0).getType());
		
		
	}
	

	@Test 
	public void shouldDetailARename() {
		
		Commit commit = git1.getCommit("f0dd1308bd904a9b108a6a40865166ee962af3d4");
		
		Assert.assertEquals("Maurício Aniche", commit.getAuthor().getName());
		Assert.assertEquals("mauricioaniche@gmail.com", commit.getAuthor().getEmail());
		
		Assert.assertEquals("Matricula.javax", commit.getModifications().get(0).getNewPath());
		Assert.assertEquals("Matricula.java", commit.getModifications().get(0).getOldPath());
		
	}
	
	@Test 
	public void shouldGetInfoFromARepo() {
		SCMRepository repo = git1.info();
		
		Assert.assertEquals(path1, repo.getPath());
		Assert.assertEquals("866e997a9e44cb4ddd9e00efe49361420aff2559", repo.getFirstCommit());
		Assert.assertEquals("e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", repo.getHeadCommit());
	}
	
	@Test 
	public void testMaxNumberOfFilesInACommit() {
		Integer expectedDefaultValue = 200;
		Assert.assertEquals(expectedDefaultValue, git1.getMaxNumberFilesInACommit());
		
		Integer expectedNewMaxNumber = 2000;
		Assert.assertEquals(expectedNewMaxNumber, git2.getMaxNumberFilesInACommit());
	}
	
	@Test
	public void invalidMaxNumberOfFilesInACommit() {
		GitRepository invalidRepository = null;
		try {
			invalidRepository = new GitRepository(path1, 0);
			Assert.fail("Should not init repository with invalid maxNumberOfFilesInACommit");
		} catch (Exception e) {
			Assert.assertNull(invalidRepository);
		}

		try {
			invalidRepository = new GitRepository(path1, -1);
			Assert.fail("Should not init repository with invalid maxNumberOfFilesInACommit");
		} catch (Exception e) {
			Assert.assertNull(invalidRepository);
		}
	}
	
}
