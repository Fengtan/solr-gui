package incorporate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class ServerTab {

	
	public void updateMenu(Menu menu, final Shell shell) { // TODO drop shell argument ?
        // File menu.
        MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
        fileMenuItem.setText("&File");
        
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        fileMenuItem.setMenu(fileMenu);

        MenuItem newItem = new MenuItem(fileMenu, SWT.PUSH);
        newItem.setText("Add &server");
        newItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new NewServerDialog(shell).open();
              }
		});
        
        MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText("&Exit");

        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.getDisplay().dispose();
                System.exit(0);
            }
        });
        
        // Columns menu.
        MenuItem columnsMenuItem = new MenuItem(menu, SWT.CASCADE);
        columnsMenuItem.setText("&Columns");

        Menu columnsMenu = new Menu(shell, SWT.DROP_DOWN);
        columnsMenuItem.setMenu(columnsMenu);

        
	}
}
