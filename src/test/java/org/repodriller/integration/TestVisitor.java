package org.repodriller.integration;

import java.util.ArrayList;
import java.util.List;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

public class TestVisitor implements CommitVisitor {

	private List<String> visitedCommits;
	
	public TestVisitor() {
		visitedCommits = new ArrayList<>();
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		visitedCommits.add(commit.getHash());
	}

	@Override
	public String name() {
		return "test";
	}

	public List<String> getVisitedCommits() {
		return visitedCommits;
	}

}
