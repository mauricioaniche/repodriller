package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public class OnlyInMainBranch implements CommitFilter {

	public boolean accept(Commit commit) {
		return commit.isInMainBranch();
	}

}
