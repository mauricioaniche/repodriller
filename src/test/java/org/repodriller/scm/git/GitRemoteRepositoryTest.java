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

package org.repodriller.scm.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.repodriller.scm.GitRemoteRepository;
import org.repodriller.scm.SCMRepository;

public class GitRemoteRepositoryTest {

	private static GitRemoteRepository git1;
	private static GitRemoteRepository git2;
	private static String url;
	private static String REMOTE_GIT_TEMP_DIR = "remoteGitTempDir";

	@BeforeClass()
	public static void readPath() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		url = "https://github.com/mauricioaniche/repodriller";

		String toDel = FileUtils.getTempDirectory().getAbsolutePath() + File.separator + "repodriller";
		FileUtils.deleteDirectory(new File(toDel));
		git1 = new GitRemoteRepository(url);

		FileUtils.deleteDirectory(new File(REMOTE_GIT_TEMP_DIR + File.separator + "repodriller"));
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
		Path expectedStart = Paths.get(REMOTE_GIT_TEMP_DIR).toAbsolutePath();
		Assert.assertTrue("Directory " + REMOTE_GIT_TEMP_DIR + " not honored. Path is " + git2.info().getPath(),
				Paths.get(git2.info().getPath()).startsWith(expectedStart));

		File bareRepositoryRefDir = new File(git2.info().getPath() + File.separator + "refs");
		Assert.assertTrue("A bare repository should have a refs directory",
				bareRepositoryRefDir.exists());
	}

	/**
	 * Doesn't work in every machine/filesystem.
	 * Mock to avoid this issue and make test independent of internet connection?
	 */
	/* TODO This works on my Linux box. Can someone test on Windows? */
//	@AfterClass
	public static void deleteTempResource() throws IOException {
		Collection<GitRemoteRepository> repos = new ArrayList<GitRemoteRepository>();
		repos.add(git1);
		repos.add(git2);

		for (GitRemoteRepository repo : repos) {
			String repoPath = repo.info().getPath();
			repo.close();
			File dir = new File(repoPath);
			Assert.assertFalse("Remote repo's directory should be deleted: " + repoPath, dir.exists());

		}
	}
}
