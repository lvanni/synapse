package simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.protocols.transport.IRequestHandler;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;

public class Simulator implements IRequestHandler {

	public Simulator() {
		ITransport transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
				10, 1, 100, this);
		((SocketImpl) transport).launchServer();
	}

	public String handleRequest(String message) {
		return null;
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

		Simulator simulator = new Simulator();

		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {
			System.out.println("0) Status");
			System.out.println("1) Quit");
			System.out.print("---> ");
			try {
				int chx = Integer.parseInt(input.readLine().trim());
				switch (chx) {
				case 0 : break;
				case 1 : 
					simulator.kill(); 
					System.exit(0);
				default : break;
				}
			} catch (NumberFormatException e) {
				System.out.println("what?");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
