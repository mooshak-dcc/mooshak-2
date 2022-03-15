package pt.up.fc.dcc.mooshak.content.types;

import java.util.List;

import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

/**
 * A type HasListingRows if it can be presented as a listing. It must be able to
 * iterate over a list of rows and return the name of its columns.
 * Examples of HasRows are Submissions, Questions, Printouts and Policies 
 * that compute rankings for different contests and pedagogical uses.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 */
public interface HasListingRows {

	public POStream<? extends HasListingRow> getRows();
	
	public List<ColumnInfo> getColumns();
}
