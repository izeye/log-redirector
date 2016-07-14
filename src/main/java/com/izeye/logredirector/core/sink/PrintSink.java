package com.izeye.logredirector.core.sink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by izeye on 16. 7. 13..
 */
@Service
@Profile("print-sink")
@Slf4j
public class PrintSink extends Sink {

	@Override
	protected void doProcess(Object value) {
		log.info("value: " + value);
		markProcessed(1);
	}

}
