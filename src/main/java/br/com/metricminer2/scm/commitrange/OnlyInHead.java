package br.com.metricminer2.scm.commitrange;

import java.util.Arrays;
import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class OnlyInHead implements CommitRange {

	@Override
	public List<ChangeSet> get(SCM scm) {
		return Arrays.asList(scm.getHead());
	}

}
