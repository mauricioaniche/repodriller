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

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.domain.ModificationType;
import br.com.metricminer2.scm.GitRemoteRepository;
import br.com.metricminer2.scm.RepositoryFile;
import br.com.metricminer2.scm.SCMRepository;

public class GitRemoteRepositoryTest {

	private GitRemoteRepository git1;
	private static String path1;

	private GitRemoteRepository git2;
	
	@BeforeClass
	public static void readPath() {
//		path1 = "https://github.com/mauricioaniche/metricminer2";
		path1 = "https://github.com/avjinder/Minimal-Todo.git";
	}

	@Before
	public void setUp() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		git1 = new GitRemoteRepository(path1);
		git2 = new GitRemoteRepository(path1);
	}
	
	@Test
	@Ignore
	public void blame() {
		String hash = git1.blame("App.java", "2545c728d9f10e2933afd2eff3384f828a46cb05", 11);
		System.out.println(hash);
		
		Assert.assertEquals("a4ece0762e797d2e2dcbd471115108dd6e05ff58", hash);

	}

	@Test
	public void shouldListAllFilesInACommit() {
		git1.checkout("1d544e8457f2ddb59333adcbf97c0fa95fb07611");
		List<RepositoryFile> files1 = git1.files();
		Assert.assertEquals(153, files1.size());
		git1.reset();
		
		git1.checkout("95bd9a74da891214c04c5ec0dc1acf0c02fd4985");
		List<RepositoryFile> files2 = git1.files();
		Assert.assertEquals(70, files2.size());
		git1.reset();
		
		git1.checkout("805ace3b95b13eea603be2e2b2cfb7564be50a1e");
		List<RepositoryFile> files3 = git1.files();
		Assert.assertEquals(54, files3.size());
		git1.reset();
		
	}
	
	@Test 
	public void shouldGetHead() {
		ChangeSet head = git1.getHead();
		
		Assert.assertEquals("e379ea037d4dd71104d94bd90c84e0bc5cdd00d2", head.getId());
	}
	
	@Test 
	public void shouldGetAllCommits() {
		List<ChangeSet> cs = git1.getChangeSets();
		
		Assert.assertEquals(116, cs.size());
		Assert.assertEquals("e379ea037d4dd71104d94bd90c84e0bc5cdd00d2", cs.get(0).getId());
		Assert.assertEquals("d3759a7671cc5ffd7e0fcac1c5647682ba6fed74", cs.get(64).getId());
	}
	
	@Test
	@Ignore
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
	@Ignore
	public void shouldDetailACommit() {
		
		Commit commit = git1.getCommit("1a0f653ed3b19d36a71061ff3a29323cfb8c5004");
		
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
		Commit commit = git2.getCommit("ddab1d39ef689a1b72065bde58602629f3ffa32b");
		Assert.assertFalse(commit.isMerge());

		commit = git2.getCommit("5b2facc038250df57c7c6f751ef0238af73a2c39");
		Assert.assertFalse(commit.isMerge());

		commit = git2.getCommit("ba2f1fec3e0dcfac4f544394117a52ab688f20bb");
		Assert.assertTrue(commit.isMerge());
	}

	@Test 
	public void shouldGetNumberOfModifications() {
		
		Commit commit = git1.getCommit("1a0f653ed3b19d36a71061ff3a29323cfb8c5004");
		
		List<Modification> modifications = commit.getModifications();
		Assert.assertEquals(15, modifications.get(6).getAdded());
		Assert.assertEquals(1, modifications.get(6).getRemoved());

		commit = git1.getCommit("8f28ae22733afa460aebecfcc875f61c6cddbd80");
		
		Assert.assertEquals(13, modifications.get(46).getAdded());
		Assert.assertEquals(1, modifications.get(29).getRemoved());
		
	}
	
	@Test 
	public void shouldGetModificationStatus() {
		
		Commit commit = git1.getCommit("1a0f653ed3b19d36a71061ff3a29323cfb8c5004");
		List<Modification> modifications = commit.getModifications();
		Assert.assertEquals(ModificationType.ADD, modifications.get(21).getType());
		
		commit = git1.getCommit("1a0f653ed3b19d36a71061ff3a29323cfb8c5004");
		Assert.assertEquals(ModificationType.MODIFY, modifications.get(1).getType());
		
		commit = git1.getCommit("1a0f653ed3b19d36a71061ff3a29323cfb8c5004");
		Assert.assertEquals(ModificationType.DELETE, modifications.get(51).getType());
		
		
	}
	

	@Test 
	public void shouldDetailARename() {
		
		Commit commit = git1.getCommit("1a0f653ed3b19d36a71061ff3a29323cfb8c5004");
		
		Assert.assertEquals("Avjinder Singh Sekhon", commit.getAuthor().getName());
		Assert.assertEquals("avisekhon@gmail.com", commit.getAuthor().getEmail());
		
		List<Modification> modifications = commit.getModifications();
		Assert.assertEquals("screenshots/screenshot_populated_list.png", modifications.get(51).getOldPath());
		Assert.assertEquals("/dev/null", modifications.get(51).getNewPath());
		
	}
	
	@Test 
	public void shouldGetInfoFromARepo() {
		SCMRepository repo = git1.info();
		
//		Assert.assertEquals(git1.getPath(), repo.getPath());
		Assert.assertEquals("f97a8b469b1f0eab3c5268d772abf323bf9b9598", repo.getFirstCommit());
		Assert.assertEquals("e379ea037d4dd71104d94bd90c84e0bc5cdd00d2", repo.getHeadCommit());
	}
}
