/*
 /*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */

package com.ltts.client;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltts.constants.ConstantMessage;
import com.ltts.exception.MessageBrokerException;

/**
 * A client which will produces and consumes to/from message brokers like
 * Kafka,ActiveMQ etc.
 *
 */
public interface MessageBrokerClient {

	/**
	 * Converts given message of type {@link java.util.HashMap<String, Object>}
	 * to the specified class object
	 * 
	 * @param <T>
	 * @param map      : Message of type {@link java.util.HashMap<String,
	 *                 Object>}
	 * @param daoClass : Any class, message to be converted into.
	 * @return : specified class object
	 */
	static <T> T getDao(HashMap<String, Object> map, Class daoClass) {
		final ObjectMapper mapper = new ObjectMapper();
		T dao = null;
		try {
			dao = (T) mapper.convertValue(map, daoClass);

		} catch (IllegalArgumentException e) {
			MessageBrokerException brokerException = new MessageBrokerException(
					e, ConstantMessage.UNRECOGNIZED_PROPERTIES_IN_OBJECT);
			throw brokerException;
		}
		return dao;
	}

	/**
	 * Consumes message from given topic
	 * 
	 * @see {@link com.ltts.client.AMQClient}
	 *      {@link com.ltts.client.KafkaClient}
	 * @param message: consumed message
	 */
	void consume(Object message);

	/**
	 * Produces message to given topic
	 * 
	 * @see {@link com.ltts.client.AMQClient}
	 *      {@link com.ltts.client.KafkaClient}
	 * @param message : message to be produced
	 * @param topic   : topic name
	 */
	<T> void produce(String topic, T message);

}