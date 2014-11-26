package br.com.metricminer2.metric.java8;

import java.util.ArrayList;
import java.util.List;

import br.com.metricminer2.metric.java8.Java8Parser.ConstructorDeclarationContext;
import br.com.metricminer2.metric.java8.Java8Parser.FormalParameterContext;
import br.com.metricminer2.metric.java8.Java8Parser.FormalParameterListContext;
import br.com.metricminer2.metric.java8.Java8Parser.MethodDeclarationContext;

public class Java8AntlrFullMethodName {

	public static String fullMethodName(String name, FormalParameterListContext parameters) {
		boolean empty = parameters == null || parameters.lastFormalParameter() == null;
		if(empty) return name + "/0";

		
		List<FormalParameterContext> allParams = new ArrayList<FormalParameterContext>();
		
		if(parameters.formalParameters()!=null) {
			allParams.addAll(parameters.formalParameters().formalParameter());
		}
		if(parameters.lastFormalParameter()!=null) allParams.add(parameters.lastFormalParameter().formalParameter());
		
		String fullName = name + "/" + allParams.size() + typesIn(allParams);
		
		return fullName;
	}

	private static String typesIn(List<FormalParameterContext> parameters) {
		StringBuilder types = new StringBuilder();
		types.append("[");
		for(FormalParameterContext p : parameters) {
			types.append(p.unannType().getText() + ",");
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
