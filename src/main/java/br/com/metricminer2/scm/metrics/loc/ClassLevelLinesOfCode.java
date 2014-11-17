package br.com.metricminer2.scm.metrics.loc;

import java.io.ByteArrayInputStream;
import java.util.Map;

import br.com.metricminer2.scm.metrics.ClassLevelCodeMetric;
import br.com.metricminer2.scm.metrics.MetricException;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8AntLRVisitor;

public class ClassLevelLinesOfCode implements ClassLevelCodeMetric {

	private LinesOfCodeListener visitor;

	@Override
	public double calculate(String sourceCode) {
		
		try {
			visitor = new LinesOfCodeListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
            
            Map<String, Integer> methodLines = visitor.methodLines();
            int totalOfLines = 0;
            for (Map.Entry<String, Integer> method : methodLines.entrySet()) {
				totalOfLines += method.getValue();
			}
            return totalOfLines;

        } catch (Throwable t) {
            throw new MetricException(this, sourceCode, t);
        }
	}

	@Override
	public String getName() {
		return "class-loc";
	}
	
	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}

	@Override
	public double threshold() {
		return 50;
	}

	@Override
	public String getPrettyName() {
		return "lines of code";
	}

}
