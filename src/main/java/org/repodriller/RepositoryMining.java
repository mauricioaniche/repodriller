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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.filter.commit.CommitFilter;
import org.repodriller.filter.commit.NoFilter;
import org.repodriller.filter.range.CommitRange;
import org.repodriller.persistence.NoPersistence;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

import com.google.common.collect.Lists;

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
 * @throws RepoDrillerException
 */
public class RepositoryMining {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final Logger log = Logger.getLogger(RepositoryMining.class);

	private List<SCMRepository> repos;
	private RepoVisitor repoVisitor;
	private CommitRange range;
	private int threads;
	private boolean reverseOrder;
	private List<CommitFilter> filters;

	/**
	 * Create a new RepositoryMining.
	 * No repos, no visitors, no filters.
	 * You must initialize with {@link RepositoryMining#in} and {@link RepositoryMining#process} before you call {@link RepositoryMining#mine}.
	 */
	public RepositoryMining() {
		repos = new ArrayList<SCMRepository>();
		repoVisitor = new RepoVisitor();
		filters = Arrays.asList((CommitFilter) new NoFilter());
		this.threads = 1;
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
	 * Add a CommitVisitor to process commits.
	 *
	 * @param visitor
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
	 * @todo I think this should be a feature of Commits. Seems odd to have it at this level.
	 */
	public RepositoryMining reverseOrder() {
		reverseOrder = true;
		return this;
	}

	/**
	 * Configure parallelism for each repo
	 *
	 * @param nThreads	Number of threads that can visit each repo concurrently (default 1).
	 * @return this
	 */
	public RepositoryMining withThreads(int nThreads) {
		if (nThreads < 1)
			throw new RepoDrillerException("Negative number of threads");

		this.threads = nThreads;
		return this;
	}

	/**
	 * The big kahuna.
	 * Go through all the repos in turn.
	 * Retrieve the commits associated with each repo.
	 * For each commit, using {@code withThreads(X)} threads:
	 *   Apply any commit filters.
	 *   Apply all visitors.
	 *
	 * @throws RepoDrillerException
	 */
	public void mine() {

		/* Make sure this RepositoryMining was initialized properly. */
		if (repos.isEmpty())
			throw new RepoDrillerException("No repos specified");
		if (repoVisitor.isEmpty())
			throw new RepoDrillerException("No visitors specified");

		for(SCMRepository repo : repos) {
			repoVisitor.beginRepoVisit(repo);
			processRepo(repo);
			repoVisitor.endRepoVisit();
		}
		repoVisitor.closeAllPersistence(); /* TODO See comment in RepoVisitor about the wisdom of this routine. */
		printScript();

	}

	/**
	 * Retrieve the commits associated with this repo, filter them, and apply visitors to the survivors.
	 * Each commit is assigned to one thread which applies the visitors.
	 *
	 * @param repo	A repo to process
	 */
	private void processRepo(SCMRepository repo) {
		log.info("Git repository in " + repo.getPath());

		List<ChangeSet> allCs = range.get(repo.getScm());
		if(!reverseOrder) Collections.reverse(allCs);

		log.info("Total commits: " + allCs.size());

		log.info("Starting threads: " + threads);
		ExecutorService exec = Executors.newFixedThreadPool(threads);
		List<List<ChangeSet>> partitions = Lists.partition(allCs, threads);
		for(List<ChangeSet> partition : partitions) {
			/* TODO This partitioning is non-optimal.
			 *      Different commits may cost different amounts (e.g. later commits may cost more than earlier commits).
			 *      We should allCs as a queue and have threads consume off of it.
			 */
			exec.submit(() -> {
				for(ChangeSet cs : partition) {
					try {
						processChangeSet(repo, cs);
					} catch (OutOfMemoryError e) {
						System.err.println("Commit " + cs.getId() + " in " + repo.getLastDir() + " caused OOME");
						e.printStackTrace();
						System.err.println("goodbye :/");

						log.fatal("Commit " + cs.getId() + " in " + repo.getLastDir() + " caused OOME", e);
						log.fatal("Goodbye! ;/");
						System.exit(-1);
					} catch(Throwable t) {
						log.error(t);
					}
				}
			});
		}

		try {
			exec.shutdown();
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			log.error("error waiting for threads to terminate in " + repo.getLastDir(), e);
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

		if(!filtersAccept(commit)) {
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
		for(CommitFilter filter : filters) {
			if(!filter.accept(commit))
				return false;
		}
		return true;
	}

}
