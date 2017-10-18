package org.repodriller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFileFormatException;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;
import org.repodriller.util.RDFileUtils;

/**
 * A RepoVisitor offers Repo-level visiting to a group of CommitVisitors.
 * The metaphor is of a "tour bus" for CommitVisitors.
 * The RepoVisitor drives each CommitVisitor to the Commits for each SCMRepository.
 *
 * Intended use:
 *   - {@link RepoVisitor#addVisitor} all of the CommitVisitor/PersistenceMechanism pairs you want.
 *   - {@link RepoVisitor#beginRepoVisit} to start a new repo
 *   - {@link RepoVisitor#visitCommit} for each Commit you're visiting in the current repo
 *   - {@link RepoVisitor#endRepoVisit} after you're done with this repo
 *
 * A RepoVisitor can maintain a pool of clones of the SCMRepository to allow safe concurrent access
 * even if the CommitVisitors might change external state (e.g. checkout).
 *
 * @author Jamie Davis
 * @author Mauricio Aniche
 *
 */
public class RepoVisitor {

	class CVPM {
		public CommitVisitor cv;
		public PersistenceMechanism pm;
		CVPM(CommitVisitor cv, PersistenceMechanism pm) {
			this.cv = cv;
			this.pm = pm;
		}
	}

	private Collection<CVPM> visitors;
	private SCMRepository currentRepo; // One at a time.

	/* Fixed-size resource pool, of SCMRepository clones. */
	private BlockingQueue<SCMRepository> clonePool;

	private static final Logger log = Logger.getLogger(RepoVisitor.class);

	public RepoVisitor() {
		visitors = new LinkedList<CVPM>();
		currentRepo = null;
	}

	/***
	 * Methods for managing CommitVisitors.
	 ***/

	/**
	 * Add this &lt;CommitVisitor, PersistenceMechanism&gt; pair to my collection.
	 *
	 * @param visitor The visitor to be added
	 * @param writer The persistence mechanism associated to this visitor
	 */
	public void addVisitor(CommitVisitor visitor, PersistenceMechanism writer) {
		visitors.add(new CVPM(visitor, writer));
	}

	/**
	 * Print a description of all of the CommitVisitors I maintain.
	 */
	void printVisitors() {
		for(CVPM cvpm : visitors) {
			log.info("- " + cvpm.cv.name() + " (" + cvpm.cv.getClass().getName() + ")");
		}
	}

	/**
	 * @return True if no CommitVisitors, else false.
	 */
	public boolean isEmpty() {
		return visitors.isEmpty();
	}

	/***
	 * Methods for visiting an SCMRepository.
	 ***/

	/**
	 * Prepare for the next repo we visit.
	 * Calls {@link CommitVisitor#initialize} on each CommitVisitor in my collection.
	 *
	 * @param repo	The repo we are visiting next
	 * @param workPath	Where to store the working copy(s) of {@code repo}? Ideally this is fast storage like a RAMDisk.
	 * @param nThreads	Allocate enough resources to support this much concurrency in {@link RepoVisitor#visitCommit}.
	 * @param visitorsChangeRepoState	True if visitors need to operate in independent copies of {@code repo} for safety.
	 */
	void beginRepoVisit(SCMRepository repo, Path workPath, int nThreads, boolean visitorsChangeRepoState) {
		if (repo == null)
			throw new RepoDrillerException("Error, repo is null");
		if (nThreads < 1)
			throw new RepoDrillerException("Error, must use at least 1 thread");
		if (workPath == null || !RDFileUtils.isDir(workPath))
			throw new RepoDrillerException("Error, workPath " + workPath + " is invalid");

		/* Cache the repo. */
		if (currentRepo != null)
			throw new RepoDrillerException("Error, one repo at a time. We are already visiting a repo.");
		currentRepo = repo;

		/* initialize() the visitors with the repo. */
		for (CVPM cvpm : visitors) {
			try {
				log.info("-> Initializing visitor " + cvpm.cv.name());
				cvpm.cv.initialize(currentRepo, cvpm.pm);
			} catch (Exception e) {
				log.error("Error in " + currentRepo.getPath() +
						"when initializing " + cvpm.cv.name() + ", error=" + e.getMessage(), e);
			}
		}

		/* Initialize clonePool. */
		boolean fair = true; // Keep visits evenly distributed among threads
		clonePool = new ArrayBlockingQueue<SCMRepository>(nThreads, fair);

		/* Populate clonePool with clones of the repo for concurrent access.
		 * Create a distinct firstClone, because:
		 *  1. Simplifies code: we always delete() the SCM for every SCMRepository in the pool.
		 *  2. If currentRepo is a remote repo, we don't want to pay the network cost on each clone.
		 *     Deriving subsequent clones from firstClone should be cheap. */
		Path cloneDir = Paths.get(workPath.toString() + "-clone1");
		SCMRepository firstClone = currentRepo.getScm().clone(cloneDir).info();
		putSCMRepositoryClone(firstClone);
		IntStream.range(2, nThreads).forEach(i -> {
			if (visitorsChangeRepoState) {
				Path dir = Paths.get(workPath.toString() + "-clone" + i);
				SCMRepository clone = firstClone.getScm().clone(dir).info();
				putSCMRepositoryClone(clone);
			}
			else {
				putSCMRepositoryClone(firstClone);
			}
		});
	}

