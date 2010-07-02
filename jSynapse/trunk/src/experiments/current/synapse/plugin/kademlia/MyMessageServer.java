package experiments.current.synapse.plugin.kademlia;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.planx.xmlstore.routing.messaging.Message;
import org.planx.xmlstore.routing.messaging.MessageFactory;
import org.planx.xmlstore.routing.messaging.Receiver;

import experiments.current.synapse.Synapse;

/**
 * Listens for incoming UDP messages and provides a framework for sending messages
 * and responding to received messages.
 * Two threads are started: One that listens for incoming messages and one that
 * handles timeout events.
 **/
public class MyMessageServer extends org.planx.xmlstore.routing.messaging.MessageServer{
	
	private Synapse synapse;
	private String identifier;

	/**
	 * Default Constructor
	 * @param udpPort
	 * @param factory
	 * @param timeout
	 * @throws SocketException
	 */
	public MyMessageServer(int udpPort, MessageFactory factory, long timeout, Synapse synapse, String identifier)
	throws SocketException {
		super(udpPort, factory, timeout);
		this.synapse = synapse;
		this.identifier = identifier;
	}
	
    public synchronized int send(Message message, InetAddress ip,
            int port, Receiver recv) throws IOException {
    	if(message.code() == 5){
    		String[] args = message.toString().split(",");
    		for(String arg : args){
    			if(arg.split("=")[0].equals("lookup")){
    				String key = arg.split("=")[1].split("]")[0];
    				String cleanKey = synapse.getInCleanTable(key);
    				if (cleanKey != null && !cleanKey.equals("null")
    						&& !cleanKey.equals("")) {
    					if (synapse.cacheTableExist(cleanKey).equals("1")) {
    						// THEN SYNAPSE AND USE THE CACHE TABLE
    						synapse.synapseGet(cleanKey, identifier);
    					}
    				}
    			}
    		}
    	}
    	return super.send(message, ip, port, recv);
    }

}
