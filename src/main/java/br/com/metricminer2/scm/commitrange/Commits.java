package br.com.metricminer2.scm.commitrange;

public class Commits {

	public static CommitRange all() {
		return new AllCommits();
	}
	
	public static CommitRange onlyInHead() {
		return new OnlyInHead();
	}
}
