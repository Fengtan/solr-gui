package com.github.fengtan.sophie.toolbars;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.github.fengtan.sophie.Sophie;
import com.github.fengtan.sophie.beans.SolrUtils;
import com.github.fengtan.sophie.beans.SophieException;
import com.github.fengtan.sophie.dialogs.CComboDialog;
import com.github.fengtan.sophie.dialogs.ExceptionDialog;
import com.github.fengtan.sophie.tables.DocumentsTable;
import com.github.fengtan.sophie.validators.FieldValidator;

public class DocumentsToolbar implements SelectionListener,ChangeListener {

    private Image imgRefresh;
    private Image imgAdd;
    private Image imgDelete;
    private Image imgClone;
    private Image imgAddField;
    private Image imgUpload;
    private Image imgClear;
    private Image imgCommit;
    private Image imgOptimize;
    private Image imgExport;
    private Image imgBackup;
    private Image imgRestore;
    
    private ToolItem itemRefresh;
    private ToolItem itemAdd;
    private ToolItem itemDelete;
    private ToolItem itemClone;
    private ToolItem itemAddField;
    private ToolItem itemUpload;
    private ToolItem itemClear;
    private ToolItem itemCommit;
    private ToolItem itemOptimize;
    private ToolItem itemExport;
    private ToolItem itemBackup;
    private ToolItem itemRestore;
    
    private DocumentsTable table;
    
    public DocumentsToolbar(Composite composite) {
    	initToolbar(composite);
    }
    
    public void setTable(DocumentsTable table) {
    	this.table = table;
    }
    
