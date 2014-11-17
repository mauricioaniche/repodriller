package br.com.metricminer2.scm.metrics;


public interface ClassLevelCodeMetric extends CodeMetric {

	double calculate(String sourceCode);

}
