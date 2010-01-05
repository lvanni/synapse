package core.protocols.transport.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;

public class SimpleSocketImpl implements ITransport{

	private int serverPort;
	private ServerSocket serverSocket = null;

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public SimpleSocketImpl(){}

	public SimpleSocketImpl(int port) throws IOException {
		do{
			serverSocket = new ServerSocket(port);
		} while(serverSocket == null);
		serverSocket.setSoTimeout(2000);
		serverPort = serverSocket.getLocalPort();
	}

	/**
	 * Sending
	 * @throws Exception 
	 */
	public String sendRequest(String message, Node destination){
		Socket socket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;
		try{
			while(socket == null){
				socket = new Socket(destination.getIp(), destination.getPort());
			}
			pin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pout = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);

			// SENDING A MESSAGE
			pout.println(message); 
			pout.flush();

			// WAIT FOR A RESPONSE
			String res = pin.readLine();

			return  res;
		} catch(IOException e){
			e.printStackTrace();
			return "";
		} finally {
			try{
				socket.close();
				pout.close();
				pin.close();
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
