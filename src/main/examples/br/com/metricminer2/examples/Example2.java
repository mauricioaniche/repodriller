package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.metric.MethodLevelMetricCalculator;
import br.com.metricminer2.metric.java8.cc.MethodLevelCyclomaticComplexity;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryNavigator;

public class Example2 implements Study {

	@Override
	public void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String projectsPath = opts.getProjectsPath();
		
		new SourceCodeRepositoryNavigator()
		.in(GitRepository.allIn(projectsPath))
		.process(new MethodLevelMetricCalculator(new MethodLevelCyclomaticComplexity()), pm)
		.start();
		
	}
}
