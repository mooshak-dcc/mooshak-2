package pt.up.fc.dcc.mooshak.shared.results;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Data related to a problem/exercise, including statement.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class ProblemInfo implements IsSerializable {

	private String id;
	private String label;
	private String title;
	private String statement;
	private boolean pdfViewable = false;
	
	
	/**
	 * Get Id of problem as PersistentObject
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set Id of problem as PersistentObject
	 * @return
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get label of problem, typically a 1 letter label (e.g "A")
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Set label of problem, typically a 1 letter label (e.g "A")
	 * @return
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Get title of problem, as shown on the statement
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set title of problem, as shown on the statement
	 * @return
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get statement as an HTML text
	 * @return
	 */
	public String getStatement() {
		return statement;
	}
	
	/**
	 * Set statement as an HTML text
	 * @return
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * @return the pfdViable
	 */
	public boolean isPDFviewable() {
		return pdfViewable;
	}

	/**
	 * @param pDFviewable the pDFviewable to set
	 */
	public void setPDFviewable(boolean pdfViewable) {
		this.pdfViewable = pdfViewable;
	}

	
}
