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
	
	/* ********************************************* */
	/* 					Attribute					 */
	/* ********************************************* */
	
	private Map<String, IDHT> topology;
	

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
		// TODO Auto-generated method stub
		return null;
	}

	/* ********************************************* */
	/* 			implements IRequestHandler			 */
	/* ********************************************* */
	public String handleRequest(String message) {
		try {
			return commandExecutor(commandInterpretor(message));
		} catch (SynapseSimException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	public void kill() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Interpret user command
	 * @param message
	 * @return
	 */
	private int commandInterpretor(String message){
		String[] messageDetail = message.split(",");
		if(messageDetail[0].equals("print")){
			return 0;
		}
		if(messageDetail[0].equals("create")){
			return 1;
		}
		if(messageDetail[0].equals("put")){
			return 2;
		}
		if(messageDetail[0].equals("get")){
			return 3;
		}
		if(messageDetail[0].equals("quit")){
			return 4;
		}
		return -1;
	}
	
	/**
	 * Execute user command
	 * @param choice
	 * @throws SynapseSimException
	 */
	private String commandExecutor(int choice) throws SynapseSimException{
		switch (choice) {
		case 0:
			System.out.println("Print topology");
			return "not yet implemented...";
		case 1:
			System.out.println("Create Node");
			return "not yet implemented...";
		case 2:
			System.out.println("put");
			return "not yet implemented...";
		case 3:
			System.out.println("get");
			return "not yet implemented...";
		case 4:
			this.kill();
			System.exit(0);
		default:
			throw new SynapseSimException("How can it be possible to have this case ? You have 2 hours to think about it");
		}
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
						System.out.println("Print topology");
						System.out.println("not yet implemented...");
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
