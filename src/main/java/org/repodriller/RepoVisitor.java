package org.repodriller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFileFormatException;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

/**
 * A RepoVisitor offers Repo-level visiting to a group of CommitVisitors.
 * The metaphor is of a "tour bus" for CommitVisitors.
 * The RepoVisitor drives each CommitVisitor to the Commits for each SCMRepository.
 *
 * Intended use:
 *   - {@link RepoVisitor#addVisitor} all of the CommitVisitor/PersistenceMechanism pairs you want.
 *   - {@link RepoVisitor#beginRepoTour} to start a new repo
 *   - {@link RepoVisitor#visit} for each Commit you're visiting in the current repo
 *   - {#link CommitVisitorIterator#endRepoTour} after you're done with this repo
 *
 * @author Mauricio Aniche
 *
 */
public class RepoVisitor {

	private Map<CommitVisitor, PersistenceMechanism> visitors;
	private static final Logger log = Logger.getLogger(RepoVisitor.class);
	private SCMRepository currentRepo; // One at a time.

	public RepoVisitor() {
		visitors = new HashMap<CommitVisitor, PersistenceMechanism>();
		currentRepo = null;
	}

	/***
	 * Methods for managing CommitVisitors.
	 ***/

	/**
	 * Add this <CommitVisitor, PersistenceMechanism> pair to my collection.
	 *
	 * @param visitor
	 * @param writer
	 */
	public void addVisitor(CommitVisitor visitor, PersistenceMechanism writer) {
		visitors.put(visitor, writer);
	}

	/**
	 * Remove this CommitVisitors from my collection, if present.
	 *
	 * @param visitor
	 */
	public void removeVisitor(CommitVisitor visitor) {
		visitors.remove(visitor);
	}

	/**
	 * Empty my collection of CommitVisitors.
	 */
	public void clearVisitors() {
		visitors.clear();
	}

	/**
	 * Print a description of all of the CommitVisitors I maintain.
	 */
	void printVisitors() {
		for(CommitVisitor visitor : visitors.keySet()) {
			log.info("- " + visitor.name() + " (" + visitor.getClass().getName() + ")");
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
	 */
	void beginRepoVisit(SCMRepository repo) {
		if (currentRepo != null)
			throw new RepoDrillerException("Error, one repo at a time. We are already visiting a repo.");
		currentRepo = repo;

		for(Map.Entry<CommitVisitor, PersistenceMechanism> entry : visitors.entrySet()) {
			CommitVisitor visitor = entry.getKey();
			PersistenceMechanism writer = entry.getValue();

			try {
				log.info("-> Initializing visitor " + visitor.name());
				visitor.initialize(currentRepo, writer);
			} catch (Exception e) {
				log.error("error in " + currentRepo.getPath() +
						"when initializing " + visitor.name() + ", error=" + e.getMessage(), e);
			}
		}
	}

	/**
	 * Visit this commit in the current repo.
	 * Calls {@link CommitVisitor#process} on each CommitVisitor in my collection.
	 *
	 * @param commit
	 */
	void visitCommit(Commit commit) {
		for(Map.Entry<CommitVisitor, PersistenceMechanism> entry : visitors.entrySet()) {
			CommitVisitor visitor = entry.getKey();
			PersistenceMechanism writer = entry.getValue();

			try {
				log.info("-> Processing " + commit.getHash() + " with " + visitor.name());
				visitor.process(currentRepo, commit, writer);
			} catch (CSVFileFormatException e) {
				log.fatal(e);
				System.exit(-1);
			} catch (Exception e) {
				log.error("error processing #" + commit.getHash() + " in " + currentRepo.getPath() +
						", processor=" + visitor.name() + ", error=" + e.getMessage(), e);
			}
		}
	}

	/**
 	 * Conclude the visit to the current repo.
 	 * Calls {@link CommitVisitor#finalize} on each CommitVisitor in my collection.
	 */
	void endRepoVisit() {
		for(Map.Entry<CommitVisitor, PersistenceMechanism> entry : visitors.entrySet()) {
			CommitVisitor visitor = entry.getKey();
			PersistenceMechanism writer = entry.getValue();

			try {
				log.info("-> Finalizing visitor " + visitor.name());
				visitor.finalize(currentRepo, writer);
			} catch (Exception e) {
				log.error("error in " + currentRepo.getPath() +
						"when finalizing " + visitor.name() + ", error=" + e.getMessage(), e);
			}
		}

		currentRepo = null;
	}

	/**
	 * Close all of the PersistenceMechanism's associated with visitors.
	 */
	void closeAllPersistence() {
		/* TODO: This API doesn't make sense to me. Why are the persistence mechanisms closed by this class?
		 *       I think it should be removed. */
		for(PersistenceMechanism persist : visitors.values()) {
			persist.close();
		}
	}
}
