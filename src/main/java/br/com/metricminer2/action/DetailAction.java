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

package br.com.metricminer2.action;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.SourceCodeRepositoryDetailer;

public class DetailAction extends Action {

	public DetailAction(Action next) {
		super(next);
	}
	
	public DetailAction() {
		super(null);
	}

	@Override
	protected boolean accept(MMOptions opts) {
		return opts.isDetail();
	}

	@Override
	protected void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		SourceCodeRepositoryDetailer detail = new SourceCodeRepositoryDetailer(pm);
		detail.detail(opts.getProjectsPath());
		pm.close();
	}

	
}
