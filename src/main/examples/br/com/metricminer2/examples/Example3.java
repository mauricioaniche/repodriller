package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.metric.ClassLevelMetricCalculator;
import br.com.metricminer2.metric.MethodLevelMetricCalculator;
import br.com.metricminer2.metric.java8.cc.MethodLevelCyclomaticComplexity;
import br.com.metricminer2.metric.java8.loc.ClassLevelLinesOfCode;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryNavigator;

public class Example3 implements Study {

	@Override
	public void execute(MMOptions opts) {
		String repoPath = opts.getProjectsPath();
		String outPath = opts.getCsv(); 
		
		new SourceCodeRepositoryNavigator()
			.in(GitRepository.build(repoPath))
			.process(new MethodLevelMetricCalculator(new MethodLevelCyclomaticComplexity()), new CSVFile(outPath, "cc.csv"))
			.process(new ClassLevelMetricCalculator(new ClassLevelLinesOfCode()), new CSVFile(outPath, "loc.csv"))
			.start();		
	}
}
