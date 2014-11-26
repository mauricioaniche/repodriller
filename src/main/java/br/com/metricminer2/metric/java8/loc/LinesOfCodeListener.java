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

import java.util.HashMap;
import java.util.Map;

import br.com.metricminer2.metric.java8.Java8AntlrFullMethodName;
import br.com.metricminer2.metric.java8.Java8BaseListener;
import br.com.metricminer2.metric.java8.Java8Parser;

public class LinesOfCodeListener extends Java8BaseListener {

	private Map<String, Integer> methodLines = new HashMap<String, Integer>();
	
	public LinesOfCodeListener() {
		methodLines = new HashMap<String, Integer>();
	}
	
	@Override public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
		methodLines.put(Java8AntlrFullMethodName.fullMethodName(ctx),
				ctx.stop.getLine() - ctx.start.getLine());
	}
	
	@Override public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
		methodLines.put(Java8AntlrFullMethodName.fullMethodName(ctx),
				ctx.stop.getLine() - ctx.start.getLine());
	}
	
	public Map<String, Integer> methodLines() {
		return methodLines;
	}

}
