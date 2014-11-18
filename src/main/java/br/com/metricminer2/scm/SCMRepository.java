package br.com.metricminer2.scm;

public class SCMRepository {

	private String path;
	private String headCommit;
	private String lastCommit;
	private SCM scm;
	private String origin;

	public SCMRepository(SCM scm, String origin, String path, String headCommit, String lastCommit) {
		this.scm = scm;
		this.origin = origin;
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

	public String getOrigin() {
		return origin==null?path:origin;
	}
	
	public String getLastDir() {
		String[] dirs = path.replace("\\", "/").split("/");
		return dirs[dirs.length-1];
	}
	
}
