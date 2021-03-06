package tgc2010.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tgc2010.ui.tool.GeoLoc;

public class LocateDialog extends Dialog{

	private Browser browser;

	public LocateDialog(final Shell parent) {
		super(parent);
		Display display = getParent().getDisplay();

		/* Init the shell */
		final Shell shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Locate Dialog");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		final Composite composite = new Composite(shell, SWT.BORDER);
		FormLayout compositeFormLayout = new FormLayout();
		composite.setLayout(compositeFormLayout);

		try {
			browser = new Browser(composite, SWT.NONE);
			FormData browserFormData = new FormData();
			browserFormData.width = 1000;
//			browserFormData.height = 400;
			browserFormData.height = 465;
//			browserFormData.top = new FormAttachment(0, -120);
			browserFormData.top = new FormAttachment(0, -185);
			browserFormData.left = new FormAttachment(0, -384);
			browser.setLayoutData(browserFormData);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
			System.exit(-1);
		}

		Label addess = new Label(shell, SWT.NONE);
		addess.setText("Address:");
		FormData addressData = new FormData();
		addressData.top = new FormAttachment(composite, 10);
		addressData.left = new FormAttachment(0, 0);
		addess.setLayoutData(addressData);

		final Text addressText = new Text(shell, SWT.BORDER);
		FormData addressTextFormData = new FormData();
		addressTextFormData.width = 415;
		addressTextFormData.height = 15;
		addressTextFormData.top = new FormAttachment(composite, 10);
		addressTextFormData.left = new FormAttachment(0, 100);
		addressText.setLayoutData(addressTextFormData);

		Label zip = new Label(shell, SWT.NONE);
		zip.setText("Zip code:");
		FormData zipFormeData = new FormData();
		zipFormeData.top = new FormAttachment(addess, 10);
		zipFormeData.left = new FormAttachment(0, 0);
		zip.setLayoutData(zipFormeData);

		final Text zipText = new Text(shell, SWT.BORDER);
		FormData zipTextFormData = new FormData();
		zipTextFormData.width = 415;
		zipTextFormData.height = 15;
		zipTextFormData.top = new FormAttachment(addess, 10);
		zipTextFormData.left = new FormAttachment(0, 100);
		zipText.setLayoutData(zipTextFormData);

		Label city = new Label(shell, SWT.NONE);
		city.setText("City:");
		FormData cityFormeData = new FormData();
		cityFormeData.top = new FormAttachment(zip, 10);
		cityFormeData.left = new FormAttachment(0, 0);
		city.setLayoutData(cityFormeData);

		final Text cityText = new Text(shell, SWT.BORDER);
		FormData cityTextFormData = new FormData();
		cityTextFormData.width = 415;
		cityTextFormData.height = 15;
		cityTextFormData.top = new FormAttachment(zip, 10);
		cityTextFormData.left = new FormAttachment(0, 100);
		cityText.setLayoutData(cityTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Locate");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				GeoLoc.search(browser, addressText.getText(), zipText.getText(), cityText.getText());
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(zip, 5);
		okFormData.left = new FormAttachment(cityText, 10);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
