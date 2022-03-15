package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import java.util.Comparator;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.view.client.ProvidesKey;

public class MyPaginationDataGrid<T> extends PagingDataGrid<T> {
	
	public static final String NON_BREAKING_SPACE = 
			new String(Character.toChars(160)); //non-breaking space ASCII code

	public MyPaginationDataGrid(ProvidesKey<T> keyProvider) {
		super(keyProvider);
	}

	@Override
	public void initTableColumns(DataGrid<T> dataGrid,
			ListHandler<T> sortHandler) {
		Column<T, String> displayNameColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				if (((ScoreRow) object).getPlayer() == null)
					return NON_BREAKING_SPACE;
				return ((ScoreRow) object).getPlayer().getDisplayName();
			}
		};
		displayNameColumn.setSortable(true);
		sortHandler.setComparator(displayNameColumn, new Comparator<T>() {
			public int compare(T o1, T o2) {
				if (((ScoreRow) o1).getPlayer().getDisplayName() == null)
					return -1;
				if (((ScoreRow) o2).getPlayer().getDisplayName() == null)
					return 1;
				return ((ScoreRow) o1).getPlayer().getDisplayName()
						.compareTo(((ScoreRow) o2).getPlayer().getDisplayName());
			}
		});
		dataGrid.addColumn(displayNameColumn, constants.player());

		dataGrid.setColumnWidth(displayNameColumn, 20, Unit.PCT);

		Column<T, String> scoreColumn = new Column<T, String>(new TextCell()) {
			@Override
			public String getValue(T object) {
				if (((ScoreRow) object).getFormattedScore() == null)
					return NON_BREAKING_SPACE;
				return ((ScoreRow) object).getFormattedScore();
			}
		};
		scoreColumn.setSortable(true);
		sortHandler.setComparator(scoreColumn, new Comparator<T>() {
			public int compare(T o1, T o2) {
				if (((ScoreRow) o1).getFormattedScore() == null)
					return -1;
				if (((ScoreRow) o2).getFormattedScore() == null)
					return 1;
				return new Float(((ScoreRow) o1).getFormattedScore()).compareTo(
						new Float(((ScoreRow) o2).getFormattedScore()));
			}
		});
		dataGrid.addColumn(scoreColumn, constants.score());
		dataGrid.setColumnWidth(scoreColumn, 20, Unit.PCT);
		
		dataGrid.addColumnSortHandler(sortHandler);

		ColumnSortList sortList = dataGrid.getColumnSortList();

		sortList.push(displayNameColumn); // make first column the sort column
		sortList.push(displayNameColumn); // invert the sort order
	}

}
