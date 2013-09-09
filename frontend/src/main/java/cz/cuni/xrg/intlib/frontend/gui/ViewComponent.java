package cz.cuni.xrg.intlib.frontend.gui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;

/**
 * Base abstract class for views. Provide functionality like CustomComponent
 * but also include support for {@link com.vaadin.navigator.Navigator} class.
 * 
 * @author Petyr
 *
 */
public abstract class ViewComponent extends CustomComponent implements View{

	/**
	 * The parameter event can be null.
	 */
	@Override
	public abstract void enter(ViewChangeEvent event);
	
	/**
	 * Method for testing if ViewCoponent was modified since last save.
	 * 
	 * @return Is component modified?
	 */
	public boolean isModified() {
		return false;
	}

	public boolean saveChanges() {
		return true;
	}

}
