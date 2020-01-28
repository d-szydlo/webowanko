package com.webapp.websocket.exceptions;

public class CoordinatesOutOfBoundsException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param e
	 */
	public CoordinatesOutOfBoundsException(Exception e) {
		super(e);
	}

	public CoordinatesOutOfBoundsException() {
	}
	
}
