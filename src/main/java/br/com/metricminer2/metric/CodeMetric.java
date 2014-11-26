package br.com.metricminer2.metric;

public interface CodeMetric {
	String getName();
	
	boolean accepts(String fileName);
	
}
