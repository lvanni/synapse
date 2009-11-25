package core.overlay.jchord;

import core.overlay.concert.Concert;
import core.protocols.transport.socket.SocketImpl;
import core.tools.HashFunction;

public class JChord1 extends Concert implements Runnable{

	public static String overlayIntifier = "JCHORD1"; // use an unique ID is possible
	
	/** Hash function */
	private HashFunction h;
	
	public JChord1(String ip, int port) {
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
