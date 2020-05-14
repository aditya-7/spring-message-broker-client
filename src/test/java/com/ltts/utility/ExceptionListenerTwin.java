package com.ltts.utility;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.ltts.event.ServiceExceptionEvent;

@Component
public class ExceptionListenerTwin
		implements ApplicationListener<ServiceExceptionEvent> {

	public static Throwable exception;

	@Override
	public void onApplicationEvent(ServiceExceptionEvent event) {
		exception = event.getException();
	}

}
