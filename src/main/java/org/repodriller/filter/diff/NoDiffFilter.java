package org.repodriller.filter.diff;

public class NoDiffFilter implements DiffFilter {

	@Override
	public boolean accept(String diffEntryPath) {
		return true;
	}
}