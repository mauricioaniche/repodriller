package br.com.metricminer2.filter.commit;

import br.com.metricminer2.domain.Commit;

public class OnlyNoMerge extends CommitFilter{

	public OnlyNoMerge() {
		super();
	}

	public OnlyNoMerge(CommitFilter filter) {
		super(filter);
	}
	
	@Override
	protected boolean shouldAccept(Commit commit) {
		return !commit.isMerge();
	}

}
