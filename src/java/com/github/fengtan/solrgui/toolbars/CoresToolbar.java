package com.github.fengtan.solrgui.toolbars;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.github.fengtan.solrgui.SolrGUI;
import com.github.fengtan.solrgui.dialogs.AddCoreDialog;
import com.github.fengtan.solrgui.dialogs.RenameCoreDialog;
import com.github.fengtan.solrgui.dialogs.SwapCoreDialog;
import com.github.fengtan.solrgui.tables.CoresTable;

public class CoresToolbar {

    private Image imgRefresh;
    private Image imgAdd;
    private Image imgDelete;
    private Image imgRename;
    private Image imgSwap;
    
    private ToolItem itemRefresh;
    private ToolItem itemAdd;
    private ToolItem itemDelete;
    private ToolItem itemRename;
    private ToolItem itemSwap;
    
    public CoresToolbar(Composite composite) {
    	initToolbar(composite);
    }
    
    protected void initToolbar(final Composite composite) {
        Display display = composite.getDisplay();
        ClassLoader loader = getClass().getClassLoader();

        imgRefresh = new Image(display, loader.getResourceAsStream("toolbar/refresh.png"));
        imgAdd = new Image(display, loader.getResourceAsStream("toolbar/add.png"));
        imgDelete = new Image(display, loader.getResourceAsStream("toolbar/delete.png"));
        imgRename = new Image(display, loader.getResourceAsStream("toolbar/clone.png")); // TODO find a better icon?
        imgSwap = new Image(display, loader.getResourceAsStream("toolbar/upload.png")); // TODO find a better icon?

        ToolBar toolBar = new ToolBar(composite, SWT.BORDER);

        itemRefresh = new ToolItem(toolBar, SWT.PUSH);
        itemRefresh.setImage(imgRefresh);
        itemRefresh.setText("Refresh");
        itemRefresh.setToolTipText("Refresh list of cores");
        itemRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrGUI.tabFolder.getCoresTabItem().getTable().refresh();
			}
		});
        
        new ToolItem(toolBar, SWT.SEPARATOR);
        
        itemAdd = new ToolItem(toolBar, SWT.PUSH);
        itemAdd.setImage(imgAdd);
        itemAdd.setText("Add");
        itemAdd.setToolTipText("Add new core");
        itemAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddCoreDialog.getDialog().open();
			}
		});

        itemDelete = new ToolItem(toolBar, SWT.PUSH);
        itemDelete.setImage(imgDelete);
        itemDelete.setText("Delete");
        itemDelete.setToolTipText("Delete core"); //TODO disable when no core selected
        itemDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO prompt confirmation ?
				CoresTable table = SolrGUI.tabFolder.getCoresTabItem().getTable();
				String coreName = table.getSelectedCore();
				// TODO what if delete default core
				try {
					CoreAdminRequest.unloadCore(coreName, SolrGUI.client);
					SolrGUI.tabFolder.getCoresTabItem().getTable().refresh();
				} catch (SolrServerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

        itemRename = new ToolItem(toolBar, SWT.PUSH);
        itemRename.setImage(imgRename);
        itemRename.setText("Rename");
        itemRename.setToolTipText("Rename core"); //TODO disable when no core selected
        itemRename.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CoresTable table = SolrGUI.tabFolder.getCoresTabItem().getTable();
				String coreName = table.getSelectedCore();
				new RenameCoreDialog(coreName).open();
			}
		});
        
        itemSwap = new ToolItem(toolBar, SWT.PUSH);
        itemSwap.setImage(imgSwap);
        itemSwap.setText("Swap");
        itemSwap.setToolTipText("Swap cores"); //TODO disable when no core selected
        itemSwap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CoresTable table = SolrGUI.tabFolder.getCoresTabItem().getTable();
				String coreName = table.getSelectedCore();
				new SwapCoreDialog(coreName).open();
			}
		});
        
        toolBar.pack();
    }
    
    @Override
    public void finalize() {
        imgRefresh.dispose();
    	imgAdd.dispose();
    	imgDelete.dispose();
    	imgRename.dispose();
    	imgSwap.dispose();
    }
	
}
