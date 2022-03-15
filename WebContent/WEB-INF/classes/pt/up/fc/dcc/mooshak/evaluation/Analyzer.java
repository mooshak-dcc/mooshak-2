package pt.up.fc.dcc.mooshak.evaluation;

import java.util.List;

import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * A submission analyzer. Different analyzers process different types of
 * submissions, such as program analyzers and quiz analyzers. An analyzer
 * operates on a submission that may be assigned with a setter. Implementation
 * may provide a convenience constructor with submission as argument.
 * The methods <code>analyze()</code> analyzes the submission and 
 * <code>getReport</code> return the analysis report. 
 * 
 * José Paulo Leal @author <zp@dcc.fc.up.pt>
 *
 */
public interface Analyzer {
	
	/**
	 * Set submission to be analyzed 
	 * @param submission
	 */
	public void setSubmission(Submission submission);
	
	/**
	 * Get submission to be analyzed
	 * @return
	 */
	public Submission getSubmission();
	
	/**
	 * Analyze the submission
	 */
	public void analyze()throws MooshakException;
	
	/**
	 * Get all reports for this analysis 
	 * @return
	 */
	public List<ReportType> getAllReports() throws MooshakException;
	
	/**
	 * Get a report for this analysis 
	 * @param reportNumber
	 * @return
	 */
	public ReportType getReport(int reportNumber) throws MooshakException;

}
