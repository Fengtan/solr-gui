package com.github.fengtan.sophie.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.LukeResponse.FieldInfo;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.apache.solr.common.util.NamedList;

import com.github.fengtan.sophie.Sophie;

public class SolrUtils {

	/**
	 * Helper to get remote fields
	 * TODO what if new fields get created ? refresh ? should update tables accordingly when refresh
	 */
	public static List<FieldInfo> getRemoteFields() throws SophieException {
		LukeRequest request = new LukeRequest();
		try {
			LukeResponse response = request.process(Sophie.client);
			return new ArrayList<FieldInfo>(response.getFieldInfo().values());
		} catch (SolrServerException|IOException|SolrException e) {
			throw new SophieException("Unable to fetch list of Solr fields", e);
		}
	}
	
	/**
	 * Get fields defined in the schema (regular + dynamic).
	 * TODO contrib LukeResponse.getDynamicFieldInfo() - similar to LukeResponse.getFieldInfo() - see LukeResponse.setResponse()
	 * @return Map<field name, field info>
	 */
	public static Map<String, FieldInfo> getRemoteSchemaFields() throws SophieException {
		Map<String, FieldInfo> fields = new HashMap<String, FieldInfo>();
		LukeRequest request = new LukeRequest();
		request.setShowSchema(true);
		try {
			LukeResponse response = request.process(Sophie.client);
			// Get regular fields.
			fields.putAll(response.getFieldInfo());
			// Get dynamic fields.
			NamedList<Object> schema = (NamedList<Object>) response.getResponse().get("schema");
			if (schema != null) {
				NamedList<Object> dynamicFields = (NamedList<Object>) schema.get("dynamicFields");
				if (dynamicFields != null) {
					for (Map.Entry<String, Object> dynamicField:dynamicFields) {
						FieldInfo fieldInfo = new FieldInfo(dynamicField.getKey());
						fieldInfo.read((NamedList<Object>) dynamicField.getValue());
						fields.put(dynamicField.getKey(), fieldInfo);
					}
				}
			}
		} catch (SolrServerException|IOException|SolrException e) {
			throw new SophieException("Unable to fetch list of Solr fields", e);
		}
		return fields;
	}
	
	// TODO could merge with getRemoteFields() to make less queries.
	// TODO could use admin/luke?show=schema
	public static String getRemoteUniqueField() throws SophieException {
		SchemaRequest.UniqueKey request = new SchemaRequest.UniqueKey();
		try {
			return request.process(Sophie.client).getUniqueKey();
		} catch (SolrServerException|IOException|SolrException e) {
			throw new SophieException("Unable to fetch name of unique field", e);
		}
	}
	
	// TODO cache result until new connection or refresh ?
	/**
	 * @return Map <core name, attributes>
	 */
	public static Map<String, NamedList<Object>> getCores() throws SophieException {
		CoreAdminRequest request = new CoreAdminRequest();
		request.setAction(CoreAdminAction.STATUS);
		try {
			CoreAdminResponse response = request.process(Sophie.client);
			return response.getCoreStatus().asMap(-1);
		} catch (SolrServerException|IOException|SolrException e) {
			throw new SophieException("Unable to fetch list of Solr cores", e);
		}
	}
	
}
