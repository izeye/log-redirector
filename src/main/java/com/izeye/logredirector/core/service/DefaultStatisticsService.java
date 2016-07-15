package com.izeye.logredirector.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by izeye on 16. 7. 15..
 */
@Service
@Slf4j
public class DefaultStatisticsService implements StatisticsService {

	private final AtomicLong sourceCounter = new AtomicLong(0);
	private final AtomicLong sinkCounter = new AtomicLong(0);
	private final AtomicLong failureCounter = new AtomicLong(0);

	@Override
	public void markSourceProcessed(int count) {
		this.sourceCounter.addAndGet(count);
	}

	@Override
	public void markSinkProcessed(int count) {
		this.sinkCounter.addAndGet(count);
	}

	@Override
	public void markFailure() {
		this.failureCounter.incrementAndGet();
	}

	@Scheduled(cron = "* * * * * ?")
	public void printStatistics() {
		long sourceCount = this.sourceCounter.getAndSet(0);
		long sinkCount = this.sinkCounter.getAndSet(0);
		long failureCount = this.failureCounter.getAndSet(0);
		
		log.info("# of consumed logs per second in source: {}", sourceCount);
		log.info("# of consumed logs per second in sink: {}", sinkCount);
		log.info("# of failures: {}", failureCount);
	}
	
}
