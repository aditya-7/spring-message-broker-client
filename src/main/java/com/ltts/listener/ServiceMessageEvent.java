package com.ltts.listener;

import java.util.HashMap;

import org.springframework.context.ApplicationEvent;

public class ServiceMessageEvent extends ApplicationEvent {

	private HashMap<String, Object> message;
	private String topic;

	public ServiceMessageEvent(Object source, HashMap<String, Object> message,
			String topic) {
		super(source);
		this.message = message;
		this.topic = topic;
	}

	public HashMap<String, Object> getMessage() {

		return message;
	}

	public String getTopic() {
		return topic;
	}

}
