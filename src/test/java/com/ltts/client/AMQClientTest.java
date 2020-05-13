package com.ltts.client;

import static org.junit.Assert.assertEquals;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ltts.config.AMQConfiguration;
import com.ltts.utility.EventListenerTwin;
import com.ltts.utility.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AMQClient.class, AMQConfiguration.class,
		EventListenerTwin.class })
public class AMQClientTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AMQClientTest.class);

	private final int AMQ_MESSAGE_TIMEOUT_IN_MILLISECONDS = 5000;

	@Autowired
	AMQClient amqClient;

	@Autowired
	private EventListenerTwin twin;

	@ClassRule
	public static EmbeddedActiveMQBroker embeddedActiveMQ = new EmbeddedActiveMQBroker();

	@BeforeClass
	public static void setUpAMQ() throws Exception {
		System.setProperty("spring.activemq.broker-url",
				embeddedActiveMQ.getVmURL());
		System.setProperty("spring.activemq.topic.name", "amq.test.topic");
	}

	@Test
	public void testProduceWithNullTopic() {
		User clientModel = new User("Robert", "Plant");
		try {
			amqClient.produce(null, clientModel);
		} catch (Exception e) {
			assertEquals("Destination name must not be null", e.getMessage());
		}
	}

	@Test
	public void testProduceWithNullMessage() {
		try {
			amqClient.produce("amq.test.topic", null);
		} catch (Exception e) {
			assertEquals(null, e.getMessage());
		}
	}

	@Test
	public void testAmqProduceConsume() throws InterruptedException {
		User clientModel = new User("Robert", "Plant");
		amqClient.produce("amq.test.topic", clientModel);
		Thread.sleep(AMQ_MESSAGE_TIMEOUT_IN_MILLISECONDS);
		assert (EventListenerTwin.topic).equals("amq.test.topic");
		assert (EventListenerTwin.message.get("firstName")).equals("Robert");
		assert (EventListenerTwin.message.get("lastName")).equals("Plant");
	}

}
