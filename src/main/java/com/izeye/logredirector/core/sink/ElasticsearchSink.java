package com.izeye.logredirector.core.sink;

import static com.izeye.logredirector.core.domain.FieldConstants.TIMESTAMP;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@EnableConfigurationProperties(ElasticsearchSinkProperties.class)
@Profile("elasticsearch-sink")
@Slf4j
public class ElasticsearchSink extends Sink {

	private static final String CLUSTER_NAME = "cluster.name";
	
	private static final char COLON = ':';
	
	@Autowired
	private ElasticsearchSinkProperties properties;
	
	private TransportClient client;
	
	private ThreadLocal<SimpleDateFormat> indexNameSuffixDateFormat;
	
	private BulkRequestBuilder bulkRequestBuilder;
	
	private ExecutorService executorService;
	
	@PostConstruct
	public void init() {
		initClient();

		this.bulkRequestBuilder = this.client.prepareBulk();

		initIndexNameSuffixDateFormat();

		this.executorService = Executors.newFixedThreadPool(this.properties.getThreadPoolSize());
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
	protected void doProcess(Object value) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
		Date timestamp = (Date) map.get(TIMESTAMP);
		String indexName = getIndexName(timestamp);
		IndexRequestBuilder indexRequestBuilder =
				this.client.prepareIndex(indexName, this.properties.getTypeName())
						.setSource(map);
		synchronized (this) {
			this.bulkRequestBuilder.add(indexRequestBuilder);
			if (this.bulkRequestBuilder.numberOfActions() == this.properties.getBatchSize()) {
				this.executorService.submit(new Worker(this.bulkRequestBuilder));
				this.bulkRequestBuilder = this.client.prepareBulk();
			}
		}
	}

	private String getIndexName(Date timestamp) {
		if (timestamp == null || this.indexNameSuffixDateFormat == null) {
			return this.properties.getIndexName();
		}
		return this.properties.getIndexName() + "-" +
				this.indexNameSuffixDateFormat.get().format(timestamp);
	}

	private class Worker implements Runnable {
		
		private final BulkRequestBuilder bulkRequestBuilder;
		
		public Worker(BulkRequestBuilder bulkRequestBuilder) {
			this.bulkRequestBuilder = bulkRequestBuilder;
		}

		@Override
		public void run() {
			int count = this.bulkRequestBuilder.numberOfActions();
			log.debug("{} bulk requests will be sent.", count);
			long startTimeInMillis = System.currentTimeMillis();
			this.bulkRequestBuilder.get();
			long elapsedTimeInMillis = System.currentTimeMillis() - startTimeInMillis;
			log.info("Elapsed time for {} bulk requests: {} ms", count, elapsedTimeInMillis);
			markProcessed(count);
		}
		
	}
	
}
