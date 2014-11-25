package br.com.metricminer2.examples;

import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.metrics.cc.ClassLevelCyclomaticComplexity;
import br.com.metricminer2.scm.processor.ClassLevelMetricProcessor;

public class Example5 {

	public static void main(String[] args) {
		String repoPath = "/Users/mauricioaniche/workspace";
		String csvPath = "cc.csv";
		
		new SourceCodeRepositoryStudy()
			.in(GitRepository.allIn(repoPath))
			.process(new ClassLevelMetricProcessor(new ClassLevelCyclomaticComplexity()), new CSVFile(csvPath))
			.start();
	}
}
