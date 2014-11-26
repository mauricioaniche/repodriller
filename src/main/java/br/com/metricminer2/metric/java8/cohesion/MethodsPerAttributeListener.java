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

package br.com.metricminer2.metric.java8.cohesion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import br.com.metricminer2.metric.java8.Java8AntlrFullMethodName;
import br.com.metricminer2.metric.java8.Java8BaseListener;
import br.com.metricminer2.metric.java8.Java8Parser;

public class MethodsPerAttributeListener extends Java8BaseListener {

	private Stack<String> currentMethod;
	private final List<String> attributes;
	private Map<String, Set<String>> methodsPerAttribute;

	public MethodsPerAttributeListener(List<String> attributes) {
		this.attributes = attributes;
		currentMethod = new Stack<String>();
		methodsPerAttribute = new HashMap<String, Set<String>>();
	}

	@Override public void enterExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { 
		checkUsedVariable(ctx);
	}

	private void checkUsedVariable(ParserRuleContext ctx) {
		String name = ctx.getText();

		if (currentMethod.size() > 0 && attributes.contains(name)) {
			if (!methodsPerAttribute.containsKey(name))
				methodsPerAttribute.put(name, new HashSet<String>());
			methodsPerAttribute.get(name).add(currentMethod.peek());
		}
	}
	
	@Override
	public void enterTypeName(@NotNull Java8Parser.TypeNameContext ctx) {
		checkUsedVariable(ctx);
	}

	@Override
	public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {

		
		currentMethod.push(Java8AntlrFullMethodName.fullMethodName(ctx));

	}

	@Override
	public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
		currentMethod.pop();
	}

	@Override public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
		currentMethod.push(Java8AntlrFullMethodName.fullMethodName(ctx));
	}
	
	@Override public void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
		currentMethod.pop();
		
	}

	public Map<String, Set<String>> getMethodsPerAttribute() {
		return methodsPerAttribute;
	}

}
