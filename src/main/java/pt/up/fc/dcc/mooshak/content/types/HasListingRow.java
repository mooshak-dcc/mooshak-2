package pt.up.fc.dcc.mooshak.content.types;

import java.util.Date;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;

/**
* A type is HasListingRow if it can be presented as row in a listing. 
* Method getRow return a map containing row data indexed by column name.
* Time of row since beginning of contest is required by virtual contests. 
* Examples of HasRow are Submission, Question, Printout and Policies 
* that compute rankings for different contests and pedagogical uses.
**/

public interface HasListingRow {
	
	/**
	 * Get id of this row
	 * @return
	 * @throws MooshakContentException
	 */
	public String getRowId() throws MooshakContentException;
	
	/**
	 * Get data of this row (column data)
	 * @return
	 * @throws MooshakContentException
	 */
	public Map<String,String> getRow() throws MooshakContentException;

	/**
	 * Get team related to this row
	 * @return
	 * @throws MooshakContentException
	 */
	public Authenticable getTeam() throws MooshakContentException;
	
	/**
	 * Get time since beginning of contest when row was issued
	 * @return
	 */
	public Date getTime();
	
}
