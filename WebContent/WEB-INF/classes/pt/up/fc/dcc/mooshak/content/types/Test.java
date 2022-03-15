package pt.up.fc.dcc.mooshak.content.types;


import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
/**
 * Element of a test vector. Instances of this class 
 * are persisted locally.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * 
 * Recoded in Java in         June  2012
 * From a Tcl module coded in April 2001
 */
public class Test extends PersistentObject implements Comparable<Test> {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	public String fatal = null;
	
	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	public String warning = null;

	@MooshakAttribute( 
			name="args",
			tip = "Arguments passed to the tested program")
	public String args = null;
	
	
	@MooshakAttribute( 
			name="input",
			type=AttributeType.FILE,
			tip = "File containing input for this test")
	public Path input = null;
	
	@MooshakAttribute( 
			name="output",
			type=AttributeType.FILE,
			tip = "File containing output for this test")
	public Path output = null;
	
	
	@MooshakAttribute( 
			name="Points",
			tip = "File containing output for this test",
			type = AttributeType.INTEGER)
	public Integer points = null;
	
	@MooshakAttribute( 
			name="context",
			type=AttributeType.FILE,
			tip = "File available for dynamic evaluators")
	public Path context = null;
	
	@MooshakAttribute( 
			name="Feedback",
			tip = "Message as feedback when reporting this test failure")
	public String feedback = null;

	@MooshakAttribute( 
			name="Show",
			tip = "Show this input/output files when reporting errors",
			type= AttributeType.MENU)
	public MooshakAttribute.YesNo show = null;

	@MooshakAttribute( 
			name="Result",
			tip = "Result obtained with input against solution is equal to output",
			type= AttributeType.HIDDEN)
	public String result = null;

	@MooshakAttribute( 
			name="Timeout",
			type= AttributeType.HIDDEN,
			tip="Maximum time for executin this test, in seconds")
	private Integer timeout = null;

	@MooshakAttribute( 
			name="SolutionErrors",
			type= AttributeType.HIDDEN,
			tip="Solutions where this test got wrong result (comma separated)")
	private String solutionErrors = null;

	//-------------------- Setters and getters ----------------------//

	/**
	 * Get fatal errors messages
	 * @return
	 */
	public String getFatal() {
		if(fatal == null)
			return "";
		else
			return fatal;
					
	}
	
	/**
	 * Set fatal error messages
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	
	/**
	 * get warning errors messages
	 * @return
	 */
	public String getWarning() {
		if(warning == null)
			return "";
		else
			return warning;
					
	}
	
	/**
	 * Set warning error messages
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Get argument to be passed to this test
	 * @return
	 */
	public String getArgs() {
		if(args==null)
			return "";
		else
			return args;
	}

	/**
	 * Set argument to be passed to this test; null reverts to default
	 * @param args
	 */
	public void setArgs(String args) {
		this.args = args;
	}

	/**
	 * Get input file associated with this test
	 * @return
	 */
	public Path getInput() {
		if(input == null)
			return null;
		else 
			return getPath().resolve(input);
	}

	/**
	 * Set input file associated with this test
	 * @param input
	 */
	public void setInput(Path input) {
		this.input = input.getFileName();
	}

	/**
	 * Get output file associated with this test
	 * @return
	 */
	public Path getOutput() {
		if(output == null)
			return null;
		else
			return getPath().resolve(output);
	}

	/**
	 * Set output file associated with this test
	 * @param input
	 */
	public void setOutput(Path output) {
		this.output = output.getFileName();
	}

	/**
	 * Get points to add if this test passes
	 * @return
	 */
	public int getPoints() {
		if(points == null)
			return 0;
		else
			return points;
	}

	/**
	 * Set points to add if this test passes; or null to revert to default
	 * @return
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Get file with data available for dynamic evaluators
	 * @return
	 */
	public Path getContext() {
		if(context == null)
			return null;
		else
			return getPath().resolve(context);
	}

	/**
	 * Set file with data available for dynamic evaluators
	 * @param context
	 */
	public void setContext(Path context) {
		this.context = context.getFileName();
	}

	/**
	 * Get message used as feedback when reporting that this test failed
	 * @return
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * Set message used as feedback when reporting that this test failed
	 * @param feedback
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	/**
	 * Show these input/output files when reporting errors?
	 * @return
	 */
	public boolean isShow() {
		if(show == null)
			return false;
		else if(show == YesNo.YES)
			return true;
		else
			return false;
	}

	/**
	 *  Set boolean indicating if the input/output files of this test can be
	 *  be shown when giving feedback
	 * @param show
	 */
	public void setShow(boolean show) {
		if(show)
			this.show = YesNo.YES;
		else
			this.show = YesNo.NO;
	}

	/**
	 * Get result of testing this test against solution and comparing to output
	 * @return
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Set result of testing this test against solution and comparing to output
	 * @param result
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Get timeout of this test
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Set timeout of this test
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Get solutions where this test got wrong result (comma separated)
	 * @return
	 */
	public String getSolutionErrors() {
		if(solutionErrors == null)
			return "";
		else
			return solutionErrors;
	}

	/**
	 * Set solutions where this test got wrong result (comma separated)
	 * @param solutionErrors
	 */
	public void setSolutionErrors(String solutionErrors) {
		this.solutionErrors = solutionErrors;
	}

	@Override
	public int compareTo(Test other) {
		return path.compareTo(other.path);
	}

}
