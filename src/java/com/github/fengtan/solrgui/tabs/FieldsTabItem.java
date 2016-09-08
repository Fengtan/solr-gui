package com.github.fengtan.solrgui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.github.fengtan.solrgui.tables.FieldsTable;
import com.github.fengtan.solrgui.toolbars.FieldsToolbar;

public class FieldsTabItem extends CTabItem {
	
	private FieldsToolbar toolbar;
	
	// TODO add filter to get only indexed/stored fields
	public FieldsTabItem(CTabFolder tabFolder) {
		super(tabFolder, SWT.NONE, tabFolder.getItemCount());

		setText("Fields");
		
		// Prepare layout.
		Composite composite = new Composite(getParent(), SWT.NULL);
		composite.setLayout(new GridLayout());
		setControl(composite);
		
		// Add toolbar and table.
		toolbar = new FieldsToolbar(composite);
		new FieldsTable(composite);
		
		// Pack.
		composite.pack();
	}
	
	@Override
	public void dispose() {
		toolbar.finalize();
		super.dispose();
	}
	
}
