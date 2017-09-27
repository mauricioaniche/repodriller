package org.repodriller.filter.range;

import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class BetweenTags implements CommitRange {
	private String from;
	private String to;
	
	public BetweenTags(String from, String to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {
		String first = scm.getCommitFromTag(from);
		String last = scm.getCommitFromTag(to);
		return new Range(first, last).get(scm);
	}
	
	
}
