package org.repodriller.filter.diff;

/**
 * Filter out unwanted Diffs based on file extensions.
 * 
 * @author Ayaan Kazerouni
 */
public interface DiffFilter {

	/**
	 * Determine whether to accept this Diff for further processing.
	 * 
	 * @param diff Diff in question
	 * @return True if the Diff should pass the filter, false if it should be filtered out.
	 */
	boolean accept(String diffEntryPath);
}
