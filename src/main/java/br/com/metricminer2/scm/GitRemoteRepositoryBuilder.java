package br.com.metricminer2.scm;

public abstract class GitRemoteRepositoryBuilder {

	protected String tempDir;
	protected boolean bare = false;
	protected Integer maxNumberFilesInACommit;

}