package experiment.simulation;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
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
	public String formatResponse(String[] args) throws SynapseSimException{
		if(args.length < 3) {
			throw new SynapseSimException("wrong number of parameters");
		}
		String command="";
		int isEven = 0;
		for(int i=0;i<args.length;i++){
			if(i==0){
				command=createCommand(args[i]).getValue()+"";
			}
			else{
				if((i+1) % 2 == isEven){
					command+=","+createNodeType(args[i]).getValue();
					if (args[i].equals("synapse")) {
						command+=","+"s1";
						isEven = 1;
					}
				}
				else{
					command+=","+args[i];
				}
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
	private Command createCommand(String comm) throws SynapseSimException{
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
	private NodeType createNodeType(String ntype) throws SynapseSimException{
		ntype=ntype.toLowerCase(Locale.ENGLISH);
		//System.out.println("ntype= "+ntype);
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
	public void printInLogsFile(String log){
		try{
			Date date = new Date();
			FileWriter f = new FileWriter("log_synapse.txt",true);
			PrintWriter pw = new PrintWriter(f);
			pw.println(log+" "+ date.toString());
			pw.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Main function to launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		ITransport transport = new SocketImpl(0, 10, RequestHandler.class.getName(), 10, 1, 100, null);
		
		try {

			Node simulator = new Node("localhost", SynapseSim.DEFAULT_PORT);
			Oracle oracle = new Oracle();
			String argument = oracle.formatResponse(args);
			//System.out.println(argument);
			String response = transport.sendRequest(argument, simulator);
			//System.out.println(response);
			oracle.printInLogsFile(response);
			
		} catch (Exception e) {
			System.out.println("usage: \ncreate [chord|kad] <networkId>");
			System.out.println("create synapse [chord|kad] <networkId> [chord|kad] <networkId> ...");
		} finally {
			System.exit(0);
		}
	}

}
