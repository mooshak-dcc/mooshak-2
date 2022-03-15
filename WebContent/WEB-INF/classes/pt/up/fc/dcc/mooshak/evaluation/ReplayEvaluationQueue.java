package pt.up.fc.dcc.mooshak.evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.UserTestData;
import pt.up.fc.dcc.mooshak.content.util.Charsets;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.events.ReplayUpdate;

/**
 * Special evaluation queue for replaying a contest
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ReplayEvaluationQueue implements EvaluationQueue {

	private final static Logger LOGGER = Logger.getLogger("");
	
	private final Path home = PersistentObject.getHomePath();
	
	private final EventSender sender = EventSender.getEventSender();
	
	private Iterator<Path> iterator;
	private int total;
	private Map<String,Submission> waiting = new HashMap<>();
	
	private List<String> languages = null;
	private List<String> problems = null;
	
	private Recipient recipient;
	
	private long maxDelay = MAX_DELAY;	// max delay of submissions (in ms)
	
	private int count = 0; // number of submissions so far

	
	ReplayEvaluationQueue(Submissions submissions) {
		List<Path> paths;
		
		try (Stream<Path> files=  Files.list(submissions.getAbsoluteFile())) {
			
			paths = files
				.filter( path -> ! path.getFileName().toString().startsWith("."))
				.sorted()
				.collect(Collectors.toList());
		
		} catch (IOException cause) {
			paths = new ArrayList<>();
			LOGGER.log(Level.WARNING,"Listing submissions for replay",cause);
		}
		
		iterator = paths.iterator();
		total = paths.size();
	}
	
	@Override
	public void enqueueEvaluationRequest(EvaluationRequest request)
			throws MooshakException {

	}


	
	@Override
	public EvaluationRequest dequeueEvaluationRequest() throws MooshakException {

		while(iterator.hasNext()) {
			Submission submission; 
			
			Path path = iterator.next();
			Path relative = home.relativize(path);
			
			try {	
				submission = PersistentObject.open(relative);
				
			} catch (MooshakContentException cause) {
				LOGGER.log(Level.WARNING,"Openning submission "+path,cause);
				continue;
			}
			
			if( ! validLanguage(submission) || ! validProblem(submission))
				continue;
			
			
			String entityTag =  "." + submission.getIdName();
			
			waitFor(submission);

			try {
				waiting.put(entityTag, submission.copy());
			} catch (MooshakContentException cause) {
				LOGGER.log(Level.WARNING,"Error clonning "+entityTag,cause);
			}
			
			return new EvaluationRequest(submission,entityTag);
			
		} 
		
		return new EvaluationRequest(Command.TERMINATE);
	}
	
	/**
	 * Get languages to select for replay
	 * @return the languages
	 */
	public List<String> getLanguages() {
		return languages;
	}

	/**
	 * Set languages to select for replay
	 * @param languages the languages to set
	 */
	public void setLanguages(List<String> languages) {
		if(languages != null && languages.size() == 0)
			this.languages = null;
		else
			this.languages = languages;
	}

	/**
	 * Get problems to select for replay
	 * @return the problems
	 */
	public List<String> getProblems() {
		return problems;
	}

	/**
	 * Set problems to select for replay
	 * @param problems the problems to set
	 */
	public void setProblems(List<String> problems) {
		if(problems != null && problems.size() == 0)
			problems = null;
		else
			this.problems = problems;
	}
	
	private boolean validProblem(Submission submission) {
		boolean valid = false;
		
		if(problems == null) 
			valid = true;
		else {
			String problem = submission.getProblemId();
			if(problems.contains(problem))
				valid = true;
		}
		return valid;
	}

	/**
	 * Get the recipient of the events sent by this replay
	 * @param sender
	 * @return
	 */
	public Recipient getRecipient() {
		return recipient;
	}
	
	/**
	 * Set the recipient of the events sent by this replay
	 * @param recipient
	 */
	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
	
	private boolean validLanguage(Submission submission) {
		boolean valid = false;
		
		if(languages == null) 
			valid = true;
		else {
			try {
				String language = submission.getLanguage().getIdName();
				if(languages.contains(language))
					valid = true;
			} catch (MooshakContentException e) {
				LOGGER.log(Level.WARNING,"REPLAY: Error getting language id",e);
			}
		}
		return valid;
	}

	Submission previous = null;
	
	private static final long MAX_DELAY = 5000; 
	
	/**
	 * Replicate the original delay over consecutive submissions
	 * @param submission
	 */
	private void waitFor(Submission submission) {
		
		if(previous != null) {
			
			long delay = 
					submission.getTime().getTime() -
					previous.getTime().getTime();
			
			if(delay < 0) {
				LOGGER.log(Level.WARNING,"Ignoring invalid delay:"+delay);
				delay = 0;
			} else if(delay > maxDelay)
				delay = maxDelay;
			
			LOGGER.info("Waiting "+delay+"ms for "+submission.getIdName());
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException cause) {
				LOGGER.log(Level.WARNING,"Error delaying submission for",cause);
			}
			
		}
		
		previous = submission;
	}

	/**
	 * Get inputs provided by user
	 * @param submission
	 * @return
	 */
	List<String> getInputs(Submission submission) {


		if(submission.hasUserTestData()) {
			List<String> inputs = new ArrayList<>();
			
			try {	
				UserTestData userData = submission.getUserTestData();

				for(Path path: userData.getInputFiles()) {
					byte[] content = getFileContent(path);
					inputs.add(Charsets.fixCharset(content));
				}


			} catch (IOException cause) {
					LOGGER.log(Level.SEVERE,"I/O error reading input files",
							cause);
			} catch (MooshakException cause) {
				LOGGER.log(Level.SEVERE,
						"Getting user data folder for "+submission,cause);
			}

			return inputs;
		} else 
			return null;
	}


	/**
	 * Get content of a file as a byte array
	 * @param path of file relative to home
	 * @throws IOException
	 */
	private byte[] getFileContent(Path path) throws IOException {
		Path absolute = PersistentObject.getAbsoluteFile(path);
		
		byte[] fileContent = Files.readAllBytes(absolute);

		return fileContent;
	}
	
	
	@Override
	public void concludeEvaluation(Submission submission) throws MooshakException {
		String id = submission.getIdName();
		Submission previous = waiting.get('.' + id);
		String language = "??";
		String extra = "";
		Level level;
		
		try {
			Language languageData = submission.getLanguage();
			if (languageData != null)
				language = languageData.getName();
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.WARNING,"Getting language name",cause);
		}
		
		if (previous.getClassify() == submission.getClassify()) {
			level = Level.INFO;
		} else {
			level = Level.WARNING;
			extra = submission.getObservations();
		}
			
		count++;
		
		ReplayUpdate event = new ReplayUpdate();
		String order = String.format("#%03d/%-3d",count,total);
		
		event.setId(order);
		event.setRecipient(recipient);
		event.addValue("Order",order);
		event.addValue("Team",previous.getTeamId());
		event.addValue("Problem",previous.getProblemId());
		event.addValue("Language",language);
		event.addValue("Classification", previous.getClassify().toString());
		event.addValue("Reclassification" , submission.getClassify().toString());
		event.addValue("Extra",extra);
		
		sender.send(event);

		LOGGER.log(				
				level,
				String.format("%s  %-40s  %-20s %-20s  %-10s %s" 
				,order,previous.getIdName()
				,previous.getClassify()
				,submission.getClassify()
				,language
				,extra));
		
		
		waiting.remove(id);
		try {
			submission.delete();
		} catch (MooshakContentException e) {
			LOGGER.log(Level.WARNING,"Could not delete reevaluation");
		}
	}

	/**
	 * Set maximum delay between consecutive evaluations (in seconds)
	 * @param delay
	 */
	public void setMaxDelay(int maxDelay) {
		this.maxDelay = (long) maxDelay * 1000L;
	}

	/**
	 * Get maximum delay between consecutive evaluations (in seconds)
	 * @return
	 */
	public int getMaxDelay() {
		return (int) maxDelay / 1000;
	}

}
