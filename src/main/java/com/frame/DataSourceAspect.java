package com.frame;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {
	private Logger logger = Logger.getLogger(DataSourceAspect.class);		
	
	
	private static final String MASTER = "write";
	private static final String SLAVE = "read";
	private static final String REGEXP = "^(get|query|select|find|load|search)[a-z|0-9]*";

	@Before("within(com.service..*)")
	public void before(JoinPoint point) {
		String method = point.getSignature().getName().toLowerCase();
		try {
			if (Pattern.matches(REGEXP, method)) {
				logger.debug(method+" SLAVE");
				HandleDataSource.putDataSource(SLAVE);
			} else {
				logger.debug(method+" MASTER");
				HandleDataSource.putDataSource(MASTER);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}