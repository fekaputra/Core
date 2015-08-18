/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.frontend.gui.views;

/**
 * Base interface for presenter classes. The presenters represents
 * the possible views in application.
 * 
 * @author Petyr
 */
public interface Presenter {

    /**
     * Activate presenter. Should return the graphical user interface object
     * that will be set as main view for application.
     * 
     * @return main view for application
     */
    Object enter();

    /**
     * Set the parameters to the view. Can be called only after previously called {@link #enter()}.
     * 
     * @param configuration
     */
    void setParameters(Object configuration);

}
