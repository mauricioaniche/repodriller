package br.com.metricminer2.metrics.attributes;

import java.io.ByteArrayInputStream;

import br.com.metricminer2.metrics.ClassLevelCodeMetric;
import br.com.metricminer2.metrics.antlr.java8.Java8AntLRVisitor;
import br.com.metricminer2.metrics.common.MethodsAndAttributesListener;

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

	@Override
	public double threshold() {
		return 10.0;
	}

	@Override
	public String getPrettyName() {
		return "number of attributes";
	}
	
}
