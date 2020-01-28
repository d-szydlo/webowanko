package com.webapp.websocket.exceptions;

public class KoRuleViolatedException extends Exception {

	private static final long serialVersionUID = 1L;
	/**
	 * @param e
	 */
	public KoRuleViolatedException(Exception e) {
		super(e);
	}

	public KoRuleViolatedException() {
	}

}
