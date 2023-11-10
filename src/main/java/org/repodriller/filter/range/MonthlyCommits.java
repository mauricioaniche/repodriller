package org.repodriller.filter.range;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class MonthlyCommits implements CommitRange {

	private final long monthsInMillis;

	private static final Logger log = LogManager.getLogger(MonthlyCommits.class);


	public MonthlyCommits(int months) {
		monthsInMillis = 1000L * 60L * 60L * 24L * 30L * months;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {

		List<ChangeSet> all = scm.getChangeSets();

		LinkedList<ChangeSet> filtered = new LinkedList<ChangeSet>();
		filtered.add(all.get(0));

		for(ChangeSet cs : all) {
			if(isFarFromTheLastOne(cs, filtered)) {
				filtered.addLast(cs);
			}
		}

		return filtered;
	}

	private boolean isFarFromTheLastOne(ChangeSet cs, LinkedList<ChangeSet> filtered) {
		ChangeSet lastOne = filtered.getLast();

		long lastInMillis = lastOne.getDate().getTimeInMillis();
		long currentInMillis = cs.getDate().getTimeInMillis();

		return (lastInMillis - currentInMillis >= monthsInMillis);
	}


}
