package com.github.fengtan.solrgui.toolbar;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
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
import com.github.fengtan.solrgui.dialogs.BackupIndexDialog;
import com.github.fengtan.solrgui.dialogs.RestoreIndexDialog;

public class SolrGUIToolbar {

    private Image imgAdd;
    private Image imgDelete;
    private Image imgClone;
    private Image imgExport;
    private Image imgRefresh;
    private Image imgUpload;
    private Image imgClear;
    private Image imgCommit;
    private Image imgOptimize;
    private Image imgBackup;
    private Image imgRestore;
    
    private ToolItem itemAdd;
    private ToolItem itemDelete;
    private ToolItem itemClone;
    private ToolItem itemExport;
    private ToolItem itemRefresh;
    private ToolItem itemUpload;
    private ToolItem itemClear;
    private ToolItem itemCommit;
    private ToolItem itemOptimize;
    private ToolItem itemBackup;
    private ToolItem itemRestore;
    
    public SolrGUIToolbar(Shell shell) {
    	initToolbar(shell);
    }
    
    protected void initToolbar(final Shell shell) {
        Display display = shell.getDisplay();
        ClassLoader loader = getClass().getClassLoader();
        imgAdd = new Image(display, loader.getResourceAsStream("toolbar/add.png"));
        imgDelete = new Image(display, loader.getResourceAsStream("toolbar/delete.png"));
        imgClone = new Image(display, loader.getResourceAsStream("toolbar/clone.png"));
        imgExport = new Image(display, loader.getResourceAsStream("toolbar/export.png")); // TODO find better icon ?
        imgRefresh = new Image(display, loader.getResourceAsStream("toolbar/refresh.png"));
        imgUpload = new Image(display, loader.getResourceAsStream("toolbar/upload.png")); // TODO find a better icon ?
        imgClear = new Image(display, loader.getResourceAsStream("toolbar/clear.png"));
        imgCommit = new Image(display, loader.getResourceAsStream("toolbar/commit.png"));
        imgOptimize = new Image(display, loader.getResourceAsStream("toolbar/optimize.png"));
        imgBackup = new Image(display, loader.getResourceAsStream("toolbar/backup.png")); // TODO find better icon
        imgRestore = new Image(display, loader.getResourceAsStream("toolbar/restore.png")); // TODO find better icon

        ToolBar toolBar = new ToolBar(shell, SWT.BORDER);

        // TODO allow to create documents with new fields
        itemAdd = new ToolItem(toolBar, SWT.PUSH);
        itemAdd.setImage(imgAdd);
        itemAdd.setToolTipText("Add new document");
        itemAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().addEmptyDocument(); // TODO is table the right place to put upload(), clear(), etc ?
			}
		});

        itemDelete = new ToolItem(toolBar, SWT.PUSH);
        itemDelete.setImage(imgDelete);
        itemDelete.setToolTipText("Delete document");
        itemDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().deleteSelectedDocument();
			}
		});

        itemClone = new ToolItem(toolBar, SWT.PUSH);
        itemClone.setImage(imgClone);
        itemClone.setToolTipText("Clone document");
        itemClone.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().cloneSelectedDocument();
			}
		});

        itemExport = new ToolItem(toolBar, SWT.PUSH);
        itemExport.setImage(imgExport);
        itemExport.setToolTipText("Export as CSV file");
        itemExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().export();
			}
		});
        
        new ToolItem(toolBar, SWT.SEPARATOR);

        itemRefresh = new ToolItem(toolBar, SWT.PUSH);
        itemRefresh.setImage(imgRefresh);
        itemRefresh.setToolTipText("Refresh from server: this will wipe out local modifications");
        itemRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().refresh();
			}
		});
        
        itemUpload = new ToolItem(toolBar, SWT.PUSH);
        itemUpload.setImage(imgUpload);
        itemUpload.setToolTipText("Upload local modifications to server");
        itemUpload.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().upload();
			}
		});
        
        new ToolItem(toolBar, SWT.SEPARATOR);
        
        itemClear = new ToolItem(toolBar, SWT.PUSH);
        itemClear.setImage(imgClear);
        itemClear.setToolTipText("Clear index");
        itemClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
		        MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		        messageBox.setText("Clear index");
		        messageBox.setMessage("Do you really want to clear the index? This will wipe out all documents on the server.");
		        int response = messageBox.open();
		        if (response == SWT.YES) {
		    		try {
		    			SolrGUI.client.deleteByQuery("*:*");
		    			SolrGUI.client.commit();
		    		} catch (SolrServerException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		SolrGUI.tabFolder.getDocumentsTabItem().getTable().refresh();
		        }
			}
		});

        itemCommit = new ToolItem(toolBar, SWT.PUSH);
        itemCommit.setImage(imgCommit);
        itemCommit.setToolTipText("Commit index");
        itemCommit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					SolrGUI.client.commit();
				} catch (SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SolrGUI.tabFolder.getDocumentsTabItem().getTable().refresh();
			}
		});
        
        itemOptimize = new ToolItem(toolBar, SWT.PUSH);
        itemOptimize.setImage(imgOptimize);
        itemOptimize.setToolTipText("Optimize index");
        itemOptimize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
		        MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		        messageBox.setText("Optimize index");
		        messageBox.setMessage("Do you really want to optimize the index? If the index is highly segmented, this may take several hours and will slow down requests.");
		        int response = messageBox.open();
		        if (response == SWT.YES) {
		    		try {
		    			SolrGUI.client.optimize();
		    		} catch (SolrServerException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		// Optimizing drops obsolete documents, obsolete facet values, etc.
		        	SolrGUI.tabFolder.getDocumentsTabItem().getTable().refresh();
		        }
			}
		});
        
        new ToolItem(toolBar, SWT.SEPARATOR);
        
        itemBackup = new ToolItem(toolBar, SWT.PUSH);
        itemBackup.setImage(imgBackup);
        itemBackup.setToolTipText("Make a backup of the index");
        itemBackup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
	        	new  BackupIndexDialog(shell).open();
			}
		});
        
        itemRestore = new ToolItem(toolBar, SWT.PUSH);
        itemRestore.setImage(imgRestore);
        itemRestore.setToolTipText("Restore index from a backup");
        itemRestore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
	        	new  RestoreIndexDialog(shell).open();
			}
		});
        
        toolBar.pack();
    }
    
    @Override
    public void finalize() {
        imgAdd.dispose();
        imgDelete.dispose();
        imgClone.dispose();
        imgExport.dispose();
        imgRefresh.dispose();
        imgUpload.dispose();
        imgClear.dispose();
        imgCommit.dispose();
        imgOptimize.dispose();
        imgBackup.dispose();
        imgRestore.dispose();
    }

}
