package com.ltts.client;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltts.config.KafkaConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KafkaClient.class, KafkaConfiguration.class })
@DirtiesContext
public class KafkaClientTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(KafkaClientTest.class);

	private static String SENDER_TOPIC = "kafka.test.topic";

	@Autowired
	KafkaClient sender;

	@InjectMocks
	private KafkaClient mockedclient = new KafkaClient();

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	private KafkaMessageListenerContainer<String, String> container;

	private BlockingQueue<ConsumerRecord<String, String>> records;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1,
			true, SENDER_TOPIC);

	@BeforeClass
	public static void setUpKafka() throws Exception {
		System.setProperty("spring.kafka.bootstrap-servers",
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
		System.setProperty("spring.kafka.topic.name", "kafka.test.topic.1");
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
				SENDER_TOPIC);

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

	@Ignore
	@Test
	public void testSend() throws InterruptedException, JsonMappingException,
			JsonProcessingException {
		// send the message
		ClientModel clientModel = new ClientModel("admin", "albert");
		sender.produce(SENDER_TOPIC, clientModel);
		ConsumerRecord<String, String> received = records.poll(10,
				TimeUnit.SECONDS);
		ObjectMapper mapper = new ObjectMapper();
		String result = received.value();
		ClientModel clientModelResult = mapper.readValue(result,
				ClientModel.class);
		System.out.println("Result" + result);
		assertEquals(clientModelResult.getFirstName(), "admin");
		assertEquals(clientModelResult.getLastName(), "albert");
	}

	@Ignore
	@Test
	public void testConsume() {
		ClientModel clientModel = new ClientModel("admin", "albert");
		sender.produce("kafka.test.topic.1", clientModel);
		// verify(applicationEventPublisher, atLeast(1))
		// .publishEvent(Mockito.any());
	}

}
