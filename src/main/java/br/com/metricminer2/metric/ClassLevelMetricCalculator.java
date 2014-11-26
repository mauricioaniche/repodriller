package br.com.metricminer2.metric;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.SCMRepository;

public class ClassLevelMetricCalculator implements MetricCalculator {

	private ClassLevelCodeMetric metric;

	public ClassLevelMetricCalculator(ClassLevelCodeMetric metric) {
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
