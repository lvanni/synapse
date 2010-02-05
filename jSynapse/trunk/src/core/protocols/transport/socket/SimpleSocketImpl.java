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

@Deprecated
public class SimpleSocketImpl implements ITransport {

	/** the server port */
	private int serverPort;
	/** the server socket */
	private ServerSocket serverSocket = null;

	/**
	 * The default constructor
	 */
	public SimpleSocketImpl() {
	}

	/**
	 * Constructor
	 * 
	 * @param port
	 *            , port number of the serverSocket
	 * @throws IOException
	 */
	public SimpleSocketImpl(int port) throws IOException {
		do {
			serverSocket = new ServerSocket(port);
		} while (serverSocket == null);
		serverSocket.setSoTimeout(2000);
		serverPort = serverSocket.getLocalPort();
	}

	/**
	 * @see core.protocols.transport.ITransport#sendRequest(String, Node)
	 */
	public String sendRequest(String message, Node destination) {
		Socket socket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;
		try {
			while (socket == null) {
				socket = new Socket(destination.getIp(), destination.getPort());
			}
			pin = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);

			// SENDING A MESSAGE
			pout.println(message);
			pout.flush();

			// WAIT FOR A RESPONSE
			String res = pin.readLine();

			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				socket.close();
				pout.close();
				pin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see core.protocols.transport.ITransport#stopServer()
	 */
	public void stopServer() { /* do nothing */
	}

	/**
	 * @see core.protocols.transport.ITransport#getPort()
	 */
	public int getPort() {
		return serverPort;
	}

	/**
	 * 
	 * @return the server socket
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

}
