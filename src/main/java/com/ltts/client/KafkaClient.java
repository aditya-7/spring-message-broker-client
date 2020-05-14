/*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */

package com.ltts.client;

import com.ltts.event.ServiceExceptionEvent;
import com.ltts.event.ServiceMessageEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * KafkaClient which produces/consumes message
 *
 */
@Service
@ConditionalOnProperty(prefix = "spring.kafka.", value = "bootstrap-servers")
public class KafkaClient implements MessageBrokerClient {

	private static final Logger logger = LoggerFactory
			.getLogger(KafkaClient.class);

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	/**
	 * It consumes the message and publishes to eventlistener
	 */
	@Override
	@KafkaListener(topics = {
			"${spring.kafka.topic.name}" }, groupId = "${spring.kafka.group.id}", containerFactory = "kafkaListenerContainerFactory")
	public void consume(Object kafkaMsg) {
		try {
			ConsumerRecord record = (ConsumerRecord) kafkaMsg;
			HashMap<String, Object> map = (HashMap) record.value();
			ServiceMessageEvent event = new ServiceMessageEvent(this, map,
					record.topic());
			applicationEventPublisher.publishEvent(event);
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
	public <T> void produce(String topic, T kafkaMsg) {
		try {
			kafkaTemplate.send(topic, kafkaMsg);
			logger.trace("Message published into topic: {}" + topic);
		} catch (Exception e) {
			logger.error("Exception while publishing: ", e);
			ServiceExceptionEvent exceptionEvent = new ServiceExceptionEvent(
					this, e);
			applicationEventPublisher.publishEvent(exceptionEvent);
		}
	}

}
