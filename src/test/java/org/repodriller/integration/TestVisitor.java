package org.repodriller.integration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

public class TestVisitor implements CommitVisitor {

	private List<String> visitedHashes;
	private List<Calendar> visitedTimes;
	private List<Commit> visitedCommits;
	
	public TestVisitor() {
		visitedHashes = new ArrayList<>();
		visitedTimes = new ArrayList<>();
		visitedCommits = new ArrayList<>();
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		visitedHashes.add(commit.getHash());
		visitedTimes.add(commit.getDate());
		visitedCommits.add(commit);
	}

	@Override
	public String name() {
		return "test";
	}

	public List<String> getVisitedHashes() {
		return visitedHashes;
	}
	
	public List<Calendar> getVisitedTimes() {
		return visitedTimes;
	}
	
	public List<Commit> getVisitedCommits() {
		return visitedCommits;
	}

}
