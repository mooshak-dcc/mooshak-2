package pt.up.fc.dcc.mooshak.client.guis.icpc.view;


import static pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.ROWS;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.views.ListingImpl;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;

public class ListingViewImpl extends ListingImpl implements ListingView {

	private static ListingUiBinder uiBinder = 
			GWT.create(ListingUiBinder.class);

	@UiTemplate("ListingView.ui.xml")
	interface ListingUiBinder 
		extends UiBinder<Widget, ListingViewImpl> {}

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	
	private Presenter presenter;

	@UiField(provided = true)
	SimplePager pager;
	
	@UiField(provided = true)
	DataGrid<Row> grid;

    ListingDataProvider dataProvider; 
    
    int rows = ROWS;
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
		configureGrid();
		initWidget(uiBinder.createAndBindUi(this));
		
		grid.ensureDebugId("listingGrid");
		pager.ensureDebugId("pagerGrid");
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

	private void configureGrid() {
		
		grid = new DataGrid<Row>(rows,Row.KEY_PROVIDER);
	    grid.setWidth("100%");
	    grid.setHeight("100%");
	    
	    grid.setAutoHeaderRefreshDisabled(true);
	    	    
	    
	    dataProvider.addDataDisplay(grid);
	    	    	    
	    pager = dataProvider.getPager();
	    
	    pager.setDisplay(grid);
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
	public boolean canReplaceSubmission() {
		return dataProvider.getKind().equals(Kind.SUBMISSIONS);
	}
	
	@Override
	public void replaceSubmission(final String id, final String team, 
			final String problem) {
		new OkCancelDialog(constants.replaceSubmissionConfirmation()) {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				presenter.onReplaceSubmission(id, team, problem);
			}
		});
	}
	
	@Override
	public void getParticipantLogged() {
		presenter.getParticipantLogged();
	}
	
	@Override
	public void getShowOwnCode() {
		presenter.getShowOwnCode();
	}

	@Override
	public boolean isExportable() {
		return false;
	}
}
