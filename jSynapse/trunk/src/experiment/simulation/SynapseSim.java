package experiment.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import core.protocol.p2p.IDHT;
import core.protocol.p2p.Node;
import core.protocol.transport.IRequestHandler;
import core.protocol.transport.ITransport;
import core.protocol.transport.local.LocalImpl;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;
import experiment.networking.current.node.chord.ChordNode;
import experiment.networking.current.node.kademlia.KadNode;
import experiment.networking.current.node.synapse.Synapse;
import experiment.networking.current.node.synapse.plugin.chord.ChordNodePlugin;
import experiment.networking.current.node.synapse.plugin.kademlia.KadNodePlugin;
import experiment.simulation.exception.SynapseSimException;

/**
   _____                                   _____ _           
  / ____|                                 / ____(_)          
 | (___  _   _ _ __   __ _ _ __  ___  ___| (___  _ _ __ ___  
  \___ \| | | | '_ \ / _` | '_ \/ __|/ _ \\___ \| | '_ ` _ \ 
  ____) | |_| | | | | (_| | |_) \__ \  __/____) | | | | | | |
 |_____/ \__, |_| |_|\__,_| .__/|___/\___|_____/|_|_| |_| |_|
          __/ |           | |                                
         |___/            |_|       

 * @author lvanni
 * @version 1.0
 * @since 2012
 */
public class SynapseSim implements ISynapseSim, IRequestHandler, Serializable {

	private static final long serialVersionUID = 1L;

	/* ********************************************* */
	/* 					Attribute					 */
	/* ********************************************* */
	private int nodeID = 0;
	// NodeType, overlayIntifier, NodeID
//	private Map<NodeType, Map<String, Map<Integer, IDHT>>> topology;
	Map<String, Map<Integer, IDHT>> topology;
	private Map<Integer, IDHT> NodeToPortMap; 

	/* ********************************************* */
	/* 				Singleton Pattern				 */
	/* ********************************************* */
	private static SynapseSim INSTANCE = new SynapseSim();

	private SynapseSim() {
		ITransport transport = new SocketImpl(DEFAULT_PORT, 10, RequestHandler.class
				.getName(), 10, 1, 100, this);
		((SocketImpl) transport).launchServer();
		topology = new HashMap<String, Map<Integer, IDHT>>();
		NodeToPortMap = new HashMap<Integer, IDHT>();
	}

	public static SynapseSim getInstance() {
		return INSTANCE;
	}

	/* ********************************************* */
	/* 			implements ISimulator				 */
	/* ********************************************* */
	public IRequestHandler getReceiver(Node node) {

		IRequestHandler receiver = null;

		try {
			synchronized (INSTANCE) {
				
				receiver = NodeToPortMap.get(node.getPort());
			}
		} catch (Exception e) {}

		return receiver;
	}

	public IDHT createNode(NodeType nodeType, String overlayIntifier) {
		return createNode(nodeType, overlayIntifier, null);
	}

	public IDHT createNode(NodeType nodeType, String overlayIdentifier, Synapse synapse) {

		IDHT node = null;
		int port = nodeID++;	// Fake port number for the simulation 
		
		switch (nodeType) {
		case CHORD:
			if(synapse != null) {
				node = new ChordNodePlugin(DEFAULT_IP, port, synapse, overlayIdentifier, (ITransport) new LocalImpl(port));
			} else {
				node = new ChordNode(DEFAULT_IP, port, overlayIdentifier, (ITransport) new LocalImpl(port));
			}
			break;
		case KAD:
			if(synapse != null) {
				node = new KadNodePlugin(overlayIdentifier, synapse, (ITransport) new LocalImpl(port));
			} else {
				node = new KadNode(overlayIdentifier, (ITransport) new LocalImpl(port));
			}
			break;
		case SYNAPSE:
			node = new Synapse(DEFAULT_IP, port, (ITransport) new LocalImpl(port));
			break;
		}

		// Add node to the topology
		synchronized (INSTANCE) {
			
			NodeToPortMap.put(port, node);
		
			if(!topology.containsKey(overlayIdentifier)) {
				topology.put(overlayIdentifier, new HashMap<Integer, IDHT>());
			} else {
				// JOIN
				Set<Entry<Integer, IDHT>> topologyByNodeTypeSet = topology.get(overlayIdentifier).entrySet();
				int id = topologyByNodeTypeSet.iterator().next().getKey();
				Node networkToJoin = topology.get(overlayIdentifier).get(id).getThisNode();
				node.join(networkToJoin.getIp(), networkToJoin.getPort());
			}

			topology.get(overlayIdentifier).put(node.getThisNode().getId(), node);
		}

		return node;
	}

