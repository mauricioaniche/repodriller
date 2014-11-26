package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.metric.MetricCalculator;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryNavigator;

public class Example4 implements Study {

	@Override
	public void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String projectsPath = opts.getProjectsPath();
		
		new SourceCodeRepositoryNavigator()
			.in(GitRepository.build(projectsPath))
			.process(new MetricCalculator() {
				
				@Override
				public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
					writer.write(commit.getHash(), commit.getMsg());
				}
				
				@Override
				public String name() {
					return "commit messages";
				}
			}, pm)
			.start();
		
	}
}
