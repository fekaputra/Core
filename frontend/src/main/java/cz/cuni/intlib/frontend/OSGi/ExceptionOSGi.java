package cz.cuni.intlib.frontend.OSGi;

/**
 * OSGi framework exception.
 * @author Petyr
 *
 */
public class ExceptionOSGi extends RuntimeException {

	/**
	 * Store original exception.
	 */
	protected Exception originalExpcetion;
	
	/**
	 * 
	 * @param message Exception message.
	 * @param ex Original exception.
	 */
	public ExceptionOSGi(String message, Exception ex) {
		super(message);
		this.originalExpcetion = ex;
	}

}
