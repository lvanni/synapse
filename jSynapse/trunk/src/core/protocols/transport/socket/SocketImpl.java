package core.protocols.transport.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


import core.overlay.concert.Concert;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;

public class SocketImpl implements ITransport{

	private int serverPort;
	private ServerSocket serverSocket = null; 

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public SocketImpl(int port){
		do{
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while(serverSocket == null);
		serverPort = serverSocket.getLocalPort();
	}

	/**
	 * Sending
	 * @throws Exception 
	 */
	public String forward(String message, Node destination){
		Socket socket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;
		try{
			while(socket == null){
				try{
					socket = new Socket(destination.getIp(), destination.getPort());
				} catch (Exception e){}
			}
			socket.setSoTimeout(2000); // TO NOT BLOCK THE READ OPERATION
			pin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pout = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);

			// SENDING A MESSAGE
			pout.println(message); 
			if(Concert.debugMode){
				System.out.println("\n** DEBUG: forward\n*\tmessage: " + message + "\n************************************");
			}

			// WAIT FOR A RESPONSE
			String res = pin.readLine();
			return  res;
		} catch(IOException e){
			return "";
		} finally {
			try{
				socket.close();
				pin.close();
				pout.close();
			} catch(IOException  e){
				e.printStackTrace();
			}
		}
	}
	
	// /////////////////////////////////////////// //
	//              GETTER AND SETTER              //
	// /////////////////////////////////////////// //
	public int getPort() {
		return serverPort;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

}
