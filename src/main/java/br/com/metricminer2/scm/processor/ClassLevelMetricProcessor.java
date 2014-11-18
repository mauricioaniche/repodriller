package br.com.metricminer2.scm.processor;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.Modification;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.metrics.ClassLevelCodeMetric;

public class ClassLevelMetricProcessor implements SCMProcessor {

	private ClassLevelCodeMetric metric;

	public ClassLevelMetricProcessor(ClassLevelCodeMetric metric) {
		this.metric = metric;
	}

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		for(Modification change : commit.getModifications()) {
			if(metric.accepts(change.getNewPath())) {
				double value = metric.calculate(change.getSourceCode());
				writer.write(commit.getHash(), change.getNewPath(), value);
			}
		}

	}

	@Override
	public String name() {
		return "Class Level metric Processor for " + metric.getName();
	}

}
