package com.github.fengtan.solrgui.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.LukeResponse.FieldInfo;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SolrGUITable { // TODO extend Composite ?

	// Fetch 50 documents at a time. TODO make this configurable ?
	private static final int PAGE_SIZE = 50;
	
	private Map<Integer, SolrDocumentList> pages;
	private List<FieldInfo> fields;
	private Map<String, FacetField> facets;
	private Map<String, String> filters = new HashMap<String, String>();

	private Table table;
	private SolrServer server;

	public SolrGUITable(Composite parent, String url) {
		this.server = new HttpSolrServer(url);
		this.fields = getRemoteFields(); // TODO what if new fields get created ? refresh ?
		this.facets = getRemoteFacets();
		this.table = createTable(parent);
		// Initialize cache + row count
		clear();
	}
	
	/**
	 * Create the Table
	 */
	private Table createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.VIRTUAL; // TODO HIDE_SELECTION ?

		final Table table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 5; // 5 according to number of buttons. TODO needed ?
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		/* TODO implement
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				// TODO document in readme: typing 'Suppr' deletes a row.
				if (event.keyCode == SWT.DEL) {
					Table table = (Table) event.getSource();
					TableItem item = table.getSelection()[0]; // TODO what if [0] does not exist.
					SolrDocument document = (SolrDocument) item.getData();
					server.removeDocument(document);
				}
			}
		});
		*/

		// Initialize item count to 1 so we can populate the first row with filters.
		table.setItemCount(1);
		table.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
	            TableItem item = (TableItem) event.item;
	            int rowIndex = table.indexOf(item);
	            // The first line is populated by filters.
	            if (rowIndex == 0) {
	            	// TODO might need to populate if existing filters
	            	return;
	            }
	            // Use rowIndex - 1 since the first line is used for filters.
	            for(int i=0; i<fields.size(); i++) {
	            	Object value = getDocumentValue(rowIndex - 1, fields.get(i));
	            	item.setText(i, Objects.toString(value, ""));
	            }
	            // TODO use item.setText(String[] values) ?
	            // TODO store status insert/update/delete using item.setData() ? 
			}
		});
		
		// Add columns
		for (FieldInfo field:fields) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(field.getName());
			column.pack(); // TODO needed ? might be worth to setLayout() to get rid of this
		}
		
		// Add filters.
		TableItem[] items = table.getItems(); // TODO do we need to load all items ?
		TableEditor editor = new TableEditor(table);
		for(int i=0; i<fields.size(); i++) {
			final CCombo combo = new CCombo(table, SWT.NONE);
			combo.add("");
			// TODO check if map contains field ?
			// TODO no need to use facets for tm_body for instance
			FacetField facet = facets.get(fields.get(i).getName());
			for(Count count:facet.getValues()) {
				combo.add(count.getName()); // TODO use count.getCount() too ?
			}
			combo.setData("field", facet.getName());
			// Filter results when user selects a facet value.
			combo.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					String filterName = combo.getData("field").toString();
					String filterValue = combo.getText();
					if (StringUtils.isEmpty(filterValue)) {
						filters.remove(filterName);
					} else {
						filters.put(filterName, filterValue);
					}
					clear();
				}
			});
		    editor.grabHorizontal = true;
		    editor.setEditor(combo, items[0], i);
		    editor = new TableEditor(table);
		}
		
		// TODO re-use editor instead of SorlGUICellModifier ?
		
		return table;
	}
	
	public void dispose() {
		server.shutdown(); // TODO move server instantiation/shutdown into SolrGUITabItem ?
	}
	
	private List<FieldInfo> getRemoteFields() {
		LukeRequest request = new LukeRequest();
		try {
			LukeResponse response = request.process(server);
			return new ArrayList<FieldInfo>(response.getFieldInfo().values());
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<FieldInfo>(); // TODO Collections.EMPTY_LIST
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<FieldInfo>(); // TODO Collections.EMPTY_LIST
		}
		/* TODO provide option to use this in case Luke handler is not available? requires at least 1 document in the server
		Collection<String> fields = getAllDocuments().get(0).getFieldNames();
		return fields.toArray(new String[fields.size()]);
		*/
	}

	private int getRemoteCount() {
		SolrQuery query = getBaseQuery(0, 0);
		try {
			// Solr returns a long, SWT expects an int.
			long count = server.query(query).getResults().getNumFound();
			return Integer.parseInt(String.valueOf(count));
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/*
	 * Map facet name => facet field
	 */
	private Map<String, FacetField> getRemoteFacets() {
		SolrQuery query = getBaseQuery(0, 0);
		query.setFacet(true);
		query.setFacetSort("index");
		query.setFacetLimit(-1); // TODO or set a limit ? no limit could be bad for perf
		for(FieldInfo field:fields) {
			// TODO we don't want facets on fields with too many values
			query.addFacetField(field.getName());	
		}
		Map<String, FacetField> facets = new HashMap<String, FacetField>();
		try {
			for(FacetField facet:server.query(query).getFacetFields()) {
				facets.put(facet.getName(), facet);
			}
		} catch(SolrServerException e) {
			e.printStackTrace();
		}
		return facets;
	}
	
	/**
	 * Not null-safe
	 */
	private Object getDocumentValue(int rowIndex, FieldInfo field) {
		int page = rowIndex / PAGE_SIZE;
		// If page has not be fetched yet, then fetch it.
		if (!pages.containsKey(page)) {
			SolrQuery query = getBaseQuery(page * PAGE_SIZE, PAGE_SIZE);
			try {
				pages.put(page, server.query(query).getResults());	
			} catch(SolrServerException e) {
				// TODO handle exception
				e.printStackTrace();
			}
		}
		return pages.get(page).get(rowIndex % PAGE_SIZE).getFieldValue(field.getName());
	}
	
	private SolrQuery getBaseQuery(int start, int rows) {
		SolrQuery query = new SolrQuery("*:*");
		query.setStart(start);
		query.setRows(rows);
		// Add filters.
		for (Entry<String, String> filter:filters.entrySet()) {
			query.addFilterQuery(filter.getKey()+":"+filter.getValue());
		}
		return query;
	}
	
	/*
	 * Re-populate table with remote data.
	 */
	private void clear() {
		// TODO re-populate columns/filters ?
		pages = new HashMap<Integer, SolrDocumentList>();
		table.setItemCount(1 + getRemoteCount()); // First row is for filters, the rest is for documents.
		table.clearAll();
	}
	
	// TODO allow to filter value on empty value (e.g. value not set)
	
}
