package com.github.fengtan.solrgui.beans;

import java.util.Random;

import org.apache.solr.common.SolrDocument;

public class SolrGUIDocument {

	private SolrGUIStatus status = SolrGUIStatus.NONE;
	private SolrDocument document;
	
	public SolrGUIDocument(SolrDocument document) {
		this.document = document;
	}
	
	public SolrGUIDocument(String[] fields) {
		document = new SolrDocument();
		for (String field:fields) {
			document.setField(field, null);
		}
		// TODO can we assume the id field always exists ?
		// TODO use something else than random
		document.setField("id", new Random().nextInt(1000));
	}
	
	public SolrGUIDocument clone() {
		return new SolrGUIDocument(document);
	}
	
	public SolrGUIStatus getStatus() {
		return status;
	}
	
	public void setStatus(SolrGUIStatus status) {
		this.status = status;
	}
	
	public SolrDocument getDocument() {
		return document;
	}
	
	public void setField(String name, Object value) {
		document.setField(name, value);
	}
	
	public Object getFieldValue(String name) {
		return document.getFieldValue(name);
	}
	
}
