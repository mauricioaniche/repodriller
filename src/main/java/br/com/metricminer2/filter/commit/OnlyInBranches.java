package br.com.metricminer2.filter.commit;

import java.util.List;

import br.com.metricminer2.domain.Commit;

public class OnlyInBranches extends CommitFilter{

	private List<String> branches;

	public OnlyInBranches(List<String> branches) {
		this(branches, null);
	}

	public OnlyInBranches(List<String> branches, CommitFilter filter) {
		super(filter);
		this.branches = branches;
	}
	
	@Override
	protected boolean shouldAccept(Commit commit) {
		return commit.getBranches().stream().anyMatch(commitBranch -> branches.stream().anyMatch(branch -> branch.equals(commitBranch)));
	}

}
