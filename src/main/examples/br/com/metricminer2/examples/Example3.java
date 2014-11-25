package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.metrics.ClassLevelMetricProcessor;
import br.com.metricminer2.scm.metrics.MethodLevelMetricProcessor;
import br.com.metricminer2.scm.metrics.cc.MethodLevelCyclomaticComplexity;
import br.com.metricminer2.scm.metrics.loc.ClassLevelLinesOfCode;

public class Example3 implements Study {

	@Override
	public void execute(MMOptions opts) {
		String repoPath = opts.getProjects();
		String outPath = opts.getCsv(); 
		
		new SourceCodeRepositoryStudy()
			.in(GitRepository.build(repoPath))
			.process(new MethodLevelMetricProcessor(new MethodLevelCyclomaticComplexity()), new CSVFile(outPath, "cc.csv"))
			.process(new ClassLevelMetricProcessor(new ClassLevelLinesOfCode()), new CSVFile(outPath, "loc.csv"))
			.start();		
	}
}
