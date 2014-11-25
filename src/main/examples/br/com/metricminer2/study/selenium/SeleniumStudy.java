package br.com.metricminer2.study.selenium;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.SourceCodeRepositoryStudy;
import br.com.metricminer2.scm.git.GitRepository;

public class SeleniumStudy implements Study {

	@Override
	public void execute(MMOptions opts) {
		String defaultPath = opts.getCsv();
		
		new SourceCodeRepositoryStudy()
			.in(
				GitRepository.allIn(opts.getProjects())
			)
			.process(new AddsAndRemoves(), new CSVFile(defaultPath, "/addsremoves.csv"))
			.process(new CommittedTogether(), new CSVFile(defaultPath, "/committed.csv"))
			.start();		
	}
}
