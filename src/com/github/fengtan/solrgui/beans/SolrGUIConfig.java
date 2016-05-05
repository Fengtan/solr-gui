package com.github.fengtan.solrgui.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class SolrGUIConfig {

	// TODO make sure hidden file works on windows
	// TODO properties file does not support name duplicates -> prevent user from creating 2 servers with the same name
	private static final String filename = ".solrgui";
	private static final String filepath = System.getProperty("user.home") + File.separator + filename;
	
	private static Properties loadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(filepath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}
	
	private static void storeProperties(Properties properties) {
		try {
			properties.store(new FileOutputStream(filepath), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<SolrGUIServer> getServers() {
		List<SolrGUIServer> servers = new ArrayList<SolrGUIServer>();
		for (Entry<Object, Object> entry:loadProperties().entrySet()) {
			String url = entry.getKey().toString();
			// TODO validate URL is valid ?
			servers.add(new SolrGUIServer(url));
		}
		return servers;
	}

	public static void addServer(SolrGUIServer server) {
		Properties properties = loadProperties();
		// We could store additional data if needed - for now we store an empty string.
		properties.put(server.getURL().toString(), "");
		storeProperties(properties);
	}
	
	public static void removeServer(SolrGUIServer server) {
		Properties properties = loadProperties();
		properties.remove(server.getURL().toString());
		storeProperties(properties);
	} 
	
}
