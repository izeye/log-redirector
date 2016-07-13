package com.izeye.logredirector.core.sink;

import static com.izeye.logredirector.core.domain.FieldConstants.TIMESTAMP;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@EnableConfigurationProperties(ElasticsearchSinkProperties.class)
@Slf4j
public class ElasticsearchSink extends Sink {

	private static final String CLUSTER_NAME = "cluster.name";
	
	private static final char COLON = ':';
	
	@Autowired
	private ElasticsearchSinkProperties properties;
	
	private TransportClient client;
	
	private ThreadLocal<SimpleDateFormat> indexNameSuffixDateFormat;

	private final AtomicLong counter = new AtomicLong(0);
	
	@PostConstruct
	public void init() {
		initClient();

		initIndexNameSuffixDateFormat();
	}

	private void initClient() {
		TransportClient client = TransportClient.builder().settings(settings()).build();
		Set<String> clusterNodes = StringUtils.commaDelimitedListToSet(this.properties.getClusterNodes());
		for (String clusterNode : clusterNodes) {
			int index = clusterNode.indexOf(COLON);
			String hostname = clusterNode.substring(0, index);
			int port = Integer.parseInt(clusterNode.substring(index + 1));
			try {
				client.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
			}
			catch (UnknownHostException ex) {
				throw new RuntimeException(ex);
			}
		}
		this.client = client;
	}

	private void initIndexNameSuffixDateFormat() {
		String indexNameSuffixDateFormat = this.properties.getIndexNameSuffixDateFormat();
		if (indexNameSuffixDateFormat != null) {
			this.indexNameSuffixDateFormat = new ThreadLocal<SimpleDateFormat>() {
				@Override
				protected SimpleDateFormat initialValue() {
					return new SimpleDateFormat(indexNameSuffixDateFormat);
				}
			};
		}
	}

	private Settings settings() {
		return Settings.settingsBuilder()
				.put(CLUSTER_NAME, this.properties.getClusterName()).build();
	}

	@Override
	public void process(Object value) {
		Map<String, Object> map = (Map<String, Object>) value;
		Date timestamp = (Date) map.get(TIMESTAMP);
		String indexName = getIndexName(timestamp);
		this.client.prepareIndex(indexName, this.properties.getTypeName())
				.setSource(map).get();

		this.counter.incrementAndGet();
	}

	@Scheduled(cron = "* * * * * ?")
	public void printStatistics() {
		long count = this.counter.getAndSet(0);
		log.info("# of consumed logs per second in sink: " + count);
	}
	
	private String getIndexName(Date timestamp) {
		if (timestamp == null || this.indexNameSuffixDateFormat == null) {
			return this.properties.getIndexName();
		}
		return this.properties.getIndexName() + "-" +
				this.indexNameSuffixDateFormat.get().format(timestamp);
	}
	
}
