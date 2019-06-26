package com.beifang.exception;

public class NoConferenceException extends PageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoConferenceException() {
		super("2001", "no ongoing conference");
		
	}
	
}
