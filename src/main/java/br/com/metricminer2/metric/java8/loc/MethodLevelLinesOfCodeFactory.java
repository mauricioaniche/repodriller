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

package br.com.metricminer2.metric.java8.loc;

import br.com.metricminer2.metric.MethodLevelMetric;
import br.com.metricminer2.metric.MethodLevelMetricFactory;

public class MethodLevelLinesOfCodeFactory implements MethodLevelMetricFactory{

	@Override
	public MethodLevelMetric build() {
		return new MethodLevelLinesOfCode();
	}

	@Override
	public String getName() {
		return "method level LOC";
	}

}
