package br.com.metricminer2.scm.metrics.methods;

import java.io.ByteArrayInputStream;
import java.util.List;

import br.com.metricminer2.scm.metrics.ClassLevelCodeMetric;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8AntLRVisitor;
import br.com.metricminer2.scm.metrics.common.MethodsAndAttributesListener;

public class NumberOfMethods implements ClassLevelCodeMetric {

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
