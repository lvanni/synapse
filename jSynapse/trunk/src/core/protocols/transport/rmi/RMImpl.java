package core.protocols.transport.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import core.protocols.p2p.IDHT;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;

/**
 * This class represent the java RMI transport layer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
@Deprecated
public class RMImpl implements ITransport, java.rmi.Remote {

	/** port of rmi registry */
	private int port;
	/** The DHT peer who use this layer */
	private IDHT o;

	/**
	 * Default constructor
	 * 
	 * @param port
	 * @param o
	 * @throws RemoteException
	 */
	public RMImpl(int port, IDHT o) throws RemoteException {
		this.port = port;
		this.o = o;
	}

	/**
	 * handle a request represented by the code parameter
	 * 
	 * @param code
	 * @return the response to this request
	 */
	public String handleRequest(String code) throws RemoteException {
		return o.handleRequest(code);
	}

	/**
	 * @see core.protocols.transport.ITransport#sendRequest(String, Node)
	 */
	public String sendRequest(String message, Node destination) {
		try {
			RMImpl dest = (RMImpl) Naming.lookup("rmi://" + destination.getIp()
					+ ":" + destination.getPort() + "/RMImpl");
			return dest.handleRequest(message);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @see core.protocols.transport.ITransport#stopServer()
	 */
	public void stopServer() { /* do nothing */
	}

	/**
	 * @see core.protocols.transport.ITransport#getPort()
	 */
	public int getPort() {
		return port;
	}

}
