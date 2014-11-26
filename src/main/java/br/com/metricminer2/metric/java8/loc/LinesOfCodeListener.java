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
