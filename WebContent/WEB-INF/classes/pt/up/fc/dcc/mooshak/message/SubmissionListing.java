package pt.up.fc.dcc.mooshak.message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Message with submission listing content
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
@Deprecated
public class SubmissionListing implements Serializable {
	private static final long serialVersionUID = 1L;
	
	ArrayList<SubmissionEntry> lines = new ArrayList<>();
	
	/**
	 * A line in a submissions listing
	 */
	public static class SubmissionEntry implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String id;
		public String flag;
		public String date;
		public String team;
		public String problem;
		public String language;
		public String result;
		public String state;
	}

	/**
	 * Add an entry to listing 
	 * @param entry SubmissionEntry
	 * @return
	 */
	public boolean add(SubmissionEntry entry) {
		return lines.add(entry);
	}
	
}
