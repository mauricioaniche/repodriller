package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

/**
 * Filter out unwanted Commits.
 *
 * @author Mauricio Aniche
 */
public interface CommitFilter {

	/**
	 * Determine whether to accept this commit for further processing.
	 *
	 * @param commit	Commit in question
	 * @return	True if commit should pass the filter, false if it should be filtered out.
	 */
	boolean accept(Commit commit);

}
