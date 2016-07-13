package com.izeye.logredirector.core.filter;

import static com.izeye.logredirector.core.domain.FieldConstants.TIMESTAMP;
import static com.izeye.logredirector.core.domain.FieldConstants.TIMESTAMP_IN_MILLIS;
import static com.izeye.logredirector.core.domain.FieldConstants.TIMESTAMP_IN_SECONDS;

import com.izeye.logredirector.core.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by izeye on 16. 7. 12..
 */
@Service
@Order(1)
@Slf4j
public class RegexTransformFilter extends Filter {
	
	@Value("${log-redirector.filters.regex-transform-filter.regex}")
	private String regex;
	
	private List<String> fieldNames;
	private Pattern pattern;
	
	@PostConstruct
	public void init() {
		this.fieldNames = RegexUtils.getGroupNames(this.regex);
		log.info("Field names: {}", fieldNames);
		
		this.pattern = Pattern.compile(this.regex);
	}
	
	@Override
	protected Object doProcess(Object value) {
		Map<String, Object> map = new HashMap<>();
		Matcher matcher = this.pattern.matcher((String) value);
		matcher.find();
		for (String fieldName : this.fieldNames) {
			String fieldValue = matcher.group(fieldName);
			map.put(fieldName, fieldValue);
			Date timestamp = null;
			switch (fieldName) {
				case TIMESTAMP_IN_SECONDS:
					long timestampInSeconds = Long.parseLong(fieldValue);
					timestamp = new Date(TimeUnit.SECONDS.toMillis(timestampInSeconds));
					break;
				
				case TIMESTAMP_IN_MILLIS:
					timestamp = new Date(Long.parseLong(fieldValue));
					break;
				
				default:
					break;
			}
			if (timestamp != null) {
				map.put(TIMESTAMP, timestamp);
			}
		}
		return map;
	}

}
