package blackbox.ui.gui.dialog;

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
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import blackbox.core.mytansport.MyTransport;
import core.protocols.p2p.IDHT;
import core.protocols.p2p.chord.AbstractChord;

public class AddServiceDialog extends Dialog{
	
	public AddServiceDialog(Shell parent, final MenuItem item, final MyTransport myTransport, final Label services) {
		super(parent);
		Display display = getParent().getDisplay();
		
		/* Init the shell */
		final Shell shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Add " + item.getText() + " service");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);
		
		Label portToStart = new Label(shell, SWT.NONE);
		portToStart.setText("New port:");
		FormData portToStartFormData = new FormData();
		portToStartFormData.top = new FormAttachment(0, 15);
		portToStartFormData.left = new FormAttachment(0, 0);
		portToStart.setLayoutData(portToStartFormData);

		final Text portToStartText = new Text(shell, SWT.BORDER);
		if(item.getText().equals("Concert"))
			portToStartText.setText("8006");
		else
			portToStartText.setText("9006");
		FormData portToStartTextFormData = new FormData();
		portToStartTextFormData.width = 50;
		portToStartTextFormData.top = new FormAttachment(0, 10);
		portToStartTextFormData.left = new FormAttachment(0, 100);
		portToStartText.setLayoutData(portToStartTextFormData);
		
		Label addressToJoin = new Label(shell, SWT.NONE);
		addressToJoin.setText("Contact");
		FormData addressToJoinFormData = new FormData();
		addressToJoinFormData.top = new FormAttachment(portToStartText, 15);
		addressToJoinFormData.left = new FormAttachment(0, 0);
		addressToJoin.setLayoutData(addressToJoinFormData);

		final Text addressToJoinText = new Text(shell, SWT.BORDER);
		if(item.getText().equals("Concert"))
			addressToJoinText.setText("localhost:8000");
		else
			addressToJoinText.setText("localhost:9000");
		FormData addressToJoinTextFormData = new FormData();
		addressToJoinTextFormData.width = 150;
		addressToJoinTextFormData.top = new FormAttachment(portToStartText, 10);
		addressToJoinTextFormData.left = new FormAttachment(0, 100);
		addressToJoinText.setLayoutData(addressToJoinTextFormData);
		
		// SEPARATOR
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 360;
		separator1FormData.top = new FormAttachment(addressToJoinText,10);
		separator1.setLayoutData(separator1FormData);
		
		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				try{
					String[] addressArgs = addressToJoinText.getText().split(":");
//					if(item.getText().equals("Concert"))
//						myTransport.join(OverlayID.MYCONCERT, Integer.parseInt(portToStartText.getText()), addressArgs[0], Integer.parseInt(addressArgs[1]));
//					else
//						myTransport.join(OverlayID.MYFOOT, Integer.parseInt(portToStartText.getText()), addressArgs[0], Integer.parseInt(addressArgs[1]));
				services.setForeground(new Color(null, 0, 180, 0));
				String text = "Services enabled: ";
				for(IDHT o : myTransport.getNetworks()){
					text += o.getIdentifier()+ "[" + ((AbstractChord) o).getThisNode().getId() + "," + ((AbstractChord) o).getPredecessor().getId() + "], ";
				}
				services.setText(text);
				} catch (Exception excep){
					excep.printStackTrace();
				}
				shell.close();
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(separator1, 10);
		okFormData.left = new FormAttachment(0, 140);
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
