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
	
	public static CommitRange list(List<String> commits) {
		return new ListOfCommits(commits);
	}

	public static CommitRange range(String start, String end) {
		return new Range(start, end);
	}
}
