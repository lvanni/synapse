package blackbox.ui.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import blackbox.core.overlay.foot.Foot;

import core.experiments.tools.InfoConsole;

public class LaunchFoot {
	public static void main(String[] args) {
		try {
			// LAUNCHING
			System.out.print("foot's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			Foot foot = new Foot(ip, Integer.parseInt(args[0]));

			// IF ARGS
			if (args.length > 1 && args[1].equals("-j")) {
				String hostToJoin = args[2];
				int portToJoin = Integer.parseInt(args[3]);
				foot.join(hostToJoin, portToJoin);
			}

			System.out.println("ok!");
			Thread.sleep(300);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			IHM: while (true) {
				InfoConsole.clearScreen();
				System.out
						.println("    _/_/_/_/                      _/           \n"
								+ "   _/        _/_/      _/_/    _/_/_/_/        \n"
								+ "  _/_/_/  _/    _/  _/    _/    _/             \n"
								+ " _/      _/    _/  _/    _/    _/              \n"
								+ "_/        _/_/      _/_/        _/_/           \n");
				System.out.println("1) Publish a football match");
				System.out.println("2) Search a football match");
				System.out.println("3) Quit");
				System.out.print("---> ");
				try {
					int chx = Integer.parseInt(input.readLine().trim());
					String key;
					switch (chx) {
					case 0:
						System.out.println("\n" + foot + "\n");
						break;
					case 1:
						System.out.print("\nDay = ");
						key = input.readLine();
						System.out.print("Destination = ");
						String key2 = input.readLine();
						System.out.print("Match = ");
						String message = input.readLine();
						System.out.print("Contact = ");
						String message2 = input.readLine();
						if (!key2.equals(""))
							foot.put(key + "+" + key2, message + "+" + message2
									+ "+car, maybe...");
						else
							foot.put(key, message + "+" + message2
									+ "+car, maybe...");
						break;
					case 2:
						System.out.print("\nDay = ");
						key = input.readLine();
						System.out.print("Destination = ");
						key2 = input.readLine();
						if (!key2.equals(""))
							System.out.println("Match found: "
									+ foot.get(key + "+" + key2));
						else
							System.out.println("Match found: " + foot.get(key));
						break;
					case 3:
						foot.kill();
					default:
						break;
					}
					System.out.println("\npress Enter to continue...");
					input.readLine();
				} catch (IOException e) {
				} catch (NumberFormatException e) {
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
