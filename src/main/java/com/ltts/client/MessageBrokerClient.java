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

public interface MessageBrokerClient {

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

	void consume(Object message);

	<T> void produce(String topic, T message);

}