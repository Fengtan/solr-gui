package com.github.fengtan.sophie;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.fengtan.sophie.dialogs.ConnectDialog;
import com.github.fengtan.sophie.dialogs.ExceptionDialog;
import com.github.fengtan.sophie.tabs.TabFolder;

public class Sophie {
	
	public static SolrClient client;
	public static Log log = LogFactory.getLog(Sophie.class);
	
	public static void main(String[] args) {
		// Create shell.
		Shell shell = new Shell();
		shell.setMaximized(true);
		shell.setLayout(new GridLayout());
		
		ConnectDialog dialog = new ConnectDialog(shell); 
		dialog.open();
		if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
			return;
		}
		client = dialog.getSolrClient();
		String connectionLabel = dialog.getConnectionLabel();
		
		// Initialize Solr client and UI.
		shell.setText("Sophie - "+connectionLabel);
		new TabFolder(shell, connectionLabel);
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
				ExceptionDialog.open(shell, e);
			}
		}
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				log.info("Unable to close Solr client", e);
			}	
		}
		display.dispose();
		shell.dispose();
	}
	
	// TODO test exotic Solr versions
	// TODO test modifying 2 documents and then commiting
	// TODO test win32/mac
	
	// TODO meta license (depends on swt, solrj, icons, other dependencies)
	// TODO meta wording server/index/collection/core - update class comments accordingly

	// TODO contrib convenience methods for replication handler (backup/restore/polling) https://issues.apache.org/jira/browse/SOLR-5640
	
	// TODO feat filters
	// TODO feat - show "(empty)" at the top
	// TODO feat - fire filter only when hit enter
	// TODO feat - selecting filter "foo (1)" generates "foo (1)" in textfield
	// TODO feat - support empty facets on free text fields (workaround: add "(empty) (1)" in free text
	// TODO feat new functionalities
	// TODO feat - allow to reload config on all cores
	// TODO feat - CoreAdmin split/mergeindexes https://wiki.apache.org/solr/CoreAdmin, CollectionAdminRequest, replicate to slave 
    // TODO feat - see what luke and solr native ui provide (dismax, spellcheck, debug, score, shard, elevation etc)/ pull from master
	// TODO feat - allow to create documents with new fields
	// TODO feat - allow to revert a specific document
	// TODO feat - allow not to use the default request handler
	
	// TODO clean look for unused/obsolete methods
	// TODO clean retest everything
	// TODO clean are all jars required ?
	// TODO clean document all methods
	// TODO clean indent (xml/java)

	// TODO doc screenshot 0.5
	// TODO doc about page + github.io
	// TODO doc measure memory footprint
	// TODO doc gif (licecap/silentcast/byzanz)
	// TODO doc how to delete favorites (edit ~/.sophie)
	// TODO doc cannot filter on unindexed fields
	// TODO doc if value is a date then calendar shows up when editing
	// TODO doc assume luke handler + select + admin/cores is available
    // TODO doc https://issues.apache.org/jira/browse/SOLR-20
    // TODO doc log4j.prop can set level to info not to trace http calls
	// TODO doc mvn generate:doc
	// TODO doc "(not stored)"
	// TODO doc unsortable fields
	// TODO doc sort by clicking on header
	// TODO doc backup stored on *server*
	// TODO dov mvn
	// TODO doc import into eclipse
	// TODO doc typing 'Suppr' deletes a row.
	// TODO doc "virtual i.e. remote documents are fetched as they are displayed for best perf"
	// TODO doc similar luke + javasoze/clue + solarium + projectblacklight.org
    // TODO doc publish javadoc
	// TODO doc "if need to use http auth, then http://user:pass@localhost:8983/solr/collection1"
	// TODO doc "can configure DEFAULT_DOCUMENTS_PAGE_SIZE + DEFAULT_DOCUMENTS_FACETS_LIMIT" (need to update .sophie manually)
	// TODO doc support zk/cloud
	// TODO doc mvn -P win32/macosx/linux (latter by default)
	
	// TODO obs trayitem - not supported by ubuntu https://bugs.eclipse.org/bugs/show_bug.cgi?id=410217
	// TODO obs allow to select multiple values for each filter
	// TODO obs what if field contains value "(empty)" ?
	// TODO obs sort by field name (fields+documents)
    // TODO obs .deb package
    // TODO - install jar into /opt or /usr/local
    // TODO - export SOPHIE_HOME=/usr/local/sophie-x.y.z
    // TODO - export PATH=$PATH:$SOPHIE_HOME/bin
    // TODO - expose log4j.prop in /etc ; slf4j
    // TODO - logs in /var/log
	// TODO obs Dialogs -> use validators to make sure values are not empty
}
