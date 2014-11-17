package br.com.metricminer2.metrics.cc;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.metricminer2.metrics.MethodLevelCodeMetric;
import br.com.metricminer2.metrics.MetricException;
import br.com.metricminer2.metrics.antlr.java8.Java8AntLRVisitor;

public class MethodLevelCyclomaticComplexity implements MethodLevelCodeMetric {

	public static final double THRESHOLD = 5;

	private CCListener visitor;
	
	@Override
	public Map<String, Double> calculate(String sourceCode) {
		
		try {
			visitor = new CCListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
            
            Map<String, Double> values = new HashMap<String, Double>();
            for(Map.Entry<String, Integer> v : visitor.getCcPerMethod().entrySet()) {
            	values.put(v.getKey(), Double.valueOf(v.getValue()));
            }
            
            return values;

        } catch (Throwable t) {
            throw new MetricException(this, sourceCode, t);
        }
	}

	@Override
	public String getName() {
		return "method-cc";
	}

	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}

	@Override
	public double threshold() {
		return THRESHOLD;
	}

	@Override
	public String getPrettyName() {
		return "complexity";
	}

}
