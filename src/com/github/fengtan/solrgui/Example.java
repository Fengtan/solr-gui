package com.github.fengtan.solrgui;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;

public class Example {

	public static void main(String[] args) {

		URL url;
		try {
			url = new URL("http", "localhost", 8983, "/solr/collection1");
			
			SolrGUIServer server = new SolrGUIServer(url);
			SolrDocumentList list = server.getAllDocuments();
			
			new SolrGUIDisplay(server.getName(), list);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
}
