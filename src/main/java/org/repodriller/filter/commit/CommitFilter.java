package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public interface CommitFilter {

	boolean accept(Commit commit);
	
}
