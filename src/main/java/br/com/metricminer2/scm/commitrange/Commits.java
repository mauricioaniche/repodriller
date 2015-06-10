package br.com.metricminer2.scm.commitrange;

import java.util.List;

public class Commits {

	public static CommitRange all() {
		return new AllCommits();
	}
	
	public static CommitRange onlyInHead() {
		return new OnlyInHead();
	}

	public static CommitRange single(String commit) {
		return new SingleCommit(commit);
	}

	public static CommitRange monthly(int months) {
		return new MonthlyCommits(months);
	}
	
	public static CommitRange range(List<String> commits) {
		return new SpecifiedCommitRange(commits);
	}
}
