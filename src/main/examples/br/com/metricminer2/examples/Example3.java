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

package br.com.metricminer2.examples;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;
import br.com.metricminer2.metric.ClassLevelMetricCalculator;
import br.com.metricminer2.metric.MethodLevelMetricCalculator;
import br.com.metricminer2.metric.java8.cc.MethodLevelCyclomaticComplexity;
import br.com.metricminer2.metric.java8.loc.ClassLevelLinesOfCode;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SourceCodeRepositoryNavigator;

public class Example3 implements Study {

	@Override
	public void execute(MMOptions opts) {
		String repoPath = opts.getProjectsPath();
		String outPath = opts.getCsv(); 
		
		new SourceCodeRepositoryNavigator(opts)
			.in(GitRepository.build(repoPath))
			.process(new MethodLevelMetricCalculator(new MethodLevelCyclomaticComplexity()), new CSVFile(outPath, "cc.csv"))
			.process(new ClassLevelMetricCalculator(new ClassLevelLinesOfCode()), new CSVFile(outPath, "loc.csv"))
			.start();		
	}
}
