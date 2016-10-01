package com.github.fengtan.sophie.dialogs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.solr.common.util.NamedList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.github.fengtan.sophie.beans.SolrUtils;
import com.github.fengtan.sophie.beans.SophieException;

public class CoreSwapDialog extends Dialog {
	
	private String coreName;
	private Combo otherCoreName;
	private String value = null;

	public CoreSwapDialog(Shell shell, String coreName) {
		super(shell);
		this.coreName = coreName;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        
        GridData grid = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
        grid.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        
        Label label = new Label(composite, SWT.WRAP);
        label.setText("Swap core \""+coreName+"\" with:");
        label.setLayoutData(grid);
        label.setFont(parent.getFont());
		
        otherCoreName = new Combo(parent, SWT.DROP_DOWN | SWT.SINGLE | SWT.BORDER);
        otherCoreName.setLayoutData(grid); // TODO add some spacing
        Map<String, NamedList<Object>> cores;
        try {
        	cores = SolrUtils.getCores();
        } catch (SophieException e) {
        	ExceptionDialog.open(parent.getShell(), new SophieException("Unable to suggest list of cores", e));
        	cores = Collections.emptyMap();
        }
        Object[] coreObjects = cores.keySet().toArray();
		String[] coreStrings = Arrays.copyOf(coreObjects, coreObjects.length, String[].class); 
		otherCoreName.setItems(coreStrings);
                
        applyDialogFont(composite);
        return composite;
	}

	/**
	 * Set title of the custom dialog.
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Swap cores");
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			value = otherCoreName.getText();
		}
		super.buttonPressed(buttonId);
	}
	
	public String getValue() {
		return value;
	}
	
}
