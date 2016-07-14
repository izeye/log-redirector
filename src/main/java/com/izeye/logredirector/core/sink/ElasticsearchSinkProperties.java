package com.izeye.logredirector.core.sink;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by izeye on 16. 7. 13..
 */
@Data
@ConfigurationProperties(prefix = "log-redirector.sink.elasticsearch")
public class ElasticsearchSinkProperties {
	
	private String clusterName;
	private String clusterNodes;
	
	private String indexName;
	private String indexNameSuffixDateFormat;
	private String typeName;
	
	private int batchSize;
	private int threadPoolSize;
	
}
