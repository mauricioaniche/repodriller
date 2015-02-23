package br.com.metricminer2.scm.commitrange;

import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public interface CommitRange {
	List<ChangeSet> get(SCM scm);
}
