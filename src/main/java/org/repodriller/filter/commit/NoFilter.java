package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public class NoFilter implements CommitFilter{

	public boolean accept(Commit commit) {
		return true;
	}

}
