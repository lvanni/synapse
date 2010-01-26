package blackbox.ui.gui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import blackbox.core.overlay.concert.Concert;

import core.protocols.p2p.chord.IChord;

public class JoinDialog extends Dialog{

	public JoinDialog(Shell parent, final IChord n) {
		super(parent);
		Display display = getParent().getDisplay();

		/* Init the shell */
		final Shell shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Join");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		Label addressToJoin = new Label(shell, SWT.NONE);
		addressToJoin.setText("Contact:  ");
		FormData addressToJoinFormData = new FormData();
		addressToJoinFormData.top = new FormAttachment(0, 15);
		addressToJoinFormData.left = new FormAttachment(0, 0);
		addressToJoin.setLayoutData(addressToJoinFormData);

		final Text addressToJoinText = new Text(shell, SWT.BORDER);
		if(n instanceof Concert)
			addressToJoinText.setText("localhost:8000");
		else
			addressToJoinText.setText("localhost:9000");
		FormData addressToJoinTextFormData = new FormData();
		addressToJoinTextFormData.width = 150;
		addressToJoinTextFormData.top = new FormAttachment(0, 10);
		addressToJoinTextFormData.left = new FormAttachment(addressToJoin, 5);
		addressToJoinText.setLayoutData(addressToJoinTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				try{
					String[] addressArgs = addressToJoinText.getText().split(":");
					n.join(addressArgs[0], Integer.parseInt(addressArgs[1]));
				} catch (Exception excep){
					excep.printStackTrace();
				}
				shell.close();
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(0, 10);
		okFormData.left = new FormAttachment(addressToJoinText, 10);
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
