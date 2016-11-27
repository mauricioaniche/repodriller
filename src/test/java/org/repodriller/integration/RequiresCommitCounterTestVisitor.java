package org.repodriller.integration;

import java.util.ArrayList;
import java.util.List;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitCounter;
import org.repodriller.scm.CommitPosition;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.RequiresCommitCounter;
import org.repodriller.scm.SCMRepository;

public class RequiresCommitCounterTestVisitor implements CommitVisitor, RequiresCommitCounter {

	private List<String> visitedHashes;
	private List<Integer> visitedAbsolutePositions;
	private List<Float> visitedRelativePositions;
	
	private CommitCounter commitCounter;
	private CommitPosition currentCount;
	
	public RequiresCommitCounterTestVisitor() {
		visitedHashes = new ArrayList<>();
		visitedAbsolutePositions = new ArrayList<>();
		visitedRelativePositions = new ArrayList<>();
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		visitedHashes.add(commit.getHash());
		visitedAbsolutePositions.add(currentCount.getAbsolutePosition());
		visitedRelativePositions.add(currentCount.getRelativePosition());
	}
	
	public List<String> getVisitedHashes() {
		return visitedHashes;
	}
	
	public List<Integer> getVisitedAbsolutePositions() {
		return visitedAbsolutePositions;
	}
	
	public List<Float> getVisitedRelativePositions() {
		return visitedRelativePositions;
	}

	@Override
	public String name() {
		return "requiresCommitCountTestVisitor";
	}

	@Override
	public void setCommitCounter(CommitCounter commitCounter) {
		this.commitCounter = commitCounter;
	}

	@Override
	public void count(Commit commit) {
		this.currentCount = this.commitCounter.count(commit);
	}

	@Override
	public void initialize(SCMRepository repo, PersistenceMechanism writer) {
		RequiresCommitCounter.super.initialize(repo, writer);
	}

}
