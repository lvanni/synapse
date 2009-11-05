package example.ui.gui.maemo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import example.overlay.concert.Concert;
import example.ui.console.InfoConsole;

public class ConcertSWTMaemo {

	private final Shell shell;
	private Display display;
	private Color error = new Color(null, 255, 0, 0);
	private Concert concert;

	public ConcertSWTMaemo(final Concert concert) {
		this.concert = concert;
		display = Display.getDefault();
		shell = new Shell(display);

		/* Init the shell */
		shell.setText("MyConcert");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);
		shell.setSize(710, 430);
		//		shell.setBackground(background);

		// CHECKBOXS
		final Button checkPublish = new Button(shell, SWT.CHECK);
		checkPublish.setText("Publish a concert");
		//		checkPublish.setBackground(background);
		FormData checkPublishFormData = new FormData();
		checkPublishFormData.top = new FormAttachment(0, 0);
		checkPublishFormData.left = new FormAttachment(0, 0);
		checkPublish.setLayoutData(checkPublishFormData);

		final Button checkSearch = new Button(shell, SWT.CHECK);
		checkSearch.setText("Search a concert");
		//		checkSearch.setBackground(background);
		FormData checkSearchFormData = new FormData();
		checkSearchFormData.top = new FormAttachment(checkPublish, 10);
		checkSearchFormData.left = new FormAttachment(0, 0);
		checkSearch.setLayoutData(checkSearchFormData);

		// ERROR
		final Label error = new Label(shell, SWT.NONE);
		error.setForeground(ConcertSWTMaemo.this.error);
		error.setText("Bad format number!");
		error.setVisible(false);
		FormData errorFormData = new FormData();
		errorFormData.top = new FormAttachment(checkSearch, 20);
		errorFormData.left = new FormAttachment(0, 140);
		error.setLayoutData(errorFormData);

		// DAY
		Label day = new Label(shell, SWT.NONE);
		day.setText("Day (jj/mm/yyyy): ");
		//		day.setBackground(background);
		FormData dayFormData = new FormData();
		dayFormData.top = new FormAttachment(checkSearch, 50);
		dayFormData.left = new FormAttachment(0, 0);
		day.setLayoutData(dayFormData);

		final Text dayText = new Text(shell, SWT.BORDER);
		//		dayText.setBackground(background);
		dayText.setTextLimit(2);
		FormData dayTextFormData = new FormData();
		dayTextFormData.width = 30;
		dayTextFormData.top = new FormAttachment(checkSearch, 46);
		dayTextFormData.left = new FormAttachment(day, 15);
		dayText.setLayoutData(dayTextFormData);

		Label slash1 = new Label(shell, SWT.NONE);
		slash1.setText("/");
		//		slash1.setBackground(background);
		FormData slash1FormData = new FormData();
		slash1FormData.top = new FormAttachment(checkSearch, 50);
		slash1FormData.left = new FormAttachment(dayText, 5);
		slash1.setLayoutData(slash1FormData);

		final Text mounthText = new Text(shell, SWT.BORDER);
		mounthText.setTextLimit(2);
		//		mounthText.setBackground(background);
		FormData mounthTextFormData = new FormData();
		mounthTextFormData.width = 30;
		mounthTextFormData.top = new FormAttachment(checkSearch, 46);
		mounthTextFormData.left = new FormAttachment(slash1, 5);
		mounthText.setLayoutData(mounthTextFormData);

		Label slash2 = new Label(shell, SWT.NONE);
		slash2.setText("/");
		//		slash2.setBackground(background);
		FormData slash2FormData = new FormData();
		slash2FormData.top = new FormAttachment(checkSearch, 50);
		slash2FormData.left = new FormAttachment(mounthText, 5);
		slash2.setLayoutData(slash2FormData);

		final Text yearText = new Text(shell, SWT.BORDER);
		yearText.setTextLimit(4);
		//		yearText.setBackground(background);
		FormData yearTextFormData = new FormData();
		yearTextFormData.width = 60;
		yearTextFormData.top = new FormAttachment(checkSearch, 46);
		yearTextFormData.left = new FormAttachment(slash2, 5);
		yearText.setLayoutData(yearTextFormData);

