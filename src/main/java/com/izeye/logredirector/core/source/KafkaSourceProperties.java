package com.izeye.logredirector.core.source;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by izeye on 16. 7. 12..
 */
@Data
@ConfigurationProperties(prefix = "log-redirector.source.kafka")
public class KafkaSourceProperties {

	private List<String> bootstrapServers;
	private String topic;
	private String groupId;
	private Class<?> keyDeserializer;
	private Class<?> valueDeserializer;
	private long timeoutInMillis;
	
}
