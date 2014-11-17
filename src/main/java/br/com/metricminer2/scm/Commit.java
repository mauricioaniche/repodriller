package br.com.metricminer2.scm;

import java.util.ArrayList;
import java.util.List;


public class Commit {

	private String hash;
	private Committer committer;
	private String msg;
	private List<Modification> modifications;
	private String parent;

	public Commit(String hash, Committer committer, String msg, String parent) {
		this.hash = hash;
		this.committer = committer;
		this.msg = msg;
		this.parent = parent;
		this.modifications = new ArrayList<Modification>();
	}

	public String getHash() {
		return hash;
	}

	public Committer getCommitter() {
		return committer;
	}

	public String getMsg() {
		return msg;
	}

	public String getParent() {
		return parent;
	}
	
	public void addModification(String oldPath, String newPath, ModificationType change, String diff, String sc) {
		Modification m = new Modification(oldPath, newPath, change, diff, sc);
		modifications.add(m);
		
	}

	@Override
	public String toString() {
		return "Commit [hash=" + hash + ", parent=" + parent + ", committer=" + committer + ", msg=" + msg + ", modifications="
				+ modifications + "]";
	}
	

	

}
