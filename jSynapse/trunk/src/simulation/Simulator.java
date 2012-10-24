package simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import core.protocols.p2p.Node;
import core.protocols.transport.IRequestHandler;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;

public class Simulator implements ISimulator, IRequestHandler, Serializable {

	private static Simulator INSTANCE = new Simulator();

	private Simulator() {
		ITransport transport = new SocketImpl(DEFAULT_PORT, 10, RequestHandler.class
				.getName(), 10, 1, 100, this);
		((SocketImpl) transport).launchServer();
	}

	public IRequestHandler getReceiver(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Simulator getInstance() {
		return INSTANCE;
	}

	public String handleRequest(String message) {
		System.out.println(message);
		return message + " world !";
	}

	public void kill() {
		// TODO Auto-generated method stub
	}

	/**
	 * Main function to launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Simulator simulator = getInstance();

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
						System.out.println("not yet implemented...");
						break;
					case 1:
						System.out.println("not yet implemented...");
						break;
					case 2:
						System.out.println("not yet implemented...");
						break;
					case 3:
						System.out.println("not yet implemented...");
						break;
					case 4:
						simulator.kill();
						System.exit(0);
					default:
						break;
					}
				} catch (NumberFormatException e) {
					System.out.println("what?");
				} catch (IOException e) {
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
