package experiment.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Map;

import core.protocol.p2p.IDHT;
import core.protocol.p2p.Node;
import core.protocol.transport.IRequestHandler;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;
import experiment.networking.current.node.chord.ChordNode;
import experiment.networking.current.node.kademlia.KadNode;
import experiment.networking.current.node.synapse.Synapse;
import experiment.simulation.exception.SynapseSimException;

/**
  _____                             _____ _           
 / ____|                           / ____(_)          
| (___  _   _ _ __  _ __  ___  ___| (___  _ _ __ ___  
 \___ \| | | | '_ \| '_ \/ __|/ _ \\___ \| | '_ ` _ \ 
 ____) | |_| | | | | |_) \__ \  __/____) | | | | | | |
|_____/ \__, |_| |_| .__/|___/\___|_____/|_|_| |_| |_|
         __/ |     | |                                
        |___/      |_|   
        
 * @author lvanni
 * @version 1.0
 * @since 2012
 */
public class SynapseSim implements ISynapseSim, IRequestHandler, Serializable {
	
	private static final long serialVersionUID = 1L;

	/* ********************************************* */
	/* 					Attribute					 */
	/* ********************************************* */
	private Map<NodeType, Map<String, IDHT>> topology;
	

	/* ********************************************* */
	/* 				Singleton Pattern				 */
	/* ********************************************* */
	private static SynapseSim INSTANCE = new SynapseSim();

	private SynapseSim() {
		ITransport transport = new SocketImpl(DEFAULT_PORT, 10, RequestHandler.class
				.getName(), 10, 1, 100, this);
		((SocketImpl) transport).launchServer();
	}

	public static SynapseSim getInstance() {
		return INSTANCE;
	}
	
	/* ********************************************* */
	/* 			implements ISimulator				 */
	/* ********************************************* */
	public IRequestHandler getReceiver(Node node) {
		return topology.get(node.getNetworkType()).get(node.getNetworkId()); 
	}

	public IDHT createNode(NodeType nodeType, String networkId) {
		return createNode(nodeType, networkId, null);
	}
	
	public IDHT createNode(NodeType nodeType, String networkId, Synapse synapse) {
		IDHT node = null;
		switch (nodeType) {
		case CHORD:
			node = new ChordNode(null, 0, null);
			node.getThisNode().setNetworkId(networkId);
			node.getThisNode().setNetworkType(nodeType.toString());
			break;
		case KAD:
			node = new KadNode(null);
			node.getThisNode().setNetworkId(networkId);
			node.getThisNode().setNetworkType(nodeType.toString());
			break;
		case SYNAPSE:
			node = new Synapse(null, 0);
			break;
		}
		return node;
	}

	public int put(String key, String value) {
		return 0;
	}
	
	public int get(String key) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* ********************************************* */
	/* 			implements IRequestHandler			 */
	/* ********************************************* */
	public String handleRequest(String message) {
		try {
			return commandExecutor(commandInterpretor(message), message.split(","));
		} catch (SynapseSimException e) {
			e.printStackTrace();
			return e.toString();
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
	 * Execute user command
	 * @param choice
	 * @throws SynapseSimException
	 */
	private String commandExecutor(Command command, String[] args) throws SynapseSimException{
		switch (command) {
		case CREATE:
			System.out.println("Print topology");
			if(args.length < 3 || (args[1].equals("Synapse") && ((args.length) % 3) != 0)){
				throw new SynapseSimException("Bad argument number to create Synapse node");
			}
			else{
				return analyseCreateCommandAndExecute(args);
			}
		case GET:
			System.out.println("Create Node");
			return "not yet implemented...";
		case PUT:
			System.out.println("put");
			return "not yet implemented...";
		default:
			throw new SynapseSimException("How can it be possible to have this case ? You have 2 hours to think about it");
		}
	}
	
	/**
	 * Analyse create command and execute it 
	 * @param args
	 * @return
	 * @throws SynapseSimException
	 */
	private String analyseCreateCommandAndExecute(String[] args) throws SynapseSimException{
		if(args[1].equals("Kad")){
			createNode(NodeType.KAD, args[2]);
		}
		if(args[1].equals("Chord")){
			createNode(NodeType.CHORD,args[2]);
		}
		if(args[1].equals("Synapse")){
			Synapse synapse=(Synapse)createNode(NodeType.SYNAPSE,args[2]);
			for(int i=3;i<args.length;i+=3){
				if(args[i+1].equals("Kad")){
					createNode(NodeType.KAD,args[i+2],synapse);
					continue;
				}
				if(args[i+1].equals("Chord")){
					createNode(NodeType.CHORD,args[i+2],synapse);
					continue;
				}
				throw new SynapseSimException("Unknown node type in synapse creation, correct this and come back");
			}
		}
		throw new SynapseSimException("Unknown node type");
	}
	
	/* ********************************************* */
	/* 					public Methods				 */
	/* ********************************************* */
	public String printTopology() {
		return "not yet implemented...";
	}
	

	/* ********************************************* */
	/* 					simulator UI				 */
	/* ********************************************* */
	public static void main(String[] args) {

		SynapseSim simulator = getInstance();

		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {
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
						System.out.println(simulator.printTopology());
						break;
					case 1:
						System.out.println("Create Node");
						System.out.println("not yet implemented...");
						break;
					case 2:
						System.out.println("put");
						System.out.println("not yet implemented...");
						break;
					case 3:
						System.out.println("get");
						System.out.println("not yet implemented...");
						break;
					case 4:
						simulator.kill();
						System.exit(0);
					default:
						throw new SynapseSimException("How can it be possible to have this case ? You have 2 hours to think about it");
					}
				} catch (NumberFormatException e) {
					System.out.println("what?");
				} catch (IOException e) {
					e.printStackTrace();
				} catch(SynapseSimException e){
					e.printStackTrace();
				}
			} else {
				try {
					input.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
