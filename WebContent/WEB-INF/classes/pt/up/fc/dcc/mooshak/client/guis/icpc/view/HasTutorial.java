package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import com.hydro4ge.raphaelgwt.client.Raphael;

/**
 * Interface to be implemented by views which have tutorial
 * 
 * @author josepaiva
 */
public interface HasTutorial {

	/**
	 * Adds the tutorial film to the screen and returns it
	 * 
	 * @return
	 */
	public Raphael showTutorialPanel();

	/**
	 * Removes the tutorial film of the screen
	 */
	public void hideTutorialPanel();
}
