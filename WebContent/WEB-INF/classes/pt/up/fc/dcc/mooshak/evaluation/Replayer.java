package pt.up.fc.dcc.mooshak.evaluation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

/**
 * Class for replaying a contest 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Replayer {
	private final static Logger LOGGER = Logger.getLogger("");
	
	private static Map<String,Replayer> replayers = 
			new HashMap<String,Replayer>();
	private static int count = 0;
	
	private Evaluator evaluator; 
	private ReplayEvaluationQueue queue;
	private String id = "Replayer"+(count++); 

	public Replayer(Submissions submissions) throws MooshakException {
		
		queue = new ReplayEvaluationQueue(submissions);
		evaluator = new Evaluator("Replayer",queue);
		
		replayers.put(id, this);

		evaluator.onEnd( () -> replayers.remove(id) );
		
	}
	
	/**
	 * Get a processing replayer given its ID.
	 * @param id
	 * @return
	 */
	public static Replayer getReplayer(String id) {
		return replayers.get(id);
	}
	
	/**
	 * Get id of this replayer. Replayers can be recovered using id.
	 * See @see pt.up.fc.dcc.mooshak.evaluation.Replayer#getReplayer(String)
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	
	/**
	 * Get languages to select for replay
	 * @return the languages
	 */
	public List<String> getLanguages() {
		return queue.getLanguages();
	}

	/**
	 * Set languages to select for replay
	 * @param languages the languages to set
	 */
	public void setLanguages(List<String> languages) {
		queue.setLanguages(languages);
	}

	/**
	 * Get problems to select for replay
	 * @return the problems
	 */
	public List<String> getProblems() {
		return queue.getProblems();
	}

	/**
	 * Set problems to select for replay
	 * @param problems the problems to set
	 */
	public void setProblems(List<String> problems) {
		queue.setProblems(problems);
	}
	
	/**
	 * Get the recipient of the events sent by this replay
	 * @param sender
	 * @return
	 */
	public Recipient getRecipient() {
		return queue.getRecipient();
	}
	
	/**
	 * Set the recipient of the events sent by this replay
	 * @param recipient
	 */
	public void setRecipient(Recipient recipient) {
		queue.setRecipient(recipient);
	}
	
	/**
	 * Get maximum delay between consecutive evaluations (in seconds)
	 * @return
	 */
	public int getMaxDelay() {
		return queue.getMaxDelay();
	}
	
	/**
	 * Set maximum delay between consecutive evaluations (in seconds)
	 * @param delay
	 */
	public void setMaxDelay(int delay) {
		queue.setMaxDelay(delay);
	}
	
	Date begin = null;
	
	public void begin() {
		begin = new Date(); 
		evaluator.begin();
	}
	
	public void stop() {
		evaluator.setPaused(true);
	}
	
	public void resume() {
		evaluator.setPaused(true);
	}
	
	public void end() {
	
		evaluator.end();
		replayers.remove(id);
	}
	
	public void join() throws InterruptedException {
		evaluator.join();
		
		if(begin != null) {
			long time = (new Date()).getTime() - begin.getTime();
			LOGGER.log(Level.INFO,"Replay completed in "+(time/1000)+"sec");
		}
		replayers.remove(id);
	}

	
}
