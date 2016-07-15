package com.izeye.logredirector.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@Order(2)
@Profile("print-filter")
@Slf4j
public class PrintFilter extends Filter {
	
	@Override
	public void process(Object value) {
		log.info("value: " + value);
		passToNext(value);
	}

}
