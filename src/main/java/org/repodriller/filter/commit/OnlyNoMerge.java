package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public class OnlyNoMerge extends CommitFilter{

	public OnlyNoMerge() {
		super();
	}

	public OnlyNoMerge(CommitFilter filter) {
		super(filter);
	}
	
	@Override
	protected boolean shouldAccept(Commit commit) {
		return !commit.isMerge();
	}

}
