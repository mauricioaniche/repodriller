package br.com.metricminer2.filter.commit;

import br.com.metricminer2.domain.Commit;

public class NoFilter extends CommitFilter{

	@Override
	protected boolean shouldAccept(Commit commit) {
		return true;
	}

}
