package com.izeye.logredirector.core.sink;

import com.izeye.logredirector.core.service.AbstractComponent;
import com.izeye.logredirector.core.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by izeye on 16. 7. 12..
 */
@Slf4j
public abstract class Sink extends AbstractComponent {
	
	@Autowired
	private StatisticsService statisticsService;

	@Override
	public void process(Object value) {
		doProcess(value);
	}
	
	protected abstract void doProcess(Object value);
	
	protected void markProcessed(int count) {
		this.statisticsService.markSinkProcessed(count);
	}
	
}