    protected void initToolbar(final Composite composite) {
        Display display = composite.getDisplay();
        ClassLoader loader = getClass().getClassLoader();
        imgRefresh = new Image(display, loader.getResourceAsStream("toolbar/refresh.png"));
        imgAdd = new Image(display, loader.getResourceAsStream("toolbar/add.png"));
        imgDelete = new Image(display, loader.getResourceAsStream("toolbar/delete.png"));
        imgClone = new Image(display, loader.getResourceAsStream("toolbar/clone.png"));
        imgAddField = new Image(display, loader.getResourceAsStream("toolbar/add.png")); // TODO find a better icon ?
        imgUpload = new Image(display, loader.getResourceAsStream("toolbar/upload.png")); // TODO find a better icon ?
        imgClear = new Image(display, loader.getResourceAsStream("toolbar/clear.png"));
        imgCommit = new Image(display, loader.getResourceAsStream("toolbar/commit.png"));
        imgOptimize = new Image(display, loader.getResourceAsStream("toolbar/optimize.png"));
        imgExport = new Image(display, loader.getResourceAsStream("toolbar/export.png")); // TODO find better icon ?
        imgBackup = new Image(display, loader.getResourceAsStream("toolbar/backup.png")); // TODO find better icon
        imgRestore = new Image(display, loader.getResourceAsStream("toolbar/restore.png")); // TODO find better icon

        ToolBar toolBar = new ToolBar(composite, SWT.BORDER);

        itemRefresh = new ToolItem(toolBar, SWT.PUSH);
        itemRefresh.setImage(imgRefresh);
        itemRefresh.setText("Refresh");
        itemRefresh.setToolTipText("Refresh from Solr: this will wipe out local modifications");
        itemRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					table.refresh();	
				} catch(SophieException e) {
					ExceptionDialog.open(composite.getShell(), new SophieException("Unable to refresh documents from Solr server", e));
				}
			}
		});
        
        new ToolItem(toolBar, SWT.SEPARATOR);
        
        // TODO allow to create documents with new fields
        itemAdd = new ToolItem(toolBar, SWT.PUSH);
        itemAdd.setImage(imgAdd);
        itemAdd.setText("Add");
        itemAdd.setToolTipText("Add new document");
        itemAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.addDocument(new SolrDocument());
			}
		});

        itemDelete = new ToolItem(toolBar, SWT.PUSH);
        itemDelete.setImage(imgDelete);
        itemDelete.setText("Delete");
        itemDelete.setToolTipText("Delete document");
        itemDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.deleteSelectedDocument();
			}
		});
        itemDelete.setEnabled(false);

        itemClone = new ToolItem(toolBar, SWT.PUSH);
        itemClone.setImage(imgClone);
        itemClone.setText("Clone");
        itemClone.setToolTipText("Clone document");
        itemClone.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.cloneSelectedDocument();
			}
		});
        itemClone.setEnabled(false);
        
        itemAddField = new ToolItem(toolBar, SWT.PUSH);
        itemAddField.setImage(imgAddField);
        itemAddField.setText("Add field");
        itemAddField.setToolTipText("Add new field");
        itemAddField.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String[] suggestions;
				try {
					List<String> fields = SolrUtils.getRemoteSchemaFields();
					fields.remove("*"); // Remove universal pattern which is not useful and will match everything.
					suggestions = new String[fields.size()];
					fields.toArray(suggestions);
					Arrays.sort(suggestions);
				} catch (SophieException e) {
					Sophie.log.error("Unable to suggest fields", e);
					suggestions = ArrayUtils.EMPTY_STRING_ARRAY;
				}
				CComboDialog dialog = new CComboDialog(composite.getShell(), "Add new field", "Field name:", suggestions, new FieldValidator(suggestions));
				dialog.open();
				if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
					return;
				}
				String fieldName = dialog.getValue();
				table.addField(fieldName);
			}
		});
        
        itemUpload = new ToolItem(toolBar, SWT.PUSH);
        itemUpload.setImage(imgUpload);
        itemUpload.setText("Upload");
        itemUpload.setToolTipText("Upload local modifications to Solr");
        itemUpload.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					table.upload();	
				} catch (SophieException e) {
					ExceptionDialog.open(composite.getShell(), new SophieException("Unable to upload local modifications to Solr", e));
				}
			}
		});
        itemUpload.setEnabled(false);
        
        new ToolItem(toolBar, SWT.SEPARATOR);
                
        itemClear = new ToolItem(toolBar, SWT.PUSH);
        itemClear.setImage(imgClear);
        itemClear.setText("Clear");
        itemClear.setToolTipText("Clear index");
        itemClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
		        MessageBox messageBox = new MessageBox(composite.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		        messageBox.setText("Clear index");
		        messageBox.setMessage("Do you really want to clear the index? This will remove all documents from the index.");
		        int response = messageBox.open();
		        if (response == SWT.YES) {
		        	try {
	        			Sophie.client.deleteByQuery("*:*");
	        			Sophie.client.commit();
		        		table.refresh();	
		        	} catch (SolrServerException|IOException|SolrException|SophieException e) {
		        		ExceptionDialog.open(composite.getShell(), new SophieException("Unable to clear index", e));
		        	}
		        }
			}
		});

        itemCommit = new ToolItem(toolBar, SWT.PUSH);
        itemCommit.setImage(imgCommit);
        itemCommit.setText("Commit");
        itemCommit.setToolTipText("Commit index");
        itemCommit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					Sophie.client.commit();
					table.refresh();
	        	} catch (SolrServerException|IOException|SolrException|SophieException e) {
	        		ExceptionDialog.open(composite.getShell(), new SophieException("Unable to commit index", e));
	        	}
			}
		});
        
        itemOptimize = new ToolItem(toolBar, SWT.PUSH);
        itemOptimize.setImage(imgOptimize);
        itemOptimize.setText("Optimize");
        itemOptimize.setToolTipText("Optimize index");
        itemOptimize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
		        MessageBox messageBox = new MessageBox(composite.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		        messageBox.setText("Optimize index");
		        messageBox.setMessage("Do you really want to optimize the index? If the index is highly segmented, this may take several hours and will slow down requests.");
		        int response = messageBox.open();
		        if (response == SWT.YES) {
		    		try {
		    			Sophie.client.optimize();
		    			// Optimizing drops obsolete documents, obsolete facet values, etc so we need to refresh the table.
			    		table.refresh();
		    		} catch (SolrServerException|IOException|SolrException|SophieException e) {
		    			ExceptionDialog.open(composite.getShell(), new SophieException("Unable to optimize index", e));
		    		}
		        }
			}
		});
        
        new ToolItem(toolBar, SWT.SEPARATOR);
        
        itemExport = new ToolItem(toolBar, SWT.PUSH);
        itemExport.setImage(imgExport);
        itemExport.setText("Export");
        itemExport.setToolTipText("Export as CSV file");
        itemExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					table.export();	
				} catch (SophieException e) {
	        		ExceptionDialog.open(composite.getShell(), e);
				}
			}
		});
        
        itemBackup = new ToolItem(toolBar, SWT.PUSH);
        itemBackup.setImage(imgBackup);
        itemBackup.setText("Backup");
        itemBackup.setToolTipText("Make a backup of the index");
        itemBackup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// TODO "Leave empty to use the default format (yyyyMMddHHmmssSSS)."
				InputDialog dialog = new InputDialog(composite.getShell(), "Make a backup of the index", "Backup name:", null, null);
				dialog.open();
				if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
					return;
				}
				String backupName = dialog.getValue();
				ModifiableSolrParams params = new ModifiableSolrParams();
				params.set("command", "backup");
				params.set("name", backupName);
				QueryRequest request = new QueryRequest(params);
				request.setPath("/replication");
				try {
					NamedList<Object> response = Sophie.client.request(request);
					// TODO progress bar with /replication?command=details ?
					// TODO "OK" solrj constant ?
					if (StringUtils.equals(response.get("status").toString(), "OK")) {
						// TODO notify user OK + name/place of backup file
					} else {
						// TODO notify user NOK + exception details
					}
				} catch (SolrServerException|IOException|SolrException e) {
					ExceptionDialog.open(composite.getShell(), new SophieException("Unable to create backup \""+backupName+"\"", e));
				}
			}
		});
        
        itemRestore = new ToolItem(toolBar, SWT.PUSH);
        itemRestore.setImage(imgRestore);
        itemRestore.setText("Restore");
        itemRestore.setToolTipText("Restore index from a backup");
        itemRestore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// TODO "Available backup names"
				// TODO "leave empty for xxx"
				InputDialog dialog = new InputDialog(composite.getShell(), "Restore index from a backup", "Backup name:", null, null);
				dialog.open();
				if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
					return;
				}
				try {
					table.restore(dialog.getValue());	
				} catch (SophieException e) {
		        	ExceptionDialog.open(composite.getShell(), e);
				}
			}
		});
        
        toolBar.pack();
    }
    
    @Override
    public void finalize() {
        imgRefresh.dispose();
        imgAdd.dispose();
        imgDelete.dispose();
        imgClone.dispose();
        imgAddField.dispose();
        imgUpload.dispose();
        imgClear.dispose();
        imgCommit.dispose();
        imgOptimize.dispose();
        imgExport.dispose();
        imgBackup.dispose();
        imgRestore.dispose();
    }

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// Do nothing.
	}

	/**
	 * Toggle buttons that require a document to be selected.
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		itemDelete.setEnabled(true);
		itemClone.setEnabled(true);
	}
	
	/**
	 * Enable 'Upload' button if there is at least one modification to send to Solr.
	 */
	@Override
	public void changed() {
		itemUpload.setEnabled(true);
	}
	
	/**
	 * Disable 'Upload' button if there is no modification to send to Solr.
	 */
	@Override
	public void unchanged() {
		itemUpload.setEnabled(false);
	}
    
}
