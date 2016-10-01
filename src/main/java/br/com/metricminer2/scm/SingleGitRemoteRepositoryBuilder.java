package br.com.metricminer2.scm;

import org.eclipse.jgit.api.errors.GitAPIException;

public class SingleGitRemoteRepositoryBuilder extends GitRemoteRepositoryBuilder {

	private String gitUrl;
	
	public SingleGitRemoteRepositoryBuilder(String gitUrl) {
		this.gitUrl = gitUrl;
	}
	
	public SingleGitRemoteRepositoryBuilder inTempDir(String tempDir) {
		super.tempDir = tempDir;
		return this;
	}

	public SingleGitRemoteRepositoryBuilder asBareRepos() {
		super.bare = true;
		return this;
	}

	public SingleGitRemoteRepositoryBuilder withMaxNumberOfFilesInACommit(Integer maxNumber) {
		super.maxNumberFilesInACommit = maxNumber;
		return this;
	}

	public GitRemoteRepository build() throws GitAPIException {
		return new GitRemoteRepository(this.gitUrl, this.tempDir, this.bare, this.maxNumberFilesInACommit);
	}

	public SCMRepository buildAsSCMRepository() {
		return GitRemoteRepository.singleProject(this.gitUrl, this.tempDir, this.bare, this.maxNumberFilesInACommit);
	}

}