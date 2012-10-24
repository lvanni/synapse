package experiment.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;

public class Oracle {
	
	/**
	 * Format the response to send to the synapse simulator
	 * 
	 * The response has this form:
	 * command,protocol,networkID,additionnal_information,...
	 * 
	 * @param args
	 * @return
	 */
	private static String formatResponse(String[] args){
		String command="";
		for(int i=0;i<args.length;i++){
			if(i==0){
				command=args[i];
			}
			else{
				command+=","+args[i];
			}
		}
		return command;
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
