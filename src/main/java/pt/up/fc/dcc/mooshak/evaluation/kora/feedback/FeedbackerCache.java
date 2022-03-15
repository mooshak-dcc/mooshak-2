/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.feedback;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author hcorreia
 *
 */
public class FeedbackerCache extends LinkedHashMap<Key,FeedbackManager> {

	private static final long serialVersionUID = 1L;
	private static final int INITIAL_CAPACITY = 5000;
	private static final float LOAD_FACTOR = 0.75F;
	private static final boolean ACCESS_ORDER = true;

	private static int maxEntries = 5*INITIAL_CAPACITY;

	public FeedbackerCache() {
		super(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);
	}

	protected boolean removeEldestEntry(Map.Entry<Key,FeedbackManager> eldest) {

		if(size() > maxEntries) {
			return true;
		} else {
			return false;
		}
	}
	
	
}





