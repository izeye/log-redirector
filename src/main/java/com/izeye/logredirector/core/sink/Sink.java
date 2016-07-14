package com.izeye.logredirector.core.sink;

import com.izeye.logredirector.core.service.AbstractComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by izeye on 16. 7. 12..
 */
@Slf4j
public abstract class Sink extends AbstractComponent {

	private final AtomicLong counter = new AtomicLong(0);

	@Override
	public void process(Object value) {
		doProcess(value);
	}
	
	protected abstract void doProcess(Object value);
	
	protected void markProcessed(int count) {
		this.counter.addAndGet(count);
	}

	@Scheduled(cron = "* * * * * ?")
	public void printStatistics() {
		long count = this.counter.getAndSet(0);
		log.info("# of consumed logs per second in sink: {}", count);
	}
	
}
