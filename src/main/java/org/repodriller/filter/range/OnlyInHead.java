package org.repodriller.filter.range;

import java.util.Arrays;
import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class OnlyInHead implements CommitRange {

	@Override
	public List<ChangeSet> get(SCM scm) {
		return Arrays.asList(scm.getHead());
	}

}
