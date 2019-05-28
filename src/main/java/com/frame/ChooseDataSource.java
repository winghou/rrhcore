package com.frame;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 采用AbstractRoutingDataSource解决多数据源
 * 
 * @author bill
 *
 */
public class ChooseDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		return HandleDataSource.getDataSource();
	}
}
