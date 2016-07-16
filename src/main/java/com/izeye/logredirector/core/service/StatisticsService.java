package com.izeye.logredirector.core.service;

/**
 * Created by izeye on 16. 7. 15..
 */
public interface StatisticsService {
	
	void markSourceProcessed(int count);
	
	void markSinkProcessed(int count);
	
	void markFailure(Object value);
	
}
