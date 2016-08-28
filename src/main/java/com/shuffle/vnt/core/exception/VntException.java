package com.shuffle.vnt.core.exception;

public class VntException extends RuntimeException {

	private static final long serialVersionUID = -2723995691408159451L;

	private String message;

	public VntException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
