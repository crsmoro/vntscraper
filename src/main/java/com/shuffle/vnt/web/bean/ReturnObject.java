package com.shuffle.vnt.web.bean;

public class ReturnObject {

	public ReturnObject(boolean success, String errorMessage, Object errorStack) {
		super();
		this.success = success;
		this.error = new ReturnObjectError(errorMessage, errorStack);
	}

	public ReturnObject(boolean success, Object data) {
		super();
		this.success = success;
		this.data = data;
	}

	private boolean success;

	private Object data;

	public class ReturnObjectError {
		public ReturnObjectError() {
			super();
		}

		public ReturnObjectError(String message, Object stack) {
			super();
			this.message = message;
			this.stack = stack;
		}

		private String message;

		private Object stack;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Object getStack() {
			return stack;
		}

		public void setStack(Object stack) {
			this.stack = stack;
		}
	}

	private ReturnObjectError error;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ReturnObjectError getError() {
		return error;
	}

	public void setError(ReturnObjectError error) {
		this.error = error;
	}
}
