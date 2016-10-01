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

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.metricminer2.scm.GitRemoteRepository;
import br.com.metricminer2.scm.SCMRepository;

public class GitRemoteRepositoryTest {

	private static GitRemoteRepository git1;
	private static GitRemoteRepository git2;
	private static String url;
	private static String REMOTE_GIT_TEMP_DIR = "remoteGitTempDir";

	@BeforeClass()
	public static void readPath() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		url = "https://github.com/mauricioaniche/metricminer2";
		git1 = new GitRemoteRepository(url);
		git2 = GitRemoteRepository.hostedOn(url).inTempDir(REMOTE_GIT_TEMP_DIR).asBareRepos().build();
	}

	@Test
	public void shouldGetInfoFromARepo() {
		SCMRepository repo = git1.info();
		Assert.assertEquals("c79c45449201edb2895f48144a3b29cdce7c6f47", repo.getFirstCommit());
	}
	
	@Test
	public void shouldGetSameOriginURL() {
		SCMRepository repo = git1.info();
		String origin = repo.getOrigin();
		Assert.assertEquals(url, origin);
	}
	
	@Test
	public void shouldInitWithGivenTempDir() {
		String expectedRepoTempDirectory = REMOTE_GIT_TEMP_DIR + File.separator + "metricminer2";
		Assert.assertEquals(expectedRepoTempDirectory, git2.info().getPath());
		
		File bareRepositoryRefDir = new File(expectedRepoTempDirectory + File.separator + "refs");
		Assert.assertTrue("A bare repository should have refs directory.", bareRepositoryRefDir.exists());
	}
	
	/**
	 * Doesn't work in every machine/filesystem.
	 * Mock to avoid this issue and make test independent of internet connection?
	 */
//	@AfterClass
	public static void deleteTempResource() throws IOException {
		String repoTempPath = git1.info().getPath();
		git1.deleteTempGitPath();
		File tempPathDir = new File(repoTempPath);
		Assert.assertFalse("Temporary directory should be deleted.", tempPathDir.exists());
		
		git2.deleteTempGitPath();
		File givenTempDir = new File(REMOTE_GIT_TEMP_DIR);
		Assert.assertFalse("Given temporary directory should be deleted.", givenTempDir.exists());
	}
	
}
