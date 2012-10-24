package core.protocol.transport.socket.request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import core.protocol.transport.IRequestHandler;

public class RequestQueue {

	private LinkedList<Object> queue = new LinkedList<Object>();

	private int maxQueueLength;

	private int minThreads;

	private int maxThreads;

	private int currentThreads = 0;

	private String requestHandlerClassName;

	private List<RequestThread> threadPool = new ArrayList<RequestThread>();

	private IRequestHandler overlay;

	private boolean running = true;

	public RequestQueue(String requestHandlerClassName, int maxQueueLength,
			int minThreads, int maxThreads, IRequestHandler overlay) {

		this.requestHandlerClassName = requestHandlerClassName;
		this.maxQueueLength = maxQueueLength;
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
		this.currentThreads = this.minThreads;
		this.overlay = overlay;

		for (int i = 0; i < this.minThreads; i++) {
			RequestThread thread = new RequestThread(this,
					requestHandlerClassName, overlay);
			thread.start();
			this.threadPool.add(thread);
		}
	}

	public String getRequestHandlerClassName() {
		return this.requestHandlerClassName;
	}

	public synchronized void add(Object o) throws RequestQueueException {
		if (queue.size() > this.maxQueueLength) {
			throw new RequestQueueException(
					"The Request Queue is full. Max size = "
							+ this.maxQueueLength);
		}
		queue.addLast(o);

		boolean availableThread = false;
		for (Iterator<RequestThread> i = this.threadPool.iterator(); i
				.hasNext();) {
			RequestThread rt = (RequestThread) i.next();
			if (!rt.isProcessing()) {
				availableThread = true;
				break;
			}
//			System.out.println("Thread is busy");
		}

		if (!availableThread) {
			if (this.currentThreads < this.maxThreads) {
				RequestThread thread = new RequestThread(this,
						this.requestHandlerClassName, overlay);
				currentThreads++;
				thread.start();
				this.threadPool.add(thread);
			} else {
				System.err.println("too much threads! maxThreads = "
						+ maxThreads);
			}
		}
		notifyAll();
	}

	public synchronized Object getNextObject() {
		while (queue.isEmpty()) {
			try {
				if (!running)
					return null;
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return queue.removeFirst();
	}

	public synchronized void shutdown() {
		this.running = false;
		for (Iterator<RequestThread> i = this.threadPool.iterator(); i
				.hasNext();) {
			RequestThread rt = (RequestThread) i.next();
			rt.killThread();
		}
		notifyAll();
	}
}