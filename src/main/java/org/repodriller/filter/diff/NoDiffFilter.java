package org.repodriller.filter.diff;

/**
 * Default filter that accepts diifs on all files.
 * 
 * @author ayaankazerouni
 * @version January 22, 2018
 */
public class NoDiffFilter implements DiffFilter {

	@Override
	public boolean accept(String diffEntryPath) {
		return true;
	}
}