	/**
	 * Visit this commit in the current repo.
	 * Calls {@link CommitVisitor#process} on each CommitVisitor in my collection.
	 *
	 * @param commit The commit that will be visited
	 */
	void visitCommit(Commit commit) {
		log.info("Visiting commit " + commit.getHash());
		/* Get a clone from the pool. */
		SCMRepository scmRepoClone = getSCMRepositoryClone();

		/* Have each visitor process this commit. */
		for (CVPM cvpm : visitors) {
			try {
				log.info("Thread " + Thread.currentThread().getId() + ": processing " + commit.getHash() + " with " + cvpm.cv.name() + " in clone " + scmRepoClone.getPath());
				cvpm.cv.process(scmRepoClone, commit, cvpm.pm);
			} catch (CSVFileFormatException e) {
				log.fatal(e);
				System.exit(-1);
			} catch (Exception e) {
				log.error("Error processing #" + commit.getHash() + " in clone " + scmRepoClone.getPath() +
						", processor=" + cvpm.cv.name() + ", error=" + e.getMessage(), e);
			}
		}

		/* Return clone. */
		putSCMRepositoryClone(scmRepoClone);
	}

	/**
 	 * Conclude the visit to the current repo.
 	 * Calls {@link CommitVisitor#finalize} on each CommitVisitor in my collection.
	 */
	void endRepoVisit() {
		/* finalize() the visitors with the repo. */
		for (CVPM cvpm : visitors) {
			try {
				log.info("-> Finalizing visitor " + cvpm.cv.name());
				cvpm.cv.finalize(currentRepo, cvpm.pm);
			} catch (Exception e) {
				log.error("Error in " + currentRepo.getPath() +
						"when finalizing " + cvpm.cv.name() + ", error=" + e.getMessage(), e);
			}
		}

		/* Clean the clonePool. */
		clonePool.forEach(clone -> {
			clone.getScm().delete(); // Safe to call more than once on the same SCM, if !visitorsChangeRepoState.
		});
		clonePool.clear();

		/* No current repo. */
		currentRepo = null;
	}

	/**
	 * Close all of the PersistenceMechanism's associated with visitors.
	 */
	void closeAllPersistence() {
		/* TODO: This API doesn't make sense to me. Why are the persistence mechanisms closed by this class?
		 *       I think it should be moved. */
		for (CVPM cvpm : visitors) {
			cvpm.pm.close();
		}
	}

	/**
	 * Put this clone into the pool
	 *
	 * @param clone	Clone to put
	 */
	private void putSCMRepositoryClone(SCMRepository clone) {
		while (true) {
			try {
				clonePool.put(clone);
				break;
			} catch (InterruptedException e) {
				;
			}
		}
	}

	/**
	 * Get a clone from the pool
	 * @return A clone
	 */
	private SCMRepository getSCMRepositoryClone() {
		while (true) {
			try {
				return clonePool.take();
			} catch (InterruptedException e) {
				;
			}
		}
	}
}