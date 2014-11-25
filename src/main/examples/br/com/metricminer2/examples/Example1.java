package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.metrics.ClassLevelMetricProcessor;
import br.com.metricminer2.scm.metrics.cc.ClassLevelCyclomaticComplexity;

public class Example1 implements Study {

	@Override
	public void execute(MMOptions opts) {
		
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String projectsPath = opts.getProjectsPath();
		
		new SourceCodeRepositoryStudy()
		.in(GitRepository.allIn(projectsPath))
		.process(new ClassLevelMetricProcessor(new ClassLevelCyclomaticComplexity()), pm)
		.start();
	}
}
