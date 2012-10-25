package experiment.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;
import experiment.simulation.ISynapseSim.Command;
import experiment.simulation.ISynapseSim.NodeType;
import experiment.simulation.exception.SynapseSimException;

public class Oracle {
	
	/**
	 * Format the response to send to the synapse simulator
	 * 
	 * The response has this form:
	 * command,protocol,networkID,additionnal_information,...
	 * 
	 * @param args
	 * @return
	 * @throws SynapseSimException 
	 */
	private static String formatResponse(String[] args) throws SynapseSimException{
		String command="";
		for(int i=0;i<args.length;i++){
			if(i==0){
				command=createCommand(args[i]).getValue()+"";
			}
			else{
				if((i+1) % 2 == 0){
					command+=","+createNodeType(args[i]).getValue();
				}
				command+=","+args[i];
			}
		}
		return command;
	}
	
	/**
	 * Transform string in the code representing a command
	 * to pass to the simulator
	 * 
	 * @param comm
	 * @return
	 * @throws SynapseSimException
	 */
	private static Command createCommand(String comm) throws SynapseSimException{
		comm = comm.toLowerCase(Locale.ENGLISH);
		if(comm.equals("create")){
			return Command.CREATE;
		}
		if(comm.equals("put")){
			return Command.PUT;
		}
		if(comm.equals("get")){
			return Command.GET;
		}
		else{
			throw new SynapseSimException("Unknown command in the oracle, maybe you wanted a coffee but sorry this program can't do that");
		}
	}
	
	/**
	 * Transform string in the code representing a node type
	 * to pass to the simulator
	 * 
	 * @param ntype
	 * @return
	 * @throws SynapseSimException
	 */
	private static NodeType createNodeType(String ntype) throws SynapseSimException{
		ntype=ntype.toLowerCase(Locale.ENGLISH);
		if(ntype.equals("chord")){
			return NodeType.CHORD;
		}
		if(ntype.equals("kad")|| ntype.equals("kademlia")){
			return NodeType.KAD;
		}
		if(ntype.equals("synapse")){
			return NodeType.SYNAPSE;
		}
		else{
			throw new SynapseSimException("Unknown node type in the oracle");
		}
	}
	
	/**
	 * Print result of commands in a log file
	 * @param log
	 */
	private static void printInLogsFile(String log){
		File f = new File("log_synapse.txt");
		PrintWriter pw=null;
		try {
			pw = new PrintWriter(f);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pw.println(log);
	}
	
	/**
	 * Main function to launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			ITransport transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 100, null);
			
			Node simulator = new Node("localhost", SynapseSim.DEFAULT_PORT);
			String response = transport.sendRequest(formatResponse(args), simulator);
			System.out.println(response);
			printInLogsFile(response);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("usage: \nCreate node [chord|kad] <networkID>");
			System.out.println("Create synapse [-a [chord|kad] <networkID>]+");
		}
	}

}
