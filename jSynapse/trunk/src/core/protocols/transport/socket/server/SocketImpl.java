package core.protocols.transport.socket.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import core.protocols.p2p.Node;
import core.protocols.transport.IRequestHandler;

/**
 * This class represent the socket transport layer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class SocketImpl extends AbstractServer {

	/** The max TTL when a destination is unreachable */
	private static int MAX_TTL = 200;
	/** The current TTL */
	private static int TTL = MAX_TTL;
	/** The request handler to use when a request is received */
	private IRequestHandler overlay = null;

	/**
	 * Default contructor
	 * 
	 * @see core.protocols.transport.socket.server.AbstractServer#AbstractServer(int,
	 *      int, String, int, int, int, IRequestHandler);
	 * @param port
	 * @param backlog
	 * @param requestHandlerClassName
	 * @param maxQueueLength
	 * @param minThreads
	 * @param maxThreads
	 * @param overlay
	 */
	public SocketImpl(int port, int backlog, String requestHandlerClassName,
			int maxQueueLength, int minThreads, int maxThreads,
			IRequestHandler overlay) {
		super(port, backlog, requestHandlerClassName, maxQueueLength,
				minThreads, maxThreads, overlay);
		this.overlay = overlay;
	}

	/**
	 * @see core.protocols.transport.ITransport#sendRequest(String, Node)
	 */
	public String sendRequest(String message, Node destination) {
		if (TTL == 0) {
			overlay.kill();
		}
		Socket socket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;
		try {
			while ((socket = new Socket(destination.getIp(), destination
					.getPort())) == null) {
				System.out.println("can not create a socket to "
						+ destination.getIp() + ":" + destination.getPort());
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
			TTL = MAX_TTL;
			try {
				socket.close();
				pout.close();
				pin.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.err.println("\tno socket opended...");
			}
			return res;
		} catch (IOException e) {
			System.err.println("\n\n[" + TTL-- + "] : time to live");
			System.out.println("trying to send: " + message + "\nto: " + destination);
			System.err.println(e.getMessage() + " => cached");
			System.err.println("\t\"unreachable\" message is sent!");
			return "unreachable";
		} finally {
			try {
				socket.close();
				pout.close();
				pin.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.err.println("\tno socket opended...");
			}
		}
	}
}
