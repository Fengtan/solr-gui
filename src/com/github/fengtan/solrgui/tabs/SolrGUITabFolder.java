package com.github.fengtan.solrgui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.fengtan.solrgui.config.SolrGUIConfig;
import com.github.fengtan.solrgui.dialogs.SolrGUIAddServerDialog;
import com.github.fengtan.solrgui.solr.SolrGUIServer;

public class SolrGUITabFolder extends CTabFolder {

	private SolrGUIAddServerDialog dialog;
	
	public SolrGUITabFolder(Shell shell) {
		// Create the tabs.
		super(shell, SWT.TOP | SWT.CLOSE | SWT.BORDER);
		this.dialog = new SolrGUIAddServerDialog(shell, this);

		// Configure tab folder.
		setBorderVisible(true);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		setSimple(false);
		setTabHeight(25);
		
		// Create the 'Add server button
		Button button = new Button(this, SWT.PUSH | SWT.CENTER);
		button.setText("Add &new server");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				dialog.open();
			}
	    });
		setTopRight(button);

		// Set up a gradient background for the selected tab
		Display display = shell.getDisplay();
		Color titleForeColor = display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
		Color titleBackColor1 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		Color titleBackColor2 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		setSelectionForeground(titleForeColor);
		setSelectionBackground(
		    new Color[] {titleBackColor1, titleBackColor2},
			new int[] {100},
			true
		);
		
		// Initialize tabs from config file.
		for (SolrGUIServer server: SolrGUIConfig.getServers()) {
			addTabItem(server);
		}
		
		// Remove server from config file if user closed the tab.
		// If this is the last item remaining, notify listeners.
		/* TODO
		addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				SolrGUIConfig.removeServer(((SolrGUITabItem) event.item).getServer()); // TODO is there a native way to persist open tabs ?
			}
		});
		*/
	}
	
	public void addTabItem(SolrGUIServer server) {
		new SolrGUITabItem(this, server);
	}

}
