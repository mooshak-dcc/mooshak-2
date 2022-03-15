package pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.views.ListingImpl;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

public class MySubmissionsViewImpl extends ListingImpl implements MySubmissionsView, 
	ResizeHandler, RequiresResize {

	private static MySubmissionsViewUiBinder uiBinder = GWT
			.create(MySubmissionsViewUiBinder.class);

	@UiTemplate("MySubmissions.ui.xml")
	interface MySubmissionsViewUiBinder extends
			UiBinder<Widget, MySubmissionsViewImpl> {
	}

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	
	private Presenter presenter = null;

	private String problemName = null;
	
	@UiField
	ResizableHtmlPanel container;
	
	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	DataGrid<Row> grid;

	ListingDataProvider dataProvider;
	
	private String participant;
	
	private int rows;
	
	public MySubmissionsViewImpl(String problemId, int rows) {
		
		this.rows = rows;

		dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		dataProvider.setDisplayRows(rows);
		
		configureGrid();
		initWidget(uiBinder.createAndBindUi(this));
		
		container.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				grid.setHeight(Math.max((event.getHeight() - pager.getOffsetHeight() 
						- 10), 0) + "px");
				dataProvider.refresh();
			}
		});
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {				
				container.onResize();
			}
		});
		
		setShowOwnCode(true);
		
	}

	private void configureGrid() {

		grid = new DataGrid<Row>(rows, 
				Row.KEY_PROVIDER);
		grid.setWidth("100%");

		grid.setAutoHeaderRefreshDisabled(true);

		dataProvider.addDataDisplay(grid);

		pager = dataProvider.getPager();

		pager.setDisplay(grid);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void getParticipantLogged() {
		presenter.getParticipantLogged();
	}


	@Override
	public void setFiltering() {
		
		if (problemName != null) {
			dataProvider.resetFilter();
			dataProvider.addFilter("problem", Arrays.asList(problemName));
			grid.setPageSize(dataProvider.getDisplayRows());
			pager.setDisplay(grid);
		}/**/
	}

	@Override
	public boolean getSortable() {
		return true;
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
	public boolean isFilterable() {
		return false;
	}

	@Override
	public void onResize(ResizeEvent event) {
		container.onResize();
	}

	@Override
	public void onResize() {
		container.onResize();
	}

	/**
	 * @return the participant
	 */
	public String getParticipant() {
		return participant;
	}

	/**
	 * @param participant the participant to set
	 */
	public void setParticipant(String participant) {
		this.participant = participant;
		super.setParticipant(participant);
		setFiltering();
	}

	@Override
	public void setProblemName(String name) {
		this.problemName = name;
		setFiltering();
	}

	@Override
	public boolean canReplaceSubmission() {
		return true;
	}
	
	@Override
	public void replaceSubmission(final String id, final String team, 
			final String problem) {
		new OkCancelDialog(constants.replaceSubmissionConfirmation()) {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				presenter.onReplaceSubmission(id, team);
			}
		});
	}
	
	@Override
	public boolean isExportable() {
		return false;
	}
}
