package ui.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.experiments.tools.InfoConsole;
import core.mytansport.MyTransport;
import core.mytansport.plugins.MyConcert;
import core.mytansport.plugins.MyFoot;
import core.protocols.p2p.IOverlay;

public class LaunchMyTransport {
	public static void main(String[] args){
		String ip, hostToJoin;
		int portToJoin, chx, port;
		try {
			// LAUNCHING
			System.out.print("MyTransport's Launching, please wait... ");
			ip = InfoConsole.getIp();
			MyTransport myTransport = new MyTransport(ip, Integer.parseInt(args[0]));
			new Thread(myTransport).start();
			do{
				Thread.sleep(1000);
			} while(myTransport.getTransport() == null);

			// IF ARGS
			if(args.length > 1 && args[1].equals("-j")){
				hostToJoin = args[2];
				portToJoin = Integer.parseInt(args[3]);
				myTransport.join(hostToJoin, portToJoin);
			}

			System.out.println("ok!");
			Thread.sleep(300);

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			boolean subMenu = false;
			IHM:
				while(true){
					int i=1;
					InfoConsole.clearScreen();
					System.out.println("   __  ___    ______                                __  \n" +
							"  /  |/  /_ _/_  __/______ ____  ___ ___  ___  ____/ /_ \n" +
							" / /|_/ / // // / / __/ _ `/ _ \\(_-</ _ \\/ _ \\/ __/ __/ \n" +
							"/_/  /_/\\_, //_/ /_/  \\_,_/_//_/___/ .__/\\___/_/  \\__/  \n" +
					"       /___/                      /_/                                \n");

					System.out.println("1) Add a new service ");
					if(subMenu){
						try{
							System.out.println("\t1) Concert");
							System.out.println("\t2) Foot");
							System.out.println("\t3) Cancel");
							System.out.print("\t---> ");
							chx = Integer.parseInt(input.readLine().trim());
							switch(chx){
							case 1:
								System.out.print("\tchoose a port number to start MyConcert: ");
								port = Integer.parseInt(input.readLine());
								System.out.print("\tMyConcert host to join = ");
								hostToJoin = input.readLine();
								System.out.print("\tMyConcert port to join = ");
								portToJoin = Integer.parseInt(input.readLine());
								MyConcert myConcert = new MyConcert(ip, 0, myTransport);
								new Thread(myConcert).start();
								Thread.yield();
								myConcert.join(hostToJoin, portToJoin);
								myTransport.getNetworks().add(myConcert);
								break;
							case 2:
								System.out.print("\tchoose a port number to start MyFoot: ");
								port = Integer.parseInt(input.readLine());
								System.out.print("\tMyFoot host to join = ");
								hostToJoin = input.readLine();
								System.out.print("\tMyFoot port to join = ");
								portToJoin = Integer.parseInt(input.readLine());
								MyFoot myFoot = new MyFoot(ip, 0, myTransport);
								new Thread(myFoot).start();
								Thread.yield();
								myFoot.join(hostToJoin, portToJoin);
								myTransport.getNetworks().add(myFoot);
								break;
							case 3:
							default: break;
							}
						} catch (Exception e){
						}
					}
					if(!subMenu){
						for(IOverlay o : myTransport.getNetworks()){
							System.out.println(++i + ") " + o.getIdentifier());
						}
						if(i!=1){
							System.out.println(++i + ") MyTransport");
						}
						System.out.println("0) Quit");
						System.out.print("---> ");
						try{
							chx = Integer.parseInt(input.readLine().trim());
							String key;
							switch(chx){
							case 1 :
								subMenu = true;
								break;
							case 2:
							case 3:
							case 4:
								if (myTransport.getNetworks().size() > chx-2 && myTransport.getNetworks().get(chx-2).getIdentifier().equals("Foot")){
									System.out.println("\t1) Publish a football match");
									System.out.println("\t2) Search a football match");
									System.out.print("\t---> ");
									chx = Integer.parseInt(input.readLine().trim());
									switch(chx){
									case 1 :
										System.out.print("\n\tDay = ");
										key = input.readLine();
										System.out.print("\tDestination = ");
										String key2 = input.readLine();
										System.out.print("\tMatch = ");
										String message = input.readLine();
										System.out.print("\tContact = ");
										String message2 = input.readLine();
										if(!key2.equals(""))
											myTransport.put(key + "+" + key2, message + "+" + message2 + "+car, maybe...");
										else
											myTransport.put(key, message + "+" + message2 + "+car, maybe...");
										break;
									case 2 :
										System.out.print("\n\tDay = ");
										key = input.readLine();
										System.out.print("\t\tDestination = ");
										key2 = input.readLine();
										if(!key2.equals(""))
											System.out.println("\t\tfound: " + myTransport.get(key + "+" + key2));
										else
											System.out.println("\t\tfound: " + myTransport.get(key));
										break;
									default : break;
									}
								} else if (myTransport.getNetworks().size() > chx-2 && myTransport.getNetworks().get(chx-2).getIdentifier().equals("Concert")){
									System.out.println("\t1) Publish a concert");
									System.out.println("\t2) Search a concert");
									System.out.print("\t---> ");
									chx = Integer.parseInt(input.readLine().trim());
									switch(chx){
									case 1 :
										System.out.print("\n\tDay = ");
										key = input.readLine();
										System.out.print("\tDestination = ");
										String key2 = input.readLine();
										System.out.print("\tConcert = ");
										String message = input.readLine();
										System.out.print("\tContact = ");
										String message2 = input.readLine();
										if(!key2.equals(""))
											myTransport.put(key + "+" + key2, message + "+" + message2 + "+car, maybe...");
										else
											myTransport.put(key, message + "+" + message2 + "+car, maybe...");
										break;
									case 2 :
										System.out.print("\n\tDay = ");
										key = input.readLine();
										System.out.print("\tDestination = ");
										key2 = input.readLine();
										if(!key2.equals(""))
											System.out.println("\t\tfound: " + myTransport.get(key + "+" + key2));
										else
											System.out.println("\t\tfound: " + myTransport.get(key));
										break;
									default : break;
									}
								} else {
									System.out.println("\t1) Publish a transport");
									System.out.println("\t2) Search a transport");
									System.out.print("\t---> ");
									chx = Integer.parseInt(input.readLine().trim());
									switch(chx){
									case 1 :
										System.out.print("\n\tDay = ");
										key = input.readLine();
										System.out.print("\tDestination = ");
										String key2 = input.readLine();
										System.out.print("\tContact = ");
										String message = input.readLine();;
										if(!key2.equals(""))
											myTransport.put(key + "+" + key2,"myTransport+" + message + "+car, maybe...");
										else
											myTransport.put(key,"myTransport+" + message + "+car, maybe...");
										break;
									case 2 :
										System.out.print("\n\tDay = ");
										key = input.readLine();
										System.out.print("\tDestination = ");
										key2 = input.readLine();
										if(!key2.equals(""))
											System.out.println("\t\tfound: " + myTransport.get(key + "+" + key2));
										else
											System.out.println("\t\tfound: " + myTransport.get(key));
										break;
									default : break;
									}
								}
								break;
							case 0:
								myTransport.kill();
								break;
							case 9 : // DEBUG!!!!!!!!
								System.out.println("\n" + myTransport + "\n"); 
								break;
							default : break;
							}
							if(!subMenu){
								System.out.println("\npress Enter to continue...");
								input.readLine();
							}
						} catch (IOException e) { 
						} catch (NumberFormatException e) {}
					} else {
						subMenu = false;
					}
				}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
}
