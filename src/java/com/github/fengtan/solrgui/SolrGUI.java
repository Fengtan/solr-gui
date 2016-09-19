package com.github.fengtan.solrgui;

import java.io.IOException;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.github.fengtan.solrgui.tabs.TabFolder;

public class SolrGUI {
	
	public static HttpSolrClient client; // TODO move into SolrGUITable ? so we do not store both this.server and this.url
	public static TabFolder tabFolder;
	public static Shell shell; // TODO keeping shell as attribute (+public static) is ugly

	// TODO load url from .properties
	public static void main(String[] args) { // TODO convert into static { code } ?
		if (args.length != 1) {
			System.err.println("Usage: java -jar SolrGUI.jar <solr-url>");
			System.exit(1);
		}
		String url = args[0];
		
		// Create shell.
		shell = new Shell();
		shell.setMaximized(true);
		shell.setLayout(new GridLayout());
		shell.setText("Solr GUI - "+url);
		
		// Prompt user for url.
		client = new HttpSolrClient(url);
		tabFolder = new TabFolder(shell, url);
		// TODO what if server works and then goes down
		
		// Make the shell display its content.
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();	
				}	
			} catch (Exception e) {
				showException(e);
			}
		}
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		display.dispose();
		shell.dispose();
	}
	
	public static void showException(Exception e) {
		// TODO use ErrorDialog instead of MessageBox - provides button "see details" to display exception stack trace
    	e.printStackTrace(); // TODO log stack trace slf4j
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
	    box.setText("An error happened");
	    box.setMessage(e.getMessage());
	    box.open();
	}
	
	// TODO test exotic Solr versions
	// TODO test elasticsearch
	// TODO test modifying 2 documents and then commiting
	
	// TODO meta screenshot 0.5
	// TODO meta about page + github.io
	// TODO meta document all methods
	// TODO meta rename solrgui
	// TODO meta measure memory footprint
	// TODO meta are all jars required ?
	// TODO meta license (depends on swt, solrj, icons, other dependencies)
	// TODO meta wording server/index/collection/core
	// TODO meta travis
    // TODO meta .deb package
    // TODO - install jar into /opt or /usr/local
    // TODO - export SOLRGUI_HOME=/usr/local/solr-gui-x.y.z
    // TODO - export PATH=$PATH:$SOLRGUI_HOME/bin
    // TODO - expose log4j.prop in /etc
    // TODO - logs in /var/log
	// TODO meta solr-gui / sophie
	// TODO meta contribute convenience methods for replication handler (backup/restore/polling) https://issues.apache.org/jira/browse/SOLR-5640
	
	// TODO feat CoreAdmin split/mergeindexes https://wiki.apache.org/solr/CoreAdmin, CollectionAdminRequest, replicate to slave / pull from master
	// TODO feat sort by field name (fields+documents)
	// TODO feat all constants overridable using .properties file or -Dpage.size=20 + provide a default .properties
	// TODO feat allow to create documents with new fields
	// TODO feat show "(empty)" at the top
	// TODO feat fire filter only when hit enter
	// TODO feat allow to revert a specific document
	// TODO feat allow not to use the default request handler
	// TODO feat selecting filter "foo (1)" genates "foo (1)" in textfield
	// TODO feat support CloudSolrServer
	// TODO feat adapt edit dialog to support multi fields
	// TODO feat allow to select multiple values for each filter
	// TODO feat support empty facets on free text fields (workaround: add "(empty) (1)" in free text 
	// TODO feat what if field contains value "(empty)" ?
	// TODO feat allow to reload config on all cores
    // TODO feat see what luke and solr native ui provide (dismax, spellcheck, debug, score, shard, elevation etc)
	// TODO feat look for unused/obsolete methods
	// TODO feat "favorites/recently opened servers"
    // TODO feat use InputDialog instead of custom dialogs
	// TODO feat allow user to edit multi-value fields (not just add/remove)
	
	// TODO doc cannot filter on unindexed fields
	// TODO doc if value is a date then calendar shows up when editing
	// TODO doc assume luke handler + select + admin/cores is available
    // TODO doc https://issues.apache.org/jira/browse/SOLR-20
    // TODO doc log4j.prop
	// TODO doc "(not stored)"
	// TODO doc unsortable fields
	// TODO doc sort by clicking on header
	// TODO doc backup stored on *server*
	// TODO dov mvn
	// TODO doc import into eclipse
	// TODO doc typing 'Suppr' deletes a row.
	// TODO doc "virtual i.e. remote documents are fetched as they are displayed for best perf"
	// TODO doc luke + javasoze/clue + solarium + projectblacklight.org
    // TODO doc publish javadoc
	// TODO doc CLI args[0]

	// TODO obs trayitem - not supported by ubuntu https://bugs.eclipse.org/bugs/show_bug.cgi?id=410217
}
