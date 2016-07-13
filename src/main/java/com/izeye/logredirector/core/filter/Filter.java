package com.izeye.logredirector.core.filter;

import com.izeye.logredirector.core.service.AbstractComponent;

/**
 * Created by izeye on 16. 7. 12..
 */
public abstract class Filter extends AbstractComponent {

	@Override
	public void process(Object value) {
		Object processed = doProcess(value);
		passToNext(processed);
	}

	protected abstract Object doProcess(Object value);
	
}
