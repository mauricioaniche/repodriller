package org.repodriller.filter.range;

import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class AllCommits implements CommitRange {

	@Override
	public List<ChangeSet> get(SCM scm) {
		return scm.getChangeSets();
	}

}
