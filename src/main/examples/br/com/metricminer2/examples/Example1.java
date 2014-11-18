package br.com.metricminer2.examples;

import br.com.metricminer2.SourceCodeRepositorySearch;
import br.com.metricminer2.persistence.csv.CSVWriter;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.metrics.cc.ClassLevelCyclomaticComplexity;
import br.com.metricminer2.scm.processor.ClassLevelMetricProcessor;

public class Example1 {

	public static void main(String[] args) {
		String repoPath = Example1.class.getResource("/repo-1/").getPath();
		String csvPath = Example1.class.getResource("/").getPath() + "cc.csv";
		
		new SourceCodeRepositorySearch()
			.in(GitRepository.build(repoPath))
			.process(new ClassLevelMetricProcessor(new ClassLevelCyclomaticComplexity()), new CSVWriter(csvPath))
			.start();
	}
}
