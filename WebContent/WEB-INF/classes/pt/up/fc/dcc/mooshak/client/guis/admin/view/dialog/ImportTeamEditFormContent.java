package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box form for collecting data and edit
 * the column field names
 * 
 *
 * @author josepaiva
 */
public class ImportTeamEditFormContent extends Composite implements DialogContent {
	
	private static final int ROWS = 19;

	private enum Columns {
		IGNORE("Ignore"), 
		PASSWORD("Password"), 
		COLOR("Color"), 
		ROLE("Role"), 
		TEAM("Team"), 
		GROUP("Group"), 
		FIRST_NAME("First_Name"),
		LAST_NAME("Last_Name"), 
		NAME("Name"), 
		EMAIL("Email"), 
		GENDER("Gender"), 
		COUNTRY("Country"), 
		FLAG("Flag"),
		ACRONYM("Acronym"),
		STATUS("Status"),
		ID("ID");
		
		private final String name;
		
		private Columns(String name) {
			this.name = name;
		}
		
		/**
		 * @return column name
		 */
		public String getName() {
			return name;
		}
	}
	
	private static ImportTeamEditFormContentUiBinder uiBinder = 
			GWT.create(ImportTeamEditFormContentUiBinder.class);
	
	@UiTemplate("ImportTeamEditFormContent.ui.xml")
	interface ImportTeamEditFormContentUiBinder 
						extends UiBinder<Widget, ImportTeamEditFormContent> {}
	
	@UiField
	ListBox separator;
	
	@UiField
	CheckBox genId;
	
	@UiField
	CheckBox useHeader;
	
	@UiField(provided = true)
	SimplePager pager;
	
	@UiField(provided = true)
	DataGrid<Row> grid;

    private ListingDataProvider dataProvider; 
	
	private Row headersRow = null;
	
	private String headerLine = null;
	
	private List<String> headers = null;
	
	private List<String> lines = null;
	
	private List<ImportCustomHeader> columnHeaders = 
			new ArrayList<>();

	private Column<Row, String> idColumn;

	public ImportTeamEditFormContent() {
		dataProvider = ListingDataProvider.getDataProvider(Kind.IMPORT);
		dataProvider.resetFilter();
		dataProvider.setDisplayRows(ROWS);
		configureGrid();
		initWidget(uiBinder.createAndBindUi(this));
		
		grid.setHeight("500px");
		
		genId.setValue(false);
		useHeader.setValue(true);
	}
	
	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
		
		for (ImportCustomHeader header : columnHeaders)
			context.addPair("header", header.getValue());
		
		String sep = separator.getValue(separator
				.getSelectedIndex());
		if(sep.equals("\\t"))
			sep = "\t";
		grid.setVisibleRange(0, grid.getRowCount());
		for (int i = 0; i < grid.getRowCount(); i++) {
			TableRowElement row = grid.getRowElement(i);
			if(row.getCells().getItem(0).getInnerText().trim()
					.equals(new String(Character.toChars(160))))
				continue;
			
			String line = "";
			for (int j = 0; j < row.getCells().getLength(); j++) {
				line += row.getCells().getItem(j).getInnerText() + sep;
			}
			
			context.addPair("dataRow", line.substring(0, 
					line.lastIndexOf(sep)));
		}
		
		context.addPair("separator", sep);
		
		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		
		headerLine = context.getValue("header");
		lines = context.getValues("dataRow");
		
		if(!context.getValue("hasId").toLowerCase().equals("true"))
			genId.setValue(true);
		else
			genId.setValue(false);
		
		String separator = context.getValue("separator");
		fillDataGridRows(separator);
		
