package com.github.fengtan.solrgui;

import java.net.URL;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class SolrGUIServer {
	
	private SolrServer server;
	private URL url;
	
	public SolrGUIServer(URL url) {
		this.url = url;
		this.server = new HttpSolrServer(url.toExternalForm());
	}
	
	public String getName() {
		return url.toExternalForm();
	}
	
	public SolrDocumentList getAllDocuments() {
		SolrQuery query = new SolrQuery();
		query.set("q", "*:*");
		return getDocumentList(query);
	}
	
	public SolrDocumentList getDocumentList(SolrQuery query) {
		QueryResponse response;
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			return new SolrDocumentList();
		}
		return response.getResults();
	}
	
}
