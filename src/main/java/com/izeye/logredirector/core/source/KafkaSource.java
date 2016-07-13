package com.izeye.logredirector.core.source;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@EnableConfigurationProperties(KafkaSourceProperties.class)
@Slf4j
public class KafkaSource extends Source {
	
	@Autowired
	private KafkaSourceProperties properties;

	private final AtomicLong counter = new AtomicLong(0);
	private final AtomicLong failureCounter = new AtomicLong(0);

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
			this.counter.addAndGet(records.count());
			
			for (ConsumerRecord<String, String> record : records) {
				String value = record.value();
				try {
					passToNext(value);
				}
				catch (Throwable ex) {
					this.failureCounter.incrementAndGet();
					log.error("Unexpected error.", ex);
				}
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

	@Scheduled(cron = "* * * * * ?")
	public void printStatistics() {
		long count = this.counter.getAndSet(0);
		log.info("# of consumed logs per second in source: {}", count);
		log.info("# of failures: {}", this.failureCounter.get());
	}
	
}
