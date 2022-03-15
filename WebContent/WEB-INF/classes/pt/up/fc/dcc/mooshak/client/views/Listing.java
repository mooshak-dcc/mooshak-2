package pt.up.fc.dcc.mooshak.client.views;

import java.util.List;

import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

public interface Listing {
	
	/**
	 * Sets the column names in the grid
	 * @param columnInfos
	 */
	void setColumns(List<ColumnInfo> columnInfos);

	/**
	 * Fills the filter list
	 */
	void setFiltersData();

	/**
	 * Sets filters given on each header
	 */
	void updateDataGrid();

	/**
	 * Hides the column identified by column name
	 * @param columnName
	 */
	void hideColumn(String columnName);

	/**
	 * Show the column identified by column name
	 * @param columnName
	 */
	void showColumn(String columnName);

}
