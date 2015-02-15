package com.github.fengtan.solrgui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.solr.common.SolrDocument;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class SolrGUI {

	private Table table;
	private TableViewer tableViewer;
	private Button closeButton;
	
	private SolrGUIServer server;

	/**
	 * Launch the window.
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setText("Solr GUI");

		// Set layout for shell.
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		// TODO loop over servers.
		URL url = Config.getServers().get(0).getURL();
		
		// Create a composite to hold the children.
		Composite composite = new Composite(shell, SWT.NONE);
		final SolrGUI solrGUI = new SolrGUI(composite, url);
		solrGUI.getControl().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				solrGUI.dispose();			
			}
		});

		// Make the shell to display its content.
		shell.open();
		solrGUI.run(shell);
	}
	
	public SolrGUI(Composite parent, URL url) {
		server = new SolrGUIServer(url);
		addChildControls(parent);
	}

	/**
	 * Run and wait for a close event
	 * @param shell Instance of Shell
	 */
	private void run(Shell shell) {
		// Add a listener for the close button
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Close the view i.e. dispose of the composite's parent.
				table.getParent().getParent().dispose();
			}
		});
		
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Release resources
	 */
	public void dispose() {
		// Tell the label provider to release its resources.
		tableViewer.getLabelProvider().dispose();
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * @return the shell that was created	 
	 */
	private void addChildControls(Composite composite) {

		// Create a composite to hold the children
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
		composite.setLayoutData (gridData);

		// Set numColumns to 3 for the buttons 
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 4;
		composite.setLayout (layout);

		// Create the table 
		createTable(composite);
		
		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new SolrGUIContentProvider());
		tableViewer.setLabelProvider(new SolrGUILabelProvider());
		// server = new SolrGUIServer(url); TODO drop
		tableViewer.setInput(server);

		// Add the buttons
		createButtons(composite);
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText("Modified");
		column.setWidth(20);
		
		for (final String field:server.getFields()) {
			column = new TableColumn(table, SWT.LEFT);
			column.setText(field);
			// Add listener to column so documents are sorted when clicked. 
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					tableViewer.setSorter(new SolrGUIServerSorter(field)); // TODO does sorting scale ?
				}
			});
			column.pack();
		}
		
/* TODO drop
		// 3rd column with document Owner
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Owner");
		column.setWidth(100);
		// Add listener to column so documents are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new SolrGUIServerSorter(SolrGUIServerSorter.OWNER));
			}
		});

		// 4th column with document PercentComplete 
		column = new TableColumn(table, SWT.CENTER, 3);
		column.setText("% Complete");
		column.setWidth(80);
		//  Add listener to column so documents are sorted by percent when clicked
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new SolrGUIServerSorter(SolrGUIServerSorter.PERCENT_COMPLETE));
			}
		});
		*/
	}

	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		String[] columns = ArrayUtils.addAll(new String[]{"Status"}, server.getFields());
		tableViewer.setColumnProperties(columns);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[tableViewer.getColumnProperties().length];
		TextCellEditor textEditor;
		for (int i=0; i<editors.length; i++) { // TODO pb: last column not editable.
			textEditor = new TextCellEditor(table);
			((Text) textEditor.getControl()).setTextLimit(60);
			editors[i] = textEditor;
		}

		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new SolrGUICellModifier(this));
		// Set the default sorter for the viewer 
		tableViewer.setSorter(new SolrGUIServerSorter(server.getFields()[0])); // TODO what if there is no field
	}

	/*
	 * Close the window and dispose of resources
	 */
	public void close() {
		Shell shell = table.getShell();
		if (shell != null && !shell.isDisposed())
			shell.dispose();
	}


	/**
	 * InnerClass that acts as a proxy for the SolrGUIServer
	 * providing content for the Table. It implements the ISolrGUIServerViewer 
	 * interface since it must register changeListeners with the 
	 * SolrGUIServer
	 */
	class SolrGUIContentProvider implements IStructuredContentProvider, ISolrGUIServerViewer {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((SolrGUIServer) newInput).addChangeListener(this);
			if (oldInput != null)
				((SolrGUIServer) oldInput).removeChangeListener(this);
		}

		public void dispose() {
			server.removeChangeListener(this);
		}

		// Return the documents as an array of Objects
		public Object[] getElements(Object parent) {
			return server.getDocuments().toArray();
		}

		public void addDocument(SolrDocument document) {
			tableViewer.add(document);
		}

		public void removeDocument(SolrDocument document) {
			tableViewer.remove(document);			
		}

		public void updateDocument(SolrDocument document) {
			tableViewer.update(document, null);	
		}
	}
	
	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Add");
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				server.addDocument();
			}
		});

		//	Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80; 
		delete.setLayoutData(gridData); 
		delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SolrDocument document = (SolrDocument) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				if (document != null) {
					server.removeDocument(document);
				} 				
			}
		});

		//	Create and configure the "Commit" button.
		Button commit = new Button(parent, SWT.PUSH | SWT.CENTER);
		commit.setText("Commit");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80; 
		commit.setLayoutData(gridData); 
		commit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO 				
			}
		});	
		
		//	Create and configure the "Close" button.
		closeButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		closeButton.setText("Close");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80; 
		closeButton.setLayoutData(gridData); 		
	}

	/**
	 * Return the column names in a collection
	 * 
	 * @return List  containing column names
	 */
	public List<String> getColumnNames() {
		return Arrays.asList(server.getFields());
	}

	/**
	 * @return currently selected item
	 */
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	/**
	 * Return the SolrGUIServer
	 */
	public SolrGUIServer getServer() {
		return server;	
	}

	/**
	 * Return the parent composite
	 */
	public Control getControl() {
		return table.getParent();
	}

	/**
	 * Return the 'close' Button
	 */
	public Button getCloseButton() {
		return closeButton;
	}
}