/*
 * Copyright (c) 2020,L&T Technology Services.
 * All Rights Reserved.
 */

package com.ltts.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * AMQ configuration for Consumer and Producer
 */
@ConditionalOnProperty(prefix = "spring.activemq.", value = "broker-url")
@EnableJms
@Configuration
public class AMQConfiguration {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AMQConfiguration.class);

	@Value("${spring.activemq.broker-url}")
	String brokerUrl;

	@Value("${spring.activemq.user}")
	String brokerUsername;

	@Value("${spring.activemq.password}")
	String brokerPassword;

	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(brokerUrl);
		connectionFactory.setPassword(brokerUsername);
		connectionFactory.setUserName(brokerPassword);
		return connectionFactory;
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setConcurrency("1-1");
		factory.setMessageConverter(jacksonJmsMessageConverter());
		factory.setPubSubDomain(true);
		factory.setErrorHandler(t -> {
			LOGGER.error("Error in listener! ", t);
		});
		return factory;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setPubSubDomain(true);
		template.setMessageConverter(jacksonJmsMessageConverter());
		return template;
	}

}
