package blackbox.core.overlay.foot;

import blackbox.core.overlay.concert.Concert;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;
import core.tools.HashFunction;

public class Foot extends Concert{

	public static String OVERLAY_IDENTIFIER = "Foot"; // use an unique ID is possible
	
	/** Hash function */
	private HashFunction h;
	
	public Foot(String ip, int port) {
		this.h = new HashFunction(OVERLAY_IDENTIFIER);
		int id = h.SHA1ToInt(ip+port+time);
		
		transport = new SocketImpl(port, 10, RequestHandler.class.getName(), 10, 1, 50, this);
		((SocketImpl) transport).launchServer();
		initialise(ip, id, transport.getPort());
		checkStable();
	}
	
	public String getIdentifier() {
		return OVERLAY_IDENTIFIER;
	}
	
	public int keyToH(String key){          // A CHANGER!
		return h.SHA1ToInt(key);
	}
}

