package org.repodriller.filter.range;

import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public interface CommitRange {
	List<ChangeSet> get(SCM scm);
}
