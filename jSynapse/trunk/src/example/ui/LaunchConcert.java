package example.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import example.overlay.concert.Concert;



public class LaunchConcert {
	public static void main(String[] args){
		try {
			// LAUNCHING
			System.out.print("Concert's Launching, please wait... ");
			String ip = InfoConsole.getIp();
			Concert concert = new Concert(ip, Integer.parseInt(args[0]));
			new Thread(concert).start();
			do{
				Thread.sleep(1000);
			} while(concert.getTransport() == null);

			// IF ARGS
			if(args.length > 1 && args[1].equals("-j")){
				String hostToJoin = args[2];
				int portToJoin = Integer.parseInt(args[3]);
				concert.join(hostToJoin, portToJoin);
			}

			System.out.println("ok!");
			Thread.sleep(300);

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			IHM:
				while(true){
					InfoConsole.clearScreen();
					System.out.println("                                                                 _/      \n" +     
									   "    _/_/_/    _/_/    _/_/_/      _/_/_/    _/_/    _/  _/_/  _/_/_/_/   \n" + 
									   " _/        _/    _/  _/    _/  _/        _/_/_/_/  _/_/        _/        \n" +
									   "_/        _/    _/  _/    _/  _/        _/        _/          _/         \n" + 
									   " _/_/_/    _/_/    _/    _/    _/_/_/    _/_/_/  _/            _/_/      \n");
					System.out.println("1) Publish a concert");
					System.out.println("2) Search a concert");
					System.out.println("3) Quit");
					System.out.print("---> ");
					try{
						int chx = Integer.parseInt(input.readLine().trim());
						String key;
						switch(chx){
						case 0 :
							System.out.println("\n" + concert + "\n"); break;
						case 1 :
							System.out.print("\nDay = ");
							key = input.readLine();
							System.out.print("Destination = ");
							String key2 = input.readLine();
							System.out.print("Concert = ");
							String message = input.readLine();
							System.out.print("Contact = ");
							String message2 = input.readLine();
							concert.put(key + "+" + key2, message + " contact: " + message2);
							break;
						case 2 :
							System.out.print("\nDay = ");
							key = input.readLine();
							System.out.print("Destination = ");
							key2 = input.readLine();
							System.out.println("concert found: " + concert.get(key + "+" + key2));
							break;
						case 3:
							concert.kill();
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
