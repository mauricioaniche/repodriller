package br.com.metricminer2.scm.metrics;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;

public interface MetricProcessor {
	void process(SCMRepository repo, Commit commit, PersistenceMechanism writer);
	String name();
}
