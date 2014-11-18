package br.com.metricminer2.scm;

public class SCMRepository {

	private String path;
	private String headCommit;
	private String lastCommit;

	public SCMRepository(String path, String headCommit, String lastCommit) {
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
	
	
}
