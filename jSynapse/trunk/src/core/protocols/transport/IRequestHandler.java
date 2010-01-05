package core.protocols.transport;

public interface IRequestHandler {
	public String handleRequest(String code);
	public void kill();
}