		// RED STAR1
		Label star1 = new Label(shell, SWT.NONE);
		star1.setForeground(new Color(null, 255, 0, 0));
		star1.setText("*");
		FormData star1FormData = new FormData();
		star1FormData.top = new FormAttachment(checkSearch, 46);
		star1FormData.left = new FormAttachment(yearText, 5);
		star1.setLayoutData(star1FormData);

		// DESTINATION
		Label destination = new Label(shell, SWT.NONE);
		destination.setText("Destination: ");
		//		destination.setBackground(background);
		FormData destinationFormData = new FormData();
		destinationFormData.top = new FormAttachment(yearText, 10);
		destinationFormData.left = new FormAttachment(0, 0);
		destination.setLayoutData(destinationFormData);

		final Text destinationText = new Text(shell, SWT.BORDER);
		//		destinationText.setBackground(background);
		FormData destinationTextFormData = new FormData();
		destinationTextFormData.width = 216;
		destinationTextFormData.top = new FormAttachment(yearText, 5);
		destinationTextFormData.left = new FormAttachment(0, 142);
		destinationText.setLayoutData(destinationTextFormData);

		// RED STAR2
		Label star2 = new Label(shell, SWT.NONE);
		star2.setForeground(new Color(null, 255, 0, 0));
		star2.setText("*");
		FormData star2FormData = new FormData();
		star2FormData.top = new FormAttachment(yearText, 5);
		star2FormData.left = new FormAttachment(destinationText, 5);
		star2.setLayoutData(star2FormData);

		// CONCERT
		Label lconcert = new Label(shell, SWT.NONE);
		lconcert.setText("Concert: ");
		//		concert.setBackground(background);
		FormData concertFormData = new FormData();
		concertFormData.top = new FormAttachment(destinationText, 10);
		concertFormData.left = new FormAttachment(0, 0);
		lconcert.setLayoutData(concertFormData);

		final Text concertText = new Text(shell, SWT.BORDER);
		//		concertText.setBackground(background);
		FormData concertTextFormData = new FormData();
		concertTextFormData.width = 216;
		concertTextFormData.top = new FormAttachment(destinationText, 5);
		concertTextFormData.left = new FormAttachment(0, 142);
		concertText.setLayoutData(concertTextFormData);

		// CONTACT
		Label contact = new Label(shell, SWT.NONE);
		contact.setText("Contact: ");
		//		contact.setBackground(background);
		FormData contactFormData = new FormData();
		contactFormData.top = new FormAttachment(concertText, 10);
		contactFormData.left = new FormAttachment(0, 0);
		contact.setLayoutData(contactFormData);

		final Text contactText = new Text(shell, SWT.BORDER);
		//		contactText.setBackground(background);
		FormData contactTextFormData = new FormData();
		contactTextFormData.width = 216;
		contactTextFormData.top = new FormAttachment(concertText, 5);
		contactTextFormData.left = new FormAttachment(0, 142);
		contactText.setLayoutData(contactTextFormData);

		// TRANSPORT
		Label transport = new Label(shell, SWT.NONE);
		transport.setText("Transport: ");
		//		transport.setBackground(background);
		FormData transportFormData = new FormData();
		transportFormData.top = new FormAttachment(contactText, 10);
		transportFormData.left = new FormAttachment(0, 0);
		transport.setLayoutData(transportFormData);

		final Text transportText = new Text(shell, SWT.BORDER);
		//		transportText.setBackground(background);
		FormData transportTextFormData = new FormData();
		transportTextFormData.width = 216;
		transportTextFormData.top = new FormAttachment(contactText, 5);
		transportTextFormData.left = new FormAttachment(0, 142);
		transportText.setLayoutData(transportTextFormData);

		// SEPARATOR
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		//		separator1.setBackground(background);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 365;
		separator1FormData.top = new FormAttachment(transportText,20);
		separator1.setLayoutData(separator1FormData);

