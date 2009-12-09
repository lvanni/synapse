package core.mytansport.plugins;

import core.mytansport.MyTransport;
import core.overlay.foot.Foot;
import core.protocols.p2p.chord.IChord;

public class MyFoot extends Foot{

	private MyTransport myTransport;

	public MyFoot(String host, int port, MyTransport myTransport) {
		super(host, port);
		this.myTransport = myTransport;
	}

	public String doStuff(String code){
		if(debugMode){
			System.out.println("\n** DEBUG: doStuff\n*\tcode: " + code);
		}
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals(OVERLAY_IDENTIFIER)){
			int f = Integer.parseInt(args[1]);
			switch(f){
			case IChord.PUT :
				// get back the clean key
				String cleanKey = myTransport.getCleanKey(args[2]+OVERLAY_IDENTIFIER);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
//					System.out.println("clean key found: " + cleanKey + " \nsynapse routing begin...");
					myTransport.put(cleanKey, args[3]);
				} else {
//					System.out.println("unknown key, no synapse routing...");
					super.doStuff(code);
				}
				break;
			case IChord.GET :
				// get back the clean key
				cleanKey = myTransport.getCleanKey(args[2]+OVERLAY_IDENTIFIER);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
//					System.out.println("clean key found: " + cleanKey + " \nsynapse routing begin...");
					result = myTransport.get(cleanKey);
				} else {
//					System.out.println("unknown key, no synapse routing...");
					result = super.doStuff(code);
				}
				break;
			default: 
				result = super.doStuff(code);
				break;
			}
		} else {
			System.err.println("!!! " + code + " FAIL !!!");
		}
		if(debugMode){
			System.out.println("*\tresult: " + result + "\n************************************");
		}
		return result;
	}
}
