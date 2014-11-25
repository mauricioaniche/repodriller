package br.com.metricminer2.executor;

public class ExecutorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExecutorException(String msg, Exception e) {
		super(msg, e);
	}

}
