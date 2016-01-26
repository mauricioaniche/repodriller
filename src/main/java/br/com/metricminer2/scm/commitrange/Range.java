package br.com.metricminer2.scm.commitrange;

import java.util.ArrayList;
import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class Range implements CommitRange {

	private String last;
	private String first;

	public Range(String last, String first) {
		this.first = first;
		this.last = last;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {
		List<ChangeSet> all = scm.getChangeSets();

		List<ChangeSet> filtered = new ArrayList<ChangeSet>();
		boolean started = false;
		for(ChangeSet cs : all) {
			if(cs.getId().equals(last)) started = true;
			
			if(started) filtered.add(cs);
			if(started && cs.getId().equals(first)) break;
		}
		
		return filtered;
	}

}
