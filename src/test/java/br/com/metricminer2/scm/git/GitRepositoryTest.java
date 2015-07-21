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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.ModificationType;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.RepositoryFile;
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
	public void blame() {
		String hash = git.blame("Arquivo.java", "e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", 3);
		
		Assert.assertEquals("a4ece0762e797d2e2dcbd471115108dd6e05ff58", hash);
	}
	
	@Test
	public void shouldListAllFilesInACommit() {
		git.checkout("a7053a4dcd627f5f4f213dc9aa002eb1caf926f8");
		List<RepositoryFile> files1 = git.files();
		Assert.assertEquals(3, files1.size());
		git.reset();
		
		git.checkout("f0dd1308bd904a9b108a6a40865166ee962af3d4");
		List<RepositoryFile> files2 = git.files();
		Assert.assertEquals(2, files2.size());
		git.reset();
		
		git.checkout("9e71dd5726d775fb4a5f08506a539216e878adbb");
		List<RepositoryFile> files3 = git.files();
		Assert.assertEquals(3, files3.size());
		git.reset();
		
	}
	
	@Test
	public void shouldGetHead() {
		ChangeSet head = git.getHead();
		
		Assert.assertEquals("e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", head.getId());
	}
	
	@Test
	public void shouldGetAllCommits() {
		List<ChangeSet> cs = git.getChangeSets();
		
		Assert.assertEquals(13, cs.size());
		Assert.assertEquals("e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", cs.get(0).getId());
		Assert.assertEquals("866e997a9e44cb4ddd9e00efe49361420aff2559", cs.get(12).getId());
	}
	
	@Test
	public void shouldDetailACommit() {
		
		Commit commit = git.getCommit("866e997a9e44cb4ddd9e00efe49361420aff2559");
		
		Assert.assertEquals("Maurício Aniche", commit.getCommitter().getName());
		Assert.assertEquals("mauricioaniche@gmail.com", commit.getCommitter().getEmail());
		
		Assert.assertEquals("Matricula adicionada", commit.getMsg());
		Assert.assertEquals(1, commit.getModifications().size());
		
		Assert.assertEquals("Matricula.java", commit.getModifications().get(0).getNewPath());
		Assert.assertTrue(commit.getModifications().get(0).getDiff().startsWith("diff --git a/Matricula.java b/Matricula.java"));
		Assert.assertTrue(commit.getModifications().get(0).getSourceCode().startsWith("package model;"));
		
	}
	
	@Test
	public void shouldGetModificationStatus() {
		
		Commit commit = git.getCommit("866e997a9e44cb4ddd9e00efe49361420aff2559");
		Assert.assertEquals(ModificationType.ADD, commit.getModifications().get(0).getType());
		
		commit = git.getCommit("57dbd017d1a744b949e7ca0b1c1a3b3dd4c1cbc1");
		Assert.assertEquals(ModificationType.MODIFY, commit.getModifications().get(0).getType());
		
		commit = git.getCommit("ffccf1e7497eb8136fd66ed5e42bef29677c4b71");
		Assert.assertEquals(ModificationType.DELETE, commit.getModifications().get(0).getType());
		
		
	}
	

	@Test
	public void shouldDetailARename() {
		
		Commit commit = git.getCommit("f0dd1308bd904a9b108a6a40865166ee962af3d4");
		
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
		Assert.assertEquals("e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2", repo.getHeadCommit());
	}
}
