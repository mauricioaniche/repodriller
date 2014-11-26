package br.com.metricminer2.metric;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.SCMRepository;

public interface MetricCalculator {
	void process(SCMRepository repo, Commit commit, PersistenceMechanism writer);
	String name();
}
