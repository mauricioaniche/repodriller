package br.com.metricminer2.filter.range;

import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class AllCommits implements CommitRange {

	@Override
	public List<ChangeSet> get(SCM scm) {
		return scm.getChangeSets();
	}

}
