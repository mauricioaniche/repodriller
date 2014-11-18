package br.com.metricminer2.scm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class Commit {

	private String hash;
	private Committer committer;
	private String msg;
	private List<Modification> modifications;
	private String parent;
	private Calendar date;

	public Commit(String hash, Committer committer, Calendar date, String msg, String parent) {
		this.hash = hash;
		this.committer = committer;
		this.date = date;
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
	
	public List<Modification> getModifications() {
		return Collections.unmodifiableList(modifications);
	}

	@Override
	public String toString() {
		return "Commit [hash=" + hash + ", parent=" + parent + ", committer=" + committer + ", msg=" + msg + ", modifications="
				+ modifications + "]";
	}
	
	public Calendar getDate() {
		return date;
	}
	

}
