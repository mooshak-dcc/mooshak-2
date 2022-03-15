package pt.up.fc.dcc.mooshak.client.widgets;

import pt.up.fc.dcc.mooshak.client.views.Listing;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.user.cellview.client.Header;

public class CategorizedHeader extends Header<String> {

	private String columnName;
	private String key;

	public CategorizedHeader(final String columnName, final String key, final Listing view,
			int x, int y) {
		super(new CategorizedHeaderCell(columnName, view, x, y));

		this.columnName = columnName;
		this.key = key;
		final CategorizedHeaderCell header = (CategorizedHeaderCell) this
				.getCell();
		this.setUpdater(new ValueUpdater<String>() {

			@Override
			public void update(String value) {
				header.showFilter();
			}
		});
	}

	@Override
	public String getValue() {
		return columnName;
	}

	public CategorizedColumnFilter getFilter() {
		return ((CategorizedHeaderCell) this.getCell()).getFilter();
	}
	
	@Override
	public Object getKey() {
		
		return key;
	}
}
