package org.repodriller;

/**
 * Catch-all type for internal exceptions thrown by RepoDriller.
 *
 * @author Mauricio Aniche
 */
public class RepoDrillerException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public RepoDrillerException(String msg) {
		super(msg);
	}

	public RepoDrillerException(Exception e) {
		super(e);
	}

}