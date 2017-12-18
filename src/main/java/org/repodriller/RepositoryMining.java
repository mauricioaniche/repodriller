/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.repodriller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.filter.commit.CommitFilter;
import org.repodriller.filter.commit.NoFilter;
import org.repodriller.filter.range.CommitRange;
import org.repodriller.persistence.NoPersistence;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.CollectConfiguration;
import org.repodriller.scm.SCMRepository;
import org.repodriller.util.RDFileUtils;

/**
 * RepositoryMining is the core class of RepoDriller.
 * A RepositoryMining instance:
 *  - maintains a set of SCMRepository's (in)
 *  - picks the commits to visit using a CommitRange (through)
 *  - filters these commits (filters)
 *  - gives the filtered commits from the CommitRange to a set of CommitVisitor's (process)
 *  - starts (mine)
 *
 * The RepositoryMining methods return the instance for easy chaining.
 *
 * @author Mauricio Aniche
 * @throws RepoDrillerException in case of any problems.
 */
public class RepositoryMining {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final Logger log = LogManager.getLogger(RepositoryMining.class);
	private static final int THREADS_PER_CORE = 2;

	private List<SCMRepository> repos;
	private RepoVisitor repoVisitor;
	private CommitRange range;

	private boolean reverseOrder;
	private List<CommitFilter> filters;

	/* Storage members. */
	/* We clone() the active repository to this location.
	 * For performance, the user should specify "fast storage" to accelerate access.
	 * This is a lightweight alternative to pre-processing repositories and storing them in-memory or
	 * in-DB as done in MSR frameworks like Boa and Alitheia Core. */
	/* TODO A utility program to create a RAM-FS on Linux would be helpful. */
	private Path repoTmpDir;

	/* Concurrency members.
	 * Rules: - If visitorsAreThreadSafe, you can have nRepoThreads > 1.
	 *        - If visitorsChangeRepoState, then each thread needs its own clone of the repo to work on.
	 *          Otherwise, the repo threads can work concurrently on the same repo instance. */
	private boolean visitorsAreThreadSafe = false; /* Each visitor can tolerate multiple threads entering it at the Java-level? */
	private boolean visitorsChangeRepoState = true; /* Visitors might call checkout or related? */
	private int nRepoThreads = 1; /* Num threads working on a repo at a time. */

	private ExecutorService threadpool; /* Owned by mine(). */

	/**
	 * Create a new RepositoryMining.
	 * No repos, no visitors, no filters.
	 * You must initialize with {@link RepositoryMining#in} and {@link RepositoryMining#process} before you call {@link RepositoryMining#mine}.
	 */
	public RepositoryMining() {
		repos = new ArrayList<SCMRepository>();
		repoVisitor = new RepoVisitor();
		filters = Arrays.asList((CommitFilter) new NoFilter());

		/* Initialize concurrency settings conservatively. */
		visitorsAreThreadSafe(false);
		visitorsChangeRepoState(true);
		withThreads(1);
	}

	/**
	 * Designate the range of commits to visit.
	 *
	 * @param range	The range of commits to visit. Applied to every repo.
	 * @return this
	 * @see org.repodriller.filter.range.Commits
	 */
	public RepositoryMining through(CommitRange range) {
		this.range = range;
		return this;
	}

	/**
	 * Add repos to mine.
	 *
	 * @param repo		One or more repos to mine
	 * @return this
	 */
	public RepositoryMining in(SCMRepository... repo) {
		this.repos.addAll(Arrays.asList(repo));
		return this;
	}

	/**
	 * Configures the repos about what data to extract.
	 * @return this
	 */
	public RepositoryMining collect(CollectConfiguration config) {
		for(SCMRepository repo : repos) {
			repo.getScm().setDataToCollect(config);
		}

		return this;
	}

	/**
	 * Add a CommitVisitor and its PersistenceMechanism to process commits.
	 *
	 * @param visitor	The visitor
	 * @param writer	The writer passed to {@code visitor}'s {@link CommitVisitor#process} method. Will be closed automatically by {@link RepositoryMining#mine}.
	 * @return this
	 */
	public RepositoryMining process(CommitVisitor visitor, PersistenceMechanism writer) {
		repoVisitor.addVisitor(visitor, writer);
		return this;
	}

	/**
	 * Add a CommitVisitor to process commits. No PersistenceMechanism.
	 *
	 * @param visitor The visitor to be processed
	 * @return this
	 */
	public RepositoryMining process(CommitVisitor visitor) {
		return process(visitor, new NoPersistence());
	}

	/**
	 * Define filters to remove commits emitted by {@link RepositoryMining#through} before they are sent to the CommitVisitor's.
	 *
	 * @param filters	An array of filters
	 * @return this
	 */
	public RepositoryMining filters(CommitFilter... filters) {
		this.filters = Arrays.asList(filters);
		return this;
	}

