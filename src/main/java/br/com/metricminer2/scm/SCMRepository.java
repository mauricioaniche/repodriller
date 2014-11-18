package br.com.metricminer2.scm;

public class SCMRepository {

	private String path;
	private String headCommit;
	private String lastCommit;
	private SCM scm;

	public SCMRepository(SCM scm, String path, String headCommit, String lastCommit) {
		this.scm = scm;
		this.path = path;
		this.headCommit = headCommit;
		this.lastCommit = lastCommit;
	}
	
	public String getPath() {
		return path;
	}

	public String getHeadCommit() {
		return headCommit;
	}

	public String getLastCommit() {
		return lastCommit;
	}

	public SCM getScm() {
		return scm;
	}
	
	
}
