package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public class NoFilter extends CommitFilter{

	@Override
	protected boolean shouldAccept(Commit commit) {
		return true;
	}

}
