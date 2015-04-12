package br.com.metricminer2;

import br.com.metricminer2.metric.ClassLevelMetricCalculator;
import br.com.metricminer2.metric.java8.loc.ClassLevelLinesOfCodeFactory;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.GitRepository;

public class LocStudy implements Study {

	public void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		String projectPath = opts.getProjectPath();

		new SourceCodeRepositoryNavigator(opts)
				.in(GitRepository.allIn(projectPath))
				.process(
						new ClassLevelMetricCalculator(
								new ClassLevelLinesOfCodeFactory()), pm).start();

	}

	public static void main(String[] args) {
		LocStudy locStudy = new LocStudy();
		MMOptions opts = new MMOptions();
		opts.setProjectPath("/home/rcp/git-teste2");
		opts.setStudy("LOC");
		opts.setCsv("/home/rcp/MM2-default.csv");
		opts.setSysOut(false);
		// opts.setThreads(1);

		long start = System.currentTimeMillis();
		locStudy.execute(opts);
		long delay = System.currentTimeMillis() - start;
		System.out.println("Demorou " + delay + " milissegundos");
	}

}
