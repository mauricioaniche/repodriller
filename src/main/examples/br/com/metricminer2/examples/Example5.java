package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.metric.ClassLevelMetricCalculator;
import br.com.metricminer2.metric.java8.cc.ClassLevelCyclomaticComplexity;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryNavigator;

public class Example5 implements Study{

	@Override
	public void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String repoPath = opts.getProjectsPath();
		
		new SourceCodeRepositoryNavigator()
			.in(GitRepository.allIn(repoPath))
			.process(new ClassLevelMetricCalculator(new ClassLevelCyclomaticComplexity()), pm)
			.start();
		
	}
}
