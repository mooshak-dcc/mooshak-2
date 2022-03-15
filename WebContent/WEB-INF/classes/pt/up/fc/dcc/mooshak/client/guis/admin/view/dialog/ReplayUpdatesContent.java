package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import static pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.ROWS;

import java.util.Arrays;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for collecting data for contest submissions replay
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ReplayUpdatesContent extends Composite implements DialogContent {

	
	private static ReplayUpdatesContentUiBinder uiBinder = 
			GWT.create(ReplayUpdatesContentUiBinder.class);
	
	@UiTemplate("ReplayUpdatesContent.ui.xml")
	interface ReplayUpdatesContentUiBinder 
						extends UiBinder<Widget, ReplayUpdatesContent> {}
	
	
	@UiField(provided = true)
	SimplePager pager;
	
	@UiField(provided = true)
	DataGrid<Row> grid;

    private ListingDataProvider dataProvider; 
	
	private MethodContext context;
	
	public ReplayUpdatesContent() {
		dataProvider = ListingDataProvider.getDataProvider(Kind.REPLAY);
		configureGrid();
		initWidget(uiBinder.createAndBindUi(this));
		
		grid.setHeight("500px");
		
		// should field names come from the server?
		setColumns(Arrays.asList("Order","Team","Problem","Language",
				"Classification","Reclassification","Extra"));
	}
	
	@Override
	public MethodContext getContext() {
		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		this.context = context;
	}
	
	@Override
	public String getWidth() {
		return "1100px";
	}

	@Override
	public String getHeight() {
		return "650px";
	}
	
	
	// @Override
	public void setColumns(List<String> columnNames) {
		ListHandler<Row> handler = dataProvider.getListHandler();
		Column<?,?> firstColumn = null;
		
		for(final String columnName: columnNames) {
	    	Column<Row, String> column = 
	    			new Column<Row, String>(new TextCell()) {

	    		@Override
	    		public String getValue(Row pair) {
	    			return pair.getValue(columnName);
	    		}
	    	};
	    	StringBuilder label = new StringBuilder(columnName);
	    	label.setCharAt(0, Character.toUpperCase(label.charAt(0)));
		    grid.addColumn(column,label.toString());
		    grid.setColumnWidth(column, 20, Unit.PCT);
		
		    if(firstColumn == null)
		    	firstColumn = column;
		    
		    column.setSortable(true);
		   	handler.setComparator(column,
		   			dataProvider.getComparator(columnName,column));
	    }
		
	    grid.addColumnSortHandler(handler);
	    
	    ColumnSortList sortList = grid.getColumnSortList();
	    
	    sortList.push(firstColumn); // make first column the sort column
	    sortList.push(firstColumn); // invert the sort order	
	}

	private  void configureGrid() {
		
		grid = new DataGrid<Row>(ROWS,Row.KEY_PROVIDER);
	    grid.setWidth("100%");
	    grid.setHeight("5cm");
	    
	    grid.setAutoHeaderRefreshDisabled(true);
	    	    
	    
	    dataProvider.addDataDisplay(grid);
	    	    	    
	    pager = dataProvider.getPager();
	    
	    pager.setDisplay(grid);
	}

}
