package com.demo.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.demo.bo.OrderEvent;
import com.demo.bo.OrderRequest;

/**
 * @author Prakash_G01
 *
 */
@Configuration
@EnableKafka
public class KafkaEventConsumerConfig {
	
	@Value("${kafka.consumer.bootstrap.servers:localhost:9092}")
	private String bootstrapServers;
	
	@Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "Event_Consumer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

	@Bean
	  public ConsumerFactory<String, OrderRequest> consumerFactory() {
	    return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
	        new JsonDeserializer<>(OrderRequest.class));
	  }
    @Bean(name = "kafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,OrderRequest>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,OrderRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(10);
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

	
	@Bean
	public KafkaEventConsumer consumer() {
		return new KafkaEventConsumer();
	}

}