	public String put(IDHT node, String key, String value) {
		node.put(key, value);
		return "put on node "+node.getThisNode().getPort()+" key "+key+" value "+ value;
	}

	public String get(IDHT node, String key) {
		return "get value "+node.get(key)+ " on node "+ node.getThisNode().getPort() + " key "+ key;
	}

	/* ********************************************* */
	/* 			implements IRequestHandler			 */
	/* ********************************************* */
	public String handleRequest(String message) {
		try {
			return commandExecutor(commandInterpretor(message), message.split(","));
		} catch (SynapseSimException e) {
			System.out.println(message);
			e.printStackTrace();
			return message;
		}
	}

	public void kill() {
		// TODO Auto-generated method stub
	}

	/* ********************************************* */
	/* 					private Methods				 */
	/* ********************************************* */
	/**
	 * Interpret user command
	 * @param message
	 * @return
	 */
	private Command commandInterpretor(String message) {
		String[] args = message.split(",");
		return Command.values()[Integer.parseInt(args[0])];
	}
	
	/**
	 * 
	 * @param nodes
	 * @return
	 */
	private IDHT getRandomNode(Map<Integer, IDHT> nodes){
		Random generator = new Random();
		Object[] values = nodes.values().toArray();
		Object randomValue = values[generator.nextInt(values.length)];
		return (IDHT) randomValue;
	}

	/**
	 * Execute user command
	 * @param choice
	 * @throws SynapseSimException
	 */
	private String commandExecutor(Command command, String[] args) throws SynapseSimException{
		switch (command) {
		case CREATE:
			if(args.length < 3 || (args[1].equals("Synapse") && ((args.length-3) % 2) != 0)) {
				throw new SynapseSimException("Bad argument number to create node");
			} else {
				return analyseCreateCommandAndExecute(args);
			}
		case GET:
			if(args.length < 3) {
				throw new SynapseSimException("Bad argument number to create node");
			} else {
				IDHT node = getRandomNode(topology.get(args[2]));
				return get(node, args[1]);
			}
		case PUT:
			if(args.length < 4) {
				throw new SynapseSimException("Bad argument number to create node");
			} else {
				IDHT node = getRandomNode(topology.get(args[3]));
				return put(node, args[1], args[2]);
			}
		default:
			throw new SynapseSimException("How can it be possible to have this case ? You have 2 hours to think about it");
		}
	}

	/**
	 * Analyse "create" command and execute it 
	 * @param args
	 * @return
	 * @throws SynapseSimException
	 */
	private String analyseCreateCommandAndExecute(String[] args) throws SynapseSimException{

		NodeType nodeTypeSelected = NodeType.values()[Integer.parseInt(args[1])];

		if(nodeTypeSelected == NodeType.SYNAPSE){
			Synapse synapse = (Synapse)createNode(NodeType.SYNAPSE,args[2]);
			NodeType synapseNodeTypeSelected;
			int chordNumber=0;
			int kadNumber=0;
			for(int i=3;i<args.length;i+=2){
				synapseNodeTypeSelected = NodeType.values()[Integer.parseInt(args[i])];
				createNode(synapseNodeTypeSelected, args[i+1],synapse);
				if(synapseNodeTypeSelected == NodeType.KAD){
					kadNumber++;
				}
				else{
					chordNumber++;
				}
			}
			return "Node Synapse created with :"+chordNumber+" chord node, "+kadNumber+" kad node, on network: "+args[2];
		}
		else{
			createNode(nodeTypeSelected, args[2]);
			return "Node "+nodeTypeSelected.toString()+ " created on network: "+ args[2];
		}
	}