		for (int i = 0; i < this.separator.getItemCount(); i++) {
			if(this.separator.getValue(i).equals(separator))
				this.separator.setSelectedIndex(i);
		}
		
	}
	
	@Override
	public String getWidth() {
		return "1100px";
	}

	@Override
	public String getHeight() {
		return "650px";
	}
	
	public void setColumns(List<String> columnNames) {
		columnHeaders = new ArrayList<ImportCustomHeader>();
		ListHandler<Row> handler = dataProvider.getListHandler();
		Column<?,?> firstColumn = null;
		
		List<String> options = new ArrayList<>();
		for (Columns column : Columns.values()) 
			options.add(column.getName());
		
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
	    	
	    	ImportCustomHeader header = 
	    			new ImportCustomHeader(label.toString(), options);
	    	
	    	if(columnName.toLowerCase().equals("id"))
	    		idColumn = column;
	    		
	    	columnHeaders.add(header);
		    grid.addColumn(column, header);
		    grid.setColumnWidth(column, 20, Unit.PCT);
		    
			if (firstColumn == null) {
				firstColumn = column;
				handler.setComparator(
						column,
						dataProvider.getComparator(columnName, column));
			}

			column.setSortable(false);
			
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
	
	private void fillDataGridRows(String sep) {
		dataProvider.getSortedIds().clear();
		dataProvider.getUnfilteredList().clear();
		
		List<Row> data = dataProvider.getList();
		data.clear();
		dataProvider.setList(data);
		dataProvider.resetFilter();
		grid.setRowCount(0);
		
		headers = new ArrayList<>();

		boolean hasId = false;
		if(genId.getValue())
			headers.add("ID");
		else if(headerLine.toLowerCase().indexOf("id") != -1)
			hasId = true;
		else
			headers.add("ID");
		
		for(String header : headerLine.split(sep)) {
			// infer names
			switch (header.toLowerCase()) {
			case "nome":
				headers.add(Columns.NAME.getName());
				break;
			case "first name":
				headers.add(Columns.FIRST_NAME.getName());
				break;
			case "last name":
				headers.add(Columns.LAST_NAME.getName());
				break;
			case "cor":
				headers.add(Columns.COLOR.getName());
				break;
			case "ncd":	case "equipa":
				headers.add(Columns.TEAM.getName());
				break;
			case "genero": case "sexo": case "sex":
				headers.add(Columns.GENDER.getName());
				break;

			default:
				headers.add(header.trim());
				break;
			}
		}
		
		int columns = grid.getColumnCount();
		for (int i = 0; i < columns; i++)
			grid.removeColumn(0);
		
		setColumns(headers);
		genId.setValue(!hasId);
		
		for (int lineId = lines.size(); lineId > 0; lineId--) {
			String line = lines.get(lineId-1);
			String values[] = line.split(sep);
			String idLabel = "ID";
			boolean isAuto = false;
			Map<String, String> row = new HashMap<>();
			try {
				for (int j = 0, i = 0; i < headers.size(); i++, j++) {
					if(headers.get(i).equalsIgnoreCase("id") && 
							genId.getValue() && !isAuto) {
						row.put(headers.get(i), lineId + "");
						j = i-1;
						isAuto = true;
					} else {
						try {
							row.put(headers.get(i), values[j]);
						} catch (Exception e) {}
						
						if(headers.get(i).toLowerCase().equals("id") &&
								!genId.getValue())
							idLabel = headers.get(i);
					}
				}
			} catch(Exception e) {}
			
			dataProvider.addOrChangeRow(lineId + "", row);
		}
		
		if(headersRow != null) {
			dataProvider.addOrChangeRow(headersRow.getId(), headersRow.getData());
			useHeader.setValue(false);
		}
		else {
			useHeader.setValue(true);
		}

		dataProvider.setDisplayRows(ROWS);
		dataProvider.refresh();
		dataProvider.flush();
		pager.setDisplay(grid);
		pager.setPageSize(ROWS);
		pager.setPageStart(0);
		grid.redraw();
	}
	
	@UiHandler("useHeader")
	void onClickUseHeader(ClickEvent e) {
		if(useHeader.getValue()) {
			for (int i = 0; i < headers.size(); i++)
				columnHeaders.get(i).setColumn(headers.get(i));
			
			if(headersRow != null) {
				dataProvider.addFillerRows(1);
				
				for (Row row : dataProvider.getList()) {
					if(row.getId().equals(headersRow.getId()))
						dataProvider.getList().remove(row);
				}
			}
			
			headersRow = null;
		}
		else {
			for (int i = 0; i < headers.size(); i++)
				columnHeaders.get(i).setColumn("");
			
			if(headersRow == null) {
				Map<String, String> data = new HashMap<>();
				for (String value : headers)
					data.put(value, value);
				
				headersRow = new Row("ID", data);
				dataProvider.addOrChangeRow(headersRow.getId(), data);
			}
		}
		
		grid.redrawHeaders();
	}
	
	@UiHandler("genId")
	void onClickGenId(ClickEvent e) {
		if(genId.getValue()) {
			fillDataGridRows(separator.getValue(separator
					.getSelectedIndex()));
		}
		else {
			grid.removeColumn(grid.getColumnIndex(idColumn));
			for (ImportCustomHeader importCustomHeader : columnHeaders) {
				if (importCustomHeader.getValue().equalsIgnoreCase("ID")) {
					columnHeaders.remove(importCustomHeader);
					break;
				}
			}
		}
		
		grid.redrawHeaders();
	}
	
	@UiHandler("separator")
	void onChangeSeparator(ChangeEvent e) {
		fillDataGridRows(separator.getValue(separator
				.getSelectedIndex()));
	}

}
