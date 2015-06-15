package br.com.metricminer2.scm.commitrange;

import java.util.LinkedList;
import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class MonthlyCommits implements CommitRange {

	private final long monthsInMillis;

	public MonthlyCommits(int months) {
		monthsInMillis = 1000L * 60L * 60L * 24L * 30L * (long) months;
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
		
		long lastInMillis = lastOne.getTime().getTimeInMillis();
		long currentInMillis = cs.getTime().getTimeInMillis();
		
		return (lastInMillis - currentInMillis >= monthsInMillis);
	}
	
	
}
