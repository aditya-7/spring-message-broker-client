package com.ltts.utility;

import java.util.HashMap;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.ltts.event.ServiceMessageEvent;

@Component
public class EventListenerTwin
		implements ApplicationListener<ServiceMessageEvent> {

	public static HashMap<String, Object> message;
	public static String topic;

	@Override
	public void onApplicationEvent(ServiceMessageEvent event) {
		message = event.getMessage();
		topic = event.getTopic();
	}

}
