package br.com.metricminer2.scm.metrics;

public interface CodeMetric {
	String getName();
	
	boolean accepts(String fileName);
	
}