	/* ********************************************* */
	/* 					public Methods				 */
	/* ********************************************* */
	public String printTopology() {

		String result = "";
		
		for(Entry<Integer, IDHT> node : NodeToPortMap.entrySet()) {
			result += node.getValue() + "\n";
		}
		return result;
	}


	/* ********************************************* */
	/* 					simulator UI				 */
	/* ********************************************* */
	public static void clearScreen() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println(
"   _____                                   _____ _         			\n" +
"  / ____|                                 / ____(_)         			\n" + 
" | (___  _   _ _ __   __ _ _ __  ___  ___| (___  _ _ __ ___  			\n" +
"  \\___ \\| | | | '_ \\ / _` | '_ \\/ __|/ _ \\\\___ \\| | '_ ` _ \\ 	\n" +
"  ____) | |_| | | | | (_| | |_) \\__ \\  __/____) | | | | | | |		\n" +
" |_____/ \\__, |_| |_|\\__,_| .__/|___/\\___|_____/|_|_| |_| |_|		\n" +
"          __/ |           | |                                			\n" +
"         |___/            |_|  \n\n");
	}

	/**
	 * Launch the simulator
	 * @param args
	 */
	public static void main(String[] args) {

		SynapseSim synapseSim = getInstance();

		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {

			clearScreen();

			if(args.length > 0 && (args[0].equals("-d") || args[0].equals("--debug"))) {
				System.out.println("\n\n0) Print topology");
				System.out.println("1) Create node");
				System.out.println("2) Put");
				System.out.println("3) Get");
				System.out.println("4) Quit");
				System.out.print("---> ");
				try {
					int chx = Integer.parseInt(input.readLine().trim());
					switch (chx) {
					case 0:
						System.out.println(synapseSim.printTopology());
						break;
					case 1:
						System.out.println("\n\n0) Chord");
						System.out.println("1) Kad");
						System.out.println("2) Synapse");
						System.out.print("---> ");
						int command = Command.CREATE.getValue();
						String commandLine = command + ",";
						int nodeType = Integer.parseInt(input.readLine().trim());
						if(nodeType < 2) {
							System.out.print("Network ID ---> ");
							String overlayIdentifier = input.readLine().trim();
							commandLine += nodeType + "," + overlayIdentifier;
						} else {
							commandLine +=  nodeType + "," + "ControlNetwork";
							while(true) {
								System.out.println("\n\t0) Add Chord node");
								System.out.println("\t1) Add Kad node");
								System.out.println("\t2) Create Synapse");
								System.out.print("\t---> ");
								nodeType = Integer.parseInt(input.readLine().trim());
								if(nodeType == 2) {
									break;
								}
								System.out.print("\tNetwork ID ---> ");
								String overlayIdentifier = input.readLine().trim();
								commandLine += "," + nodeType + "," + overlayIdentifier;
							}
							
						}
						synapseSim.commandExecutor(synapseSim.commandInterpretor(commandLine), commandLine.split(","));
						break;
					case 2:
						System.out.print("Key ---> ");
						String key = input.readLine().trim();
						System.out.print("Value ---> ");
						String value = input.readLine().trim();
						System.out.print("Network ID ---> ");
						String networkID = input.readLine().trim();
						command = Command.PUT.getValue();
						commandLine = command + "," + key + "," + value + "," + networkID;
						synapseSim.commandExecutor(synapseSim.commandInterpretor(commandLine), commandLine.split(","));
						break;
					case 3:
						System.out.print("Key ---> ");
						key = input.readLine().trim();
						System.out.print("Network ID ---> ");
						networkID = input.readLine().trim();
						command = Command.GET.getValue();
						commandLine = command + "," + key + "," + networkID;
						value = synapseSim.commandExecutor(synapseSim.commandInterpretor(commandLine), commandLine.split(","));
						System.out.println("value = " + value);
						break;
					case 4:
						synapseSim.kill();
						System.exit(0);
					default:
						clearScreen();
						throw new SynapseSimException("How can it be possible to have this case ? You have 2 hours to think about it");
					}
					System.out.println("Press enter to continue...");
					input.readLine();
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println("what?");
				} catch (IOException e) {
					e.printStackTrace();
				} catch(SynapseSimException e){
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			} else {
				try {
					System.out.println("SynapseSim started...");
					input.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
