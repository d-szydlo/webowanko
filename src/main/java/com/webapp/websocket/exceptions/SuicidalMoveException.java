package com.webapp.websocket.exceptions;

public class SuicidalMoveException extends Exception {

	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param e
	 */
	public SuicidalMoveException(Exception e) {
		super(e);
	}

	public SuicidalMoveException() {
	}

}
