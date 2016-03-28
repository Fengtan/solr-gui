package com.github.fengtan.solrgui.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.github.fengtan.solrgui.beans.SolrGUIDocument;
import com.github.fengtan.solrgui.beans.SolrGUIServer;

// TODO measure memory footprint
// TODO see luke ui
// TODO measure table items http://www.eclipse.org/articles/article.php?file=Article-CustomDrawingTableAndTreeItems/index.html
// TODO allow to export in xls (may require org.eclipse.nebula.widgets.nattable.extension.poi*.jar)

public class SolrGUITable { // TODO extend Composite ?

	private List<String> fieldsDisplayed = new ArrayList<String>(); // Fields displayed in the table.
	// TODO could be worth using style VIRTUAL since the data source is remote http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fwidgets%2FTable.html
	private Table table;
	private TableViewer tableViewer;
	private SolrGUISorter sorter;
	private SolrGUIServer server; // TODO should probably not be in this class
	
	public SolrGUITable(Composite parent, SolrGUIServer server) {
		this.server = server;
		
		createTable(parent);
		refreshColumns(); // TODO why not call refresh () ? which calls refreshColumns()
		createTableViewer();
		createContextualMenu();

		// By default, we show all fields available.
		for (String field:server.getFields()) {
			fieldsDisplayed.add(field);
		}
	}
	
	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 5; // 5 according to number of buttons.
		table.setLayoutData(gridData);
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				// TODO document in readme: typing 'Suppr' deletes a row.
				if (event.keyCode == SWT.DEL) {
					Table table = (Table) event.getSource();
					TableItem item = table.getSelection()[0]; // TODO what if [0] does not exist.
					SolrGUIDocument document = (SolrGUIDocument) item.getData();
					server.removeDocument(document);
				}
			}
		});

	}


	/**
	 * Attaches TableColumn objects to this.table.
	 */
	public void refreshColumns() { // TODO probably should be private
		// Make sure the user does not see what we're doing.
		table.setRedraw(false);
		// Remove all columns.
		while (table.getColumnCount() > 0) {
		    table.getColumns()[0].dispose();
		}
		// Add the new ones.
		Collections.sort(fieldsDisplayed);
		for (final String field:fieldsDisplayed) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(field);
			// Add listener to column so documents are sorted when clicked.
			column.addSelectionListener(new SelectionAdapter() { 
				public void widgetSelected(SelectionEvent e) {
					sorter = new SolrGUISorter(field);
					tableViewer.setSorter(sorter); // TODO does sorting scale ?
				}
			});
			column.pack(); // TODO needed ? might be worth to setLayout() to get rid of this
		}
		// Let user see what we did.
		table.setRedraw(true);
	}
	

	/**
	 * Create contextual menu to show/hide columns.
	 * TODO support special fields like shards/score etc ?
	 */
	private void createContextualMenu() {
		Menu menu = new Menu(table);
		for (String field:server.getFields()) {
			final MenuItem item = new MenuItem(menu, SWT.CHECK);
			item.setText(field);
			item.setSelection(fieldsDisplayed.contains(field));
			item.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (item.getSelection()) {
						// If user checked menu item, then add column.
						fieldsDisplayed.add(item.getText());
					} else {
						// If user unchecked menu item, then remove column.
						fieldsDisplayed.remove(item.getText());
					}
					refresh();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					// Do nothing
				}
			});
		}
		table.setMenu(menu);
	}

	// TODO sort using setSortDirection ?
	
	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(server.getFields());

		// Create the cell editors
		CellEditor[] editors = new CellEditor[tableViewer.getColumnProperties().length];
		TextCellEditor textEditor;

		for (int i =0; i < editors.length; i++) {
			textEditor = new TextCellEditor(table);
			((Text) textEditor.getControl()).setTextLimit(60);
			editors[i] = textEditor;
		}
		
		tableViewer.setCellEditors(editors);
		tableViewer.setCellModifier(new SolrGUICellModifier(server));
		tableViewer.setSorter(sorter); // Set default sorter (null-safe).
		tableViewer.setContentProvider(new SolrGUIContentProvider(server, tableViewer));
		tableViewer.setLabelProvider(new SolrGUILabelProvider(fieldsDisplayed));
		tableViewer.setInput(server);
	}

	// Return selected document (or null if none selected).
	public SolrGUIDocument getSelectedDocument() {
		return (SolrGUIDocument) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
	}
	
	public List<String> getFieldsDisplayed() { // TODO probably should be hidden
		return fieldsDisplayed;
	}
	
	public void refresh() {
		tableViewer.refresh();
	}
	
	public int getItemCount() {
		return tableViewer.getTable().getItemCount();
	}
	
	public void dispose() {
		tableViewer.getLabelProvider().dispose();
	}

}
