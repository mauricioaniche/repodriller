package br.com.metricminer2.filter.commit;

import br.com.metricminer2.domain.Commit;

public class OnlyInMainBranch extends CommitFilter{

	public OnlyInMainBranch() {
		super();
	}

	public OnlyInMainBranch(CommitFilter filter) {
		super(filter);
	}
	
	@Override
	protected boolean shouldAccept(Commit commit) {
		return commit.isInMainBranch();
	}

}
