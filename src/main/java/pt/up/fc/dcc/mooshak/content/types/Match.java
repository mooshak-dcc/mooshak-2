package pt.up.fc.dcc.mooshak.content.types;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * {@link Match} is an execution of a game with a set of players.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Match extends PersistentContainer<MatchResultEntry> {
	private static final long serialVersionUID = 1L;
	
	private static final String MOVIE_FILE_NAME = "movie.json";
	
	/*private static final String SEP = "\n";*/

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning;
	
	/*@MooshakAttribute(name = "Submissions", type = AttributeType.LABEL)
	private String submissions = "";*/
	
	@MooshakAttribute(
			name = "Movie",
			type = AttributeType.FILE, 
			tip = "File with the movie of the match")
	private Path movie = null;
	
	@MooshakAttribute(
			name = "Bye",
			type = AttributeType.MENU)
	private MooshakAttribute.YesNo bye;
	
	@MooshakAttribute(
			name = "result", 
			type = AttributeType.CONTENT)
	private Void matchResultEntry;
	
	/*-------------------------------------------------------------------*\
	 * 		            Setters and getters                              *
	\*-------------------------------------------------------------------*/

	/**
	 * Fatal errors messages of this folder
	 * 
	 * @return the fatal
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;
	}

	/**
	 * Set fatal error messages
	 * 
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * Warning errors messages of this folder
	 * 
	 * @return the warning
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;
	}

	/**
	 * Set warning error messages
	 * 
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Get the path to the movie of this match
	 * 
	 * @return {@link Path} to the movie of this match
	 */
	public Path getMovie() {
		if (movie == null)
			return null;
		else
			return getPath().resolve(movie);
	}

	/**
	 * Set the path to the movie of this match
	 * 
	 * @param movie {@link Path} to the movie of the match
	 */
	public void setMovie(Path movie) {
		if (movie == null)
			this.movie = null;
		else
			this.movie = movie.getFileName();
	}
	
	/**
	 * Is this match a bye?
	 * 
	 * @return {@code boolean} <code>true</code> if this match is a bye, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isBye() {
		
		if(YesNo.YES.equals(bye))
			return true;	
		else
			return false;	
	}
	
	/**
	 * Set if this match is a bye
	 * 
	 * @return <code>true</code> if this match is a bye, 
	 * 		<code>false</code> otherwise
	 */
	public void setBye(boolean bye) {
		if(bye)
			this.bye = YesNo.YES;
		else
			this.bye = YesNo.NO;
	}
	
	/**
	 * Add a submission to the match
	 * 
	 * @param submission
	 */
	/*public void addSubmission(Submission submission) {
		if (!this.submissions.isEmpty()) 
			this.submissions += SEP;
		this.submissions += submission.getIdName();
	}*/
	
	/**
	 * Remove a submission of the match
	 * 
	 * @param submission
	 */
	/*public void removeSubmission(Submission submission) {
		submissions = submissions.replace(submission.getIdName(), "").replace(SEP + SEP, SEP).trim();
	}*/
	
	/**
	 * Fetch the list of submissions that participated in this match
	 * 
	 * @return {@link List<Submission>} that played this match
	 * @throws MooshakContentException 
	 */
	/*public List<Submission> getSubmissionsList() throws MooshakContentException {
		
		List<Submission> submissionsList = new ArrayList<>();
		
		if (submissions.isEmpty()) 
			return submissionsList;
		
		String[] submissionIds = submissions.split(SEP);
		
		// round -> stage -> tournament -> contest
		Contest contest = getParent().getParent().getParent().getGrandParent();
		
		Submissions submissions = contest.open("submissions");
		
		for (String submissionId : submissionIds) {
			Submission submission = submissions.find(submissionId);
			
			if (submission == null)
				continue;
			
			submissionsList.add(submission);
		}
		
		return submissionsList;
	}*/
	
	/**
	 * Get submissions {@link Submission} participating in the match
	 * 
	 * @return submissions {@link Submission[]} participating in the match
	 * @throws MooshakException 
	 */
	public Submission[] getSubmissions() throws MooshakException {
		
		List<PersistentObject> resultEntries = getChildren(true);
		
		return resultEntries.stream().map(po -> {
			MatchResultEntry resultEntry = (MatchResultEntry) po;
			try {
				return resultEntry.getSubmission();
			} catch (MooshakException e) {
				return null;
			}
		}).toArray(Submission[]::new);
	}
	
	/**
	 * Save JSON file
	 * 
	 * @param json {@link String} movie JSON
	 * @throws MooshakException - if an error occurs while saving a file
	 */
	public void saveMovie(String json) throws MooshakException {

		File file = getAbsoluteFile(MOVIE_FILE_NAME).toFile();
		
		try (
				BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			
			bw.append(json);
			bw.flush();
		} catch (IOException e) {
			throw new MooshakException("Writing JSON file.");
		}
		
		setMovie(file.toPath());
		save();
	}
	
	/**
	 * Read JSON file
	 * 
	 * @param {@link String} movie JSON
	 * @throws MooshakException - if an error occurs while reading a file
	 */
	public String readMovie() throws MooshakException {

		File file = getAbsoluteFile().resolve(getMovie().getFileName()).toFile();
	
		String movieJson = "";
		try (
				BufferedReader br = new BufferedReader(new FileReader(file))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				movieJson += line;
			}
		} catch (IOException e) {
			throw new MooshakException("Writing JSON file.");
		}
		
		return movieJson;
	}
}
