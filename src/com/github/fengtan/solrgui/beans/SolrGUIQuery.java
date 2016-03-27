package com.github.fengtan.solrgui.beans;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import com.github.fengtan.solrgui.filters.SolrGUIFilter;

public class SolrGUIQuery extends SolrQuery {
	
	private static final long serialVersionUID = 1L;

	private String q = "*:*"; // TODO allow user to alter ?
	private int rows = 500; // TODO load 500 rows by default + add button "load more" if necessary
	
	public static SolrGUIQuery ALL_DOCUMENTS = new SolrGUIQuery();
	
	private SolrGUIQuery() {
		super(); // TODO needed ?
		setQuery(q);
		setRows(rows);
	}

	public SolrGUIQuery(Collection<SolrGUIFilter> filters, Collection<String> fields) {
		this();
		for (SolrGUIFilter filter:filters) {
			if (StringUtils.isNotEmpty(filter.getField()) && StringUtils.isNotEmpty(filter.getValue())) {
				addFilterQuery(filter.getField() + ":" + filter.getValue());
			}
		}
		for (String field:fields) {
			addField(field);
		}
	}
	
}
