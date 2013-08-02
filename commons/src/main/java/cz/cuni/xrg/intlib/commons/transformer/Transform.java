package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;

/**
 * Is responsible for transforming- cleaning or enriching RDF data.
 * 
 * Transformations may include e.g.
 * <ul>
 * <li>Cleaning data</li>
 * <li>Converting to another RDF schema</li>
 * <li>Linking resources to external datasets</li>
 * </ul>
 * 
 * 
 * @author Jiri Tomes
 */
public interface Transform {

	/**
	 * Transforms the cached RDF data in the repository.
	 * 
	 * @param context The context containing meta information about this
	 *            transformation process
	 * @throws TransformException If the transformation fails, this exception
	 *             has to be thrown
	 * @throws DataUnitException
	 * @throws DPUException
	 */
	public void transform(TransformContext context)
			throws TransformException,
				DataUnitException,
				DPUException;
}
