package com.izeye.logredirector.core.service;

/**
 * Created by izeye on 16. 7. 12..
 */
public abstract class AbstractComponent implements Component {
	
	private Component next;

	@Override
	public void setNext(Component next) {
		this.next = next;
	}

	@Override
	public void passToNext(Object value) {
		this.next.process(value);
	}

	@Override
	public void process(Object value) {
		throw new UnsupportedOperationException("This component doesn't support process.");
	}
	
}
