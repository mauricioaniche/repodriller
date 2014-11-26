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

package br.com.metricminer2.metric;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.CommitVisitor;
import br.com.metricminer2.scm.SCMRepository;

public class ClassLevelMetricCalculator implements CommitVisitor {

	private ClassLevelMetricFactory factory;

	public ClassLevelMetricCalculator(ClassLevelMetricFactory factory) {
		this.factory = factory;
	}

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		ClassLevelMetric metric = factory.build();
		for(Modification change : commit.getModifications()) {
			if(metric.accepts(change.getNewPath())) {
				double value = metric.calculate(change.getSourceCode());
				writer.write(commit.getHash(), change.getNewPath(), value);
			}
		}
	}

	@Override
	public String name() {
		return "Class Level metric Processor for " + factory.getName();
	}

}
