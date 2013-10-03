package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 *
 * @author Jiri Tomes
 */
public class GraphNotEmptyException extends DataUnitException {

	private static String defaultMessage = "Target graph is not empty. Load to SPARQL endpoint fail.";

	public GraphNotEmptyException() {
		super(defaultMessage);
	}

	public GraphNotEmptyException(String message) {
		super(message);
	}

	public GraphNotEmptyException(Throwable cause) {
		super(cause);
	}

	public GraphNotEmptyException(String message, Throwable cause) {
		super(message, cause);
	}
}