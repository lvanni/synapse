package ui.console.dev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import core.experiments.tools.InfoConsole;
import core.overlay.jchord.JChord1;





public class LaunchChord1 {
	public static void main(String[] args){
		try {
			// LAUNCHING
			System.out.print("JChord1's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			JChord1 jchord1 = new JChord1(ip, Integer.parseInt(args[0]));
			new Thread(jchord1).start();
			do{
				Thread.sleep(1000);
			} while(jchord1.getTransport() == null);

			// IF ARGS
			if(args.length > 1 && args[1].equals("-j")){
				String hostToJoin = args[2];
				int portToJoin = Integer.parseInt(args[3]);
				jchord1.join(hostToJoin, portToJoin);
			}

			System.out.println("ok!");
			Thread.sleep(300);

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			IHM:
				while(true){
					InfoConsole.clearScreen();
					System.out.println("JCHORD 1:\n");
					System.out.println("1) Put");
					System.out.println("2) Get");
					System.out.println("3) Quit");
					System.out.print("---> ");
					try{
						int chx = Integer.parseInt(input.readLine().trim());
						String key;
						switch(chx){
						case 0 :
							System.out.println("\n" + jchord1 + "\n"); break;
						case 1 :
							System.out.print("\nkey = ");
							key = input.readLine();
							System.out.print("value = ");
							String message = input.readLine();
							jchord1.put(key,message);
							break;
						case 2 :
							System.out.print("\nkey = ");
							key = input.readLine();
							System.out.println("value received: " + jchord1.get(key));
							break;
						case 3:
							jchord1.kill();
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
