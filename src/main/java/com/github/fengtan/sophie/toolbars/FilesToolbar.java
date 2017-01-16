/**
 * Sophie - A Solr browser and administration tool
 * 
 * Copyright (C) 2016 fengtan<https://github.com/fengtan>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.fengtan.sophie.toolbars;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.github.fengtan.sophie.beans.SophieException;
import com.github.fengtan.sophie.composites.FilesTree;
import com.github.fengtan.sophie.dialogs.ExceptionDialog;

/**
 * Toolbar to make operations on files.
 */
public class FilesToolbar {

    /**
     * Refresh operation - image.
     */
    private Image imgRefresh;

    /**
     * Refresh operation - button.
     */
    private ToolItem itemRefresh;

    /**
     * Tree listing the files.
     */
    private FilesTree tree;

    /**
     * Create a new toolbar to make operations on files.
     * 
     * @param composite
     *            Parent composite.
     */
    public FilesToolbar(Composite composite) {
        initToolbar(composite);
    }

    /**
     * Set tree.
     * 
     * @param tree
     *            Tree.
     */
    public void setTree(FilesTree tree) {
        this.tree = tree;
    }

    /**
     * Populate toolbar with buttons.
     * 
     * @param composite
     *            Parent composite.
     */
    private void initToolbar(final Composite composite) {
        Display display = composite.getDisplay();
        ClassLoader loader = getClass().getClassLoader();

        // Instantiate toolbar.
        ToolBar toolBar = new ToolBar(composite, SWT.BORDER);

        // Add button.
        imgRefresh = new Image(display, loader.getResourceAsStream("toolbar/refresh.png"));
        itemRefresh = new ToolItem(toolBar, SWT.PUSH);
        itemRefresh.setImage(imgRefresh);
        itemRefresh.setText("Refresh");
        itemRefresh.setToolTipText("Refresh files from Solr");
        itemRefresh.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                try {
                    tree.refresh();
                } catch (SophieException e) {
                    ExceptionDialog.open(composite.getShell(), new SophieException("Unable to refresh files", e));
                }
            }
        });

        // Pack.
        toolBar.pack();
    }

    @Override
    public void finalize() {
        imgRefresh.dispose();
    }

}