	/**
	 * Make CommitVisitor's visit the commits in the opposite order returned by the CommitRange.
	 *
	 * @return this
	 */
	// TODO: I think this should be a feature of Commits. Seems odd to have it at this level.
	public RepositoryMining reverseOrder() {
		reverseOrder = true;
		return this;
	}

	/**
	 * Control where we store temporary copies of repos.
	 * For optimal performance, this should be a *fast* place, e.g. an FS stored on RAMDisk or SSD.
	 *
	 * @param repoTmpDir
	 * @return this
	 */
	public RepositoryMining setRepoTmpDir(Path repoTmpDir) {
		if (repoTmpDir == null) {
			try {
				repoTmpDir = Paths.get(RDFileUtils.getTempPath(null));
				if (!RDFileUtils.mkdir(repoTmpDir))
					throw new RepoDrillerException("Couldn't create tmp dir " + repoTmpDir);
			} catch (IOException e) {
				throw new RepoDrillerException("Couldn't get tmp dir: " + e);
			}
		}
		log.debug("Using repoTmpDir " + repoTmpDir);
		this.repoTmpDir = repoTmpDir;
		return this;
	}

	/**
	 * Indicate whether a visitor can be safely entered by multiple threads concurrently.
	 *
	 * @param conflict True if any visitor expects to be the only active visitor
	 * @return this
	 */
	public RepositoryMining visitorsAreThreadSafe(boolean conflict) {
		visitorsAreThreadSafe = conflict;
		return this;
	}

	/**
	 * Indicate whether visitor threads may change repo state.
	 * If they change repo state, the repo cannot be safely accessed by multiple visitor threads concurrently.
	 *
	 * @param change True if any visitor might change repo state (e.g. checkout)
	 * @return this
	 */
	public RepositoryMining visitorsChangeRepoState(boolean change) {
		visitorsChangeRepoState = change;
		return this;
	}

	/**
	 * RepoDriller will choose a good number of threads for you.
	 * @return this
	 */
	public RepositoryMining withThreads() {
		if (visitorsAreThreadSafe)
			/* 4x cores, presuming some I/O-bound work. */
			nRepoThreads = THREADS_PER_CORE*Runtime.getRuntime().availableProcessors();
		else
			nRepoThreads = 1;
		return this;
	}

	/**
	 * Configure parallelism.
	 * If you use >1 thread, {@link RepositoryMining#through} will not define a FIFO order of commit processing.
	 *
	 * @param nThreads	Number of threads that can visit each repo concurrently (default 1).
	 * @return this
	 */
	public RepositoryMining withThreads(int nThreads) {
		if (nThreads < 1)
			throw new RepoDrillerException("Invalid number of threads: " + nThreads);

		nRepoThreads = nThreads;
		return this;
	}

	/**
	 * The big kahuna.
	 * Using the current configuration of repositories, visitors, threads, etc.:
	 *   Go through all the repos in turn.
	 *   Retrieve the commits associated with each repo.
	 *   For each commit, using {@code withThreads(X)} threads:
	 *     Apply any commit filters.
	 *     Apply all visitors.
	 *
	 * @throws RepoDrillerException in case of any problems.
	 */
	public void mine() {

		/* Make sure this RepositoryMining was initialized properly. */
		if (repos.isEmpty())
			throw new RepoDrillerException("No repos specified");
		if (repoVisitor.isEmpty())
			throw new RepoDrillerException("No visitors specified");
		if (!validateConcurrencyConfiguration())
			throw new RepoDrillerException("Invalid concurrency configuration");

		/* Finish any setup. */
		boolean cleanUpRepoTmpDir = false;
		if (repoTmpDir == null) {
			log.debug("Initializing repoTmpDir");
			cleanUpRepoTmpDir = true;
			setRepoTmpDir(null);
		}

		log.info("Mining " + repos.size() + " repos with " + nRepoThreads + " threads");
		threadpool = Executors.newFixedThreadPool(nRepoThreads);

		long begin = System.nanoTime();
		for (SCMRepository repo : repos) {
			repoVisitor.beginRepoVisit(repo, repoTmpDir, nRepoThreads, visitorsChangeRepoState);
			processRepo(repo);
			repoVisitor.endRepoVisit();
		}
		long end = System.nanoTime();
		long elapsedInMS = (long) ((end - begin)/10e6);
		log.info("Mining took " + elapsedInMS + " ms");;

		/* Teardown. */
		threadpool.shutdown();

		repoVisitor.closeAllPersistence(); /* TODO See comment in RepoVisitor about the wisdom of this routine. */
		printScript();

		if (cleanUpRepoTmpDir) {
			try {
				FileUtils.deleteDirectory(new File(repoTmpDir.toString()));
			} catch (IOException e) {
				log.error("Couldn't delete repoTmpDir " + repoTmpDir + ": " + e);
			}
		}
	}

