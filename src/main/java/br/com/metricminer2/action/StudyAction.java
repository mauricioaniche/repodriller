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

import org.apache.log4j.Logger;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;

public class StudyAction extends Action {

	private static Logger log = Logger.getLogger(StudyAction.class);
	
	public StudyAction(Action next) {
		super(next);
	}
	
	public StudyAction() {
		super(null);
	}
	
	@Override
	protected boolean accept(MMOptions opts) {
		return opts.isStudy();
	}

	@Override
	protected void execute(MMOptions opts) {
		try {
			log.info("Initializing study in " + opts.getStudy());
			
			Class<?> studyClazz = Class.forName(opts.getStudy());
			Study study = (Study) studyClazz.newInstance();
			study.execute(opts);

		} catch (Exception e) {
			throw new ActionException("error executing study", e);
		}

	}

}
