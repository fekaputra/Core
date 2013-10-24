/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter for regex matching of RDF data.
 *
 * @author Bogo
 */
public class RDFRegexFilter implements Filter {
	
	private String columnName;
	private String regex;
	
	public RDFRegexFilter(String columnName, String regex) {
		this.columnName = columnName;
		this.regex = regex;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
		String value = item.getItemProperty(columnName).getValue().toString();
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return true;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getRegex() {
		return regex;
	}
	
}
