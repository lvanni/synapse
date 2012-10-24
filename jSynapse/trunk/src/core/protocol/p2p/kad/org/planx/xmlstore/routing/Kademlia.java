package core.protocol.p2p.kad.org.planx.xmlstore.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import core.protocol.p2p.kad.org.planx.xmlstore.routing.messaging.MessageFactory;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.messaging.MessageServer;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.ConnectOperation;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.DataLookupOperation;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.MessageFactoryImpl;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.Operation;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.RemoveOperation;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.RestoreOperation;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.operation.StoreOperation;

import experiment.networking.current.synapse.plugin.kademlia.KadNodePlugin;

/**
 * Maps keys to values in a distributed setting using the Kademlia protocol.
 * Three threads are started: One that listens for incoming routing messages,
 * one that handles timeouts for routing messages, and one that handles
 * hourly refresh/restore.
 **/
public class Kademlia implements DistributedMap {
	private Configuration conf;
	private Map localMap;
	private String name;
	private Node local;
	private Space space;
	private MessageServer server;
	private Timer timer;
	private boolean isClosed = false;
	
	/**
	 * Creates a Kademlia DistributedMap with the specified identifier and no
	 * persistence.
	 *
	 * @param id         The id to use or null to create a random id
	 * @param udpPort    The UDP port to use for routing messages
	 **/
	public Kademlia(Identifier id, int udpPort) throws IOException, RoutingException {
		this(null, id, udpPort, null, null, null);
	}

	/**
	 * Creates a Kademlia DistributedMap with the specified identifier and no
	 * persistence.
	 *
	 * @param id         The id to use or null to create a random id
	 * @param udpPort    The UDP port to use for routing messages
	 **/
	public Kademlia(Identifier id, int udpPort, KadNodePlugin plugin) throws IOException, RoutingException {
		this(null, id, udpPort, null, null, plugin);
	}

	/**
	 * Creates a Kademlia DistributedMap with the specified identifier and no
	 * persistence.
	 *
	 * @param id         The id to use or null to create a random id
	 * @param udpPort    The UDP port to use for routing messages
	 * @param config     Configuration parameters
	 **/
	public Kademlia(Identifier id, int udpPort, Configuration config)
	throws IOException, RoutingException {
		this(null, id, udpPort, null, config, null);
	}

	/**
	 * Creates a Kademlia DistributedMap using the specified name as filename base.
	 * If the id cannot be read from disk a random id is created.
	 * The instance is bootstraped to an existing network by specifying the
	 * address of a bootstrap node in the network.
	 *
	 * @param name       Filename base or null if persistence should not be used
	 * @param udpPort    The UDP port to use for routing messages
	 * @param bootstrap  IP and UDP port of bootstrap node or null if the instance
	 *                   should not attempt to connect
	 * @param config     Configuration parameters. If null default parameters are used.
	 **/
	public Kademlia(String name, int udpPort, InetSocketAddress bootstrap,
			Configuration config) throws IOException, RoutingException {
		this(name, null, udpPort, bootstrap, config, null);
	}

	/**
	 * Creates a Kademlia DistributedMap using the specified name as filename base.
	 * If the id cannot be read from disk the specified defaultId is used.
	 * The instance is bootstraped to an existing network by specifying the
	 * address of a bootstrap node in the network.
	 *
	 * @param name       Filename base or null if persistence should not be used
	 * @param defaultId  Default id if it could not be read from disk or null for
	 *                   random default id
	 * @param udpPort    The UDP port to use for routing messages
	 * @param bootstrap  IP and UDP port of bootstrap node or null if the instance
	 *                   should not attempt to connect
	 * @param config     Configuration parameters. If null default parameters are used.
	 *
	 * @throws RoutingException If the bootstrap node did not respond
	 * @throws IOException      If an error occurred while reading id or local map
	 *                          from disk <i>or</i> a network error occurred while
	 *                          attempting to connect to the network
	 **/
	public Kademlia(String name, Identifier defaultId, int udpPort,
			InetSocketAddress bootstrap, Configuration config, KadNodePlugin plugin)
	throws IOException, RoutingException {

		conf = (config == null) ? new Configuration() : config;

		if (defaultId == null) defaultId = Identifier.randomIdentifier();
		Identifier id = defaultId;
		localMap = new Hashtable();

		// Read id and localMap from disk or start with specified id and empty map
		this.name = name;
		if (name != null) id = initFromDisk(defaultId);

		local = new Node(InetAddress.getLocalHost(), udpPort, id);

		initialize(udpPort, bootstrap, plugin);
	}

	private void initialize(int udpPort, InetSocketAddress bootstrap, KadNodePlugin plugin) throws RoutingException, IOException{
		NeighbourhoodListenerImpl listener = new NeighbourhoodListenerImpl(local);
		space = new Space(local, conf, listener);
		MessageFactory factory = new MessageFactoryImpl(localMap, local, space);
		server = new MessageServer(udpPort, factory, conf.RESPONSE_TIMEOUT, plugin);
		listener.setMessageServer(server);

		// Schedule recurring RestoreOperation
		timer = new Timer(true);
		timer.schedule(
				new TimerTask() {
					public void run() {
						try {
							Operation op = new RestoreOperation(conf, server, space,
									local, localMap);
							op.execute();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				},
				conf.RESTORE_INTERVAL, conf.RESTORE_INTERVAL
		);

		// Possibly try to connect
		if (bootstrap != null) connect(bootstrap);
	}

	private Identifier initFromDisk(Identifier defaultId) {
		// Read id from disk or create new random id
		Identifier id = null;
		try {
			String idname = name+".id";
			DataInputStream in = new DataInputStream(new FileInputStream(idname));
			id = new Identifier(in);
			in.close();
		} catch (IOException e) {
			id = defaultId;
		}

		// Read local map from disk or start empty
		try {
			String mapname = name+".map";
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(mapname)));
			localMap = (Hashtable) in.readObject();
			in.close();
		} catch (IOException e) {
			localMap = new Hashtable();
		} catch (ClassNotFoundException e) {
			localMap = new Hashtable();
		}
		return id;
	}

