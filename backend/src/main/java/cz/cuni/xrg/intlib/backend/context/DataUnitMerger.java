package cz.cuni.xrg.intlib.backend.context;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.ManagableDataUnit;

/**
 * Basic interface for data merger.
 * 
 * @author Petyr
 * 
 */
class DataUnitMerger {

	private static final Logger LOG = LoggerFactory.getLogger(DataUnitMerger.class);
	
	/**
	 * Search for first command that can be applied to the DataUnit with given
	 * name.
	 * 
	 * @param dataUnitName DataUnit's name.
	 * @param instruction
	 * @return Command or empty string.
	 */
	protected String findRule(String dataUnitName, String instruction) {
		// check for null
		if (instruction == null) {
			return "";
		}

		String[] rules = instruction.split(DataUnitMergerInstructions.Separator
				.getValue());
		for (String item : rules) {
			String[] elements = item.split(" ", 2);
			// test name ..
			if (elements.length < 2) {
				// not enough data .. skip
			} else { // elements.length == 2
				if (elements[0].compareToIgnoreCase(dataUnitName) == 0) {
					// math !!
					return elements[1];
				}
			}
		}
		return "";
	}	
	
	/**
	 * Merge the data, the result is store in 'left'. If the two Lists of
	 * DataUnits can't be merge throw ContextException.
	 * 
	 * @param left Target {@link DataUnitManager}.
	 * @param right Source of DataUnits, do not change!
	 * @param instruction Instruction for merger. See
	 *            {@link cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions}
	 * @throw ContextException
	 */
	public void merger(DataUnitManager left, List<ManagableDataUnit> right, 
			String instruction) throws ContextException {
		Iterator<ManagableDataUnit> iterRight = right.iterator();

		// add the rest from right
		while (iterRight.hasNext()) {
			
			DataUnit rightDataUnit = iterRight.next();
			String rightDataUnitName = rightDataUnit.getURI();
			// name for new DataUnit, use right's name as default
			String leftDataUnitName = rightDataUnitName;
			// get command
			String cmd = this.findRule(rightDataUnitName, instruction);
			if (cmd == "") {
				// nothing .. use name from the rightDataUnit 
				leftDataUnitName = rightDataUnitName;
			} else {
				String[] cmdSplit = cmd.split(" ");
				if (cmdSplit[0].compareToIgnoreCase(
						DataUnitMergerInstructions.Rename.getValue()) == 0) {
					// renaming .. we need second arg
					if (cmdSplit.length == 2) {
						leftDataUnitName = cmdSplit[1];
						LOG.info("renaming: {} -> {}", rightDataUnitName, leftDataUnitName);
					} else {
						// not enough parameters .. use right name
						leftDataUnitName = rightDataUnitName;
						LOG.info("passing: {}", rightDataUnitName);
					}
				} else if (cmdSplit[0].compareToIgnoreCase(
						DataUnitMergerInstructions.Drop.getValue()) == 0) {
					// drop this DataUnit -> skip
					LOG.info("dropping: {}", rightDataUnitName);
					continue;
				}			
			}
			
			// we need dataUnit into which merge data
			ManagableDataUnit leftDataUnit = null;
			// first check for existing one
			for(ManagableDataUnit item : left.getDataUnits()) {
				if (item.getURI().compareTo(leftDataUnitName) == 0 && 
						item.getType() == rightDataUnit.getType()) {
					LOG.info("merge into existing dataUnit: {}", rightDataUnitName);
					// DataUnit with same name and type already exist, use it
					leftDataUnit = item;
					break;
				}
			}
			
			// create new data unit (in context into which we merge)			
			if (leftDataUnit == null) {
				try {
					LOG.info("creating new dataUnit: {}", rightDataUnitName);
					leftDataUnit = left.addDataUnit(rightDataUnit.getType(), leftDataUnitName);
				} catch (DataUnitCreateException e) {
					throw new ContextException("Failed to create input object.", e);
				}
			}
			// and copy the data
			try {
				leftDataUnit.merge(rightDataUnit);
			} catch (IllegalArgumentException e) {
				throw new ContextException(
						"Can't merge data units, type miss match.", e);
			} catch (Exception e) {
				throw new ContextException("Can't merge data units.", e);
			}
		}
	}

}
