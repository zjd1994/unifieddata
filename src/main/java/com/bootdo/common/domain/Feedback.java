package com.bootdo.common.domain;

public class Feedback {
	public static final int SUCCESS = 0;

	public static class FeedbackWithData extends Feedback {
		private Object data;

		private FeedbackWithData(int status, Object data) {
			super(status);
			this.data = data;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}

	public static Feedback createSuccess() {
		return new Feedback(SUCCESS);
	}

	public static Feedback createSuccess(Object data) {
		return new FeedbackWithData(SUCCESS, data);
	}

	public static Feedback create(int status, Object data) {
		return new FeedbackWithData(status, data);
	}

	public static Feedback create(int status, String format, Object... args) {
		return create(status, String.format(format, args));
	}

	private int status;

	private Feedback() {
	}

	private Feedback(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
