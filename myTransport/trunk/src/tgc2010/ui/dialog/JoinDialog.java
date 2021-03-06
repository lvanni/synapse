package tgc2010.ui.dialog;

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

import tgc2010.ui.CarPal;
import core.ITracker;
import core.protocols.p2p.Node;
import core.tools.InfoConsole;
import experiments.current.synapse.Synapse;
import experiments.current.synapse.plugin.ChordNodePlugin;

public class JoinDialog extends Dialog{

	private Color red = new Color(null, 255, 0, 0);

	public JoinDialog(final Shell parent, final CarPal carPal) {
		super(parent);
		Display display = getParent().getDisplay();
		final Synapse synapse = carPal.getSynapse();

		/* Init the shell */
		final Shell shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Join Dialog");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		Label accessPass = new Label(shell, SWT.NONE);
		accessPass.setText("Access Pass:");
		FormData accessPassFormData = new FormData();
		accessPassFormData.top = new FormAttachment(0, 0);
		accessPassFormData.left = new FormAttachment(0, 2);
		accessPass.setLayoutData(accessPassFormData);

		final Label error = new Label(shell, SWT.NONE);
		error.setForeground(red);
		error.setText("invalid access pass!");
		error.setVisible(false);
		FormData errorFormData = new FormData();
		errorFormData.top = new FormAttachment(0, 0);
		errorFormData.left = new FormAttachment(accessPass, 10);
		error.setLayoutData(errorFormData);

		final Text accessPassText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		FormData accessPassTextFormData = new FormData();
		accessPassTextFormData.width = 200;
		accessPassTextFormData.height = 15;
		accessPassTextFormData.top = new FormAttachment(error, 5);
		accessPassTextFormData.left = new FormAttachment(0, 0);
		accessPassText.setLayoutData(accessPassTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				// CONNECT ON TRACKER
				String trackerResponse = synapse.getTransport().sendRequest(
						ITracker.JOIN + "," + accessPassText.getText(),
						new Node(ITracker.TRACKER_HOST, 0,
								ITracker.TRACKER_PORT));
				if(trackerResponse.equals("null")) { // no invitation for this password
					System.out.println("tracker response is null");
					error.setVisible(true);
				} else if (trackerResponse.equals("unreachable")) { // transport error, try again
					System.out.println("tracker response is unreachable");
					shell.close();
				} else {
					System.out.println("tracker response is " + trackerResponse);
					String args[] = trackerResponse.split(",");
					final ChordNodePlugin overlay = new ChordNodePlugin(InfoConsole.getIp(), 0, synapse, args[0]);
					synapse.getNetworks().add(overlay);
					System.out.println("new chord plugin created!");
					synapse.getTransport().sendRequest(
							ITracker.ADDNODE + "," + overlay.getIdentifier() + ","
							+ overlay.getThisNode(),
							new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
					final Node n = new Node(args[1] + "," + args[2] + "," + args[3]);
					System.out.println("new chord plugin added to the tracker ");
					shell.getDisplay().asyncExec(new Runnable() {
						public void run() {
							overlay.join(n.getIp(), n.getPort());
							parent.setText(parent.getText() + " gold member");
							carPal.updateBackground("goldBack.png");
							System.out.println("new chord plugin joined!");
						}
					});
					parent.pack();
					shell.close();
				} 
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(error, 0);
		okFormData.left = new FormAttachment(accessPassText, 10);
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
		cancelFormData.top = new FormAttachment(error, 0);
		cancelFormData.left = new FormAttachment(okButton, 5);
		cancelButton.setLayoutData(cancelFormData);

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
