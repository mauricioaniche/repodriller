package org.repodriller.integration;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

import junit.framework.Assert;

public class ConcurrencyTest {

	private String repoPath;
	private static List<Map<String, Integer>> results;

	@BeforeClass
	public static void setUp() {
		System.out.println("SET UP");
		results = new ArrayList<Map<String, Integer>>();
	}

	@Before
	public void setPath() {
		repoPath = "/tmp/clone-libuv";
	}

	@Test
	public void sequential() {
		TSTestVisitor visitor = new TSTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(repoPath))
		.through(Commits.daily(1))
		.process(visitor)
		.visitorsAreThreadSafe(true)
		.visitorsChangeRepoState(false)
		.withThreads(1)
		.setRepoTmpDir(Paths.get("/tmp/rd"))
		.mine();

		addVisitorToResults("sequential", visitor);
	}

	@Test
	public void simpleConcurrent() {
		TSTestVisitor visitor = new TSTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(repoPath))
		.through(Commits.daily(1))
		.process(visitor)
		.visitorsAreThreadSafe(true)
		.visitorsChangeRepoState(false)
		.withThreads(2) // >1 thread
		.setRepoTmpDir(Paths.get("/tmp/rd"))
		.mine();

		addVisitorToResults("simpleConcurrent", visitor);
	}

	@Test
	public void heavyConcurrent() {
		TSTestVisitor visitor = new TSTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(repoPath))
		.through(Commits.daily(1))
		.process(visitor)
		.visitorsAreThreadSafe(true)
		.visitorsChangeRepoState(false)
		.withThreads(100) // Many threads
		.setRepoTmpDir(Paths.get("/tmp/rd"))
		.mine();

		addVisitorToResults("heavyConcurrent", visitor);
	}

	@Test
	public void changingRepoState_Sequential() {
		TSCheckoutTestVisitor visitor = new TSCheckoutTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(repoPath))
		.through(Commits.daily(1))
		.process(visitor)
		.visitorsAreThreadSafe(true)
		.visitorsChangeRepoState(true) // Clone for each thread
		.withThreads(1) // One thread
		.setRepoTmpDir(Paths.get("/tmp/rd"))
		.mine();

		addVisitorToResults("changingRepoState_Sequential", visitor);
	}

	@Test
	public void changingRepoState_Concurrent() {
		TSCheckoutTestVisitor visitor = new TSCheckoutTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(repoPath))
		.through(Commits.daily(1))
		.process(visitor)
		.visitorsAreThreadSafe(true)
		.visitorsChangeRepoState(true) // Clone for each thread
		.withThreads(20) // Many threads
		.setRepoTmpDir(Paths.get("/tmp/rd"))
		.mine();

		addVisitorToResults("changingRepoState_Concurrent", visitor);
	}

	@Test
	public void changingRepoState_defaultThreads() {
		TSCheckoutTestVisitor visitor = new TSCheckoutTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(repoPath))
		.through(Commits.daily(1))
		.process(visitor)
		.visitorsAreThreadSafe(true)
		.visitorsChangeRepoState(true)
		.withThreads(-1) // Default # threads
		.setRepoTmpDir(Paths.get("/tmp/rd"))
		.mine();

		addVisitorToResults("changingRepoState_defaultThreads", visitor);
	}

	@Test
	public void catchesInvalidConfig() {
		TSTestVisitor visitor = new TSTestVisitor();
		boolean threw = false;

		try {
			new RepositoryMining()
			.in(GitRepository.singleProject(repoPath))
			.through(Commits.daily(1))
			.process(visitor)
			.visitorsAreThreadSafe(false) // Non-thread safe
			.withThreads(2) // but 2 threads
			.setRepoTmpDir(Paths.get("/tmp/rd"))
			.mine();

			addVisitorToResults("catchesInvalidConfig", visitor);
		} catch (Exception e) {
			threw = true;
		}

		Assert.assertTrue(threw);
	}


	@AfterClass
	public static void checkResults() {
		/* Ensure all the results match.
		 * One of the results is definitely correct because it processes commits sequentially. */
		Map<String, Integer> firstResult = results.remove(0);

		results.forEach(result -> {
			Assert.assertEquals(firstResult.get("nHashesVisited"), result.get("nHashesVisited"));
			Assert.assertEquals(firstResult.get("nTimesVisited"), result.get("nTimesVisited"));
			Assert.assertEquals(firstResult.get("nCommitsVisited"), result.get("nCommitsVisited"));
		});
	}

	private void addVisitorToResults(String label, TSTestVisitor visitor) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("nHashesVisited", visitor.getVisitedHashes().size());
		result.put("nTimesVisited", visitor.getVisitedCommits().size());
		result.put("nCommitsVisited", visitor.getVisitedCommits().size());
		results.add(result);
		System.out.println(label + ": " + "nHashesVisited " + result.get("nHashesVisited") + ", nTimesVisited " + result.get("nTimesVisited") + ", nCommitsVisited " + result.get("nCommitsVisited"));
	}

	private void addVisitorToResults(String label, TSCheckoutTestVisitor visitor) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("nHashesVisited", visitor.getVisitedHashes().size());
		result.put("nTimesVisited", visitor.getVisitedCommits().size());
		result.put("nCommitsVisited", visitor.getVisitedCommits().size());
		results.add(result);
		System.out.println(label + ": " + "nHashesVisited " + result.get("nHashesVisited") + ", nTimesVisited " + result.get("nTimesVisited") + ", nCommitsVisited " + result.get("nCommitsVisited"));
	}
}
