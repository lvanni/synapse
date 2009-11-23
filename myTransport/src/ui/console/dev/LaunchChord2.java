package ui.console.dev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ui.console.InfoConsole;

import core.overlay.jchord.JChord2;





public class LaunchChord2 {
	public static void main(String[] args){
		try {
			// LAUNCHING
			System.out.print("JChord2's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			JChord2 jchord2 = new JChord2(ip, Integer.parseInt(args[0]));
			new Thread(jchord2).start();
			do{
				Thread.sleep(1000);
			} while(jchord2.getTransport() == null);

			// IF ARGS
			if(args.length > 1 && args[1].equals("-j")){
				String hostToJoin = args[2];
				int portToJoin = Integer.parseInt(args[3]);
				jchord2.join(hostToJoin, portToJoin);
			}

			System.out.println("ok!");
			Thread.sleep(300);

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			IHM:
				while(true){
					InfoConsole.clearScreen();
					System.out.println("JCHORD 2:\n");
					System.out.println("1) Put");
					System.out.println("2) Get");
					System.out.println("3) Quit");
					System.out.print("---> ");
					try{
						int chx = Integer.parseInt(input.readLine().trim());
						String key;
						switch(chx){
						case 0 :
							System.out.println("\n" + jchord2 + "\n"); break;
						case 1 :
							System.out.print("\nkey = ");
							key = input.readLine();
							System.out.print("value = ");
							String message = input.readLine();
							jchord2.put(key,message);
							break;
						case 2 :
							System.out.print("\nkey = ");
							key = input.readLine();
							System.out.println("value received: " + jchord2.get(key));
							break;
						case 3:
							jchord2.kill();
						default : break;
						}
						System.out.println("\npress Enter to continue...");
						input.readLine();
					} catch (IOException e) { 
					} catch (NumberFormatException e) {}
				}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
}
