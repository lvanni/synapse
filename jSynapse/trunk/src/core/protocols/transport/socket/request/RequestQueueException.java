package core.protocols.transport.socket.request;


public class RequestQueueException extends Exception{
	private static final long serialVersionUID = -1114788459015621328L;
	
	private String errorMessage;
	
	public RequestQueueException(String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	public void printStackTrace() {
		System.err.println(errorMessage + "\n\n");
		super.printStackTrace();
	}
	
}
