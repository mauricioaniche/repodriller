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

package org.repodriller.parser.antlr.java8;

import java.util.ArrayList;
import java.util.List;

import org.repodriller.parser.antlr.java8.Java8Parser.ConstructorDeclarationContext;
import org.repodriller.parser.antlr.java8.Java8Parser.FormalParameterContext;
import org.repodriller.parser.antlr.java8.Java8Parser.FormalParameterListContext;
import org.repodriller.parser.antlr.java8.Java8Parser.LastFormalParameterContext;
import org.repodriller.parser.antlr.java8.Java8Parser.MethodDeclarationContext;

public class Java8AntlrMethods {

	public static String fullMethodName(String name, FormalParameterListContext parameters) {
		boolean empty = parameters == null || parameters.lastFormalParameter() == null;
		if(empty) return name + "/0";

		
		List<String> allParams = new ArrayList<String>();
		
		if(parameters.formalParameters()!=null) {
			
			for(FormalParameterContext f : parameters.formalParameters().formalParameter()) {
				if(f!=null) {
					allParams.add(f.unannType().getText());
				}
			}
		}
		
		if(parameters.lastFormalParameter()!=null) {
			LastFormalParameterContext p = parameters.lastFormalParameter();
			if(p!=null && p.formalParameter() !=null) {
				String f = p.formalParameter().unannType().getText();
				if(p.ELLIPSIS()!=null && !p.ELLIPSIS().getText().isEmpty()) f+="...";
				allParams.add(f);
			}
		}
		
		String fullName = name + "/" + allParams.size() + typesIn(allParams);
		
		return fullName;
	}
	
	public static boolean hasNoParameters(FormalParameterListContext parameters) {
		boolean noLastParameter = parameters == null || parameters.lastFormalParameter() == null;
		boolean noParams = (parameters == null || parameters.formalParameters() == null || parameters.formalParameters().formalParameter() == null);
		
		return noLastParameter && noParams;
	}
	
	public static List<FormalParameterContext> params(FormalParameterListContext parameters) {
		boolean noLastParameter = parameters == null || parameters.lastFormalParameter() == null;
		boolean noParams = (parameters == null || parameters.formalParameters() == null || parameters.formalParameters().formalParameter() == null);
		
		List<FormalParameterContext> all = new ArrayList<FormalParameterContext>();
		if(!noParams) {
			all.addAll(parameters.formalParameters().formalParameter());
		}
		if(!noLastParameter) all.add(parameters.lastFormalParameter().formalParameter());
		
		return all;
	}

	private static String typesIn(List<String> parameters) {
		StringBuilder types = new StringBuilder();
		types.append("[");
		for(String p : parameters) {
			types.append(p + ",");
		}
		
		return types.substring(0, types.length() - 1) + "]";
	}

	public static String fullMethodName(MethodDeclarationContext ctx) {
		return fullMethodName(ctx.methodHeader().methodDeclarator().Identifier().getText(), ctx.methodHeader().methodDeclarator().formalParameterList());		
	}

	public static String fullMethodName(ConstructorDeclarationContext ctx) {
		return fullMethodName(ctx.constructorDeclarator().simpleTypeName().getText(), ctx.constructorDeclarator().formalParameterList());
	}
}
