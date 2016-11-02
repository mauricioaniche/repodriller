package org.repodriller.filter.commit;

import java.util.List;

import org.repodriller.domain.Commit;

public class OnlyInBranches implements CommitFilter{

	private List<String> branches;

	public OnlyInBranches(List<String> branches) {
		this.branches = branches;
	}
	
	@Override
	public boolean accept(Commit commit) {
		return commit.getBranches().stream().anyMatch(commitBranch -> branches.stream().anyMatch(branch -> branch.equals(commitBranch)));
	}

}
