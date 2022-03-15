package pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.resources.ProgramErrorResources;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.utils.ProgramError;
import pt.up.fc.dcc.mooshak.client.utils.ProgramError.Type;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageCell;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;

public class ProgramErrorListViewImpl extends ResizeComposite
	implements ProgramErrorListView {

	private static ProgramErrorListViewUiBinder uiBinder = GWT
			.create(ProgramErrorListViewUiBinder.class);

	@UiTemplate("ProgramErrorListView.ui.xml")
	interface ProgramErrorListViewUiBinder extends
			UiBinder<Widget, ProgramErrorListViewImpl> {
	}
	
	EnkiConstants constants = GWT.create(EnkiConstants.class);
	
	private Presenter presenter = null;
	
	@UiField(provided = true)
	DataGrid<Row> errorsGrid;
	
	ListDataProvider<Row> dataProvider;
	
	private int rows = Integer.MAX_VALUE;
	
	public ProgramErrorListViewImpl() {
		dataProvider = new ListDataProvider<Row>();
		configureGrid();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void configureGrid() {
		initGrid();
	    errorsGrid.setWidth("100%");
	    errorsGrid.setHeight("100%");
	    
	    errorsGrid.setAutoHeaderRefreshDisabled(true);
	    	    
	    
	    dataProvider.addDataDisplay(errorsGrid);
	    	    	    
	}

	private void initGrid() {
		errorsGrid = new DataGrid<Row>(rows,Row.KEY_PROVIDER);
		
		Column<Row, String> typeColumn = new Column<Row, String>(new CustomImageCell()) {
			@Override
			public String getValue(Row object) {
				
				if (object.getValue("type").equals(Type.WARNING.toString()))
					return ProgramErrorResources.INSTANCE.warning().getSafeUri().asString();
				return ProgramErrorResources.INSTANCE.error().getSafeUri().asString();
			}
			
			@Override
			public void render(Context context, Row object, SafeHtmlBuilder sb) {
		        String imagePath = getValue(object);
		        sb.appendHtmlConstant("<img src = '"+UriUtils.fromTrustedString(imagePath).asString()
		        		+"' height = '16px' "
		        		+ "width = '16px' />");
			}
		};
		errorsGrid.addColumn(typeColumn, constants.type());
		errorsGrid.setColumnWidth(typeColumn, 20, Unit.PCT);
		
		Column<Row, String> descriptionColumn = new Column<Row, String>(new TextCell()) {
			@Override
			public String getValue(Row object) {
				
				return object.getValue("description");
			}
		};
		errorsGrid.addColumn(descriptionColumn, constants.description());
		errorsGrid.setColumnWidth(descriptionColumn, 80, Unit.PCT);
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void setErrorList(List<ProgramError> errors) {
		presenter.clearEditorAnnotations();
		dataProvider.getList().clear();
		
		if (errors.size() == 0)
			return;
		
		int id = 0;
		for (ProgramError error : errors) {
			if (error.getDescription().toLowerCase().indexOf("warning") >= 0) {
				presenter.addEditorAnnotation(error.getRow(), error.getColumn(), 
						error.getDescription(), AceAnnotationType.WARNING);
				error.setType(Type.WARNING);
			}
			else {
				presenter.addEditorAnnotation(error.getRow(), error.getColumn(), 
						error.getDescription(), AceAnnotationType.ERROR);
				error.setType(Type.ERROR);
			}
				
			Map<String, String> data = new HashMap<String, String>();
			data.put("type", error.getType().toString());
			data.put("description", error.getDescription());
			Row row = new Row(id+"", data);
			dataProvider.getList().add(row);
		}
		
		presenter.setEditorAnnotations();
	}
	
	
}
