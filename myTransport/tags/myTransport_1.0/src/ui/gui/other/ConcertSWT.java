package ui.gui.other;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConcertSWT {
	
	private final Shell shell;
	private Display display;
	
	public ConcertSWT() {
		display = Display.getDefault();
		shell = new Shell(display);

		/* Init the shell */
		shell.setText("MyConcert");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);
		shell.setSize(340, 480);
		
		// CHECKBOXS
		final Button checkPublish = new Button(shell, SWT.CHECK);
		checkPublish.setText("Publish a concert");
		FormData checkPublishFormData = new FormData();
		checkPublishFormData.top = new FormAttachment(0, 0);
		checkPublishFormData.left = new FormAttachment(0, 0);
		checkPublish.setLayoutData(checkPublishFormData);

		final Button checkSearch = new Button(shell, SWT.CHECK);
		checkSearch.setText("Search a concert");
		FormData checkSearchFormData = new FormData();
		checkSearchFormData.top = new FormAttachment(checkPublish, 10);
		checkSearchFormData.left = new FormAttachment(0, 0);
		checkSearch.setLayoutData(checkSearchFormData);
		
		// DAY
		Label day = new Label(shell, SWT.NONE);
		day.setText("Day (jj/mm/yyyy): ");
		FormData dayFormData = new FormData();
		dayFormData.top = new FormAttachment(checkSearch, 10);
		dayFormData.left = new FormAttachment(0, 0);
		day.setLayoutData(dayFormData);
		
		Text dayText = new Text(shell, SWT.BORDER);
		FormData dayTextFormData = new FormData();
		dayTextFormData.width = 30;
		dayTextFormData.top = new FormAttachment(checkSearch, 3);
		dayTextFormData.left = new FormAttachment(day, 15);
		dayText.setLayoutData(dayTextFormData);
		
		Label slash1 = new Label(shell, SWT.NONE);
		slash1.setText("/");
		FormData slash1FormData = new FormData();
		slash1FormData.top = new FormAttachment(checkSearch, 7);
		slash1FormData.left = new FormAttachment(dayText, 5);
		slash1.setLayoutData(slash1FormData);
		
		Text mounthText = new Text(shell, SWT.BORDER);
		FormData mounthTextFormData = new FormData();
		mounthTextFormData.width = 30;
		mounthTextFormData.top = new FormAttachment(checkSearch, 3);
		mounthTextFormData.left = new FormAttachment(slash1, 5);
		mounthText.setLayoutData(mounthTextFormData);
		
		Label slash2 = new Label(shell, SWT.NONE);
		slash2.setText("/");
		FormData slash2FormData = new FormData();
		slash2FormData.top = new FormAttachment(checkSearch, 7);
		slash2FormData.left = new FormAttachment(mounthText, 5);
		slash2.setLayoutData(slash2FormData);
		
		Text yearText = new Text(shell, SWT.BORDER);
		FormData yearTextFormData = new FormData();
		yearTextFormData.width = 60;
		yearTextFormData.top = new FormAttachment(checkSearch, 3);
		yearTextFormData.left = new FormAttachment(slash2, 5);
		yearText.setLayoutData(yearTextFormData);
		
		// DESTINATION
		Label destination = new Label(shell, SWT.NONE);
		destination.setText("Destination: ");
		FormData destinationFormData = new FormData();
		destinationFormData.top = new FormAttachment(yearText, 10);
		destinationFormData.left = new FormAttachment(0, 0);
		destination.setLayoutData(destinationFormData);
		
		Text destinationText = new Text(shell, SWT.BORDER);
		FormData destinationTextFormData = new FormData();
		destinationTextFormData.width = 216;
		destinationTextFormData.top = new FormAttachment(yearText, 5);
		destinationTextFormData.left = new FormAttachment(destination, 5);
		destinationText.setLayoutData(destinationTextFormData);
		
		// CONCERT
		Label concert = new Label(shell, SWT.NONE);
		concert.setText("Concert: ");
		FormData concertFormData = new FormData();
		concertFormData.top = new FormAttachment(destinationText, 10);
		concertFormData.left = new FormAttachment(0, 0);
		concert.setLayoutData(concertFormData);
		
		Text concertText = new Text(shell, SWT.BORDER);
		FormData concertTextFormData = new FormData();
		concertTextFormData.width = 216;
		concertTextFormData.top = new FormAttachment(destinationText, 5);
		concertTextFormData.left = new FormAttachment(concert, 29);
		concertText.setLayoutData(concertTextFormData);
		
		// CONTACT
		Label contact = new Label(shell, SWT.NONE);
		contact.setText("Contact: ");
		FormData contactFormData = new FormData();
		contactFormData.top = new FormAttachment(concertText, 10);
		contactFormData.left = new FormAttachment(0, 0);
		contact.setLayoutData(contactFormData);
		
		Text contactText = new Text(shell, SWT.BORDER);
		FormData contactTextFormData = new FormData();
		contactTextFormData.width = 216;
		contactTextFormData.top = new FormAttachment(concertText, 5);
		contactTextFormData.left = new FormAttachment(contact, 28);
		contactText.setLayoutData(contactTextFormData);
		
		// TRANSPORT
		Label transport = new Label(shell, SWT.NONE);
		transport.setText("Transport: ");
		FormData transportFormData = new FormData();
		transportFormData.top = new FormAttachment(contactText, 10);
		transportFormData.left = new FormAttachment(0, 0);
		transport.setLayoutData(transportFormData);
		
		Text transportText = new Text(shell, SWT.BORDER);
		FormData transportTextFormData = new FormData();
		transportTextFormData.width = 216;
		transportTextFormData.top = new FormAttachment(contactText, 5);
		transportTextFormData.left = new FormAttachment(transport, 22);
		transportText.setLayoutData(transportTextFormData);
		
		// SEPARATOR
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 420;
		separator1FormData.top = new FormAttachment(transportText,10);
		separator1.setLayoutData(separator1FormData);
		
		// RESULT
		Text result = new Text(shell, SWT.BORDER);
		FormData resultTextFormData = new FormData();
		resultTextFormData.width = 310;
		resultTextFormData.height = 200;
		resultTextFormData.top = new FormAttachment(separator1, 5);
		resultTextFormData.left = new FormAttachment(0, 0);
		result.setLayoutData(resultTextFormData);
		
		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(0, 0);
		okFormData.left = new FormAttachment(85, 0);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

	}
	
	public void start(){
		shell.open();		
		while (!shell.isDisposed())
			display.readAndDispatch();
	}

	public static void main(String[] args) {
		ConcertSWT concert = new ConcertSWT();
		concert.start();
	}
}