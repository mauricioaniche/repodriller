package br.com.metricminer2.metric.java8.attributes;

import java.io.ByteArrayInputStream;

import br.com.metricminer2.metric.ClassLevelCodeMetric;
import br.com.metricminer2.metric.java8.Java8AntLRVisitor;
import br.com.metricminer2.metric.java8.MethodsAndAttributesListener;

public class NumberOfAttributes implements ClassLevelCodeMetric {

	private MethodsAndAttributesListener visitor;

	@Override
	public double calculate(String sourceCode) {
		try {
			
			visitor = new MethodsAndAttributesListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
			
			return visitor.getAttributes().size();
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
		return "number-of-attributes";
	}

}