	private void stateToDisk() throws IOException {
		// Write id to disk
		String idname = name+".id";
		DataOutputStream dout = new DataOutputStream(new FileOutputStream(idname));
		local.getId().toStream(dout);
		dout.close();

		// Write local map to disk
		String mapname = name+".map";
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(mapname)));
		out.writeObject(localMap);
		out.close();
	}

	/**
	 * Attempts to connect to an existing peer-to-peer network.
	 *
	 * @param bootstrap  The IP of a known node in the peer-to-peer network
	 * @param bootPort   The UDP port of the <code>bootstrap</code> node
	 *
	 * @throws RoutingException      If the bootstrap node could not be contacted
	 * @throws IOException           If a network error occurred
	 * @throws IllegalStateException If this object is closed
	 **/
	public void connect(InetSocketAddress bootstrap) throws IOException,
	RoutingException {
		if (isClosed) throw new IllegalStateException("Kademlia instance is closed");
		Operation op = new ConnectOperation(conf, server, space, local,
				bootstrap.getAddress(), bootstrap.getPort());
		op.execute();
	}

	/**
	 * Closes the map. Any subsequent calls to methods are invalid.
	 *
	 * @throws IOException           If an error occurred while writing data to disk
	 * @throws IllegalStateException If this object is closed
	 **/
	public void close() throws IOException {
		if (isClosed) throw new IllegalStateException("Kademlia instance is closed");
		isClosed = true;
		timer.cancel();
		server.close();
		if (name != null) stateToDisk();
	}

	/**
	 * Returns <code>true</code> if the map contains the specified key and
	 * <code>false</code> otherwise.
	 *
	 * @param  key The key to lookup
	 * @return <code>true</code> if a mappings with the key was found and
	 *         <code>false</code> otherwise.
	 *
	 * @throws RoutingException      If the lookup operation timed out
	 * @throws IOException           If a network error occurred
	 * @throws IllegalStateException If this object is closed
	 **/
	public boolean contains(Identifier key) throws IOException, RoutingException {
		if (isClosed) throw new IllegalStateException("Kademlia instance is closed");
		return (get(key) != null);
	}

	/**
	 * Returns the value associated with the specified key.
	 * If the value is stored locally no Kademlia lookup is performed.
	 * See also {@link DataLookupOperation}.
	 *
	 * @param  key The key to lookup
	 * @return The value mapped to the key or <code>null</code> if no value
	 *         is mapped to the key.
	 *
	 * @throws RoutingException      If the lookup operation timed out
	 * @throws IOException           If a network error occurred
	 * @throws IllegalStateException If this object is closed
	 **/
	public Serializable get(Identifier key) throws IOException, RoutingException {
		if (isClosed) throw new IllegalStateException("Kademlia instance is closed");
		synchronized (localMap) {
			if (localMap.containsKey(key)) {
				return ((TimestampedValue) localMap.get(key)).getObject();
			}
		}
		Operation op = new DataLookupOperation(conf, server, space, local, key);
		return (Serializable) op.execute();
	}

	/**
	 * Associates the specified value with the specified key.
	 * It is guaranteed that the mapping is stored on
	 * <i>K</i> nodes or all nodes if less than
	 * this number of nodes exist in the network. See also {@link StoreOperation}.
	 *
	 * @param  key   The key
	 * @param  value The value associated with the key
	 *
	 * @throws RoutingException      If the operation timed out
	 * @throws IOException           If a network error occurred
	 * @throws IllegalStateException If this object is closed
	 **/
	public void put(Identifier key, Serializable value) throws IOException,
	RoutingException {
		if (isClosed) throw new IllegalStateException("Kademlia instance is closed");
		long now = System.currentTimeMillis();
		TimestampedValue timeval = new TimestampedValue(value, now);
		Operation op = new StoreOperation(conf, server, space, localMap, local, key, timeval);
		op.execute();
	}

	/**
	 * Removes the mapping with the specified key.
	 *
	 * @param  key  The key of the mapping to remove
	 *
	 * @throws RoutingException      If the operation timed out
	 * @throws IOException           If a network error occurred
	 * @throws IllegalStateException If this object is closed
	 **/
	public void remove(Identifier key) throws IOException {
		if (isClosed) throw new IllegalStateException("Kademlia instance is closed");
		Operation op = new RemoveOperation(conf, server, space, localMap, local, key);
		op.execute();
	}

	///////////////////////////////////////////////////////////////////////////
	/////////////////////// Methods for Testing Purposes //////////////////////
	///////////////////////////////////////////////////////////////////////////

	/**
	 * For debugging purposes, returns the contents of the internal space.
	 **/
	public String toString() {
		return local.getInetAddress().toString()+":"+local.getPort()+", "+
		"map size="+localMap.size()+", "+
		"space size="+space.nodeCount();
	}

	Space internalGetSpace() {
		return space;
	}

	Node internalGetLocal() {
		return local;
	}

	Map internalGetMap() {
		return localMap;
	}
}
