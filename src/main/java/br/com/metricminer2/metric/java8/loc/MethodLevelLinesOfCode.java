package br.com.metricminer2.metric.java8.loc;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.metricminer2.metric.MethodLevelCodeMetric;
import br.com.metricminer2.metric.MetricException;
import br.com.metricminer2.metric.java8.Java8AntLRVisitor;

public class MethodLevelLinesOfCode implements MethodLevelCodeMetric {

	public static final double THRESHOLD = 15;
	private LinesOfCodeListener visitor;

	@Override
	public Map<String, Double> calculate(String sourceCode) {
		
		try {
			visitor = new LinesOfCodeListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
            
            Map<String, Double> values = new HashMap<String, Double>();
            for (Map.Entry<String, Integer> method : visitor.methodLines().entrySet()) {
            	values.put(method.getKey(), Double.valueOf(method.getValue()));
			}
            return values;

        } catch (Throwable t) {
            throw new MetricException(this, sourceCode, t);
        }
	}

	@Override
	public String getName() {
		return "method-loc";
	}
	
	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}


}
