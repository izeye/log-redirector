package com.izeye.logredirector.core.service;

import com.izeye.logredirector.core.filter.Filter;
import com.izeye.logredirector.core.sink.Sink;
import com.izeye.logredirector.core.source.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@Slf4j
public class DefaultLogRedirector implements LogRedirector {
	
	@Autowired
	private Source source;
	
	@Autowired
	private List<Filter> filters;
	
	@Autowired
	private Sink sink;
	
	@Override
	public void run() {
		Component previousComponent = this.source;
		log.info("Active filters: {}", this.filters);
		if (!this.filters.isEmpty()) {
			for (int i = 0; i < this.filters.size(); i++) {
				Filter filter = this.filters.get(i);
				previousComponent.setNext(filter);
				previousComponent = filter;
			}
		}
		previousComponent.setNext(this.sink);
		
		this.source.run();
	}
	
}
