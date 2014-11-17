package br.com.metricminer2.metrics;


public interface ClassLevelCodeMetric extends CodeMetric {

	double calculate(String sourceCode);

}
