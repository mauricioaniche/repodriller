package br.com.metricminer2.scm.metrics;

public class MetricException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MetricException(ClassLevelCodeMetric metric,
			String sourceCode, Throwable t) {
		super("Metric: " + metric.getClass().getName() + " in source code " + sourceCode, t);
	}
	
	public MetricException(MethodLevelCodeMetric metric,
			String sourceCode, Throwable t) {
		super("Metric: " + metric.getClass().getName() + " in source code " + sourceCode, t);
	}

}
