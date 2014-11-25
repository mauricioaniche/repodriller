package br.com.metricminer2.study.selenium;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.Modification;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.metrics.MetricProcessor;

public class AddsAndRemoves implements MetricProcessor {

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		for(Modification m : commit.getModifications()) {
			if(Utils.isASeleniumTest(m)) {
				String[] lines = m.getDiff().replace("\r", "").split("\n");
				
				for(String line : lines) {
					for(Categories c : Categories.values()) {
						if(isAddOrRemove(line) && c.isContainedIn(line)) {
							writer.write(
								repo.getLastDir(), 
								commit.getHash(), 
								Utils.format(commit.getDate()), 
								m.getNewPath(), 
								c.name(), 
								line.startsWith("+") ? "added" : "removed"
							);
						}
					}
				}
			}
		}
	}
	
	private boolean isAddOrRemove(String line) {
		return line.startsWith("+") || line.startsWith("-");
	}
	
	@Override
	public String name() {
		return "selenium adds and removes";
	}

}
