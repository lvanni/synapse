//package experiments.current.synapse.plugin.kademlia;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.util.TimerTask;
//
//import org.planx.xmlstore.routing.messaging.Message;
//import org.planx.xmlstore.routing.messaging.MessageFactory;
//import org.planx.xmlstore.routing.messaging.Receiver;
//
//import experiments.current.synapse.Synapse;
//
///**
// * Listens for incoming UDP messages and provides a framework for sending messages
// * and responding to received messages.
// * Two threads are started: One that listens for incoming messages and one that
// * handles timeout events.
// **/
//public class MyMessageServer extends org.planx.xmlstore.routing.messaging.MessageServer{
//	
//	private Synapse synapse;
//	private String identifier;
//
//	/**
//	 * Default Constructor
//	 * @param udpPort
//	 * @param factory
//	 * @param timeout
//	 * @throws SocketException
//	 */
//	public MyMessageServer(int udpPort, MessageFactory factory, long timeout, Synapse synapse, String identifier)
//	throws SocketException {
//		super(udpPort, factory, timeout);
//		this.synapse = synapse;
//		this.identifier = identifier;
//	}
//	
//	 /**
//     * Started in a separate thread.
//     **/
//    private void listen() {
//        try {
//            while (isRunning) {
//                try {
//                	
//                	System.out.println("cool!!!!!!!!!!");
//                	
//                    // Wait for packet
//                    byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE];
//                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                    socket.receive(packet);
//
//                    // Decode data into Message object
//                    ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(),
//                                                 packet.getOffset(), packet.getLength());
//                    DataInputStream din = new DataInputStream(bin);
//                    int comm = din.readInt();
//                    byte messCode = din.readByte();
//                    Message message = factory.createMessage(messCode, din);
//                    din.close();
//
//                    // Create Receiver if one is supported
//                    Receiver recv = null;
//                    recv = factory.createReceiver(messCode, this);
//
//                    // If no receiver, get registered Receiver, if any
//                    if (recv == null) {
//                        synchronized (this) {
//                            Integer key = new Integer(comm);
//                            recv = (Receiver) receivers.remove(key);
//                            // Cancel timer if there was a registered Receiver
//                            if (recv != null) {
//                                TimerTask task = (TimerTask) tasks.remove(key);
//                                task.cancel();
//                            }
//                        }
//                    }
//
//                    // Invoke Receiver if one was found
//                    if (recv != null) {
//                        recv.receive(message, comm);
//                    }
//                } catch (SocketException e) {
//                    // Socket has been closed, done by the close() method
//                    isRunning = false;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } finally {
//            if (!socket.isClosed()) socket.close();
//            isRunning = false;
//        }
//    }
//	
////    public synchronized int send(Message message, InetAddress ip,
////            int port, Receiver recv) throws IOException {
////    	if(message.code() == 5){
////    		String[] args = message.toString().split(",");
////    		for(String arg : args){
////    			if(arg.split("=")[0].equals("lookup")){
////    				String key = arg.split("=")[1].split("]")[0];
////    				String cleanKey = synapse.getInCleanTable(key);
////    				if (cleanKey != null && !cleanKey.equals("null")
////    						&& !cleanKey.equals("")) {
////    					
////    					if (synapse.cacheTableExist(cleanKey).equals("1")) {
////    						// THEN SYNAPSE AND USE THE CACHE TABLE
////    						synapse.synapseGet(cleanKey, identifier);
////    					}
////    				}
////    			}
////    		}
////    	}
////    	return super.send(message, ip, port, recv);
////    }
//}
