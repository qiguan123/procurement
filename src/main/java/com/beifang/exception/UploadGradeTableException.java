package com.beifang.exception;

public class UploadGradeTableException extends PageException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadGradeTableException(String msg) {
		super("2002", msg);
	}


}
