package br.com.metricminer2.scm.commitrange;

import java.util.ArrayList;
import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class SpecifiedCommitRange implements CommitRange {

	private List<String> commits;

	public SpecifiedCommitRange(List<String> commits) {
		this.commits = commits;
	}
	
	@Override
	public List<ChangeSet> get(SCM scm) {
		List<ChangeSet> all = scm.getChangeSets();

		List<ChangeSet> filtered = new ArrayList<ChangeSet>();
		for(ChangeSet cs : all) {
			if(commits.contains(cs.getId())) {
				filtered.add(cs);
			}
		}
		
		return filtered;
	}

}
