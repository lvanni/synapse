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
import core.ITracker;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;

public class EnterpriseEdition {

	private final Shell shell;
	private Display display;
	private Color white = new Color(null, 220, 190, 130);
	private Synapse synapse;
	private StyledText checkpoints;
	private int default_ttl = 3;
	private List<String> checkpointsList = new ArrayList<String>();

	public EnterpriseEdition(final Synapse synapse) {
		this.synapse = synapse;
		display = Display.getDefault();
		shell = new Shell(display);

		// BACKGROUND
		Image background = new Image(display, EnterpriseEdition.class
				.getResourceAsStream("studentBack.png"));
//				.getResourceAsStream("enterpriseBack.png"));

		/* Init the shell */
		shell.setText("MyTransport: Student Edition");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setBackgroundImage(background);
		shell.setLayout(layout);
		shell.setSize(703, 455);

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
		checkPrivateFormData.left = new FormAttachment(0, 270);
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
		idFormData.left = new FormAttachment(0, 272);
		id.setLayoutData(idFormData);

		final Text idText = new Text(shell, SWT.BORDER);
		idText.setBackgroundImage(background);
		idText.setVisible(false);
		FormData idTextFormData = new FormData();
		idTextFormData.width = 55;
		idTextFormData.height = 15;
		idTextFormData.top = new FormAttachment(checkPublish, 0);
		idTextFormData.left = new FormAttachment(0, 300);
		idText.setLayoutData(idTextFormData);

		// SEPARATOR
		Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
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

		// ERROR
		final Label error = new Label(shell, SWT.NONE);
		error.setBackgroundImage(background);
		// error.setForeground(EnterpriseEdition.this.error);
		error.setText("Bad format number!");
		error.setVisible(false);
		FormData errorFormData = new FormData();
		errorFormData.top = new FormAttachment(separator, 20);
		errorFormData.left = new FormAttachment(0, 200);
		error.setLayoutData(errorFormData);

		// DAY
		final Label day = new Label(shell, SWT.NONE);
		day.setBackgroundImage(background);
		day.setText("Day: ");
		FormData dayFormData = new FormData();
		dayFormData.top = new FormAttachment(roadTrip, 0);
		dayFormData.left = new FormAttachment(0, 0);
		day.setLayoutData(dayFormData);

		final Text dayText = new Text(shell, SWT.BORDER);
		// dayText.setBackgroundImage(background);
		dayText.setTextLimit(2);
		FormData dayTextFormData = new FormData();
		dayTextFormData.width = 30;
		dayTextFormData.height = 15;
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
		mounthTextFormData.height = 15;
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
		yearTextFormData.height = 15;
		yearTextFormData.top = new FormAttachment(roadTrip, 0);
		yearTextFormData.left = new FormAttachment(slash2, 5);
		yearText.setLayoutData(yearTextFormData);

		// CHECKALL
		final Button checkAll = new Button(shell, SWT.CHECK);
		checkAll.setBackgroundImage(background);
		checkAll.setText("All");
		FormData checkAllFormData = new FormData();
		checkAllFormData.top = new FormAttachment(roadTrip, 0);
		checkAllFormData.left = new FormAttachment(yearText, 80);
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
		myTransportFormData.height = 100;
		myTransportFormData.width = 200;
		myTransportFormData.top = new FormAttachment(yearText, 5);
		myTransportFormData.left = new FormAttachment(roadBook, 45);
		checkpoints.setLayoutData(myTransportFormData);

		// CHECKPOINTS
		Label location = new Label(shell, SWT.NONE);
		location.setBackgroundImage(background);
		location.setText("Location: ");
		FormData locationFormData = new FormData();
		locationFormData.top = new FormAttachment(checkpoints, 10);
		locationFormData.left = new FormAttachment(0, 0);
		location.setLayoutData(locationFormData);

		final Text locationText = new Text(shell, SWT.BORDER);
		// locationText.setBackgroundImage(background);
		locationText.setTextLimit(8);
		FormData locationTextFormData = new FormData();
		locationTextFormData.width = 60;
		locationTextFormData.height = 15;
		locationTextFormData.top = new FormAttachment(checkpoints, 10);
		locationTextFormData.left = new FormAttachment(location, 0);
		locationText.setLayoutData(locationTextFormData);

		Label time = new Label(shell, SWT.NONE);
		time.setBackgroundImage(background);
		time.setText("- Time: ");
		FormData timeFormData = new FormData();
		timeFormData.top = new FormAttachment(checkpoints, 10);
		timeFormData.left = new FormAttachment(locationText, 5);
		time.setLayoutData(timeFormData);

		final Text hourText = new Text(shell, SWT.BORDER);
		// hourText.setBackgroundImage(background);
		hourText.setTextLimit(2);
		FormData hourTextFormData = new FormData();
		hourTextFormData.width = 20;
		hourTextFormData.height = 15;
		hourTextFormData.top = new FormAttachment(checkpoints, 10);
		hourTextFormData.left = new FormAttachment(time, 5);
		hourText.setLayoutData(hourTextFormData);

		Label doubleColon = new Label(shell, SWT.NONE);
		doubleColon.setBackgroundImage(background);
		doubleColon.setText(":");
		FormData doubleColonFormData = new FormData();
		doubleColonFormData.top = new FormAttachment(checkpoints, 10);
		doubleColonFormData.left = new FormAttachment(hourText, 5);
		doubleColon.setLayoutData(doubleColonFormData);

		final Text minText = new Text(shell, SWT.BORDER);
		// minText.setBackgroundImage(background);
		minText.setTextLimit(2);
		FormData minTextFormData = new FormData();
		minTextFormData.width = 20;
		minTextFormData.height = 15;
		minTextFormData.top = new FormAttachment(checkpoints, 10);
		minTextFormData.left = new FormAttachment(doubleColon, 5);
		minText.setLayoutData(minTextFormData);

		final Button addCheckPoint = new Button(shell, SWT.PUSH);
		// addCheckPoint.setBackgroundImage(background);
		addCheckPoint.setText("+");
		FormData destinationTextFormData = new FormData();
		destinationTextFormData.top = new FormAttachment(checkpoints, 5);
		destinationTextFormData.left = new FormAttachment(minText, 5);
		addCheckPoint.setLayoutData(destinationTextFormData);
		addCheckPoint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String location = locationText.getText();
				if (location.length() < 3) {
					location += "\t\t\t\t: ";
				} else if (location.length() < 5) {
					location += "\t\t\t: ";
				} else if (location.length() < 8) {
					location += "\t\t: ";
				} else if (location.length() == 8) {
					location += "\t: ";
				} else {
					location += " : ";
				}
				checkpointsList.add("- " + location + hourText.getText() + "h"
						+ minText.getText());
				String checkpts = "";
				for (String s : checkpointsList) {
					checkpts += s + "\n";
				}
				checkpoints.setText(checkpts);
				shell.pack();
			}
		});

		// SEPARATOR
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		separator1.setBackgroundImage(background);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 365;
		separator1FormData.top = new FormAttachment(addCheckPoint, 5);
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
		contactTextFormData.height = 15;
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
		transportTextFormData.height = 15;
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
		result.setText(" No service found...");
		result.setBackgroundImage(background);
		result.setEditable(false);
		result.setForeground(white);
		Image font = new Image(display, EnterpriseEdition.class
				.getResourceAsStream("studentRes.png"));
