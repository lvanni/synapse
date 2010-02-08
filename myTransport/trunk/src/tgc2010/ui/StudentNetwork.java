package tgc2010.ui;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import core.ITracker;

public class StudentNetwork {
	public static void main(String[] args) {
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
		CarPal.launchCarPal(trackerHost, trackerPort, "student", "studentBack.png", "studentRes.png");
	}
}
