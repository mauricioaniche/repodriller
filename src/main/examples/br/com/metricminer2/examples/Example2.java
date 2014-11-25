package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.metrics.MethodLevelMetricProcessor;
import br.com.metricminer2.scm.metrics.cc.MethodLevelCyclomaticComplexity;

public class Example2 implements Study {

	@Override
	public void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String projectsPath = opts.getProjects();
		
		new SourceCodeRepositoryStudy()
		.in(GitRepository.allIn(projectsPath))
		.process(new MethodLevelMetricProcessor(new MethodLevelCyclomaticComplexity()), pm)
		.start();
		
	}
}
