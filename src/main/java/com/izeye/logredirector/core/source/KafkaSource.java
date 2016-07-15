package com.izeye.logredirector.core.source;

import com.izeye.logredirector.core.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@EnableConfigurationProperties(KafkaSourceProperties.class)
@Slf4j
public class KafkaSource extends Source {
	
	@Autowired
	private KafkaSourceProperties properties;
	
	@Autowired
	private StatisticsService statisticsService;

	@Override
	public void run() {
		Map<String, Object> properties = createConsumerProperties();
		Consumer<String, String> consumer = new KafkaConsumer<>(properties);
		consumer.subscribe(Arrays.asList(this.properties.getTopic()));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(this.properties.getTimeoutInMillis());
			if (records.isEmpty()) {
				continue;
			}
			int readCount = records.count();
			this.statisticsService.markSourceProcessed(readCount);
			log.debug("Read {} records from Kafka.", readCount);
			
			for (ConsumerRecord<String, String> record : records) {
				log.debug("{}/{}/{}", record.topic(), record.partition(), record.offset());
				
				passToNext(record.value());
			}
		}
	}

	private Map<String, Object> createConsumerProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				this.properties.getBootstrapServers());
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.properties.getGroupId());
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				this.properties.getKeyDeserializer());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				this.properties.getValueDeserializer());
		return properties;
	}
	
}
