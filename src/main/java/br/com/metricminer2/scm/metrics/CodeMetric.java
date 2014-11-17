package br.com.metricminer2.scm.metrics;

public interface CodeMetric {
	String getName();
	String getPrettyName();
	
	boolean accepts(String fileName);
	
	double threshold();
	
}
