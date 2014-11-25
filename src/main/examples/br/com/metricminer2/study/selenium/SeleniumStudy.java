package br.com.metricminer2.study.selenium;

import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;

public class SeleniumStudy {

	public static void main(String[] args) {
		String defaultPath = System.getProperty("java.io.tmpdir");
		
		new SourceCodeRepositoryStudy()
			.in(
				GitRepository.build("/repo1"),
				GitRepository.build("/repo2"),
				GitRepository.build("/repo3"),
				GitRepository.build("/repo4")
			)
			.process(new AddsAndRemoves(), new CSVFile(defaultPath + "/addsremoves.csv"))
			.process(new CommittedTogether(), new CSVFile(defaultPath + "/committed.csv"))
			.start();
	}
}
