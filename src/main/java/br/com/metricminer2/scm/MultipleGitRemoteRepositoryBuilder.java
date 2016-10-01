package br.com.metricminer2.scm;

import java.util.List;

public class MultipleGitRemoteRepositoryBuilder extends GitRemoteRepositoryBuilder {

	private List<String> gitUrls;

	public MultipleGitRemoteRepositoryBuilder(List<String> gitUrls) {
		this.gitUrls = gitUrls;
	}
	
	public MultipleGitRemoteRepositoryBuilder inTempDir(String tempDir) {
		super.tempDir = tempDir;
		return this;
	}

	public MultipleGitRemoteRepositoryBuilder asBareRepos() {
		super.bare = true;
		return this;
	}

	public MultipleGitRemoteRepositoryBuilder withMaxNumberOfFilesInACommit(Integer maxNumber) {
		super.maxNumberFilesInACommit = maxNumber;
		return this;
	}

	public SCMRepository[] buildAsSCMRepositories() {
		return GitRemoteRepository.allProjectsIn(this.gitUrls, this.tempDir, this.bare, this.maxNumberFilesInACommit);
	}

}