	/**
	 * Check that the concurrency configuration is valid at mine time.
	 *
	 * @return True if concurrency configuration is valid, else false
	 */
	private boolean validateConcurrencyConfiguration() {
		if (1 < nRepoThreads) {
			if (visitorsAreThreadSafe) {
				log.debug("Visitors are thread safe, so " + nRepoThreads + " repo threads is OK");
				return true;
			}
			log.debug("Visitors are not threads afe, so " + nRepoThreads + " repo threads is not OK");
			return false;
		}
		else if (nRepoThreads == 1) {
			log.debug("nRepoThreads " + nRepoThreads + " is fine");
			return true;
		}
		else {
			log.debug("Invalid nRepoThreads " + nRepoThreads);
			return false;
		}
	}

	/**
	 * Retrieve the commits associated with this repo, filter them, and apply visitors to the survivors.
	 * Each commit is assigned to one thread which applies the visitors via calls to {@link RepoVisitor#visitCommit}
	 *
	 * @param repo	A repo to process
	 */
	private void processRepo(SCMRepository repo) {
		log.info("Git repository in " + repo.getPath());

		List<ChangeSet> rawCs = range.get(repo.getScm());
		if (!reverseOrder) Collections.reverse(rawCs); // TODO This is counter-intuitive. Why do we reverse if we are not reversing?
		log.info(rawCs.size() + " ChangeSets to process");

		/* Shared queue of ChangeSets. */
		Queue<ChangeSet> csQueue = new ConcurrentLinkedQueue<ChangeSet>(rawCs);

		List<Future<Integer>> threadDone = new ArrayList<Future<Integer>>();
		/* Divide csQueue among the worker threads.
		 * If there is 1 worker thread, ChangeSets are processed in FIFO order.
		 * With many threads the processing order is hard to characterize. */
		for (int i = 0; i < nRepoThreads; i++) {
			threadDone.add(threadpool.submit(() -> {
				int nConsumed = 0;
				/* Consume ChangeSets until there are no more. */
				while (true) {
					ChangeSet cs = null;
					try {
						cs = csQueue.remove();
						processChangeSet(repo, cs);
						nConsumed++;
					} catch (NoSuchElementException e) {
						log.debug("No ChangeSets left to process, must be done with this repo");
						break;
					} catch (OutOfMemoryError e) {
						String msg = "ChangeSet " + cs.getId() + " in " + repo.getLastDir() + " caused OOME:" + e + "\n\nGoodbye :/";
						System.err.println(msg);
						log.fatal(msg);
						e.printStackTrace();
						System.exit(1);
					} catch(Throwable t) {
						log.error("Got exception from ChangeSet  " + cs.getId() + ": " + t);
						continue;
					}
				}
				log.debug("Thread done");
				return nConsumed;
			}));
		}

		/* Await the futures. */
		int totalConsumed = 0;
		for (Future<Integer> f: threadDone) {
			try {
				totalConsumed += f.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error(e);
			}
		}

		/* Make sure we didn't lose any ChangeSets. */
		if (totalConsumed != rawCs.size()) {
			log.fatal("Error, consumed " + totalConsumed + " ChangeSets but had " + rawCs.size() + " ChangeSets to work on");
			System.exit(1);
		}
	}

	/**
	 * Print summary.
	 */
	private void printScript() {
		log.info("# --------------------------------------------------");
		log.info("Study has been executed in the following projects:");
		for(SCMRepository repo : repos) {
			log.info("- " + repo.getOrigin() + ", from " + repo.getFirstCommit() + " to " + repo.getHeadCommit());
		}

		log.info("The following processors were executed:");

		repoVisitor.printVisitors();
	}

	/**
	 * If {@code cs} survives the filters, apply the visitors to it.
	 *
	 * @param repo	repo
	 * @param cs	changeset being visited
	 */
	private void processChangeSet(SCMRepository repo, ChangeSet cs) {
		Commit commit = repo.getScm().getCommit(cs.getId());
		log.info(
				"Commit #" + commit.getHash() +
				" @ " + repo.getLastDir() +
				" in " + DateFormatUtils.format(commit.getDate().getTime(), DATE_FORMAT) +
				" from " + commit.getAuthor().getName() +
				" with " + commit.getModifications().size() + " modifications");

		if (!filtersAccept(commit)) {
			log.info("-> Filtered");
			return;
		}

		repoVisitor.visitCommit(commit);
	}

	/**
	 * True if all filters accept, else false.
	 *
	 * @param commit	Commit to evaluate
	 * @return allAccepted
	 */
	private boolean filtersAccept(Commit commit) {
		for (CommitFilter filter : filters) {
			if (!filter.accept(commit))
				return false;
		}
		return true;
	}

}
