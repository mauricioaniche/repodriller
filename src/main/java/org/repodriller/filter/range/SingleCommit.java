package org.repodriller.filter.range;

import java.util.Arrays;
import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

public class SingleCommit implements CommitRange {

	private String commit;

	public SingleCommit(String commit) {
		this.commit = commit;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {
		List<ChangeSet> commits = scm.getChangeSets();
		
		for(ChangeSet cs : commits) {
			if(cs.getId().equals(commit)) 
				return Arrays.asList(cs);
		}
		throw new RuntimeException("commit " + commit + " does not exist");
		
	}

}
