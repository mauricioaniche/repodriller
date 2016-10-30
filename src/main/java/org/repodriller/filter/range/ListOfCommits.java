package org.repodriller.filter.range;

import java.util.ArrayList;
import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class ListOfCommits implements CommitRange {

	private List<String> commits;

	public ListOfCommits(List<String> commits) {
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
