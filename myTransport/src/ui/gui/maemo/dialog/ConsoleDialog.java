package ui.gui.maemo.dialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import core.protocols.p2p.chord.AbstractChord;

public class ConsoleDialog extends Dialog{

	private Color green = new Color(null, 0, 255, 0);
	private Color black = new Color(null, 0, 0, 0);

	private Shell shell;
	private Display display;
	private StyledText console;
	private AbstractChord concert;

	public ConsoleDialog(Shell parent, final AbstractChord concert) {
		super(parent);
		display = getParent().getDisplay();

		this.concert = concert;

		/* Init the shell */
		shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Debug Mode");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		console = new StyledText(shell, SWT.BORDER);
		console.setBackground(black);
		console.setForeground(green);
		console.setText(concert.toString());
		console.setEditable(false);
		FormData consoleTextFormData = new FormData();
		consoleTextFormData.width = 550;
		consoleTextFormData.height = 200;
		consoleTextFormData.top = new FormAttachment(0, 0);
		consoleTextFormData.left = new FormAttachment(0, 0);
		console.setLayoutData(consoleTextFormData);

		final Text command = new Text(shell, SWT.BORDER);
		command.setToolTipText("List of available commands:" +
				"\n\t- put(key,value)\t# Set a new key/value in the DHT" +
		"\n\t- get(key)\t\t# Return the value if the key exist" +
		"\n\t- exit\t\t\t# Close the dialog box");
		command.setBackground(black);
		command.setForeground(green);
		FormData commandTextFormData = new FormData();
		commandTextFormData.width = 458;
		commandTextFormData.top = new FormAttachment(console, 3);
		commandTextFormData.left = new FormAttachment(0, 0);
		command.setLayoutData(commandTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				try{
					Pattern put = Pattern.compile("^(put|Put)\\([^,)]+,[^\\),]+\\)");
					Pattern get = Pattern.compile("^(get|Get)\\([^,)]+\\)");
					Pattern exit = Pattern.compile("Exit|exit");
					Matcher mPut = put.matcher(command.getText());
					Matcher mGet = get.matcher(command.getText());
					Matcher mExit = exit.matcher(command.getText());
					if(mExit.matches()){
						shell.close();
					} else {
						if(mPut.matches() || mGet.matches()){
							String[] cmd = command.getText().split("\\(");
							String args1 = cmd[1].split(",")[0].split("\\)")[0];
							if(cmd[0].equals("put") || cmd[0].equals("Put")){
								String args2 = cmd[1].split(",")[1].split("\\)")[0];
								concert.put(args1, args2);
							} else if (cmd[0].equals("get") || cmd[0].equals("Get")){
								command.setText("Result: " + concert.get(args1));
							}
						} else {
							throw new Exception();
						}
					}
				} catch (Exception err) {
					command.setText("Syntax error!");
				}
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(console, 2);
		okFormData.left = new FormAttachment(command, 5);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);
	}

	public void checkConsole(){
		new Thread(new Runnable() {
			public void run() {
				while(!shell.isDisposed()){
					try {
						shell.getDisplay().asyncExec(new Runnable() {
							public void run() {
								console.setText(concert.toString());
							}
						});
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void start(){
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
