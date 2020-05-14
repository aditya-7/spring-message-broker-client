/*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */

package com.ltts.client;

import java.util.HashMap;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltts.event.ServiceExceptionEvent;
import com.ltts.event.ServiceMessageEvent;

/**
 * AMQClient which produces/consumes message
 *
 */
@Service
@ConditionalOnProperty(prefix = "spring.activemq.", value = "broker-url")
public class AMQClient implements MessageBrokerClient {

	private static final Logger logger = LoggerFactory
			.getLogger(AMQClient.class);

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private JmsTemplate jmsTemplate;

	/**
	 * It consumes the message and publishes to eventlistener
	 */
	@Override
	@JmsListener(destination = "${spring.activemq.topic.name}")
	public void consume(Object message) {
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> map = null;
		try {
			json = ((ActiveMQTextMessage) message).getText();
			map = mapper.readValue(json, HashMap.class);
			ServiceMessageEvent event = new ServiceMessageEvent(this, map,
					((ActiveMQTextMessage) message).getDestination()
							.getPhysicalName());
			applicationEventPublisher.publishEvent(event);
			logger.info("Published event on: {}", event.getTopic());
		} catch (Exception e) {
			logger.error("Exception while consuming: ", e);
			ServiceExceptionEvent exceptionEvent = new ServiceExceptionEvent(
					this, e);
			applicationEventPublisher.publishEvent(exceptionEvent);
		}
	}

	/**
	 * It publishes the message to specified topic
	 */
	@Override
	public <T> void produce(String topic, T message) {

		try {
			jmsTemplate.convertAndSend(topic, message);
			logger.trace("Message published into topic: {}", topic);
		} catch (Exception e) {
			logger.error("Exception while publishing", e);
			ServiceExceptionEvent exceptionEvent = new ServiceExceptionEvent(
					this, e);
			applicationEventPublisher.publishEvent(exceptionEvent);
		}

	}

}
