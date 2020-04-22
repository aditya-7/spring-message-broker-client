package com.ltts.client;

import static org.junit.Assert.assertEquals;

import javax.jms.JMSException;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.ltts.config.AMQConfiguration;
import com.ltts.listener.ServiceMessageEvent;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AMQClient.class, AMQConfiguration.class })
@DirtiesContext
@Import(AMQClientTest.AMQClient.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AMQClientTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AMQClientTest.class);

	@Autowired
	MessageBrokerClient amqClient;

	private static ClientModel result2;

	@ClassRule
	public static EmbeddedActiveMQBroker embeddedActiveMQ = new EmbeddedActiveMQBroker();

	@BeforeClass
	public static void setUpAMQ() throws Exception {
		System.setProperty("spring.activemq.broker-url",
				embeddedActiveMQ.getVmURL());
		System.setProperty("spring.activemq.topic.name", "amq.test.topic.1");
	}

	@Test
	@Order(1)
	public void testPublish() throws JMSException {
		ClientModel clientModel = new ClientModel("admin", "user");
		amqClient.produce("amq.test.topic.1", clientModel);
		// TextMessage message = (TextMessage) consumer.receive();

	}

	public static class AMQClient
			implements ApplicationListener<ServiceMessageEvent> {
		ClientModel result1;

		@Override
		public void onApplicationEvent(ServiceMessageEvent event) {
			// TODO Auto-generated method stub
			result1 = MessageBrokerClient.getDao(event.getMessage(),
					ClientModel.class);
			System.out.println("Invoked" + event.getMessage());
			result2 = result1;
			System.out.println("Message: " + result2.getFirstName());
		}

	}

	@Test
	@Order(2)
	public void testlistener() {
		if (result2 != null) {
			assertEquals(result2.getFirstName(), "abh");
		} else {
			System.out.println("result2 is null");
		}

	}

}
