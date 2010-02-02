package core.protocols.transport.socket.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ServerSocketFactory;

import core.protocols.transport.IRequestHandler;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestQueue;

public abstract class AbstractServer extends Thread implements ITransport {
	protected ServerSocket serverSocket;
	protected RequestQueue requestQueue;
	protected int port;
	/** how many connections are queued */
	protected int backlog;

	public AbstractServer(int port, int backlog,
			String requestHandlerClassName, int maxQueueLength, int minThreads,
			int maxThreads, IRequestHandler overlay) {
		this.port = port;
		this.backlog = backlog;
		this.requestQueue = new RequestQueue(requestHandlerClassName,
				maxQueueLength, minThreads, maxThreads, overlay);
	}

	public void launchServer() {
		try {
			ServerSocketFactory ssf = ServerSocketFactory.getDefault();
			serverSocket = ssf.createServerSocket(this.port, this.backlog);
			this.port = serverSocket.getLocalPort();
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			this.serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (!serverSocket.isClosed()) {
			try {
				Socket s = serverSocket.accept();
				this.requestQueue.add(s);
			} catch (SocketException e) {
				if(!serverSocket.isClosed()){
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.requestQueue.shutdown();
	}

	public int getPort() {
		return port;
	}
}