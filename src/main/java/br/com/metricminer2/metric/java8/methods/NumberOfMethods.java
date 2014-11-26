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

package br.com.metricminer2.metric.java8.methods;

import java.io.ByteArrayInputStream;
import java.util.List;

import br.com.metricminer2.metric.ClassLevelMetric;
import br.com.metricminer2.metric.java8.Java8AntLRVisitor;
import br.com.metricminer2.metric.java8.MethodsAndAttributesListener;

public class NumberOfMethods implements ClassLevelMetric {

	private MethodsAndAttributesListener visitor;

	@Override
	public double calculate(String sourceCode) {
		try {
			visitor = new MethodsAndAttributesListener(true);
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));

			return getMethods().size();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	@Override
	public boolean accepts(String fileName) {
		return fileName.endsWith(".java");
	}

	@Override
	public String getName() {
		return "number-of-methods";
	}
	
	private List<Method> getMethods() {
		return visitor.getMethods();
	}

	
}
