package com.ltts.client;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.ltts.config.KafkaConfiguration;
import com.ltts.utility.EventListenerTwin;
import com.ltts.utility.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KafkaClient.class, KafkaConfiguration.class,
		EventListenerTwin.class })
@DirtiesContext
public class KafkaClientTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(KafkaClientTest.class);

	private final int KAFKA_MESSAGE_TIMEOUT_IN_MILLISECONDS = 5000;

	private static String TOPIC = "kafka.test.topic";

	@Autowired
	private KafkaClient kafkaClient = new KafkaClient();

	@Autowired
	private EventListenerTwin twin;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	private KafkaMessageListenerContainer<String, String> container;

	private BlockingQueue<ConsumerRecord<String, String>> records;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1,
			true, TOPIC);

	@BeforeClass
	public static void setUpKafka() throws Exception {
		System.setProperty("spring.kafka.bootstrap-servers",
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
		System.setProperty("spring.kafka.topic.name", "kafka.test.topic");
		System.setProperty("spring.kafka.group.id", "kafka.group.id");

	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void setUp() throws Exception {
		// set up the Kafka consumer properties
		Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(
				"sender", "false", embeddedKafka.getEmbeddedKafka());

		// create a Kafka consumer factory
		DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<String, String>(
				consumerProperties);

		// set the topic that needs to be consumed
		ContainerProperties containerProperties = new ContainerProperties(
				TOPIC);

		// create a Kafka MessageListenerContainer
		container = new KafkaMessageListenerContainer<>(consumerFactory,
				containerProperties);

		// create a thread safe queue to store the received message
		records = new LinkedBlockingQueue<>();

		// setup a Kafka message listener
		container.setupMessageListener(new MessageListener<String, String>() {
			@Override
			public void onMessage(ConsumerRecord<String, String> record) {
				LOGGER.debug("test-listener received message='{}'",
						record.toString());
				records.add(record);
			}
		});

		// start the container and underlying message listener
		container.start();

		// wait until the container has the required number of assigned
		// partitions
		ContainerTestUtils.waitForAssignment(container,
				embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
	}

	@After
	public void tearDown() {
		// stop the container
		container.stop();
	}

	@Test
	public void testKafkProduceExceptionWithNullTopic()
			throws InterruptedException {
		User userModel = new User("Jimmy", "Page");
		kafkaClient.produce(null, userModel);
		Thread.sleep(KAFKA_MESSAGE_TIMEOUT_IN_MILLISECONDS);
		assertEquals("Null Topic", EventListenerTwin.exception.getMessage());

	}

	@Test
	public void testKafkProduceExceptionWithInvalidMessage()
			throws InterruptedException {
		kafkaClient.produce(TOPIC, "Jimmy");
		Thread.sleep(KAFKA_MESSAGE_TIMEOUT_IN_MILLISECONDS);
		assertEquals("Invalid Json", EventListenerTwin.exception.getMessage());

	}

	@Test
	public void testKafkProduceConsume() throws InterruptedException {
		User userModel = new User("Jimmy", "Page");
		kafkaClient.produce(TOPIC, userModel);
		Thread.sleep(KAFKA_MESSAGE_TIMEOUT_IN_MILLISECONDS);
		assert (EventListenerTwin.topic).equals(TOPIC);
		assert (EventListenerTwin.message.get("firstName")).equals("Jimmy");
		assert (EventListenerTwin.message.get("lastName")).equals("Page");
	}

}
