/*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */
package com.ltts.exception;

/**
 * Creates a Custom Exception based on the message and the HTTP status passed.
 */
public class MessageBrokerException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String message;

	public MessageBrokerException(Throwable cause, String message) {
		super(cause);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
