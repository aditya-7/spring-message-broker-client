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
import com.ltts.listener.ServiceMessageEvent;

@Service
@ConditionalOnProperty(prefix = "spring.activemq.", value = "broker-url")
public class AMQClient implements MessageBrokerClient {

	private static final Logger logger = LoggerFactory
			.getLogger(AMQClient.class);

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	@JmsListener(destination = "${spring.activemq.topic.name}")
	public void consume(Object message) {
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> map = null;
		try {
			json = ((ActiveMQTextMessage) message).getText();
			map = mapper.readValue(json, HashMap.class);
		} catch (Exception e) {
			logger.error("Exception: " + e);
		}
		ServiceMessageEvent event = new ServiceMessageEvent(this, map,
				((ActiveMQTextMessage) message).getDestination()
						.getPhysicalName());
		applicationEventPublisher.publishEvent(event);

	}

	@Override
	public <T> void produce(String topic, T message) {
		jmsTemplate.convertAndSend(topic, message);
		logger.trace("Message published into topic: " + topic);
	}

}
