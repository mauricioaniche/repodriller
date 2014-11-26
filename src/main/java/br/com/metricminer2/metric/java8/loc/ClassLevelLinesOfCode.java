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

import java.io.ByteArrayInputStream;
import java.util.Map;

import br.com.metricminer2.metric.ClassLevelCodeMetric;
import br.com.metricminer2.metric.MetricException;
import br.com.metricminer2.metric.java8.Java8AntLRVisitor;

public class ClassLevelLinesOfCode implements ClassLevelCodeMetric {

	private LinesOfCodeListener visitor;

	@Override
	public double calculate(String sourceCode) {
		
		try {
			visitor = new LinesOfCodeListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
            
            Map<String, Integer> methodLines = visitor.methodLines();
            int totalOfLines = 0;
            for (Map.Entry<String, Integer> method : methodLines.entrySet()) {
				totalOfLines += method.getValue();
			}
            return totalOfLines;

        } catch (Throwable t) {
            throw new MetricException(this, sourceCode, t);
        }
	}

	@Override
	public String getName() {
		return "class-loc";
	}
	
	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}


}
