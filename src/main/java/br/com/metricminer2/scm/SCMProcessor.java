package br.com.metricminer2.scm;

import br.com.metricminer2.output.PersistenceMechanism;

public interface SCMProcessor {
	void process(SCMRepository repo, Commit commit, PersistenceMechanism writer);
}
