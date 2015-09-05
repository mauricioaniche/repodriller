package br.com.metricminer2.scm.commitrange;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class LastMonths implements CommitRange {

	private int months;
	private Calendar sixMonthsAgo;

	public LastMonths(int months) {
		this.months = months;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {
		
		LinkedList<ChangeSet> filtered = new LinkedList<ChangeSet>();
		List<ChangeSet> all = scm.getChangeSets();
		
		sixMonthsAgo = all.get(0).getTime();
		sixMonthsAgo.add(Calendar.MONTH, -months);
		filtered.add(all.get(0));
		
		for(ChangeSet cs : all) {
			if(cs.getTime().after(sixMonthsAgo)) {
				filtered.addLast(cs);
			}
		}
		
		return filtered;
	}

}
