package core.protocol.p2p.kad.org.planx.xmlstore.routing.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import experiment.networking.current.node.synapse.plugin.kademlia.KadNodePlugin;

/**
 * Listens for incoming UDP messages and provides a framework for sending messages
 * and responding to received messages.
 * Two threads are started: One that listens for incoming messages and one that
 * handles timeout events.
 **/
public class MessageServer {
    /**
     * Maximum size of a received datagram packet.
     * Could probably be less, but is set too high to be on the safe side.
     **/
	private static final int DATAGRAM_BUFFER_SIZE = 10*1024;

	private static Random random = new Random();
	private MessageFactory factory;
	private long timeout;
	private DatagramSocket socket;
	private Timer timer;
	private boolean isRunning = true;
	private Map receivers; // keeps track of registered receivers
	private Map tasks;     // keeps track of timeout events
    
    private KadNodePlugin plugin;

    /**
     * Constructs a MessageServer listening on the specified UDP port using the
     * specified MessageFactory for interpreting incoming messages.
     *
     * @param udpPort The UDP port on which to listen for incoming messages
     * @param factory Factory for creating Message and Receiver objects
     * @param timeout The timeout period in milliseconds
     *
     * @throws SocketException if the socket could not be opened, or the socket
     *                         could not bind to the specified local port
     **/
    public MessageServer(int udpPort, MessageFactory factory, long timeout, KadNodePlugin plugin)
                                                    throws SocketException {
        this.factory = factory;
        this.timeout = timeout;
        socket = new DatagramSocket(udpPort);
        timer = new Timer(true);
        receivers = new HashMap();
        tasks = new HashMap();
        
        this.plugin = plugin;

        new Thread() {
            public void run() {
                listen();
            }
        }.start();
    }

    /**
     * Sends the specified Message and calls the specified Receiver when a reply
     * for the message is received. If <code>recv</code> is <code>null</code>
     * any reply is ignored. Returns a unique communication id which can be used
     * to identify a reply.
     **/
    public synchronized int send(Message message, InetAddress ip,
                                 int port, Receiver recv) throws IOException {
        if (!isRunning) throw new IllegalStateException("MessageServer not running");
        int comm = random.nextInt();
        if (recv != null) {
            Integer key = new Integer(comm);
            receivers.put(key, recv);
            TimerTask task = new TimeoutTask(comm, recv);
            timer.schedule(task, timeout);
            tasks.put(key, task);
        }
        sendMessage(comm, message, ip, port);
        return comm;
    }

    /**
     * Sends a reply to the message with the specified communication id.
     **/
    public synchronized void reply(int comm, Message message, InetAddress ip,
                                                int port) throws IOException {
        if (!isRunning) throw new IllegalStateException("MessageServer not running");
        sendMessage(comm, message, ip, port);
    }

    private void sendMessage(int comm, Message message, InetAddress ip, int port)
                                                              throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        dout.writeInt(comm);
        dout.writeByte(message.code());
        message.toStream(dout);
        dout.close();

        byte[] data = bout.toByteArray();
        if (data.length > DATAGRAM_BUFFER_SIZE) {
            throw new IOException("Message too big, size="+data.length+
                                  " bytes, max="+DATAGRAM_BUFFER_SIZE+" bytes");
        }
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
        socket.send(packet);
    }

    private synchronized void unregister(int comm) {
        Integer key = new Integer(comm);
        receivers.remove(key);
        tasks.remove(key);
    }

    /**
     * Started in a separate thread.
     **/
    private void listen() {
        try {
            while (isRunning) {
                try {
                    // Wait for packet
                    byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // Decode data into Message object
                    ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(),
                                                 packet.getOffset(), packet.getLength());
                    DataInputStream din = new DataInputStream(bin);
                    int comm = din.readInt();
                    byte messCode = din.readByte();
                    Message message = factory.createMessage(messCode, din);
                    
                    // notify the listening plugin
                    if(plugin != null){
                    	plugin.handleRequest(message.toString());
                    }
                    
                    din.close();

                    // Create Receiver if one is supported
                    Receiver recv = null;
                    recv = factory.createReceiver(messCode, this);

                    // If no receiver, get registered Receiver, if any
                    if (recv == null) {
                        synchronized (this) {
                            Integer key = new Integer(comm);
                            recv = (Receiver) receivers.remove(key);
                            // Cancel timer if there was a registered Receiver
                            if (recv != null) {
                                TimerTask task = (TimerTask) tasks.remove(key);
                                task.cancel();
                            }
                        }
                    }

                    // Invoke Receiver if one was found
                    if (recv != null) {
                        recv.receive(message, comm);
                    }
                } catch (SocketException e) {
                    // Socket has been closed, done by the close() method
                    isRunning = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (!socket.isClosed()) socket.close();
            isRunning = false;
        }
    }

    /**
     * Signals to the MessageServer thread that it should stop running.
     **/
    public synchronized void close() {
        if (!isRunning) throw new IllegalStateException("MessageServer not running");
        isRunning = false;
        timer.cancel();
        socket.close(); // breaks the wait for incoming packets
        receivers.clear();
        tasks.clear();
    }

    /**
     * Task that gets called by a separate thread if a timeout for a receiver occurs.
     * When a reply arrives this task must be cancelled using the <code>cancel()</code>
     * method inherited from <code>TimerTask</code>. In this case the caller is
     * responsible for removing the task from the <code>tasks</code> map.
     **/
    class TimeoutTask extends TimerTask {
        private int comm;
        private Receiver recv;

        public TimeoutTask(int comm, Receiver recv) {
            this.comm = comm;
            this.recv = recv;
        }

        public void run() {
            try {
                unregister(comm);
                recv.timeout(comm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
