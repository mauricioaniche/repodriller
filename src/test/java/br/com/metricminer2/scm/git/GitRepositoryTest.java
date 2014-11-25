package br.com.metricminer2.scm.git;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.scm.ChangeSet;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;

public class GitRepositoryTest {

	private GitRepository git;
	private String path;

	@Before
	public void setUp() {
		path = this.getClass().getResource("/repo-1/").getPath();
		git = new GitRepository(path);
	}
	
	@Test
	public void shouldGetAllCommits() {
		List<ChangeSet> cs = git.getChangeSets();
		
		Assert.assertEquals(10, cs.size());
		Assert.assertEquals("f0dd1308bd904a9b108a6a40865166ee962af3d4", cs.get(0).getId());
		Assert.assertEquals("866e997a9e44cb4ddd9e00efe49361420aff2559", cs.get(9).getId());
	}
	
	@Test
	public void shouldDetailACommit() {
		
		Commit commit = git.detail("866e997a9e44cb4ddd9e00efe49361420aff2559");
		
		Assert.assertEquals("Maurício Aniche", commit.getCommitter().getName());
		Assert.assertEquals("mauricioaniche@gmail.com", commit.getCommitter().getEmail());
		
		Assert.assertEquals("Matricula adicionada", commit.getMsg());
		Assert.assertEquals(1, commit.getModifications().size());
		
		Assert.assertEquals("Matricula.java", commit.getModifications().get(0).getNewPath());
		Assert.assertTrue(commit.getModifications().get(0).getDiff().startsWith("diff --git a/Matricula.java b/Matricula.java"));
		Assert.assertTrue(commit.getModifications().get(0).getSourceCode().startsWith("package model;"));
		
	}
	

	@Test
	public void shouldDetailARename() {
		
		Commit commit = git.detail("f0dd1308bd904a9b108a6a40865166ee962af3d4");
		
		Assert.assertEquals("Maurício Aniche", commit.getCommitter().getName());
		Assert.assertEquals("mauricioaniche@gmail.com", commit.getCommitter().getEmail());
		
		Assert.assertEquals("Matricula.javax", commit.getModifications().get(0).getNewPath());
		Assert.assertEquals("Matricula.java", commit.getModifications().get(0).getOldPath());
		
	}
	
	@Test
	public void shouldGetInfoFromARepo() {
		SCMRepository repo = git.info();
		
		Assert.assertEquals(path, repo.getPath());
		Assert.assertEquals("866e997a9e44cb4ddd9e00efe49361420aff2559", repo.getFirstCommit());
		Assert.assertEquals("f0dd1308bd904a9b108a6a40865166ee962af3d4", repo.getHeadCommit());
	}
}
