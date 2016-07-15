package com.izeye.logredirector.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Created by izeye on 16. 7. 12..
 */
public class KafkaLogProducer {
	
	String bootstrapServers = "localhost:9092";
	String topic = "my-topic";
	String keySerializer = StringSerializer.class.getName();
	String valueSerializer = StringSerializer.class.getName();
	
	@Ignore
	@Test
	public void run() {
		Properties properties = new Properties();
		properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
		properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, this.keySerializer);
		properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, this.valueSerializer);

		Producer<String, String> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 100; i++) {
			String log = "1\t" + System.currentTimeMillis() + "\t" + i + "\t" + (i * 10);
			producer.send(new ProducerRecord<>(this.topic, null, log));
		}

		// NOTE: Should sleep because `send()` is asynchronous.
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