		// RESULT
		final StyledText result = new StyledText(shell, SWT.BORDER);
		result.setEditable(false);
		Image font = new Image(display,
				ConcertSWTMaemo.class.getResourceAsStream(
				"concert.png"));
		result.setBackgroundImage(font);
		FormData resultTextFormData = new FormData();
		resultTextFormData.width = 280;
		resultTextFormData.height = 380;
		resultTextFormData.top = new FormAttachment(0, 0);
		resultTextFormData.left = new FormAttachment(0, 400);
		result.setLayoutData(resultTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setEnabled(false);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				try{
					Integer.parseInt(dayText.getText() + mounthText.getText() + yearText.getText());
					error.setVisible(false);
					result.setText("");
					String key1 = dayText.getText() + "/" + mounthText.getText() + "/" + yearText.getText();
					String key2 = destinationText.getText();
					String message1 = concertText.getText();
					String message2 = contactText.getText();
					String message3 = transportText.getText();
					if(checkPublish.getSelection()){
						if(!yearText.getText().equals(destinationText.getText())) { // DEBUG MODE! 
							concert.put(key1 + "+" + key2, message1 + "+" + message2 + "+" + message3);
						} else {
							concert.put(key2, message1 + "+" + message2 + "+" + message3); // Debug
						}
						result.setText("Summary:\n\t- concert: " + message1 + "\n\t- contact: " + message2 + "\n\t- transport: " + message3 + "\n\n===> Concert published!");
					} else {
						String found;
						if(!yearText.getText().equals(destinationText.getText())) { // DEBUG MODE! 
							found = concert.get(key1 + "+" + key2);
						} else {
							found = concert.get(key2);
						}
						if(found == null || found.equals("null")){
							result.setText("No concert found...");
						} else {
							String[] args = found.split("\\+");
							for(int i=0 ; i<args.length ; i+=3)
								result.setText(result.getText() + "Concert found:\n\t- concert: " + args[i] + "\n\t- contact: " + args[i+1] + "\n\t- transport: " + args[i+2] + "\n\n");
						}
					}
				} catch(NumberFormatException excep){
					error.setVisible(true);
				} catch(Exception excep) {
					excep.printStackTrace();
				}
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(separator1, 20);
		okFormData.left = new FormAttachment(0, 150);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// SELECTION LISTENER
		Listener sendListener = new Listener() {
			public void handleEvent(Event event) {
				okButton.setEnabled(
						(checkPublish.getSelection() || checkSearch.getSelection()) &&
						!dayText.getText().equals("") && !mounthText.getText().equals("") &&
						!yearText.getText().equals("") && !destinationText.getText().equals(""));
				checkPublish.setEnabled(!checkSearch.getSelection());
				checkSearch.setEnabled(!checkPublish.getSelection());
			}
		};
		checkPublish.addListener(SWT.Selection, sendListener);
		checkSearch.addListener(SWT.Selection, sendListener);
		dayText.addListener(SWT.KeyUp, sendListener);
		mounthText.addListener(SWT.KeyUp, sendListener);
		yearText.addListener(SWT.KeyUp, sendListener);
		destinationText.addListener(SWT.KeyUp, sendListener);
	}

	public void start(){
		shell.open();		
		while (!shell.isDisposed())
			display.readAndDispatch();
		concert.kill();
	}

	public static void main(String[] args) {
		try{
			// LAUNCHING CHORD
			System.out.print("Concert's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			Concert concert = new Concert(ip, Integer.parseInt(args[0]));
			new Thread(concert).start();
			do{
				Thread.sleep(1000);
			} while(concert.getTransport() == null);

			// IF ARGS
			if(args.length > 1 && args[1].equals("-j")){
				String hostToJoin = args[2];
				int portToJoin = Integer.parseInt(args[3]);
				concert.join(hostToJoin, portToJoin);
			}

			System.out.println("ok!");
			Thread.sleep(300);

			ConcertSWTMaemo concertGUI = new ConcertSWTMaemo(concert);
			concertGUI.start();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}