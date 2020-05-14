/*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */

package com.ltts.event;

import org.springframework.context.ApplicationEvent;

public class ServiceExceptionEvent extends ApplicationEvent {

	private Throwable exception;

	public ServiceExceptionEvent(Object source, Throwable exception) {
		super(source);
		this.exception = exception;

	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

}
