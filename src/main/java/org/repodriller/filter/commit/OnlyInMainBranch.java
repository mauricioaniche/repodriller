package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public class OnlyInMainBranch extends CommitFilter{

	public OnlyInMainBranch() {
		super();
	}

	public OnlyInMainBranch(CommitFilter filter) {
		super(filter);
	}
	
	@Override
	protected boolean shouldAccept(Commit commit) {
		return commit.isInMainBranch();
	}

}
