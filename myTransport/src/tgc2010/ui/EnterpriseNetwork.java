package tgc2010.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tgc2010.core.synapse.Synapse;
import tgc2010.core.synapse.plugin.ChordNodePlugin;
import tgc2010.ui.dialog.ConsoleDialog;
import tgc2010.ui.dialog.JoinDialog;
import core.ITracker;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;

public class EnterpriseNetwork {

	private final Shell shell;
	private Display display;
	private Color red = new Color(null, 255, 0, 0);
	private Color white = new Color(null, 220, 190, 130);
	private Synapse synapse;
	private StyledText checkpoints;
	private List<Checkpoint> checkpointsList = new ArrayList<Checkpoint>();
	private Label error;

	public EnterpriseNetwork(final Synapse synapse) {
		this.synapse = synapse;
		display = Display.getDefault();
		shell = new Shell(display);

		// BACKGROUND
		Image background = new Image(display, EnterpriseNetwork.class
				//				.getResourceAsStream("studentBack.png"));
				.getResourceAsStream("enterpriseBack.png"));

		/* Init the shell */
		shell.setText("MyTransport: Enterprise Network");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setBackgroundImage(background);
		shell.setLayout(layout);

		// MENU
		Menu menuBar = new Menu(shell, SWT.BAR);
		final MenuItem joinItem = new MenuItem(menuBar, SWT.PUSH);
		joinItem.setText("Join");
		shell.setMenuBar(menuBar);
		joinItem.addListener(SWT.Selection, new MenuListener(joinItem));

		// CHECKBOXS
		final Button checkPublish = new Button(shell, SWT.CHECK);
		checkPublish.setBackgroundImage(background);
		checkPublish.setSelection(true);
		checkPublish.setText("Publish");
		FormData checkPublishFormData = new FormData();
		checkPublishFormData.top = new FormAttachment(0, 0);
		checkPublishFormData.left = new FormAttachment(0, 0);
		checkPublish.setLayoutData(checkPublishFormData);

		final Button checkPrivate = new Button(shell, SWT.CHECK);
		checkPrivate.setBackgroundImage(background);
		checkPrivate.setText("Private");
		FormData checkPrivateFormData = new FormData();
		checkPrivateFormData.top = new FormAttachment(0, 0);
		checkPrivateFormData.left = new FormAttachment(0, 290);
		checkPrivate.setLayoutData(checkPrivateFormData);

		final Button checkSearch = new Button(shell, SWT.CHECK);
		checkSearch.setBackgroundImage(background);
		checkSearch.setText("Search");
		FormData checkSearchFormData = new FormData();
		checkSearchFormData.top = new FormAttachment(checkPublish, 0);
		checkSearchFormData.left = new FormAttachment(0, 0);
		checkSearch.setLayoutData(checkSearchFormData);

		// ID
		final Label id = new Label(shell, SWT.NONE);
		id.setBackgroundImage(background);
		id.setVisible(false);
		id.setText("ID: ");
		FormData idFormData = new FormData();
		idFormData.top = new FormAttachment(checkPublish, 0);
		idFormData.left = new FormAttachment(0, 267);
		id.setLayoutData(idFormData);

		final Text idText = new Text(shell, SWT.BORDER);
		idText.setBackgroundImage(background);
		idText.setVisible(false);
		FormData idTextFormData = new FormData();
		idTextFormData.width = 60;
		idTextFormData.height = 10;
		idTextFormData.top = new FormAttachment(checkPublish, 0);
		idTextFormData.left = new FormAttachment(id, 0);
		idText.setLayoutData(idTextFormData);

		// SEPARATOR
		Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		separator.setBackgroundImage(background);
		FormData separatorFormData = new FormData();
		separatorFormData.width = 365;
		separatorFormData.top = new FormAttachment(checkSearch, 0);
		separator.setLayoutData(separatorFormData);

		// TRIP
		Label roadTrip = new Label(shell, SWT.NONE);
		roadTrip.setBackgroundImage(background);
		roadTrip.setText("Trip: ");
		FormData roadTripFormData = new FormData();
		roadTripFormData.top = new FormAttachment(separator, 4);
		roadTrip.setLayoutData(roadTripFormData);

		// DAY
		final Label day = new Label(shell, SWT.NONE);
		day.setBackgroundImage(background);
		day.setText("Day: ");
		FormData dayFormData = new FormData();
		dayFormData.top = new FormAttachment(roadTrip, 0);
		dayFormData.left = new FormAttachment(0, 0);
		day.setLayoutData(dayFormData);

		final Text dayText = new Text(shell, SWT.BORDER);
		dayText.setTextLimit(2);
		FormData dayTextFormData = new FormData();
		dayTextFormData.width = 30;
		dayTextFormData.height = 10;
		dayTextFormData.top = new FormAttachment(roadTrip, 0);
		dayTextFormData.left = new FormAttachment(day, 0);
		dayText.setLayoutData(dayTextFormData);

		final Label slash1 = new Label(shell, SWT.NONE);
		slash1.setBackgroundImage(background);
		slash1.setText("/");
		FormData slash1FormData = new FormData();
		slash1FormData.top = new FormAttachment(roadTrip, 0);
		slash1FormData.left = new FormAttachment(dayText, 5);
		slash1.setLayoutData(slash1FormData);

		final Text mounthText = new Text(shell, SWT.BORDER);
		// mounthText.setBackgroundImage(background);
		mounthText.setTextLimit(2);
		FormData mounthTextFormData = new FormData();
		mounthTextFormData.width = 30;
		mounthTextFormData.height = 10;
		mounthTextFormData.top = new FormAttachment(roadTrip, 0);
		mounthTextFormData.left = new FormAttachment(slash1, 5);
		mounthText.setLayoutData(mounthTextFormData);

		final Label slash2 = new Label(shell, SWT.NONE);
		slash2.setBackgroundImage(background);
		slash2.setText("/");
		FormData slash2FormData = new FormData();
		slash2FormData.top = new FormAttachment(roadTrip, 0);
		slash2FormData.left = new FormAttachment(mounthText, 5);
		slash2.setLayoutData(slash2FormData);

		final Text yearText = new Text(shell, SWT.BORDER);
		// yearText.setBackgroundImage(background);
		yearText.setTextLimit(4);
		FormData yearTextFormData = new FormData();
		yearTextFormData.width = 60;
		yearTextFormData.height = 10;
		yearTextFormData.top = new FormAttachment(roadTrip, 0);
		yearTextFormData.left = new FormAttachment(slash2, 5);
		yearText.setLayoutData(yearTextFormData);

		// CHECKALL
		final Button checkAll = new Button(shell, SWT.CHECK);
		checkAll.setBackgroundImage(background);
		checkAll.setText("Every");
		FormData checkAllFormData = new FormData();
		checkAllFormData.top = new FormAttachment(roadTrip, 0);
		checkAllFormData.left = new FormAttachment(0, 290);
		checkAll.setLayoutData(checkAllFormData);

		// DESTINATION
		Label roadBook = new Label(shell, SWT.NONE);
		roadBook.setBackgroundImage(background);
		roadBook.setText("Road Book: ");
		FormData destinationFormData = new FormData();
		destinationFormData.top = new FormAttachment(yearText, 5);
		destinationFormData.left = new FormAttachment(0, 0);
		roadBook.setLayoutData(destinationFormData);

		// MyTransport
		checkpoints = new StyledText(shell, SWT.NONE);
		checkpoints.setBackgroundImage(background);
		checkpoints.setEditable(false);
		checkpoints.setText("empty...");
		FormData myTransportFormData = new FormData();
		myTransportFormData.height = 110;
		myTransportFormData.width = 200;
		myTransportFormData.top = new FormAttachment(yearText, 5);
		myTransportFormData.left = new FormAttachment(roadBook, 5);
		checkpoints.setLayoutData(myTransportFormData);

		Label check = new Label(shell, SWT.NONE);
		check.setBackgroundImage(background);
		check.setText("Checkpoint:");
		FormData checkFormData = new FormData();
		checkFormData.top = new FormAttachment(checkpoints, 10);
		checkFormData.left = new FormAttachment(0, 0);
		check.setLayoutData(checkFormData);

		Label time = new Label(shell, SWT.NONE);
		time.setBackgroundImage(background);
		time.setText("Time: ");
		FormData timeFormData = new FormData();
		timeFormData.top = new FormAttachment(check, 10);
		timeFormData.left = new FormAttachment(0, 0);
		time.setLayoutData(timeFormData);

		final Text hourText = new Text(shell, SWT.BORDER);
		// hourText.setBackgroundImage(background);
		hourText.setTextLimit(2);
		FormData hourTextFormData = new FormData();
		hourTextFormData.width = 20;
		hourTextFormData.height = 10;
		hourTextFormData.top = new FormAttachment(check, 10);
		hourTextFormData.left = new FormAttachment(time, 5);
		hourText.setLayoutData(hourTextFormData);

		Label doubleColon = new Label(shell, SWT.NONE);
		doubleColon.setBackgroundImage(background);
		doubleColon.setText(":");
		FormData doubleColonFormData = new FormData();
		doubleColonFormData.top = new FormAttachment(check, 10);
		doubleColonFormData.left = new FormAttachment(hourText, 5);
		doubleColon.setLayoutData(doubleColonFormData);

		final Text minText = new Text(shell, SWT.BORDER);
		// minText.setBackgroundImage(background);
		minText.setTextLimit(2);
		FormData minTextFormData = new FormData();
		minTextFormData.width = 20;
		minTextFormData.height = 10;
		minTextFormData.top = new FormAttachment(check, 10);
		minTextFormData.left = new FormAttachment(doubleColon, 5);
		minText.setLayoutData(minTextFormData);

		// CHECKPOINTS
		Label location = new Label(shell, SWT.NONE);
		location.setBackgroundImage(background);
		location.setText("Location: ");
		FormData locationFormData = new FormData();
		locationFormData.top = new FormAttachment(check, 10);
		locationFormData.left = new FormAttachment(minText, 10);
		location.setLayoutData(locationFormData);

		final Text locationText = new Text(shell, SWT.BORDER);
		FormData locationTextFormData = new FormData();
		locationTextFormData.width = 109;
		locationTextFormData.height = 10;
		locationTextFormData.top = new FormAttachment(check, 10);
		locationTextFormData.left = new FormAttachment(location, 0);
		locationText.setLayoutData(locationTextFormData);

		final Button addCheckPoint = new Button(shell, SWT.PUSH);
		// addCheckPoint.setBackgroundImage(background);
		addCheckPoint.setText("+");
		FormData destinationTextFormData = new FormData();
		destinationTextFormData.top = new FormAttachment(check, 5);
		destinationTextFormData.left = new FormAttachment(locationText, 5);
		addCheckPoint.setLayoutData(destinationTextFormData);
		addCheckPoint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try{
					error.setVisible(false);
					int hour = Integer.parseInt(hourText.getText());
					int minute = Integer.parseInt(minText.getText());
					if(locationText.getText().equals("") || hour<0 || hour>=24 || minute<0 || minute>=60){
						throw new NumberFormatException();
					}

					String location = locationText.getText();
					checkpointsList.add(new Checkpoint(location, hour, minute));
					String checkpts = "";
					for (Checkpoint s : checkpointsList) {
						checkpts += s + "\n";
					}
					checkpoints.setText(checkpts);
					shell.pack();
				} catch (NumberFormatException excep) {
					error.setVisible(true);
				}
			}
		});

		final Button removeCheckPoint = new Button(shell, SWT.PUSH);
		// addCheckPoint.setBackgroundImage(background);
		removeCheckPoint.setText("-");
		FormData removeTextFormData = new FormData();
		removeTextFormData.top = new FormAttachment(check, 5);
		removeTextFormData.left = new FormAttachment(addCheckPoint, 2);
		removeCheckPoint.setLayoutData(removeTextFormData);
		removeCheckPoint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(checkpointsList.size() > 0) {
					checkpointsList.remove(checkpointsList.size()-1);
					String checkpts = "";
					for (Checkpoint s : checkpointsList) {
						checkpts += s + "\n";
					}
					checkpoints.setText(checkpts);
					shell.pack();
				}
			}
		});

		// SEPARATOR
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		separator1.setBackgroundImage(background);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 365;
		separator1FormData.top = new FormAttachment(removeCheckPoint, 5);
		separator1.setLayoutData(separator1FormData);

		// CONTACT
		final Label contact = new Label(shell, SWT.NONE);
		contact.setBackgroundImage(background);
		contact.setText("Contact: ");
		FormData contactFormData = new FormData();
		contactFormData.top = new FormAttachment(separator1, 5);
		contactFormData.left = new FormAttachment(0, 0);
		contact.setLayoutData(contactFormData);

		final Text contactText = new Text(shell, SWT.BORDER);
		// contactText.setBackgroundImage(background);
		FormData contactTextFormData = new FormData();
		contactTextFormData.width = 211;
		contactTextFormData.height = 10;
		contactTextFormData.top = new FormAttachment(separator1, 5);
		contactTextFormData.left = new FormAttachment(0, 145);
		contactText.setLayoutData(contactTextFormData);

		// INFORMATIONS
		final Label informations = new Label(shell, SWT.NONE);
		informations.setBackgroundImage(background);
		informations.setText("Informations: ");
		FormData transportFormData = new FormData();
		transportFormData.top = new FormAttachment(contactText, 5);
		transportFormData.left = new FormAttachment(0, 0);
		informations.setLayoutData(transportFormData);

		final Text informationsText = new Text(shell, SWT.BORDER);
		// informationsText.setBackgroundImage(background);
		FormData transportTextFormData = new FormData();
		transportTextFormData.width = 211;
		transportTextFormData.height = 10;
		transportTextFormData.top = new FormAttachment(contactText, 5);
		transportTextFormData.left = new FormAttachment(0, 145);
		informationsText.setLayoutData(transportTextFormData);

		// SEPARATOR
		Label separator2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		separator2.setBackgroundImage(background);
		FormData separator2FormData = new FormData();
		separator2FormData.width = 365;
		separator2FormData.top = new FormAttachment(informationsText, 5);
		separator2.setLayoutData(separator2FormData);

		// RESULT
		final StyledText result = new StyledText(shell, SWT.BORDER);
		result.setText(" Result:");
		result.setBackgroundImage(background);
		result.setEditable(false);
		result.setForeground(white);
		Image font = new Image(display, EnterpriseNetwork.class
				//				.getResourceAsStream("studentRes.png"));
				.getResourceAsStream("enterpriseRes.png"));
		result.setBackgroundImage(font);
		FormData resultTextFormData = new FormData();
		resultTextFormData.width = 279;
		resultTextFormData.height = 380;
		resultTextFormData.top = new FormAttachment(0, 0);
		resultTextFormData.left = new FormAttachment(0, 400);
		result.setLayoutData(resultTextFormData);

		// ERROR
		error = new Label(shell, SWT.NONE);
		error.setBackgroundImage(background);
		error.setForeground(red);
		error.setText("Date/Time: Bad format number!");
		error.setVisible(false);
		FormData errorFormData = new FormData();
		errorFormData.top = new FormAttachment(separator2, 5);
		errorFormData.left = new FormAttachment(0, 80);
		error.setLayoutData(errorFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setEnabled(false);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					error.setVisible(false);
					if(!checkAll.getSelection()){
						int day = Integer.parseInt(dayText.getText());
						int mounth = Integer.parseInt(mounthText.getText());
						int year = Integer.parseInt(yearText.getText());
						if(day<0 || day>31 || mounth<0 || mounth>12 || year<2010 ){
							throw new NumberFormatException();
						}
					}

					// Format header
					String header = "Every";
					if(!checkAll.getSelection()){
						header = dayText.getText();
						header += " / " + mounthText.getText();
						header += " / " + yearText.getText();
					}

					// Format key/value and send
					String resultStr = " Summary:\n\n\t- day: " + header + "\n\t- Trip:";
					boolean hasFound = false;
					int cpt = 0;
					for(int i = 0 ; i<checkpointsList.size() ; i++){
						for(int j = i+1 ; j<checkpointsList.size() ; j++){
							String key = header + checkpointsList.get(i).formatTokey() + checkpointsList.get(j).formatTokey();
							if (checkPublish.getSelection()) {
								String value = checkpointsList.get(i) + "+" + checkpointsList.get(j) + "+" + contactText.getText() + "+" + informationsText.getText();
								synapse.put(key,value);
								resultStr += "\n\t\t----------------------" + "\n\t\t" + checkpointsList.get(i) + "\n\t\t" + checkpointsList.get(j);
								if(i+2 >= checkpointsList.size()){
									resultStr += "\n\t\t----------------------" + "\n\n\t- contact: " + contactText.getText()
									+ "\n\t- informations: " + informationsText.getText();
									resultStr += "\n\n===> Published!";
								}
							} else {
								String found = synapse.get(key);
								if (found == null || found.equals("null")){
									key = "all" + checkpointsList.get(i).formatTokey() + checkpointsList.get(j).formatTokey();
									found = synapse.get(key);
								}
								if (found != null && !found.equals("null")){
									System.out.println(found);
									hasFound = true;
									String[] founds =  found.split("\\*\\*\\*\\*");
									for(String f : founds) {
										if (f != null && !f.equals("null")){
											String[] args = f.split("\\+");
											if(args.length == 4){
												resultStr += "\n\t\t----------------------" + "\n\t\t" + args[0] + "\n\t\t" + args[1];
												resultStr += "\n\t\t. contact: " + args[2] + "\n\t\t. informations: " + args[3];
												cpt++;
											} else {
												System.err.println("args.length != 4 on " + f);
											}
										}
									}
								}
								if(i+2 >= checkpointsList.size()){
									if(!hasFound){
										resultStr = "No result found...";
									} else {
										resultStr += (cpt > 1) ? "\n\n===> " + cpt + " results found!" : "\n\n===> " + cpt + " result found!";
									}
								}
							}
						}
					}
					result.setText(resultStr);
				} catch (NumberFormatException excep) {
					error.setVisible(true);
				} catch (Exception excep) {
					excep.printStackTrace();
				}
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(error, 5);
		okFormData.left = new FormAttachment(0, 100);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CLEAR"
		final Button clearButton = new Button(shell, SWT.PUSH);
		// clearButton.setBackgroundImage(background);
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (informationsText.getText().equals("DebugOn") || informationsText.getText().equals("debugOn")) { // DEBUG MODE!
					id.setVisible(true);
					idText.setVisible(true);
					ConsoleDialog console = new ConsoleDialog(shell,
							synapse);
					console.start();
				} else if (informationsText.getText().equals("DebugOff") || informationsText.getText().equals("debugOff")) {
					id.setVisible(false);
					idText.setVisible(false);
				} else {
					result.setText(" Result: ");
					dayText.setText("");
					mounthText.setText("");
					yearText.setText("");
					locationText.setText("");
					hourText.setText("");
					minText.setText("");
					informationsText.setText("");
					contactText.setText("");
					okButton.setEnabled(false);
					error.setVisible(false);
					checkpointsList.clear();
					checkpoints.setText("empty...");
					shell.pack();
				}
			}
		});
		FormData clearFormData = new FormData();
		clearFormData.width = 80;
		clearFormData.top = new FormAttachment(error, 5);
		clearFormData.left = new FormAttachment(okButton, 5);
		clearButton.setLayoutData(clearFormData);

		// SELECTION LISTENER
		Listener sendListener = new Listener() {
			public void handleEvent(Event event) {
				okButton.setEnabled((checkAll.getSelection() ||
						(!dayText.getText().equals("") &&
								!mounthText.getText().equals("") &&
								!yearText.getText().equals(""))) &&
								checkpointsList.size() >= 2);
			}
		};
		dayText.addListener(SWT.KeyUp, sendListener);
		mounthText.addListener(SWT.KeyUp, sendListener);
		yearText.addListener(SWT.KeyUp, sendListener);
		addCheckPoint.addListener(SWT.Selection, sendListener);
		removeCheckPoint.addListener(SWT.Selection, sendListener);

		// CHECKBOX LISTENER
		checkPublish.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				boolean checked = checkPublish.getSelection();
				checkSearch.setSelection(!checked);
				contact.setEnabled(checked);
				contactText.setEnabled(checked);
				informations.setEnabled(checked);
				informationsText.setEnabled(checked);
				checkPrivate.setEnabled(checked);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		checkSearch.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				boolean checked = checkSearch.getSelection();
				checkPublish.setSelection(!checked);
				contact.setEnabled(!checked);
				contactText.setEnabled(!checked);
				informations.setEnabled(!checked);
				informationsText.setEnabled(!checked);
				checkPrivate.setEnabled(!checked);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		checkAll.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				boolean checked = checkAll.getSelection();
				day.setEnabled(!checked);
				slash1.setEnabled(!checked);
				slash2.setEnabled(!checked);
				dayText.setEnabled(!checked);
				mounthText.setEnabled(!checked);
				yearText.setEnabled(!checked);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		shell.pack();
	}

	private class MenuListener implements Listener {
		private MenuItem item;

		public MenuListener(MenuItem item) {
			this.item = item;
		}

		public void handleEvent(Event arg0) {
			new JoinDialog(shell, synapse);
		}
	}

	public void start() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		for (IOverlay o : synapse.getNetworks()) {
			synapse.getTransport().sendRequest(
					ITracker.REMOVENODE + "," + o.getIdentifier() + ","
					+ o.getThisNode(),
					new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
		}
		synapse.getTransport().sendRequest(
				ITracker.REMOVENODE + "," + synapse.getIdentifier() + ","
				+ synapse.getThisNode(),
				new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
		synapse.kill();
		System.exit(0);
	}

	public static void main(String[] args) {
		try {
			// LAUNCHING CHORD
			System.out.print("MyTransport's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			Synapse synapse = new Synapse(ip, 0);
			ChordNodePlugin overlay = new ChordNodePlugin(ip, 0, synapse, "enterprise");

			/* TRACKER */
			// control network
			String trackerResponse = synapse.getTransport().sendRequest(
					ITracker.GETCONNECTION + "," + synapse.getIdentifier(),
					new Node(ITracker.TRACKER_HOST, 0,
							ITracker.TRACKER_PORT));
			synapse.getTransport().sendRequest(
					ITracker.ADDNODE + "," + synapse.getIdentifier() + ","
					+ synapse.getThisNode(),
					new Node(ITracker.TRACKER_HOST, 0,
							ITracker.TRACKER_PORT));
			if (!trackerResponse.equals("null")) {
				Node n = new Node(trackerResponse);
				synapse.join(n.getIp(), n.getPort());
			}

			// overlay network
			trackerResponse = synapse.getTransport().sendRequest(
					ITracker.GETCONNECTION + "," + "enterprise",
					new Node(ITracker.TRACKER_HOST, 0,
							ITracker.TRACKER_PORT));
			synapse.getNetworks().add(overlay);
			synapse.getTransport().sendRequest(
					ITracker.ADDNODE + "," + overlay.getIdentifier() + ","
					+ overlay.getThisNode(),
					new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
			if (!trackerResponse.equals("null")) {
				Node n = new Node(trackerResponse);
				overlay.join(n.getIp(), n.getPort());
			}

			System.out.println("ok!");
			Thread.sleep(300);

			EnterpriseNetwork myTransportGUI = new EnterpriseNetwork(synapse);
			myTransportGUI.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}