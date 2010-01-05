package core.protocols.transport.socket.request;

import java.net.Socket;

import core.protocols.transport.IRequestHandler;

public class RequestThread extends Thread {
	private RequestQueue queue;

	private boolean running;

	private boolean processing = false;

	private RequestHandler requestHandler;

	public RequestThread(RequestQueue queue, String requestHandlerClassName,
			IRequestHandler overlay) {
		this.queue = queue;
		try {
			this.requestHandler = (RequestHandler) (Class
					.forName(requestHandlerClassName).newInstance());
			this.requestHandler.setOverlay(overlay);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isProcessing() {
		return this.processing;
	}

	public void killThread() {
		this.running = false;
	}

	public void run() {
		this.running = true;
		while (running) {
			try {
				Object o = queue.getNextObject();
				if (running) {
					Socket socket = (Socket) o;
					this.processing = true;
					this.requestHandler.handleRequest(socket);
					this.processing = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}