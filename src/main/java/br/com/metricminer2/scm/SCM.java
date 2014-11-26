package br.com.metricminer2.scm;

import java.util.List;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;

public interface SCM {

	List<ChangeSet> getChangeSets();

	Commit getCommit(String id);

}