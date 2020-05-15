/*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */

package com.ltts.client;

import java.util.HashMap;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltts.constants.ConstantMessage;
import com.ltts.event.ServiceMessageEvent;
import com.ltts.exception.MessageBrokerException;

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
			ServiceMessageEvent messageEvent = new ServiceMessageEvent(this,
					map, ((ActiveMQTextMessage) message).getDestination()
							.getPhysicalName());
			applicationEventPublisher.publishEvent(messageEvent);
		} catch (JMSException e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.JMS_EXCEPTION);
			ServiceMessageEvent exceptionEvent = new ServiceMessageEvent(this,
					brokerException);
			applicationEventPublisher.publishEvent(exceptionEvent);
		} catch (JsonMappingException e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.INCOMPATABLE_TYPES);
			ServiceMessageEvent exceptionEvent = new ServiceMessageEvent(this,
					brokerException);
			applicationEventPublisher.publishEvent(exceptionEvent);
		} catch (JsonProcessingException e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.JSON_PRPCOESSING);
			ServiceMessageEvent exceptionEvent = new ServiceMessageEvent(this,
					brokerException);
			applicationEventPublisher.publishEvent(exceptionEvent);
		} catch (Exception e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.GENERAL_EXCEPTION);
			ServiceMessageEvent exceptionEvent = new ServiceMessageEvent(this,
					brokerException);
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
		} catch (IllegalArgumentException e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.INVALID_TOPIC);
			ServiceMessageEvent exceptionEvent = new ServiceMessageEvent(this,
					brokerException);
			applicationEventPublisher.publishEvent(exceptionEvent);
		} catch (NullPointerException e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.INVALID_MESSAGE);
			ServiceMessageEvent exceptionEvent = new ServiceMessageEvent(this,
					brokerException);
			applicationEventPublisher.publishEvent(exceptionEvent);
		}

	}

}
