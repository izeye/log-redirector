package com.izeye.logredirector.core.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by izeye on 16. 7. 15..
 */
@Data
@ConfigurationProperties(prefix = "log-redirector.filters.regex-transform-filter")
public class RegexTransformFilterProperties {
	
	private String regex;
	private int threadPoolSize;
	
}
