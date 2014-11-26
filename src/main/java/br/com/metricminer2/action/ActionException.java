package br.com.metricminer2.action;

public class ActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ActionException(String msg, Exception e) {
		super(msg, e);
	}

}