//				.getResourceAsStream("enterpriseRes.png"));
		result.setBackgroundImage(font);
		FormData resultTextFormData = new FormData();
		resultTextFormData.width = 279;
		resultTextFormData.height = 380;
		resultTextFormData.top = new FormAttachment(0, 0);
		resultTextFormData.left = new FormAttachment(0, 400);
		result.setLayoutData(resultTextFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		// okButton.setBackgroundImage(background);
		okButton.setEnabled(false);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Integer.parseInt(locationText.getText()
							+ hourText.getText() + minText.getText());
					error.setVisible(false);
					result.setText("");
					String key1 = locationText.getText() + "/"
					+ hourText.getText() + "/" + minText.getText();
					String key2 = addCheckPoint.getText();
					String message1 = "MyTransport";
					String message2 = contactText.getText();
					String message3 = informationsText.getText();
					if (key1.equals("0/0/0")) { // DEBUG MODE!
						if (key2.equals("DebugOn") || key2.equals("debugOn")) {
							//							id.setVisible(true);
							idText.setVisible(true);
							ConsoleDialog console = new ConsoleDialog(shell,
									synapse);
							console.checkConsole();
							console.start();
						}
						if (key2.equals("DebugOff") || key2.equals("debugOff")) {
							//							id.setVisible(false);
							idText.setVisible(false);
						}
					} else {
						if (checkPublish.getSelection()) {
							if (idText.getText().equals("")) { // DEBUG MODE!
								synapse.put(key1 + "+" + key2, message1 + "+"
										+ message2 + "+" + message3);
							} else {
								synapse.put(idText.getText(), message1 + "+"
										+ message2 + "+" + message3); // Debug
							}
							result.setText("Summary:\n\t- event: " + message1
									+ "\n\t- contact: " + message2
									+ "\n\t- transport: " + message3
									+ "\n\n===> MyTransport published!");
						} else {
							String found;
							if (idText.getText().equals("")) { // DEBUG MODE!
								found = synapse.get(key1 + "+" + key2,
										default_ttl);
							} else {
								found = synapse.get(idText.getText(),
										default_ttl);
							}
							if (found == null || found.equals("null")
									|| found.equals("")) {
								result.setText("No transport found...");
							} else {
								String[] nbResult = found.split("\\*\\*\\*\\*");
								ArrayList<String> cache = new ArrayList<String>();
								for (int i = 0; i < nbResult.length; i++) {
									if (!nbResult[i].equals("")
											&& !nbResult[i].equals("null")
											&& !cache.contains(nbResult[i])) {
										String[] args = nbResult[i]
										                         .split("\\+");
										result
										.setText(result.getText()
												+ "MyTransport found:\n\t- event: "
												+ args[0]
												       + "\n\t- contact: "
												       + args[1]
												              + "\n\t- transport: "
												              + args[2] + "\n\n");
										cache.add(nbResult[i]);
									}
								}
							}
						}
					}
				} catch (NumberFormatException excep) {
					error.setVisible(true);
				} catch (Exception excep) {
					excep.printStackTrace();
				}
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(separator2, 5);
		okFormData.left = new FormAttachment(0, 100);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CLEAR"
		final Button clearButton = new Button(shell, SWT.PUSH);
		// clearButton.setBackgroundImage(background);
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
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
				checkpointsList.clear();
				checkpoints.setText("empty...");
				shell.pack();
			}
		});
		FormData clearFormData = new FormData();
		clearFormData.width = 80;
		clearFormData.top = new FormAttachment(separator2, 5);
		clearFormData.left = new FormAttachment(okButton, 5);
		clearButton.setLayoutData(clearFormData);

		// SELECTION LISTENER
		Listener sendListener = new Listener() {
			public void handleEvent(Event event) {
				okButton.setEnabled(
						(checkPublish.getSelection() || checkSearch.getSelection())
						&&
						!dayText.getText().equals("") &&
						!mounthText.getText().equals("") &&
						!yearText.getText().equals("") &&
						checkpointsList.size() != 0);
			}
		};
		dayText.addListener(SWT.KeyUp, sendListener);
		mounthText.addListener(SWT.KeyUp, sendListener);
		yearText.addListener(SWT.KeyUp, sendListener);
		addCheckPoint.addListener(SWT.Selection, sendListener);

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

	}

	private class MenuListener implements Listener {
		private MenuItem item;

		public MenuListener(MenuItem item) {
			this.item = item;
		}

		public void handleEvent(Event arg0) {
			// new AddServiceDialog(shell, item, myTransport, services);
			IOverlay overlay = null;
			String ip = synapse.getThisNode().getIp();
			String trackerResponse = "null";
			if (item.getText().equals("student")) {
				overlay = new ChordNodePlugin(ip, 0, synapse, "student");
				trackerResponse = synapse.getTransport().sendRequest(
						ITracker.GETCONNECTION + "," + "student",
						new Node(ITracker.TRACKER_HOST, 0,
								ITracker.TRACKER_PORT));
			} else {
				overlay = new ChordNodePlugin(ip, 0, synapse, "enterprise");
				trackerResponse = synapse.getTransport().sendRequest(
						ITracker.GETCONNECTION + "," + "enterprise",
						new Node(ITracker.TRACKER_HOST, 0,
								ITracker.TRACKER_PORT));
			}
			synapse.getNetworks().add(overlay);

			// CONNECT ON TRACKER
			synapse.getTransport().sendRequest(
					ITracker.ADDNODE + "," + overlay.getIdentifier() + ","
					+ overlay.getThisNode(),
					new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
			if (!trackerResponse.equals("null")) {
				Node n = new Node(trackerResponse);
				System.out.println("join to " + trackerResponse);
				overlay.join(n.getIp(), n.getPort());
			}
			System.out.println("join ok!");

			// Set GUI Text
			checkpoints.setForeground(new Color(null, 0, 180, 0));
			String text = "Services enabled: ";
			for (IOverlay o : synapse.getNetworks()) {
				text += o.getIdentifier() + "["
				+ ((AbstractChord) o).getThisNode().getId() + ","
				+ ((AbstractChord) o).getPredecessor().getId() + "], ";
			}
			checkpoints.setText(text);
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
	}

	public static void main(String[] args) {
		try {
			// LAUNCHING CHORD
			System.out.print("MyTransport's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			Synapse synapse = new Synapse(ip, 0);

			// IF ARGS
			if (args.length > 1 && args[1].equals("-j")) {
				String hostToJoin = args[2];
				int portToJoin = Integer.parseInt(args[3]);
				synapse.join(hostToJoin, portToJoin);
			} else {

				// CONNECT ON TRACKER
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
			}

			System.out.println("ok!");
			Thread.sleep(300);

			EnterpriseEdition myTransportGUI = new EnterpriseEdition(synapse);
			myTransportGUI.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}