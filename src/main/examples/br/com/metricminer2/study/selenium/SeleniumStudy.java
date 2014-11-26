package br.com.metricminer2.study.selenium;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryNavigator;

public class SeleniumStudy implements Study {

	@Override
	public void execute(MMOptions opts) {
		String defaultPath = opts.getCsv();
		
		new SourceCodeRepositoryNavigator()
			.in(
				GitRepository.allIn(opts.getProjectsPath())
			)
			.process(new AddsAndRemoves(), new CSVFile(defaultPath, "/addsremoves.csv"))
			.process(new CommittedTogether(), new CSVFile(defaultPath, "/committed.csv"))
			.start();		
	}
}
