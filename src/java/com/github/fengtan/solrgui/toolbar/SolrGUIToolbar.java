package com.github.fengtan.solrgui.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.github.fengtan.solrgui.SolrGUI;
import com.github.fengtan.solrgui.tabs.ISolrGUITabFolderListener;
import com.github.fengtan.solrgui.tabs.SolrGUITabItem;

public class SolrGUIToolbar implements ISolrGUITabFolderListener {

    private Image imgAdd;
    private Image imgDelete;
    private Image imgClone;
    private Image imgRefresh;
    private Image imgCommit;
    private Image imgClear;
    
    private ToolItem itemAdd;
    private ToolItem itemDelete;
    private ToolItem itemClone;
    private ToolItem itemRefresh;
    private ToolItem itemCommit;
    private ToolItem itemClear;
    
    private SolrGUI solrGUI;
    
    public SolrGUIToolbar(Shell shell, SolrGUI solrGUI) {
    	initToolbar(shell);
    	this.solrGUI = solrGUI;
    }
    
    protected void initToolbar(final Shell shell) {
        Display display = shell.getDisplay();
        ClassLoader loader = getClass().getClassLoader();
        imgAdd = new Image(display, loader.getResourceAsStream("toolbar/add.png"));
        imgDelete = new Image(display, loader.getResourceAsStream("toolbar/delete.png"));
        imgClone = new Image(display, loader.getResourceAsStream("toolbar/clone.png"));
        imgRefresh = new Image(display, loader.getResourceAsStream("toolbar/refresh.png"));
        imgCommit = new Image(display, loader.getResourceAsStream("toolbar/commit.png")); // TODO find a better icon ?
        imgClear = new Image(display, loader.getResourceAsStream("toolbar/clear.png"));

        ToolBar toolBar = new ToolBar(shell, SWT.BORDER);

        // TODO allow to create documents with new fields
        itemAdd = new ToolItem(toolBar, SWT.PUSH);
        itemAdd.setEnabled(false);
        itemAdd.setImage(imgAdd);
        itemAdd.setToolTipText("Add new document");
        itemAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUITabItem tabItem = (SolrGUITabItem) solrGUI.getTabFolder().getSelection();
				tabItem.getTable().addEmptyDocument();
			}
		});

        itemDelete = new ToolItem(toolBar, SWT.PUSH);
        itemDelete.setEnabled(false);
        itemDelete.setImage(imgDelete);
        itemDelete.setToolTipText("Delete document");
        itemDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUITabItem tabItem = (SolrGUITabItem) solrGUI.getTabFolder().getSelection();
				tabItem.getTable().deleteSelectedDocument();
			}
		});

        itemClone = new ToolItem(toolBar, SWT.PUSH);
        itemClone.setEnabled(false);
        itemClone.setImage(imgClone);
        itemClone.setToolTipText("Clone document");
        itemClone.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUITabItem tabItem = (SolrGUITabItem) solrGUI.getTabFolder().getSelection();
				tabItem.getTable().cloneSelectedDocument();
			}
		});

        new ToolItem(toolBar, SWT.SEPARATOR);

        itemRefresh = new ToolItem(toolBar, SWT.PUSH);
        itemRefresh.setEnabled(false);
        itemRefresh.setImage(imgRefresh);
        itemRefresh.setToolTipText("Refresh from server: this will wipe out local modifications");
        itemRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUITabItem tabItem = (SolrGUITabItem) solrGUI.getTabFolder().getSelection();
				tabItem.getTable().refresh();
			}
		});
        
        itemCommit = new ToolItem(toolBar, SWT.PUSH);
        itemCommit.setEnabled(false);
        itemCommit.setImage(imgCommit);
        itemCommit.setToolTipText("Commit local modifications to server");
        itemCommit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUITabItem tabItem = (SolrGUITabItem) solrGUI.getTabFolder().getSelection();
				tabItem.getTable().commit();
			}
		});
        
        itemClear = new ToolItem(toolBar, SWT.PUSH);
        itemClear.setEnabled(false);
        itemClear.setImage(imgClear);
        itemClear.setToolTipText("Clear the index");
        itemClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
		        MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		        messageBox.setText("Clear the index");
		        messageBox.setMessage("Do you really want to clear the index? This will wipe out all documents on the server.");
		        int response = messageBox.open();
		        if (response == SWT.YES) {
		        	SolrGUITabItem tabItem = (SolrGUITabItem) solrGUI.getTabFolder().getSelection();
					tabItem.getTable().clear();
		        }
			}
		});
        
        toolBar.pack();
    }
    
    @Override
    public void finalize() {
        imgAdd.dispose();
        imgDelete.dispose();
        imgClone.dispose();
        imgRefresh.dispose();
        imgCommit.dispose();
        imgClear.dispose();
    }

	// TODO a row should be selected for itemAdd/delete/clone to be enabled
	@Override
	public void tabItemsCountModified(int newCount) {
		boolean enabled = (newCount != 0);
		itemAdd.setEnabled(enabled);
		itemDelete.setEnabled(enabled);
		itemClone.setEnabled(enabled);
		itemRefresh.setEnabled(enabled);
		itemCommit.setEnabled(enabled);
		itemClear.setEnabled(enabled);	
	}

}
