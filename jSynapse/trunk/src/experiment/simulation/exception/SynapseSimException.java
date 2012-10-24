package experiment.simulation.exception;

/**
 * Exception class for synapse simulatror
 *
 */
public class SynapseSimException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2041236774469738968L;
	
	/**
	 * Empty exception
	 */
	public SynapseSimException(){
		this("");
	}
	
	/**
	 * Exception with message
	 * @param message
	 */
	public SynapseSimException(String message){
		super(message);
	}

}
