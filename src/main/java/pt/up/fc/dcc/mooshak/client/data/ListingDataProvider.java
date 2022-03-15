package pt.up.fc.dcc.mooshak.client.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;

/**
 * Data provider for Mooshak listings. The kind of listings supported by this
 * DataProvider are those identified in the {@link Kind} enumeration
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class ListingDataProvider extends ListDataProvider<Row> {

	public static final int ROWS = 24;
	
	public static final String NON_BREAKING_SPACE = 
			new String(Character.toChars(160)); //non-breaking space ASCII code
	
	private static final int HEAVY_COMPUTATION_DELAY_MS = 100; // 0.1s
	
	public enum Kind { SUBMISSIONS, QUESTIONS, PRINTOUTS, 
						 BALLOONS, RANKINGS, REPLAY, IMPORT, PENDING}

	/**
	 * Kind of listing in this provider
	 */
	private Kind kind;
	/**
	 * IDs of rows currently recorded (sorted to speed up lookup)
	 */
	private Set<String> sortedIds = new TreeSet<String>();
	/**
	 * Number of displayed rows 
	 */
	private int rows = ROWS;
	/**
	 * Filler rows are created to ensure that tables display always a full page
	 */
	private int fillerRowsCount = 0;
	/**
	 * default Comparator used for inserting rows in sorted order
	 */
	private ColumnComparator defaultComparator = null;
	
	private static final Map<Kind,ListingDataProvider> providers =
			new HashMap<Kind,ListingDataProvider>();
	
	// does a map of column names to allowed values 
	protected Map<String, List<String>> filters = null;

	/**
	 * Get a {@link ListingDataProvider} for a certain {@link Kind} of data
	 * @param problem
	 * @return
	 */
 	public static ListingDataProvider getDataProvider(Kind kind) {
		ListingDataProvider provider = null;
		
		if(providers.containsKey(kind)) {
			provider = providers.get(kind);
		}
		else {
			if(kind.equals(Kind.PENDING))
				return PendingDataProvider.getDataProvider();
			
			provider = new ListingDataProvider(kind);
			providers.put(kind, provider);
		}
		return provider;
	}
 	

	ListingDataProvider(Kind kind) {
		this.kind = kind;
		
		reset();
	}

	
	/**
	 * Use this method just for unit testing purposes,
	 * since instances of this class are singletons 
	 */
	void reset() {
		defaultComparator = null;
		sortedIds.clear();
		filteredSortedList.clear();
		list.clear();
		fillerRowsCount = 0;
		addFillerRows(rows);
		
		refresh();
	}
	

	/**
	 * @return the kind
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}
	
	/**
	 * Get number of rows displayed in a listing
	 * @return
	 */
	public void setDisplayRows(int rows) {
		
		int limit = filteredSortedList.size() - fillerRowsCount;
		int neededFillerRows = limit % rows == 0 ? 0 : rows - (limit % rows);
		
		if(neededFillerRows > fillerRowsCount)
			addFillerRows(neededFillerRows - fillerRowsCount);
		else
			takeFillerRows(fillerRowsCount - neededFillerRows);
		
		this.rows = rows;
		
		Scheduler.get().scheduleDeferred(new Command() {
			
			public void execute () {
					refresh();
			    }
		});
	}
	
	/**
	 * Get number of rows displayed in a listing
	 * @return
	 */
	public int getDisplayRows() {
		return rows;
	}
	
	/**
	 * A row in a listing
	 */
	public static class Row {
		
		private String id;
		private Map<String,String> data;

		public static final ProvidesKey<Row> KEY_PROVIDER = 
				new ProvidesKey<Row>() {
			@Override
			public Object getKey(Row item) {
				return item == null ? null : item.getId();
			}
		};

		public Row(String id, Map<String,String> data) {
			this.id = id;
			this.data = data;
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the data
		 */
		public Map<String, String> getData() {
			return data;
		}

		/**
		 * @param data the data to set
		 */
		public void setData(Map<String, String> data) {
			this.data = data;
		}

		/**
		 * Returns a cell value given the column name
		 * @param columnName
		 * @return
		 */
		public String getValue(String columnName) {
			if(data == null || ! data.containsKey(columnName))
				return NON_BREAKING_SPACE;
			else 
				return data.get(columnName);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Row [id=" + id + ", data=" + data + "]";
		}
		
		public String toString(String sep) {
			
			if (data == null)
				return "";
			
			String string = "";
			for (String key : data.keySet()) {
				string += data.get(key) + "\t";
			}
			
			return string;
		}

		public String toString(String sep, List<String> headers) {
			
			if (data == null)
				return "";
			
			String string = "";
			for (String key : headers) {
				if (data.get(key) != null)
					string += data.get(key).replace("\n", "") + "\t";
				else
					string += NON_BREAKING_SPACE + "\t";
			}
			
			return string;
		}
		
	}

	private List<Row> filteredSortedList = getList();
	protected final List<Row> list = new ArrayList<ListingDataProvider.Row>();
	
	private Timer addOrChangeRowComputationTimer = null;
	
	/**
	 * If id already exists in table then row is replaced;
	 * otherwise the row is added to the table
	 * @param id		of row ({@code null} id are silently ignored)
	 * @param record	map containing cells indexed by field names 
	 */
	public void addOrChangeRow(String id, Map<String, String> record) {

		addOrChangeRow(id, record, false);
	}
	
	/**
	 * If id already exists in table then row is replaced;
	 * otherwise the row is added to the table
	 * 
	 * @param id		of row ({@code null} id are silently ignored)
	 * @param record	map containing cells indexed by field names 
	 * @param compute	force instant computation
	 */
	public synchronized void addOrChangeRow(String id, Map<String, String> record,
			boolean compute) {

		if(id == null)
			return;
		
		if(sortedIds.contains(id)) { // remove row (may become out of order)
			int index = findRowIndex(id);
			if(index == -1) {
				
				String message = 
						"Row with id missing:"+id+
						"\tin listing "+kind+
						"\t# list:"+list.size()+
						"\t# rows:"+(list.size()-fillerRowsCount)+
						"\t# sortedIds:"+sortedIds.size();
				Logger.getLogger("").log(Level.SEVERE,message);

			} else {
				list.remove(index);
			}
		} else 
			sortedIds.add(id);
		
		// add
		list.add(new Row(null, null));
		Row row = new Row(id,record);
		int index = findRowIndex(row);
		for(int i=list.size()-1; i>index; i--)
			list.set(i,list.get(i-1));
		list.set(index,row);	
		// flush();
		
		if (compute) {
			addOrChangeRowComputation();
			return;
		}
		
		// schedule heavy computation so that it is not repeated every time 
		// a row is added in batch updates
		if (addOrChangeRowComputationTimer == null) {
				
			addOrChangeRowComputationTimer = new Timer() {
				
				@Override
				public void run() {
					addOrChangeRowComputation();
					addOrChangeRowComputationTimer = null;
				}
			};
			addOrChangeRowComputationTimer.schedule(HEAVY_COMPUTATION_DELAY_MS);
		}
	}
	
	/**
	 * Do the required heavy computation when adding/changing a row.
	 */
	private void addOrChangeRowComputation() {
		computeRanks();
		reapplyFilters();
	}

	/**
	 * Compute rank column on client side to avoid sending every row.
	 * This method is effective only on listings with a "rank" column.
	 * It assumes that listing is always ordered by the default order.
	 */
	private void computeRanks() {
		for(int i=0; i<list.size(); i++) {
			Row row = list.get(i);
			
			if (row == null || row.data == null || ! row.data.containsKey("#"))
				return;
			
			row.data.put("#",Integer.toString(i+1)+"\n"+NON_BREAKING_SPACE);
		}
	}
	
	
	/**
	 * Find index for this key by performing a sequential search on ids
	 * 
	 * @param id
	 * @return
	 */
	protected int findRowIndex(String id) {
		int limit = list.size();
		
		if(id == null)
			return -1;
		
		for(int i=0; i<limit;i++)
			if(id.equals(list.get(i).getId()))
				return i;
		
		return -1;
	}
	
	
	/**
	 * Find index for this row doing a binary search over a sorted list
	 * @param insert
	 * @return
	 */
	protected int findRowIndex(Row insert) {
		int limit = list.size();
		int start = 0;
		int end   = limit;
		
		ColumnComparator comparator = getCurrentComparator();
		boolean isAscending = comparator.isAscending();		
		
		while(start+1 < end) {
			int middle = (start + end) / 2;
			Row middleRow = list.get(middle);
			int compare = compare(comparator,isAscending,insert, middleRow);
			
			if(compare <= 0)
				end = middle;
			else
				start = middle;
		}
			
		if(start != end) { 

			if(compare(comparator,isAscending,insert, list
					.get(start)) > 0)
				return end;
			else
				return start;
			
		} 
		return start;
	}

	private int compare(ColumnComparator comparator,boolean isAscending, 
			Row a, Row b) {
		
		int compare = comparator.compare(a,b);
		
		if(isAscending)
			return compare;
		else
			return - compare;
	}
	
	/**
	 * Create as much filler rows as the number of lines in the grid.
	 * Filler rows have no ID and no data
	 */
	public void addFillerRows(int add) {
		for(int count = 0; count<add; count++) {
			
			filteredSortedList.add(new Row(null,null));
			fillerRowsCount++;
		}
	}

	/**
	 * Remove a certain number of no longer useful filler rows
	 * @param take
	 */
	private void takeFillerRows(int take) {
		for(int count = 0; count<take; count++) {
			filteredSortedList.remove(filteredSortedList.size()-1);
			fillerRowsCount--;
		}
	}
	
	/**
	 * @return the fillerRowsCount
	 */
	public int getFillerRowsCount() {
		return fillerRowsCount;
	}

	/**
	 * @param fillerRowsCount the fillerRowsCount to set
	 */
	public void setFillerRowsCount(int fillerRowsCount) {
		this.fillerRowsCount = fillerRowsCount;
	}
	
	/**
	 * @return the sortedIds
	 */
	public Set<String> getSortedIds() {
		return sortedIds;
	}

	/**
	 * Get a list handler that records the last sort order 
	 * (ascending, descending) of each column
	 * 
	 * @return
	 */
	public ListHandler<Row> getListHandler() {
		return new ListHandler<Row>(filteredSortedList) {
			
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				setAscending(event.getColumn(), event.isSortAscending());
				
				super.onColumnSort(event);
			}
			
		};
	}
	
	
	 
	/**
	 *  Comparator for a given column. Values are compared as strings.
	 *  
	 */
	private class ColumnComparator implements Comparator<Row> {	
		public String name;
		public boolean isAscending;

		private ColumnComparator() {
			this.isAscending = false;
			
			switch(kind) {
			case SUBMISSIONS:
			case BALLOONS:
			case PRINTOUTS:
			case QUESTIONS:
			case PENDING:
				name = "time";
				break;
			case RANKINGS:
				name = "order";
			default:
				
			}
		}
		
		private ColumnComparator(String name,Column<?,?> column) {
			this.name = name;
			this.isAscending = true;
		}
		

		@Override
		public int compare(Row r1, Row r2) {
			int compare; 
						
			if(r1==r2)
				compare = 0;
			else if(r1 == null) 
				compare = -1;
			else if(r2 == null)
				compare = 1;
			else {
				String v1 = r1.getValue(name);
				String v2 = r2.getValue(name);

				if(NON_BREAKING_SPACE.equals(v1)) {
					if(NON_BREAKING_SPACE.equals(v2))
						compare = 0;
					else
						compare = isAscending ? 1 : -1; 
				} else if(NON_BREAKING_SPACE.equals(v2))
					compare = isAscending ? -1 : 1;
				else {
					try {  
						double d1 = Double.parseDouble(v1);  
						double d2 = Double.parseDouble(v2);
						compare = Double.compare(d1, d2);
					} catch (NumberFormatException e) {
						compare = v1.compareTo(v2);
					}
				}
			}	

			return compare;
		}

		public boolean isAscending() {
			return isAscending;
		}
	}
	
	
	private ColumnComparator getCurrentComparator() {
		if(defaultComparator == null)
			defaultComparator = new ColumnComparator();
		
		return defaultComparator;
	}
	

	private Map<Column<?,?>,ColumnComparator> comparators =
			new HashMap<Column<?,?>,ColumnComparator>();
	 
	private void setAscending(Column<?,?> column,boolean isAscending) {
		ColumnComparator comparator = comparators.get(column);
			
		if(comparator == null)
				Logger.getLogger("").log(Level.WARNING,
						"Sorting an unregistered column");
		else 
				comparator.isAscending = isAscending;
	}
	
	
	/**
	 * Returns a comparator for column with given name.
	 * Column values are compared as strings.
	 * 
	 * This comparator is set as default comparator and 
	 * will influence new inserted rows
	 * 
	 * @param name of column
	 * @return a comparator for given column name
	 */
	public Comparator<Row> getComparator(String name,Column<?,?> column) {
		
		ColumnComparator comparator;
		
		if(comparators.containsKey(column))
			comparator = comparators.get(column);
		else {
			comparator = new ColumnComparator(name,column);
			comparators.put(column,comparator);
		}
		
		return comparator;
	}

	private static RegExp pagerTextRegExp = 
			RegExp.compile("([\\w]+)-([\\w]+)\\sof\\s([\\w]+)");
	
	/**
	 * A pager that considers filler rows in the pager's text 
	 * @return the pager
	 */
	public SimplePager getPager() {
		
		return new SimplePager() {
			
			protected String createText() {
				// avoid dots and commas used as thousands separator 
				String text = super.createText().replace(".","").replace(",", "");
				MatchResult result;

				if((result = pagerTextRegExp.exec(text)) != null) {
					int first = Integer.parseInt(result.getGroup(1));
					int last  = Integer.parseInt(result.getGroup(2));
					int total = Integer.parseInt(result.getGroup(3));

					if(last==total) {
						last -= fillerRowsCount;
					}
					total -= fillerRowsCount;

					return first+"-"+last+" of "+total;
				} else {
					return "??";
				}
			}
			
		};
	}
	
	/**
	 * Returns the active filters
	 * 
	 * @return {@link Map} active filters
	 */
	public Map<String, List<String>> getFilters() {
		return filters;
	}
 
	/**
	 * Sets the filters
	 * 
	 * @param filters {@link Map} new filters
	 */
	public void setFilters(Map<String, List<String>> filters) {
		
		this.filters = filters;
		
		reapplyFilters();
	}
	
	/**
	 * Re-apply filters to list
	 */
	public void reapplyFilters() {
		
		filteredSortedList.clear();
		filteredSortedList.addAll(filterList(list));
		
		int missingRows = rows - (filteredSortedList.size() % rows);
		setFillerRowsCount(0);
		if (rows != missingRows)
			addFillerRows(missingRows);
		
		refresh();
		
		flush();
	}
 
	/**
	 * Clears all filters
	 */
	public void resetFilter() {
		
		filters = null;
		filteredSortedList.clear();
		filteredSortedList.addAll(list);
		
		int missingRows = rows - (filteredSortedList.size() % rows);
		
		setFillerRowsCount(0);
		if (rows != missingRows)
			addFillerRows(missingRows);
		
		refresh();
		
		flush();
	}
 
	@Override
	protected void updateRowData(HasData<Row> display, int start, 
			List<Row> values) {	
		super.updateRowData(display, start, values);
		if(values.size() == 0 && fillerRowsCount == 0)
			addFillerRows(rows);
	}


	/**
	 * Filter a given list with current filters
	 * @param rows {@link List} data rows
	 * @return
	 */
	List<Row> filterList(List<Row> rows) {
		
		if (!hasFilters())
			return new ArrayList<Row>(rows);
		
		List<Row> filtered = new ArrayList<>();
		for (Row row : rows) {
			boolean passed = true;
			for (String filterColumn : filters.keySet()) {
				String value = "";
				if((value = row.getValue(filterColumn)) != null) {
					if(!filters.get(filterColumn).contains(value)) {
						passed = false;
						break;
					}
				}
			}
			if (passed)
				filtered.add(row);
		}
		return filtered;
	}
 
	/**
	 * Verifies the existence of filters
	 * @return
	 */
	public boolean hasFilters() {
		return filters != null && !filters.keySet().isEmpty();
	}
	
	/**
	 * Adds a filter to this data provider
	 * @param columnName
	 * @param allowed
	 */
	public void addFilter(String columnName, List<String> allowed) {
		if(!hasFilters())
			filters = new HashMap<>();
		
		filters.put(columnName, allowed);
		setFilters(filters);
	}


	/**
	 * Getter for the full list
	 * @return
	 */
	public List<Row> getUnfilteredList() {
		return list;
	}
	
	
	public String exportDataToTSV(List<String> headers) {

		String string = "";
		for (String key : headers) {
			string += key + "\t";
		}
		string += "\n";
		
		for (Row row : filteredSortedList) {
			
			string += row.toString("\t", headers);
			string += "\n";
		}	
		
		return string;
	}
	
}
