/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.metricminer2.study.selenium;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.metric.MetricCalculator;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.SCMRepository;

public class AddsAndRemoves implements MetricCalculator {

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
