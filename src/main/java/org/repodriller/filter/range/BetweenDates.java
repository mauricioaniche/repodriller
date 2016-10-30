package org.repodriller.filter.range;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class BetweenDates implements CommitRange {
	
	private Calendar from;
	private Calendar to;

	public BetweenDates(Calendar from, Calendar to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {
		
		List<ChangeSet> all = scm.getChangeSets();
		
		LinkedList<ChangeSet> filtered = new LinkedList<ChangeSet>();
		
		for(ChangeSet cs : all) {
			if(isInTheRange(cs)) {
				filtered.addLast(cs);
			}
		}
		
		return filtered;
	}

	private boolean isInTheRange(ChangeSet cs) {
		return from.before(cs.getTime()) && to.after(cs.getTime());
	}


}
