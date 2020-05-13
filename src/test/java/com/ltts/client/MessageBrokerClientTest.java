package com.ltts.client;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ltts.config.KafkaConfiguration;
import com.ltts.utility.EventListenerTwin;
import com.ltts.utility.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KafkaClient.class, KafkaConfiguration.class,
		EventListenerTwin.class })
@DirtiesContext
public class MessageBrokerClientTest {

	@Test
	public void testGetDao()
			throws JsonMappingException, JsonProcessingException {
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("firstName", "tokyo");
		hashMap.put("lastName", "nairobi");
		User userResult = MessageBrokerClient.getDao(hashMap, User.class);
		assertEquals("tokyo", userResult.getFirstName());
		assertEquals("nairobi", userResult.getLastName());
	}

}
