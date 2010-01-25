package tgc2010.ui.dialog;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LocateDialog extends Dialog{

	private Color red = new Color(null, 255, 0, 0);

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

		Label addess = new Label(shell, SWT.NONE);
		addess.setText("Address:");
		FormData addressData = new FormData();
		addressData.top = new FormAttachment(0, 0);
		addressData.left = new FormAttachment(0, 2);
		addess.setLayoutData(addressData);

		final Text addressText = new Text(shell, SWT.BORDER);
		FormData addressTextFormData = new FormData();
		addressTextFormData.width = 200;
		addressTextFormData.height = 15;
		addressTextFormData.top = new FormAttachment(0, 0);
		addressTextFormData.left = new FormAttachment(0, 100);
		addressText.setLayoutData(addressTextFormData);

		Label zip = new Label(shell, SWT.NONE);
		zip.setText("Zip code:");
		FormData zipFormeData = new FormData();
		zipFormeData.top = new FormAttachment(addess, 0);
		zipFormeData.left = new FormAttachment(0, 0);
		zip.setLayoutData(zipFormeData);

		final Text zipText = new Text(shell, SWT.BORDER);
		FormData zipTextFormData = new FormData();
		zipTextFormData.width = 200;
		zipTextFormData.height = 15;
		zipTextFormData.top = new FormAttachment(addess, 0);
		zipTextFormData.left = new FormAttachment(0, 100);
		zipText.setLayoutData(zipTextFormData);

		Label city = new Label(shell, SWT.NONE);
		city.setText("City:");
		FormData cityFormeData = new FormData();
		cityFormeData.top = new FormAttachment(zip, 0);
		cityFormeData.left = new FormAttachment(0, 0);
		city.setLayoutData(cityFormeData);

		final Text cityText = new Text(shell, SWT.BORDER);
		FormData cityTextFormData = new FormData();
		cityTextFormData.width = 200;
		cityTextFormData.height = 15;
		cityTextFormData.top = new FormAttachment(zip, 0);
		cityTextFormData.left = new FormAttachment(0, 100);
		cityText.setLayoutData(cityTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				search(addressText.getText(), zipText.getText(), cityText.getText());
				shell.close();
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(city, 0);
		okFormData.left = new FormAttachment(0, 0);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CANCEL"
		final Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		FormData cancelFormData = new FormData();
		cancelFormData.width = 80;
		cancelFormData.top = new FormAttachment(city, 0);
		cancelFormData.left = new FormAttachment(okButton, 0);
		cancelButton.setLayoutData(cancelFormData);

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public static void search(String address, String zipcode, String city) {
	    String scheme = "http";
	    String authority = "maps.google.fr";
	    String path = "/maps";
	    String query = "f=q&hl=fr&q=" + address+ " , " + zipcode + " " + city ;
	    if(Desktop.isDesktopSupported()) {
	        if(Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
	            try {
	                URI uri = new URI(scheme, authority, path, query, null);
	                System.out.println(uri.toASCIIString());
	                java.awt.Desktop.getDesktop().browse(uri);
	            } catch (URISyntaxException ex) {
	                ex.printStackTrace();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        } else {
	            //La fonction n'est pas supportée par votre système d'exploitation
	        }
	    } else {
	        //Desktop pas supportée par votre système d'exploitation
	    }
	}
}
