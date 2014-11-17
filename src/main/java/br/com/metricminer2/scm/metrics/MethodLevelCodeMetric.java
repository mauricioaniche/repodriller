package br.com.metricminer2.scm.metrics;

import java.util.Map;

public interface MethodLevelCodeMetric extends CodeMetric {

	Map<String, Double> calculate(String sourceCode);

}
