package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.metrics.MetricProcessor;

public class Example4 implements Study {

	@Override
	public void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String projectsPath = opts.getProjectsPath();
		
		new SourceCodeRepositoryStudy()
			.in(GitRepository.build(projectsPath))
			.process(new MetricProcessor() {
				
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
