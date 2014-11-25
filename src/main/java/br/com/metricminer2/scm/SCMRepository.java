package br.com.metricminer2.scm;

public class SCMRepository {

	private String path;
	private String headCommit;
	private String firstCommit;
	private SCM scm;
	private String origin;

	public SCMRepository(SCM scm, String origin, String path, String headCommit, String firstCommit) {
		this.scm = scm;
		this.origin = origin;
		this.path = path;
		this.headCommit = headCommit;
		this.firstCommit = firstCommit;
	}
	
	public String getPath() {
		return path;
	}

	public String getHeadCommit() {
		return headCommit;
	}

	public String getFirstCommit() {
		return firstCommit;
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

	@Override
	public String toString() {
		return "SCMRepository [path=" + path + ", headCommit=" + headCommit + ", lastCommit=" + firstCommit + ", scm="
				+ scm + ", origin=" + origin + "]";
	}
	
	
	
}
