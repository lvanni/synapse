package ui.gui.other;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ConcertSWING{

	public JPanel createContentPane (){

		// We create a bottom JPanel to place everything on.
		JPanel concertGUI = new JPanel();
		concertGUI.setLayout(null);

		JCheckBox publish = new JCheckBox();
		publish.setLocation(10, 1);
		publish.setSize(20, 20);
		publish.setHorizontalAlignment(0);
		concertGUI.add(publish);

		JLabel publishLabel = new JLabel("publish a concert");
		publishLabel.setLocation(20, 0);
		publishLabel.setSize(150, 20);
		publishLabel.setHorizontalAlignment(0);
		concertGUI.add(publishLabel);
		
		JCheckBox search = new JCheckBox();
		search.setLocation(10, 20);
		search.setSize(20, 20);
		search.setHorizontalAlignment(0);
		concertGUI.add(search);

		JLabel searchLabel = new JLabel("search a concert");
		searchLabel.setLocation(20, 15);
		searchLabel.setSize(150, 30);
		searchLabel.setHorizontalAlignment(0);
		concertGUI.add(searchLabel);

		JLabel day = new JLabel("Day (jj/mm/yyyy): ");
		day.setLocation(0, 39);
		day.setSize(150, 30);
		day.setHorizontalAlignment(0);
		concertGUI.add(day);

		JTextField dayT = new JTextField();
		dayT.setLocation(135, 46);
		dayT.setSize(50, 20);
		dayT.setHorizontalAlignment(0);
		concertGUI.add(dayT);
		
		JLabel slash1 = new JLabel(" / ");
		slash1.setLocation(185, 39);
		slash1.setSize(20, 30);
		slash1.setHorizontalAlignment(0);
		concertGUI.add(slash1);

		JTextField monthT = new JTextField();
		monthT.setLocation(205, 46);
		monthT.setSize(50, 20);
		monthT.setHorizontalAlignment(0);
		concertGUI.add(monthT);

		JLabel slash2 = new JLabel(" / ");
		slash2.setLocation(255, 39);
		slash2.setSize(20, 30);
		slash2.setHorizontalAlignment(0);
		concertGUI.add(slash2);

		JTextField yearT = new JTextField();
		yearT.setLocation(275, 46);
		yearT.setSize(60, 20);
		yearT.setHorizontalAlignment(0);
		concertGUI.add(yearT);
		
		JLabel dest = new JLabel("Destination: ");
		dest.setLocation(-14, 60);
		dest.setSize(150, 30);
		dest.setHorizontalAlignment(0);
		concertGUI.add(dest);

		JTextField destT = new JTextField();
		destT.setLocation(135, 66);
		destT.setSize(200, 20);
		destT.setHorizontalAlignment(0);
		concertGUI.add(destT);
		
		JLabel concert = new JLabel("Concert: ");
		concert.setLocation(-28, 81);
		concert.setSize(150, 30);
		concert.setHorizontalAlignment(0);
		concertGUI.add(concert);

		JTextField concertT = new JTextField();
		concertT.setLocation(135, 86);
		concertT.setSize(200, 20);
		concertT.setHorizontalAlignment(0);
		concertGUI.add(concertT);
		
		JLabel contact = new JLabel("Contact: ");
		contact.setLocation(-28, 101);
		contact.setSize(150, 30);
		contact.setHorizontalAlignment(0);
		concertGUI.add(contact);

		JTextField contactT = new JTextField();
		contactT.setLocation(135, 106);
		contactT.setSize(200, 20);
		contactT.setHorizontalAlignment(0);
		concertGUI.add(contactT);
		
		JLabel transport = new JLabel("Transport: ");
		transport.setLocation(-18, 121);
		transport.setSize(150, 30);
		transport.setHorizontalAlignment(0);
		concertGUI.add(transport);

		JTextField transportT = new JTextField();
		transportT.setLocation(135, 126);
		transportT.setSize(200, 20);
		transportT.setHorizontalAlignment(0);
		concertGUI.add(transportT);
		
		JButton pSend = new JButton();
		pSend.setText("Send");
		pSend.setLocation(255, 5);
		pSend.setSize(80, 20);
		pSend.setHorizontalAlignment(0);
		concertGUI.add(pSend);
		
		JSeparator separator = new JSeparator();
		separator.setLocation(0, 155);
		separator.setSize(355, 2);
		concertGUI.add(separator);
		
		JPanel result = new JPanel();
		result.setLayout(null);
		result.setLocation(5, 165);
		result.setSize(336, 200);
		result.setBackground(Color.white);
		concertGUI.add(result);
		
		JLabel lResult = new JLabel("result: ");
		lResult.setLocation(-5, 0);
		lResult.setSize(80, 20);
		lResult.setHorizontalAlignment(0);
		result.add(lResult);
		
		concertGUI.setOpaque(true);
		return concertGUI;
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Concert");
		frame.setContentPane(new ConcertSWING().createContentPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(355, 400);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	} 
}