package com.izeye.logredirector.core.service;

/**
 * Created by izeye on 16. 7. 12..
 */
public interface Component {
	
	void setNext(Component next);
	
	void passToNext(Object value);

	void process(Object value);
	
}
