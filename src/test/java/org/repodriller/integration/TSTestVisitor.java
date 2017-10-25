package org.repodriller.integration;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

/**
 * Thread-Safe version of TestVisitor.
 */
public class TSTestVisitor implements CommitVisitor {

	private Collection<String> visitedHashes;
	private Collection<Calendar> visitedTimes;
	private Collection<Commit> visitedCommits;

	public TSTestVisitor() {
		visitedHashes = new ConcurrentLinkedQueue<String>();
		visitedTimes = new ConcurrentLinkedQueue<Calendar>();
		visitedCommits = new ConcurrentLinkedQueue<Commit>();
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
		String[] arr = new String[] {};
		List<String> hashes = Arrays.asList(visitedHashes.toArray(arr));
		return hashes;
	}

	public List<Calendar> getVisitedTimes() {
		Calendar[] arr = new Calendar[] {};
		List<Calendar> times = Arrays.asList(visitedTimes.toArray(arr));
		return times;
	}

	public List<Commit> getVisitedCommits() {
		Commit[] arr = new Commit[] {};
		List<Commit> hashes = Arrays.asList(visitedCommits.toArray(arr));
		return hashes;
	}
}
