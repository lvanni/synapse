package example.ui.console;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class InfoConsole {
	public static String getIp(){
		try{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface interfaceN = (NetworkInterface)interfaces.nextElement(); 
				Enumeration<InetAddress> ienum = interfaceN.getInetAddresses();
				while (ienum.hasMoreElements()) {  
					InetAddress ia = ienum.nextElement();
					String adress = ia.getHostAddress().toString();
					if( adress.length() < 16){         
						if(!adress.startsWith("127") && !adress.startsWith("0") && !adress.startsWith("fe")){  
							return adress;       
						}
					}
				}
			}
		} catch(Exception e){
			System.out.println("pas de carte reseau");
			e.printStackTrace();
		}
		return "";
	}
	
	public static void clearScreen(){
		for(int i=0 ; i<300 ; i++){
			System.out.println("\n");
		}
	}
}
