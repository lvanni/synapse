package core.overlay.foot;

import core.overlay.concert.Concert;
import core.protocols.transport.socket.SocketImpl;
import core.tools.HashFunction;

public class Foot extends Concert implements Runnable{

	public static String overlayIntifier = "Foot"; // use an unique ID is possible
	
	/** Hash function */
	private HashFunction h;
	
	public Foot(String ip, int port) {
		this.h = new HashFunction(overlayIntifier);
		int id = h.SHA1ToInt(ip+port+time);
		this.transport = new SocketImpl(port); // TRANSPORT CHOICE
		initialise(ip, id, port);
	}
	
	public String getIdentifier() {
		return overlayIntifier;
	}
	
	public int keyToH(String key){          // A CHANGER!
		return h.SHA1ToInt(key);
	}
}

