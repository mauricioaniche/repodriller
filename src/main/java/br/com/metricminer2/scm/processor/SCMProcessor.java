package br.com.metricminer2.scm.processor;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;

public interface SCMProcessor {
	void process(SCMRepository repo, Commit commit, PersistenceMechanism writer);
	String name();
}
