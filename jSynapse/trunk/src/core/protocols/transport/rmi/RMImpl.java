package core.protocols.transport.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;

public class RMImpl implements ITransport, java.rmi.Remote{

	private static final long serialVersionUID = -923635613325196936L;
	private int port;
	private IOverlay o;

	public RMImpl(int port, IOverlay o) throws RemoteException{
		this.port = port;
		this.o = o;
	}

	public String doStuff(String code){
		return o.doStuff(code);
	}

	public void test(){
		o.put("test", "test");
	}

	public String forward(String message, Node destination) {
		try {
			//			System.out.println("rmi://" + destination.getIp() + ":" + destination.getPort() + "/RMImpl");
			RMImpl dest = (RMImpl) Naming.lookup("rmi://" + destination.getIp() + ":" + destination.getPort() + "/RMImpl");
//			if(destination.getPort() != port){
//				System.out.println("rmi://" + destination.getIp() + ":" + destination.getPort() + "/RMImpl");
//				dest.test();
//			} else {
				return dest.doStuff(message);
//			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public int getPort() {
		return port;
	}

}
