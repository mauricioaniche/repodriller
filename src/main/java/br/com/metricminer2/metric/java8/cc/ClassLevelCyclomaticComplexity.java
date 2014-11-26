package br.com.metricminer2.metric.java8.cc;

import java.io.ByteArrayInputStream;

import br.com.metricminer2.metric.ClassLevelCodeMetric;
import br.com.metricminer2.metric.MetricException;
import br.com.metricminer2.metric.java8.Java8AntLRVisitor;

public class ClassLevelCyclomaticComplexity implements ClassLevelCodeMetric {

	private CCListener visitor;
	
	@Override
	public double calculate(String sourceCode) {
		
		try {
			visitor = new CCListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
			
			return visitor.getCc();
		} catch (Throwable t) {
			throw new MetricException(this, sourceCode, t);
		}

	}

	@Override
	public String getName() {
		return "class-cc";
	}

	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}

}
