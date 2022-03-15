package pt.up.fc.dcc.mooshak.client.guis.runner.view;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.runner.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.views.ListingImpl;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ListingViewImpl extends ListingImpl implements ListingView {

	private static ListingUiBinder uiBinder = GWT.create(ListingUiBinder.class);

	@UiTemplate("ListingView.ui.xml")
	interface ListingUiBinder extends UiBinder<Widget, ListingViewImpl> {
	}

	private Presenter presenter;

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	DataGrid<Row> grid;

	@UiField
	CardPanel detail;

	ListingDataProvider dataProvider;
	
	final SingleSelectionModel<Row> selectionModel = new SingleSelectionModel<Row>();

	Kind kind;
    
    int rows = 12;
    boolean sortable = true;

	public ListingViewImpl(final Kind kind, int rows,
			final TopLevelPresenter toplevel) {
		
		this.kind = kind;
		this.rows = rows;

		String height = "45%";
		switch(kind) {
		case RANKINGS:
			height = "98%";
			sortable= false;
			break;
		default:
			height = "98%";
			this.rows = 24;
			sortable = true;
		}
		
		dataProvider = ListingDataProvider.getDataProvider(kind);
		dataProvider.setDisplayRows(this.rows);
		configureGrid();
		
		this.rows = rows;
		
		initWidget(uiBinder.createAndBindUi(this));

		grid.setHeight(height);
		
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						presenter.onSelectedItemChanged();
						
						if (dataProvider.getDisplayRows() == 24) {
							grid.setHeight("45%");
							dataProvider.setDisplayRows(getRows());
							grid.setPageSize(getRows());
						}
					}
				});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * @return the presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SingleSelectionModel<Row> getSelectionModel() {
		return (SingleSelectionModel<Row>) grid.getSelectionModel();
	}

	private void configureGrid() {
		dataProvider.setDisplayRows(rows);
		
		grid = new DataGrid<Row>(rows, Row.KEY_PROVIDER);
		grid.setWidth("100%");

		grid.setAutoHeaderRefreshDisabled(true);

		dataProvider.addDataDisplay(grid);
		
		dataProvider.refresh();

		pager = dataProvider.getPager();
		
		pager.setDisplay(grid);
		
		grid.setSelectionModel(selectionModel);
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	@Override
	public CardPanel getDetail() {
		return detail;
	}

	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	public boolean getSortable() {
		return sortable;
	}

	@Override
	public ListingDataProvider getDataProvider() {
		return dataProvider;
	}

	@Override
	public DataGrid<Row> getDataGrid() {
		return grid;
	}

	@Override
	public SimplePager getPager() {
		return pager;
	}

	
	@Override
	public boolean showColors() {
		return true;
	}
}
