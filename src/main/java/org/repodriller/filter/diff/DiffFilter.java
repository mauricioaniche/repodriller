package org.repodriller.filter.diff;

/**
 * Filter out unwanted Diffs based on file extensions.
 * 
 * @author Ayaan Kazerouni
 */
public interface DiffFilter {

	/**
	 * Determine whether to accept a diff on this file for further processing.
	 * 
	 * @param diffEntryPath The path to the file being diffed
	 * @return True if the file should pass the filter, false if it should be filtered out.
	 */
	boolean accept(String diffEntryPath);
}
