package org.repodriller.scm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

public class FirstParentFilter extends RevFilter {
	private Set<RevCommit> ignoreCommits = new HashSet<>();

	@Override
	public boolean include(RevWalk revWalk, RevCommit commit) throws IOException {
		if (commit.getParentCount() > 1) {
			ignoreCommits.add(commit.getParent(1));
		}
		boolean include = true;
		if (ignoreCommits.contains(commit)) {
			include = false;
			ignoreCommits.remove(commit);
		}
		return include;
	}

	@Override
	public RevFilter clone() {
		return new FirstParentFilter();
	}
}