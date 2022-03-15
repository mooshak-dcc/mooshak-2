package pt.up.fc.dcc.mooshak.client.data;

import static pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.ROWS;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.cellview.client.Column;

public class GwtTestListingDataProvider extends GWTTestCase {
	final String NBSP = new String(new byte[] { -62, -96 });

	public String getModuleName() {
		return "pt.up.fc.dcc.mooshak.ICPCModule";
	}

	@Test
	public void testAdd() {

		addList(Arrays.asList("00:01", "00:02", "00:03", "00:04", "00:05", "00:06"));

		addList(Arrays.asList("00:01", "00:03", "00:02", "00:06", "00:04", "00:05"));

		addList(Arrays.asList("00:06", "00:05", "00:04", "00:03", "00:02", "00:01"));

	}

	@Test
	public void testChange() {

		ListingDataProvider dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.reset();

		addList(Arrays.asList("00:01", "00:02", "00:03", "00:04", "00:05", "00:06"));

		assertEquals(6, countNonEmptyLines(dataProvider.getUnfilteredList(), "time"));
		List<String> columns = Arrays.asList("time", "a", "b");

		dataProvider.addOrChangeRow("r2", makeRowData(columns, "00:07", "1", "2"));

		assertEquals(6, countNonEmptyLines(dataProvider.getUnfilteredList(), "time"));

		checkOrdered(dataProvider.getUnfilteredList(), "time", false);

	}

	@Test
	public void testChangeWithSameIds() {

		ListingDataProvider dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.reset();

		addList(Arrays.asList("00:01", "00:02", "00:03", "00:04", "00:05", "00:06"));

		assertEquals(6, countNonEmptyLines(dataProvider.getUnfilteredList(), "time"));
		List<String> columns = Arrays.asList("time", "a", "b");

		dataProvider.addOrChangeRow("r2", makeRowData(columns, "00:07", "1", "2"));

		assertEquals(6, countNonEmptyLines(dataProvider.getUnfilteredList(), "time"));

		checkOrdered(dataProvider.getUnfilteredList(), "time", false);

		Column<Row, String> columnA = new Column<Row, String>(new TextCell()) {

			@Override
			public String getValue(Row row) {
				return row.getValue("a");
			}
		};
		dataProvider.getComparator("a", columnA);

		dataProvider.addOrChangeRow("r2", makeRowData(columns, "00:07", "1", "3"));

		checkOrdered(dataProvider.getUnfilteredList(), "time", false);

		addList(Arrays.asList("00:01", "00:02", "00:03", "00:04", "00:05", "00:06"));

		checkOrdered(dataProvider.getUnfilteredList(), "time", false);

	}

	@Test
	public void testSize() {

		addSeq(100, ROWS);

		addSeq(200, ROWS);
	}

	@Test
	public void testChangeDisplayRows() {

		ListingDataProvider dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.reset();
		
		List<Row> rows = dataProvider.getList();

		for (int total = 50; total < 300; total += 10) {
			dataProvider.setDisplayRows(ROWS);
			addSeq(total, ROWS);

			for (int delta = -10; delta <= 10; delta++) {

				dataProvider.setDisplayRows(ROWS + delta);
				assertEquals(ROWS + delta, dataProvider.getDisplayRows());

				checkNumberOfRows(ROWS + delta, rows, total);
			}
		}
	}

	@Test
	public void testFilter() {

		ListingDataProvider dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.reset();
		
		addList(Arrays.asList("00:01", "00:02", "00:03", "00:04", "00:05", "00:06"));

		dataProvider.addFilter("time", Arrays.asList("00:01"));

		List<Row> filtered = dataProvider.filterList(dataProvider.getUnfilteredList());
		assertEquals(1, filtered.size());

		for (Row row : filtered) {
			assertEquals("00:01", row.getValue("time"));
		}
	}

	private void addList(List<String> values) {
		ListingDataProvider dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.reset();
		
		List<String> columns = Arrays.asList("time", "a", "b");
		
		int count = 1;

		for (String time : values) {
			
			dataProvider.addOrChangeRow("r" + (count++), makeRowData(columns, time, "1", "2"));

			checkOrdered(dataProvider.getUnfilteredList(), "time", false);
		}

	}

	private void addSeq(int n, int displayRows) {
		ListingDataProvider dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.reset();
		
		List<String> columns = Arrays.asList("time", "a", "b");
		List<Row> rows = dataProvider.getList();

		int count = 1;
		for (int i = 1; i <= n; i++) {

			dataProvider.addOrChangeRow("r" + (count++),
					makeRowData(columns, formatAsTime(i), "1", "2"), true);

			checkOrdered(rows, "time", false);

			checkNumberOfRows(displayRows, rows, i);
		}
	}

	/**
	 * @param displayRows
	 * @param rows
	 * @param i
	 */
	private void checkNumberOfRows(int displayRows, List<Row> rows, int i) {
		int expected = (i / displayRows) * displayRows;

		if (i % displayRows > 0)
			expected += displayRows;

		assertEquals(expected, rows.size());
	}

	private String formatAsTime(int i) {
		StringBuilder builder = new StringBuilder("");
		int h = i / 60;
		int m = i % 60;

		if (h < 10)
			builder.append("0");
		builder.append(h);
		if (m < 10)
			builder.append("0");
		builder.append(m);

		return builder.toString();
	}

	private int countNonEmptyLines(List<Row> list, String column) {
		int count = 0;

		for (Row row : list)
			if (NBSP.equals(row.getValue(column)))
				break;
			else
				count++;

		return count;
	}

	private void checkOrdered(List<Row> list, String column, boolean isAscending) {
		assertTrue(!NBSP.equals(list.get(0).getValue(column)));
		
		if (isAscending)
			Collections.reverse(list);
		
		String previous = list.get(0).getValue(column);

		for (Row row : list) {
			String value = row.getValue(column);
			int compare = previous.compareTo(value);

			if (NBSP.equals(value))
				break;

			assertTrue(compare >= 0);
			previous = value;
		}
		if (isAscending)
			Collections.reverse(list);
	}

	private Map<String, String> makeRowData(List<String> columns, String... values) {
		Map<String, String> row = new HashMap<String, String>();
		int pos = 0;

		for (String column : columns)
			row.put(column, values[pos++]);

		return row;
	}

}
