package tgc2010.ui;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tgc2010.ui.dialog.ConsoleDialog;
import tgc2010.ui.dialog.JoinDialog;
import tgc2010.ui.dialog.LocateDialog;
import tgc2010.ui.tool.GeoLoc;
import tgc2010.ui.tool.Value;
import core.ITracker;
import core.protocols.p2p.IDHT;
import core.protocols.p2p.Node;
import core.tools.InfoConsole;
import experiments.current.synapse.Synapse;
import experiments.current.synapse.plugin.ChordNodePlugin;

/**
 * This is a GUI of a simple application based on the DHT architecture
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class EnterpriseNetwork {

	private final Shell shell;
	private Display display;
	private Color red = new Color(null, 255, 0, 0);
	private Synapse synapse;
	private Button locate;
	private StyledText result;
	private Browser browser = null;
	private List<Checkpoint> checkpointsList = new ArrayList<Checkpoint>();
	private Label error;

	public EnterpriseNetwork(final Synapse synapse) {
		this.synapse = synapse;
		display = Display.getDefault();
		shell = new Shell(display);

		// BACKGROUND
		Image background = new Image(display, EnterpriseNetwork.class
		// .getResourceAsStream("studentBack.png"));
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
		joinItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				new JoinDialog(shell, synapse);
			}
		});
		final MenuItem locateItem = new MenuItem(menuBar, SWT.PUSH);
		locateItem.setText("Locate");
		shell.setMenuBar(menuBar);
		locateItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				new LocateDialog(shell);
			}
		});
		final MenuItem hideItem = new MenuItem(menuBar, SWT.PUSH);
		hideItem.setText("HideMap");
		shell.setMenuBar(menuBar);
		hideItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				if (locate.getEnabled()) {
					locate.setEnabled(false);
					locateItem.setEnabled(false);
					result.setVisible(true);
					hideItem.setText("showMap");
				} else {
					locate.setEnabled(true);
					locateItem.setEnabled(true);
					result.setVisible(false);
					hideItem.setText("HideMap");
				}
			}
		});

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
		checkPrivateFormData.left = new FormAttachment(0, 300);
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
		idTextFormData.height = 15;
		idTextFormData.top = new FormAttachment(checkPublish, 0);
		idTextFormData.left = new FormAttachment(id, 0);
		idText.setLayoutData(idTextFormData);

		// SEPARATOR
		Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		separator.setBackgroundImage(background);
		FormData separatorFormData = new FormData();
		separatorFormData.width = 380;
		separatorFormData.top = new FormAttachment(checkSearch, 10);
		separator.setLayoutData(separatorFormData);

		// TRIP
		// Label roadTrip = new Label(shell, SWT.NONE);
		// roadTrip.setBackgroundImage(background);
		// roadTrip.setText("Trip: ");
		// FormData roadTripFormData = new FormData();
		// roadTripFormData.top = new FormAttachment(separator, 4);
		// roadTrip.setLayoutData(roadTripFormData);

		// DAY
		final Label day = new Label(shell, SWT.NONE);
		day.setBackgroundImage(background);
		day.setText("*Day: ");
		FormData dayFormData = new FormData();
		dayFormData.top = new FormAttachment(separator, 10);
		dayFormData.left = new FormAttachment(0, 0);
		day.setLayoutData(dayFormData);

		final Text dayText = new Text(shell, SWT.BORDER);
		dayText.setTextLimit(2);
		FormData dayTextFormData = new FormData();
		dayTextFormData.width = 20;
		dayTextFormData.height = 15;
		dayTextFormData.top = new FormAttachment(separator, 10);
		dayTextFormData.left = new FormAttachment(0, 130);
		dayText.setLayoutData(dayTextFormData);

		final Label slash1 = new Label(shell, SWT.NONE);
		slash1.setBackgroundImage(background);
		slash1.setText("/");
		FormData slash1FormData = new FormData();
		slash1FormData.top = new FormAttachment(separator, 10);
		slash1FormData.left = new FormAttachment(dayText, 0);
		slash1.setLayoutData(slash1FormData);

		final Text mounthText = new Text(shell, SWT.BORDER);
		// mounthText.setBackgroundImage(background);
		mounthText.setTextLimit(2);
		FormData mounthTextFormData = new FormData();
		mounthTextFormData.width = 20;
		mounthTextFormData.height = 15;
		mounthTextFormData.top = new FormAttachment(separator, 10);
		mounthTextFormData.left = new FormAttachment(slash1, 0);
		mounthText.setLayoutData(mounthTextFormData);

		final Label slash2 = new Label(shell, SWT.NONE);
		slash2.setBackgroundImage(background);
		slash2.setText("/");
		FormData slash2FormData = new FormData();
		slash2FormData.top = new FormAttachment(separator, 10);
		slash2FormData.left = new FormAttachment(mounthText, 0);
		slash2.setLayoutData(slash2FormData);

		final Text yearText = new Text(shell, SWT.BORDER);
		// yearText.setBackgroundImage(background);
		yearText.setTextLimit(4);
		FormData yearTextFormData = new FormData();
		yearTextFormData.width = 40;
		yearTextFormData.height = 15;
		yearTextFormData.top = new FormAttachment(separator, 10);
		yearTextFormData.left = new FormAttachment(slash2, 0);
		yearText.setLayoutData(yearTextFormData);

		// CHECKALL
		final Button checkAll = new Button(shell, SWT.CHECK);
		checkAll.setBackgroundImage(background);
		checkAll.setText("Every");
		FormData checkAllFormData = new FormData();
		checkAllFormData.top = new FormAttachment(separator, 10);
		checkAllFormData.left = new FormAttachment(0, 300);
		checkAll.setLayoutData(checkAllFormData);

		// CHECKPOINTS
		final Label address = new Label(shell, SWT.NONE);
		address.setBackgroundImage(background);
		address.setText("Address: ");
		FormData addressFormData = new FormData();
		addressFormData.top = new FormAttachment(yearText, 0);
		addressFormData.left = new FormAttachment(0, 0);
		address.setLayoutData(addressFormData);

		final Text addressText = new Text(shell, SWT.BORDER);
		FormData addressTextFormData = new FormData();
		addressTextFormData.width = 160;
		addressTextFormData.height = 15;
		addressTextFormData.top = new FormAttachment(yearText, 0);
		addressTextFormData.left = new FormAttachment(0, 130);
		addressText.setLayoutData(addressTextFormData);

		final Label zip = new Label(shell, SWT.NONE);
		zip.setBackgroundImage(background);
		zip.setText("Zip Code: ");
		FormData zipFormData = new FormData();
		zipFormData.top = new FormAttachment(addressText, 0);
		zipFormData.left = new FormAttachment(0, 0);
		zip.setLayoutData(zipFormData);

		final Text zipText = new Text(shell, SWT.BORDER);
		FormData zipTextFormData = new FormData();
		zipTextFormData.width = 160;
		zipTextFormData.height = 15;
		zipTextFormData.top = new FormAttachment(addressText, 0);
		zipTextFormData.left = new FormAttachment(0, 130);
		zipText.setLayoutData(zipTextFormData);

		Label city = new Label(shell, SWT.NONE);
		city.setBackgroundImage(background);
		city.setText("*City: ");
		FormData cityFormData = new FormData();
		cityFormData.top = new FormAttachment(zipText, 0);
		cityFormData.left = new FormAttachment(0, 0);
		city.setLayoutData(cityFormData);

		final Text cityText = new Text(shell, SWT.BORDER);
		FormData cityTextFormData = new FormData();
		cityTextFormData.width = 160;
		cityTextFormData.height = 15;
		cityTextFormData.top = new FormAttachment(zipText, 0);
		cityTextFormData.left = new FormAttachment(0, 130);
		cityText.setLayoutData(cityTextFormData);

		Label time = new Label(shell, SWT.NONE);
		time.setBackgroundImage(background);
		time.setText("*Time: ");
		FormData timeFormData = new FormData();
		timeFormData.top = new FormAttachment(cityText, 0);
		timeFormData.left = new FormAttachment(0, 0);
		time.setLayoutData(timeFormData);

		final Text hourText = new Text(shell, SWT.BORDER);
		// hourText.setBackgroundImage(background);
		hourText.setTextLimit(2);
		FormData hourTextFormData = new FormData();
		hourTextFormData.width = 20;
		hourTextFormData.height = 15;
		hourTextFormData.top = new FormAttachment(cityText, 0);
		hourTextFormData.left = new FormAttachment(0, 130);
		hourText.setLayoutData(hourTextFormData);

		Label doubleColon = new Label(shell, SWT.NONE);
		doubleColon.setBackgroundImage(background);
		doubleColon.setText(":");
		FormData doubleColonFormData = new FormData();
		doubleColonFormData.top = new FormAttachment(cityText, 0);
		doubleColonFormData.left = new FormAttachment(hourText, 0);
		doubleColon.setLayoutData(doubleColonFormData);

		final Text minText = new Text(shell, SWT.BORDER);
		// minText.setBackgroundImage(background);
		minText.setTextLimit(2);
		FormData minTextFormData = new FormData();
		minTextFormData.width = 20;
		minTextFormData.height = 15;
		minTextFormData.top = new FormAttachment(cityText, 0);
		minTextFormData.left = new FormAttachment(doubleColon, 0);
		minText.setLayoutData(minTextFormData);

		final Button addCheckPoint = new Button(shell, SWT.PUSH);
		addCheckPoint.setText("Add");
		FormData destinationTextFormData = new FormData();
		destinationTextFormData.top = new FormAttachment(minText, 0);
		destinationTextFormData.left = new FormAttachment(0, 130);
		addCheckPoint.setLayoutData(destinationTextFormData);

		final Button removeCheckPoint = new Button(shell, SWT.PUSH);
		removeCheckPoint.setText("Remove");
		FormData removeTextFormData = new FormData();
		removeTextFormData.top = new FormAttachment(minText, 0);
		removeTextFormData.left = new FormAttachment(addCheckPoint, 0);
		removeCheckPoint.setLayoutData(removeTextFormData);

		locate = new Button(shell, SWT.PUSH);
		locate.setText("Locate");
		FormData locateFormData = new FormData();
		locateFormData.top = new FormAttachment(minText, 0);
		locateFormData.left = new FormAttachment(removeCheckPoint, 0);
		locate.setLayoutData(locateFormData);

		// SEPARATOR
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		separator1.setBackgroundImage(background);
		FormData separator1FormData = new FormData();
		separator1FormData.width = 380;
		separator1FormData.top = new FormAttachment(removeCheckPoint, 10);
		separator1.setLayoutData(separator1FormData);

		// CONTACT
		final Label contact = new Label(shell, SWT.NONE);
		contact.setBackgroundImage(background);
		contact.setText("*Contact: ");
		FormData contactFormData = new FormData();
		contactFormData.top = new FormAttachment(separator1, 10);
		contactFormData.left = new FormAttachment(0, 0);
		contact.setLayoutData(contactFormData);

		final Text contactText = new Text(shell, SWT.BORDER);
		// contactText.setBackgroundImage(background);
		FormData contactTextFormData = new FormData();
		contactTextFormData.width = 211;
		contactTextFormData.height = 15;
		contactTextFormData.top = new FormAttachment(separator1, 10);
		contactTextFormData.left = new FormAttachment(0, 130);
		contactText.setLayoutData(contactTextFormData);

		// INFORMATIONS
		final Label informations = new Label(shell, SWT.NONE);
		informations.setBackgroundImage(background);
		informations.setText("Information: ");
		FormData transportFormData = new FormData();
		transportFormData.top = new FormAttachment(contactText, 0);
		transportFormData.left = new FormAttachment(0, 0);
		informations.setLayoutData(transportFormData);

		final Text informationsText = new Text(shell, SWT.BORDER);
		informationsText.setToolTipText("Number of seats, vehicule type, ...");
		// informationsText.setBackgroundImage(background);
		FormData transportTextFormData = new FormData();
		transportTextFormData.width = 211;
		transportTextFormData.height = 15;
		transportTextFormData.top = new FormAttachment(contactText, 0);
		transportTextFormData.left = new FormAttachment(0, 130);
		informationsText.setLayoutData(transportTextFormData);

		// SEPARATOR
		Label separator2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_SOLID);
		separator2.setBackgroundImage(background);
		FormData separator2FormData = new FormData();
		separator2FormData.width = 380;
		separator2FormData.top = new FormAttachment(informationsText, 10);
		separator2.setLayoutData(separator2FormData);

		// RESULT
		result = new StyledText(shell, SWT.BORDER);
		result.setVisible(false);
		result.setText(" Road Book: \n\n\tEmpty...");
		result.setBackgroundImage(background);
		result.setEditable(false);
		// result.setForeground(white);
		Image font = new Image(display, EnterpriseNetwork.class
		// .getResourceAsStream("studentRes.png"));
				.getResourceAsStream("enterpriseRes.png"));
		result.setBackgroundImage(font);
		FormData resultTextFormData = new FormData();
		resultTextFormData.width = 279;
		resultTextFormData.height = 380;
		resultTextFormData.top = new FormAttachment(0, 0);
		resultTextFormData.left = new FormAttachment(0, 400);
		result.setLayoutData(resultTextFormData);

		// HIDDEN BROWSER
		final Composite composite = new Composite(shell, SWT.BORDER);
		FormData compositeFormData = new FormData();
		compositeFormData.width = 279;
		compositeFormData.height = 380;
		compositeFormData.top = new FormAttachment(0, 0);
		compositeFormData.left = new FormAttachment(0, 400);
		composite.setLayoutData(compositeFormData);
		FormLayout compositeFormLayout = new FormLayout();
		composite.setLayout(compositeFormLayout);

		try {
			browser = new Browser(composite, SWT.NONE);
			browser
					.setUrl("http://maps.google.fr/maps?f=q&hl=fr&q=%20,%20%20nice");
			FormData browserFormData = new FormData();
			browserFormData.width = 665;
			// browserFormData.height = 500;
			browserFormData.height = 565;
			browserFormData.top = new FormAttachment(0, -185);
			// browserFormData.top = new FormAttachment(0, -120);
			browserFormData.left = new FormAttachment(0, -385);
			browser.setLayoutData(browserFormData);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
			locate.setEnabled(false);
			locateItem.setEnabled(false);
			hideItem.setEnabled(false);
			result.setVisible(true);
		}

		// CRUD ROADBOOK
		addCheckPoint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					error.setVisible(false);
					int hour = Integer.parseInt(hourText.getText());
					int minute = Integer.parseInt(minText.getText());
					if (cityText.getText().equals("") || hour < 0 || hour >= 24
							|| minute < 0 || minute >= 60) {
						throw new NumberFormatException();
					}
					String address = addressText.getText();
					String zipCode = zipText.getText();
					String city = cityText.getText();
					checkpointsList.add(new Checkpoint(address, zipCode, city,
							hour, minute));
					updateRoadBook();
					composite.setVisible(false);
					result.setVisible(true);
				} catch (NumberFormatException excep) {
					error.setVisible(true);
				}
			}
		});
		removeCheckPoint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (checkpointsList.size() > 0) {
					checkpointsList.remove(checkpointsList.size() - 1);
					updateRoadBook();
					composite.setVisible(false);
					result.setVisible(true);
				}
			}
		});
		locate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String address = addressText.getText();
				String zipCode = zipText.getText();
				String city = cityText.getText();
				GeoLoc.search(browser, address, zipCode, city);
				composite.setVisible(true);
				result.setVisible(false);
			}
		});

		// ERROR
		error = new Label(shell, SWT.NONE);
		error.setBackgroundImage(background);
		error.setForeground(red);
		error.setText("Date/Time: Bad format number!");
		error.setVisible(false);
		FormData errorFormData = new FormData();
		errorFormData.top = new FormAttachment(separator2, 0);
		errorFormData.left = new FormAttachment(0, 80);
		error.setLayoutData(errorFormData);

		// button "SEND"
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setEnabled(false);
		okButton.setText("Send");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							error.setVisible(false);
							if (!checkAll.getSelection()) {
								int day = Integer.parseInt(dayText.getText());
								int mounth = Integer.parseInt(mounthText
										.getText());
								int year = Integer.parseInt(yearText.getText());
								if (day < 0 || day > 31 || mounth < 0
										|| mounth > 12 || year < 2010) {
									throw new NumberFormatException();
								}
							}

							// Format header
							String header = "Every";
							if (!checkAll.getSelection()) {
								header = dayText.getText();
								header += "/" + mounthText.getText();
								header += "/" + yearText.getText();
							}

							// Format key/value and send
							String resultStr = " Summary:\n\n\tDay: " + header
									+ "\n";
							boolean hasFound = false;
							int cpt = 0;

							for (int i = 0; i < checkpointsList.size(); i++) {
								for (int j = i + 1; j < checkpointsList.size(); j++) {
									String key = header
											+ checkpointsList.get(i)
													.formatToKey()
											+ checkpointsList.get(j)
													.formatToKey();
									if (checkPublish.getSelection()) {
										String value = Value.serializeValue(
												checkpointsList.get(i),
												checkpointsList.get(j),
												contactText.getText(),
												informationsText.getText(),
												"\tENTERPRISE NETWORK");
										synapse.put(key, value);
										resultStr += Value
												.deserializeValue(value)
												+ "\n";
										if (i + 2 >= checkpointsList.size()) {
											resultStr += "\n\n===> Published!";
										}
									} else {
										String found = synapse.get(key);
										if ((found == null
												|| found.equals("null") || found
												.split("\\+").length < 4)
												&& !key.equals("Every")) {
											key = "Every"
													+ checkpointsList.get(i)
															.formatToKey()
													+ checkpointsList.get(j)
															.formatToKey();
											found = synapse.get(key);
										}
										if (found != null
												&& !found.equals("null")
												&& found.split("\\+").length >= 4) {
											System.out.println(found);
											hasFound = true;
											String[] founds = found
													.split("\\*\\*\\*\\*");
											for (String f : founds) {
												if (f != null
														&& !f.equals("null")) {
													resultStr += Value
															.deserializeValue(f)
															+ "\n";
													cpt++;
												}
											}
										}
										if (i + 2 >= checkpointsList.size()) {
											if (!hasFound) {
												resultStr = "No result found...";
											} else {
												resultStr += (cpt > 1) ? "\n\n===> "
														+ cpt
														+ " results found!"
														: "\n\n===> "
																+ cpt
																+ " result found!";
											}
										}
									}
									result.setText(resultStr);
									result.redraw();
									display.update();
								}
							}
						}
					});
				} catch (NumberFormatException excep) {
					error.setVisible(true);
				} catch (Exception excep) {
					excep.printStackTrace();
				}
			}
		});
		FormData okFormData = new FormData();
		okFormData.width = 80;
		okFormData.top = new FormAttachment(error, 0);
		okFormData.left = new FormAttachment(0, 100);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CLEAR"
		final Button clearButton = new Button(shell, SWT.PUSH);
		// clearButton.setBackgroundImage(background);
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (informationsText.getText().equals("DebugOn")
						|| informationsText.getText().equals("debugOn")) { // DEBUG
					// MODE!
					id.setVisible(true);
					idText.setVisible(true);
					ConsoleDialog console = new ConsoleDialog(shell, synapse);
					console.start();
				} else if (informationsText.getText().equals("DebugOff")
						|| informationsText.getText().equals("debugOff")) {
					id.setVisible(false);
					idText.setVisible(false);
				} else {
					result.setText(" Road Book: \n\n\tEmpty");
					dayText.setText("");
					mounthText.setText("");
					yearText.setText("");
					cityText.setText("");
					hourText.setText("");
					minText.setText("");
					informationsText.setText("");
					contactText.setText("");
					okButton.setEnabled(false);
					error.setVisible(false);
					checkpointsList.clear();
					composite.setVisible(true);
					if (locate.getEnabled())
						result.setVisible(false);
					shell.pack();
				}
			}
		});
		FormData clearFormData = new FormData();
		clearFormData.width = 80;
		clearFormData.top = new FormAttachment(error, 0);
		clearFormData.left = new FormAttachment(okButton, 5);
		clearButton.setLayoutData(clearFormData);

		// SELECTION LISTENER
		Listener sendListener = new Listener() {
			public void handleEvent(Event event) {
				okButton.setEnabled((checkAll.getSelection() || (!dayText
						.getText().equals("")
						&& !mounthText.getText().equals("") && !yearText
						.getText().equals("")))
						&& checkpointsList.size() >= 2);
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
				address.setEnabled(checked);
				addressText.setEnabled(checked);
				zip.setEnabled(checked);
				zipText.setEnabled(checked);
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
				address.setEnabled(!checked);
				addressText.setEnabled(!checked);
				zip.setEnabled(!checked);
				zipText.setEnabled(!checked);
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

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		shell.pack();
	}

	public void updateRoadBook() {
		String checkpts = " Road Book: \n";
		for (Checkpoint s : checkpointsList) {
			checkpts += "\n\t" + s.toString() + "\n";
		}
		result.setText(checkpts);
		shell.pack();
	}

	public void start() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		for (IDHT o : synapse.getNetworks()) {
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
			ChordNodePlugin overlay = new ChordNodePlugin(ip, 0, synapse,
					"enterprise");

			// CONFIGURE THE TRACKER ROUTE
			String trackerHost = ITracker.TRACKER_HOST;
			int trackerPort = ITracker.TRACKER_PORT;
			if (args.length == 2) { // if there is a tracker configuration file
				if (args[0].equals("-tc")
						|| args[0].equals("--trackerConfiguration")) {
					File file = new File(args[1]);
					FileInputStream fis = null;
					BufferedInputStream bis = null;
					DataInputStream dis = null;
					try {
						fis = new FileInputStream(file);
						bis = new BufferedInputStream(fis);
						dis = new DataInputStream(bis);
						String[] address = dis.readLine().split(":");
						trackerHost = address[0];
						trackerPort = Integer.parseInt(address[1]);
						fis.close();
						bis.close();
						dis.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.err
							.println("wrong parametter: (-tc | --trackerConfiguration) <fileName>");
				}
			}

			// CONNECT TO THE TRACKER
			// control network
			String trackerResponse = synapse.getTransport().sendRequest(
					ITracker.GETCONNECTION + "," + synapse.getIdentifier(),
					new Node(trackerHost, 0, trackerPort));
			synapse.getTransport().sendRequest(
					ITracker.ADDNODE + "," + synapse.getIdentifier() + ","
							+ synapse.getThisNode(),
					new Node(trackerHost, 0, trackerPort));
			if (!trackerResponse.equals("null")) {
				Node n = new Node(trackerResponse);
				synapse.join(n.getIp(), n.getPort());
			}

			// overlay network
			trackerResponse = synapse.getTransport().sendRequest(
					ITracker.GETCONNECTION + "," + "enterprise",
					new Node(trackerHost, 0, trackerPort));
			synapse.getNetworks().add(overlay);
			synapse.getTransport().sendRequest(
					ITracker.ADDNODE + "," + overlay.getIdentifier() + ","
							+ overlay.getThisNode(),
					new Node(trackerHost, 0, trackerPort));
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