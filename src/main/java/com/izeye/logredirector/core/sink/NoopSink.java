package com.izeye.logredirector.core.sink;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by izeye on 16. 7. 13..
 */
@Service
@Profile("noop-sink")
public class NoopSink extends Sink {

	@Override
	protected void doProcess(Object value) {
	}

}
