package br.com.metricminer2.metric;


public interface ClassLevelCodeMetric extends CodeMetric {

	double calculate(String sourceCode);

}
