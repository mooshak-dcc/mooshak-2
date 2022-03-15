package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

public abstract class PagingDataGrid<T> extends ResizeComposite {

	EnkiConstants constants = GWT.create(EnkiConstants.class);
	private DataGrid<T> dataGrid;
	private SimplePager pager;
	private String height;
	private ListDataProvider<T> dataProvider;
	private List<T> dataList;
	private ResizableHtmlPanel dock = new ResizableHtmlPanel("");

	public PagingDataGrid(ProvidesKey<T> keyProvider) {
		initWidget(dock);
		dataGrid = new DataGrid<T>(7, keyProvider);
		dataGrid.setWidth("100%");
		
		SimplePager.Resources pagerResources = GWT
				.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 7,
				true);
		pager.setDisplay(dataGrid);
		dataProvider = new ListDataProvider<T>();
		dataProvider.setList(new ArrayList<T>());
		dataGrid.setEmptyTableWidget(new HTML(constants.noDataToDisplay()));
		ListHandler<T> sortHandler = new ListHandler<T>(dataProvider.getList());

		initTableColumns(dataGrid, sortHandler);

		dataProvider.addDataDisplay(dataGrid);
		pager.setVisible(true);
		dataGrid.setVisible(true);

		dock.add(dataGrid);
		dock.add(pager);
		dock.setWidth("100%");
		dock.setHeight("100%");
		
		dataGrid.setHeight(Math.max(dock.getOffsetHeight() - pager.getOffsetHeight(), 0) + "px");
		pager.getElement().getStyle().setMarginLeft((dock.getOffsetWidth() - pager.getOffsetWidth())/2,
				Unit.PX);
		
		dock.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				dataGrid.setHeight(Math.max(event.getHeight() - pager.getOffsetHeight(), 0) + "px");
				pager.getElement().getStyle().setMarginLeft((dock.getOffsetWidth() - pager.getOffsetWidth())/2,
						Unit.PX);
			}
		});
		
	}

	public void setEmptyTableWidget() {
		dataGrid.setEmptyTableWidget(new HTML(
				"The current request has taken longer than the allowed time limit. "
				+ "Please try your report query again."));
	}

	/**
	 * 
	 * Abstract Method to implements for adding Column into Grid
	 * 
	 * @param dataGrid
	 * @param sortHandler
	 */
	public abstract void initTableColumns(DataGrid<T> dataGrid,
			ListHandler<T> sortHandler);

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
		dataGrid.setHeight(height);
		dataGrid.redraw();
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
		List<T> list = dataProvider.getList();
		list.clear();
		list.addAll(this.dataList);
		dataProvider.refresh();
		dataGrid.redraw();
	}

	public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(ListDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}

    public void refreshDisplays() {
        dataProvider.refresh();
        dataGrid.redraw();
        pager.setPageSize(7);
        pager.setDisplay(dataGrid);
    }

	/**
	 * @return the dataGrid
	 */
	public DataGrid<T> getDataGrid() {
		return dataGrid;
	}

	/**
	 * @return the pager
	 */
	public SimplePager getPager() {
		return pager;
	}

}
