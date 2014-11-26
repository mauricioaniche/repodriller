package br.com.metricminer2.metric;

import java.util.Map;

public interface MethodLevelCodeMetric extends CodeMetric {

	Map<String, Double> calculate(String sourceCode);

}
