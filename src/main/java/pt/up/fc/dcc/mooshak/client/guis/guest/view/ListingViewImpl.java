package pt.up.fc.dcc.mooshak.client.guis.guest.view;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.views.ListingImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;

public class ListingViewImpl extends ListingImpl
	implements ListingView {

	private static ListingUiBinder uiBinder = 
			GWT.create(ListingUiBinder.class);

	@UiTemplate("ListingView.ui.xml")
	interface ListingUiBinder 
		extends UiBinder<Widget, ListingViewImpl> {}
	
	private Presenter presenter;

	@UiField(provided = true)
	SimplePager pager;
	
	@UiField(provided = true)
	DataGrid<Row> grid;

    ListingDataProvider dataProvider; 
    
    int rows;
    boolean sortable = true;
	
	public ListingViewImpl(Kind kind, int rows) {
		
		this.rows = rows;
		
		switch(kind) {
		case RANKINGS:
			sortable= false;
			break;
		default:
			sortable = true;
		}
		
		dataProvider = ListingDataProvider.getDataProvider(kind);
		dataProvider.setDisplayRows(rows);
		
		configureGrids();
		initWidget(uiBinder.createAndBindUi(this));
		
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

	private  void configureGrids() {
		
		grid = new DataGrid<Row>(rows,Row.KEY_PROVIDER);
	    grid.setWidth("100%");
	    
	    grid.setAutoHeaderRefreshDisabled(true);
	    	    
	    
	    dataProvider.addDataDisplay(grid);
	    	    	    
	    pager = dataProvider.getPager();
	    
	    pager.setDisplay(grid);
	    
	    grid.setPageSize(rows);
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

}
