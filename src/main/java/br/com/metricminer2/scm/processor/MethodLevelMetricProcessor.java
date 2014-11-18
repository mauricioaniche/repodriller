package br.com.metricminer2.scm.processor;

import java.util.Map;

import br.com.metricminer2.output.PersistenceMechanism;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.Modification;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.metrics.MethodLevelCodeMetric;

public class MethodLevelMetricProcessor implements SCMProcessor {

	private MethodLevelCodeMetric metric;

	public MethodLevelMetricProcessor(MethodLevelCodeMetric metric) {
		this.metric = metric;
	}

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		for(Modification change : commit.getModifications()) {
			if(metric.accepts(change.getNewPath())) {
				Map<String, Double> values = metric.calculate(change.getSourceCode());
				for(Map.Entry<String, Double> entry : values.entrySet()) {
					String method = entry.getKey();
					Double value = entry.getValue();
					writer.write(commit.getHash(), change.getNewPath(), method, value);
				}
			}
		}

	}

	@Override
	public String name() {
		return "Method Level metric Processor for " + metric.getName();
	}

}
