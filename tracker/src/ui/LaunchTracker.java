package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;

import core.Tracker;
import core.protocols.p2p.Node;

public class LaunchTracker {

	public static void main(String[] args) {
		try {
			System.out.print("Create tracker...");
			Tracker tracker = new Tracker();
			System.out.println("\t\t\t[ ok ]");

			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				try {
					System.out.println("\n\n1) print status");
					System.out.println("2) clear history");
					System.out.println("0) exit");
					System.out.print("---> ");
					int chx = Integer.parseInt(input.readLine().trim());
					switch (chx) {
					case 0:
						System.exit(0);
					case 1:
						System.out.println("\ntracker connected on : "
								+ tracker.getTransport().getPort() + "\n");
						Set<String> keys = tracker.getPeerSet().keySet();
						for (String key : keys) {
							System.out.println(key + ":");
							for (Node n : tracker.getPeerSet().get(key)) {
								System.out.println("\t" + n);
							}
						}
						break;
					case 2:
						tracker.getPeerSet().clear();
						break;
					default:
						break;
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
