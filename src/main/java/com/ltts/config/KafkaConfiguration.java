package com.ltts.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for Consumer and Producer
 */
@ConditionalOnProperty(prefix = "spring.kafka.", value = "bootstrap-servers")
@EnableKafka
@Configuration
public class KafkaConfiguration {

	@Value("${spring.kafka.bootstrap-servers}")
	String bootStrapServerAddress;

	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		JsonDeserializer<Object> deserializer = new JsonDeserializer<>(
				Object.class);
		deserializer.setRemoveTypeHeaders(false);
		deserializer.addTrustedPackages("*");
		deserializer.setUseTypeMapperForKey(true);

		Map<String, Object> configs = new HashMap<>();
		configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootStrapServerAddress);
		// configs.put(ConsumerConfig.GROUP_ID_CONFIG,
		// ConstantMessage.GROUP_ID);
		configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class);
		configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				JsonDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(configs,
				new StringDeserializer(), deserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootStrapServerAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				JsonSerializer.class);
		return new DefaultKafkaProducerFactory<String, Object>(configProps);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<String, Object>(producerFactory());
	}

}
