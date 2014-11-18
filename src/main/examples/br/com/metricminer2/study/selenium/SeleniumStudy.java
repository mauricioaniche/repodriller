package br.com.metricminer2.study.selenium;

import br.com.metricminer2.SourceCodeRepositorySearch;
import br.com.metricminer2.persistence.csv.CSVWriter;
import br.com.metricminer2.scm.git.GitRepository;

public class SeleniumStudy {

	public static void main(String[] args) {
		String defaultPath = System.getProperty("java.io.tmpdir");
		
		new SourceCodeRepositorySearch()
			.in(
				GitRepository.build("/repo1"),
				GitRepository.build("/repo2"),
				GitRepository.build("/repo3"),
				GitRepository.build("/repo4")
			)
			.process(new AddsAndRemoves(), new CSVWriter(defaultPath + "/addsremoves.csv"))
			.process(new CommittedTogether(), new CSVWriter(defaultPath + "/committed.csv"))
			.start();
	}
}
