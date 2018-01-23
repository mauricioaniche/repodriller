package org.repodriller.filter.diff;

/**
 * Default filter that accepts diffs on all files.
 * 
 * @author ayaankazerouni
 */
public class NoDiffFilter implements DiffFilter {

	@Override
	public boolean accept(String diffEntryPath) {
		return true;
	}
}