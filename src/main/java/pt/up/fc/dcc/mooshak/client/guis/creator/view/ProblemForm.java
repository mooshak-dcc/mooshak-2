package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.form.admin.ColorPicker;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomTextBox;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.form.admin.TextDateBox;
import pt.up.fc.dcc.mooshak.client.form.admin.TextListBox;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.ContentDataProvider;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.ContentFileDataLine;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.ProblemTestsDataLine;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.ProgramFileDataLine;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.ProgramFileDataProvider;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.TestDataProvider;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.ProblemView.BaseStyle;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.ProblemView.Presenter;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.TabbedMultipleEditor.EditorMode;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Form with fields for editing problems
 * 
 * @author josepaiva
 */
public class ProblemForm extends Composite implements HasFormData,
		HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue> {

	private static ProblemFormUiBinder uiBinder = GWT.create(ProblemFormUiBinder.class);

	@UiTemplate("ProblemForm.ui.xml")
	interface ProblemFormUiBinder extends UiBinder<Widget, ProblemForm> {
	}

	// Data grid styles
	public interface CustomDataGridResources extends DataGrid.Resources {
	 
	    @Source({DataGrid.Style.DEFAULT_CSS, "DataGridOverride.css"})
	    CustomStyle dataGridStyle();
	 
	    interface CustomStyle extends DataGrid.Style {
	    	@Override
	    	public String dataGridKeyboardSelectedCell();
	    	
	    	@Override
	    	public String dataGridSelectedRow();
	    	
	    	@Override
	    	public String dataGridSelectedRowCell();
	    	
	    	@Override
	    	public String dataGridHeader();
	    	
	    	@Override
	    	public String dataGridFooter();
	    	
	    	@Override
	    	public String dataGridSortableHeader();
	    	
	    	@Override
	    	public String dataGridCell();
	    	
	    	@Override
	    	public String dataGridWidget();
	    	
	    	@Override
	    	public String dataGridFirstColumnHeader();
	    	
	    	@Override
	    	public String dataGridFirstColumn();
	    	
	    	public String selected();
	    }
	}
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	@UiField
	BaseStyle style;

	@UiField
	CustomTextBox name;

	@UiField
	CustomTextBox title;

	@UiField
	ColorPicker color;

	@UiField
	TextListBox difficulty;

	@UiField
	TextListBox type;

	@UiField
	TextListBox editorKind;

	@UiField
	CustomLabelPath environment;

	@UiField
	CustomTextBox timeout;

	@UiField
	TextDateBox start;

	@UiField
	TextDateBox stop;

	@UiField
	CustomTextBox staticCor;

	@UiField
	CustomTextBox dynamicCor;

	@UiField
	CustomLabelPath description;

	@UiField
	CustomLabelPath pdf;

	@UiField
	DropFileManager fileManager;

	@UiField(provided = true)
	DataGrid<ProblemTestsDataLine> iodata;

	@UiField(provided = true)
	DataGrid<ProgramFileDataLine> programdata;

	@UiField(provided = true)
	DataGrid<ContentFileDataLine> imagedata;
	
	@UiField
	TextArea observations;
	
	private TestDataProvider ioDataProvider = new TestDataProvider();
	private ProgramFileDataProvider programDataProvider = new ProgramFileDataProvider();
	private ContentDataProvider imageDataProvider = new ContentDataProvider();
	
	private String dataSelectedCell = null;

	CustomDataGridResources resource = GWT.create(CustomDataGridResources.class);
	
	private Presenter presenter = null;
	

	public ProblemForm() {
		configureIOdata();
		configureImagedata();
		configureProgramData();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		description.setStyle(style);
		pdf.setStyle(style);
		environment.setStyle(style);

		editorKind.addItem("");
		for (EditorKind kind : EditorKind.values()) 
			editorKind.addItem(kind.toString());
		
		name.setMaxLength(3);
		for (final CustomLabelPath files : Arrays.asList(description,
				environment)) {
			files.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					clearSelected();
					
					if(files.getValue().getName() == null)
						presenter.createDefaultFile(files.getValue().getField());
					else
						setSelectedValue(files.getValue());
					
					files.getElement().addClassName(style.selected());
				}
			});
		}
		
		pdf.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(pdf.getValue().getName() == null)
					return;
				
				clearSelected();
				setSelectedValue(pdf.getValue());
				
				pdf.getElement().addClassName(style.selected());
			}
		});
		
		fileManager.editor.diagramLanguageSelector
			.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {
	
				@Override
				public void onValueChange(ValueChangeEvent<SelectableOption> event) {
					presenter.onChangeDiagramLanguage(event.getValue().getId());
				}
			});
		
		linkFieldsToHandlers();
	}

	public void setSelectedValue(MooshakValue value) {
					
		dataSelectedCell = value.getField()
				.toLowerCase() + "," + value.getName();
		updateEditorMode();
		fileManager.setValue("", value);
	}

	private void linkFieldsToHandlers() {
		fields.put("Name", name);
		name.addValueChangeHandler(this);
		
		fields.put("Title", title);
		title.addValueChangeHandler(this);

		fields.put("Color", color);
		color.addValueChangeHandler(this);

		fields.put("Difficulty", difficulty);
		difficulty.addValueChangeHandler(this);

		fields.put("Type", type);
		type.addValueChangeHandler(this);

		fields.put("Editor_kind", editorKind);
		editorKind.addValueChangeHandler(this);

		fields.put("Timeout", timeout);
		timeout.addValueChangeHandler(this);

		fields.put("Start", start);
		start.addValueChangeHandler(this);

		fields.put("Stop", stop);
		stop.addValueChangeHandler(this);

		fields.put("Static_corrector", staticCor);
		staticCor.addValueChangeHandler(this);

		fields.put("Dynamic_corrector", dynamicCor);
		dynamicCor.addValueChangeHandler(this);

		fields.put("Description", description);
		description.addValueChangeHandler(this);

		fields.put("PDF", pdf);
		pdf.addValueChangeHandler(this);

		fields.put("Environment", environment);
		environment.addValueChangeHandler(this);
	}

	/**
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		
		fileManager.setAssociatedForm(this);
	}

	/**
	 * Fills the possible selection options
	 * 
	 * @param values
	 */
	public void fillPossibleValues(Map<String, List<String>> values) {

		difficulty.addItem("");
		type.addItem("");
		
		for (String option : values.get("difficulty")) 
			difficulty.addItem(option);
		for (String option : values.get("type")) 
			type.addItem(option);
	}


	/**
	 * Configures the test grid
	 */
	private void configureIOdata() {

		iodata = new DataGrid<ProblemTestsDataLine>(Integer.MAX_VALUE,
				resource);
		iodata.setWidth("100%");

		iodata.setAutoHeaderRefreshDisabled(true);

		Column<ProblemTestsDataLine, String> nameColumn 
			= new Column<ProblemTestsDataLine, String>(new TextCell()) {

			@Override
			public String getValue(ProblemTestsDataLine object) {
				return object.getName();
			}

		};

		iodata.addColumn(nameColumn, "Test");
		iodata.setColumnWidth(nameColumn, 15, Unit.PCT);

		Column<ProblemTestsDataLine, String> inputColumn 
			= new Column<ProblemTestsDataLine, String>(new ClickableTextCell()) {

			@Override
			public String getValue(ProblemTestsDataLine object) {
				return object.getInput().getValue().getName();
			}
		};
		inputColumn.setFieldUpdater(new FieldUpdater<ProblemTestsDataLine, String>() {

			@Override
			public void update(int index, ProblemTestsDataLine object, String value) {
				if(object.getName().equals(""))
					return;
				
				Element selected = getSelectedFieldElement();
				if(selected != null)
					selected.removeClassName(style.selected());
				dataSelectedCell = "test," + object.getName() + "," + object.getInput()
						.getValue().getName();

				iodata.getRowElement(index)
						.getCells().getItem(1).addClassName(style.selected());
				
				updateEditorMode();
				
				fileManager.setValue(object.getId(), object.getInput().getValue());
			}
		});		

		iodata.addColumn(inputColumn, "Input");
		iodata.setColumnWidth(inputColumn, 20, Unit.PCT);
		
		Column<ProblemTestsDataLine, String> outputColumn 
			= new Column<ProblemTestsDataLine, String>(new ClickableTextCell()) {

			@Override
			public String getValue(ProblemTestsDataLine object) {
				return object.getOutput().getValue().getName();
			}

		};
		outputColumn.setFieldUpdater(new FieldUpdater<ProblemTestsDataLine, String>() {

			@Override
			public void update(int index, ProblemTestsDataLine object, String value) {
				if(object.getName().equals(""))
					return;
				
				Element selected = getSelectedFieldElement();
				if(selected != null)
					selected.removeClassName(style.selected());
				dataSelectedCell = "test," + object.getName() + "," + object.getOutput()
						.getValue().getName();
				iodata.getRowElement(index)
						.getCells().getItem(2).addClassName(style.selected());
				
				updateEditorMode();
				
				fileManager.setValue(object.getId(), object.getOutput().getValue());
			}
		});

		iodata.addColumn(outputColumn, "Output");
		iodata.setColumnWidth(outputColumn, 20, Unit.PCT);
		
		Column<ProblemTestsDataLine, String> resultColumn 
			= new Column<ProblemTestsDataLine, String>(new TextCell()) {
	
			@Override
			public String getValue(ProblemTestsDataLine object) {
				return object.getResult().getValue().getSimple();
			}
			
			@Override
			public void render(Context context, ProblemTestsDataLine object,
					SafeHtmlBuilder sb) {
				if(object == null)
					return;
				
				String value = object.getResult().getValue().getSimple();
				if (value != null) {
					if(value.equalsIgnoreCase("A"))
					     sb.appendHtmlConstant("<span style=\"color: green\""
					     		+ " title=\"\">"
					    		 + value + "</span>");
					else
						sb.appendHtmlConstant("<span style=\"color: red\""
					     		+ " title=\"Wrong in: " + object.getSolutionErrors() + "\">"
					    		 + value + "</span>");
			    }
			}
	
		};

		iodata.addColumn(resultColumn, "Result");
		iodata.setColumnWidth(resultColumn, 16, Unit.PCT);
		
		Column<ProblemTestsDataLine, String> timeoutColumn 
			= new Column<ProblemTestsDataLine, String>(new TextCell()) {
	
			@Override
			public String getValue(ProblemTestsDataLine object) {
				return object.getTimeout().getValue().getSimple();
			}
			
		};

		iodata.addColumn(timeoutColumn, "Timeout");
		iodata.setColumnWidth(timeoutColumn, 16, Unit.PCT);
		
		Column<ProblemTestsDataLine, String> buttonColumn = new Column<ProblemTestsDataLine, String>(
				new CellDeleteButton()) {

			@Override
			public String getValue(ProblemTestsDataLine pair) {
				return pair.getName();
			}
			
		};
		buttonColumn.setFieldUpdater(new FieldUpdater<ProblemTestsDataLine, String>() {
			
			@Override
			public void update(int index, ProblemTestsDataLine object, String value) {
				if(!object.getName().equals("")) {
					presenter.onDeleteTest(object.getId());
				}
					
				clearSelected();
			}
		});

		iodata.addColumn(buttonColumn, "");
		iodata.setColumnWidth(buttonColumn, 15, Unit.PCT);

		ioDataProvider.addDataDisplay(iodata);
	}



	/**
	 * Configures the program grid
	 */
	private void configureProgramData() {

		programdata = new DataGrid<ProgramFileDataLine>(Integer.MAX_VALUE,
				resource);
		programdata.setWidth("100%");

		programdata.setAutoHeaderRefreshDisabled(true);

		Column<ProgramFileDataLine, String> nameColumn 
			= new Column<ProgramFileDataLine, String>(new ClickableTextCell()) {

			@Override
			public String getValue(ProgramFileDataLine object) {
				return object.getName();
			}

		};
		nameColumn.setFieldUpdater(new FieldUpdater<ProgramFileDataLine, String>() {

			@Override
			public void update(int index, ProgramFileDataLine object, String value) {
				if(object.getName().equals(""))
					return;
				
				Element selected = getSelectedFieldElement();
				if(selected != null)
					selected.removeClassName(style.selected());
				
				if(object.getType().toLowerCase().equals("solution"))
					dataSelectedCell = object.getType().toLowerCase() + "," + object.getId();
				else
					dataSelectedCell = object.getType().toLowerCase() + "," + object.getName();

				programdata.getRowElement(index)
						.getCells().getItem(0).addClassName(style.selected());
				
				updateEditorMode();
				
				fileManager.setValue(object.getId(), object.getValue());
			}
		});		

		programdata.addColumn(nameColumn, "Name");
		programdata.setColumnWidth(nameColumn, 40, Unit.PCT);

		Column<ProgramFileDataLine, String> typeColumn 
			= new Column<ProgramFileDataLine, String>(
					new SelectionCell(Arrays.asList("", "Solution", "Skeleton"))) {

			@Override
			public String getValue(ProgramFileDataLine object) {
				
				if(object.getType() == null)
					return null;
				return object.getType().equals("Program") ? "Solution" : object.getType();
			}

		};
		typeColumn.setFieldUpdater(new FieldUpdater<ProgramFileDataLine, String>() {

			@Override
			public void update(int index, ProgramFileDataLine object, String newType) {
				if(object.getType().toLowerCase().equals("solution"))
					presenter.changeProgramType(object.getId().substring(0, object
							.getId().lastIndexOf("/")),	object.getValue(), newType);
				else
					presenter.changeProgramType(object.getId(), 
							object.getValue(), newType);
				
				programDataProvider.deleteRow(object.getId());
			}
		});		

		programdata.addColumn(typeColumn, "Type");
		programdata.setColumnWidth(typeColumn, 40, Unit.PCT);

		Column<ProgramFileDataLine, String> buttonColumn = 
				new Column<ProgramFileDataLine, String>(
				new CellDeleteButton()) {

			@Override
			public String getValue(ProgramFileDataLine pair) {
				return pair.getName();
			}
			
		};
		buttonColumn.setFieldUpdater(new FieldUpdater<ProgramFileDataLine, String>() {
			
			@Override
			public void update(int index, ProgramFileDataLine object, String value) {
				if (object.getType().equals("Skeleton"))
					presenter.onRemoveSkeleton(value);
				else
					presenter.onRemoveSolution(value);
				clearSelected();
			}
		});

		programdata.addColumn(buttonColumn, "");
		programdata.setColumnWidth(buttonColumn, 16, Unit.PCT);

		programDataProvider.addDataDisplay(programdata);
	}

	
	/**
	 * Clears the selected element
	 */
	private void clearSelected() {
		Element selected = getSelectedFieldElement();
		if(selected == null) 
			return;
		selected.removeClassName(style.selected());
		dataSelectedCell = null;
	}

	/**
	 * Configures the image grid
	 */
	private void configureImagedata() {

		imagedata = new DataGrid<ContentFileDataLine>(Integer.MAX_VALUE,
				resource);
		imagedata.setWidth("100%");

		imagedata.setAutoHeaderRefreshDisabled(true);

		Column<ContentFileDataLine, String> nameColumn 
			= new Column<ContentFileDataLine, String>(new ClickableTextCell()) {

			@Override
			public String getValue(ContentFileDataLine object) {
				return object.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<ContentFileDataLine, String>() {

			@Override
			public void update(int index, ContentFileDataLine object, String value) {
				if(object.getName().equals(""))
					return;
				
				Element selected = getSelectedFieldElement();
				if(selected != null)
					selected.removeClassName(style.selected());
				dataSelectedCell = "image," + object.getName();
				imagedata.getRowElement(index)
						.getCells().getItem(0).addClassName(style.selected());
				
				updateEditorMode();
			}
		});		

		imagedata.addColumn(nameColumn, "Image Name");
		imagedata.setColumnWidth(nameColumn, 54, Unit.PCT);

		Column<ContentFileDataLine, String> buttonColumn = new Column<ContentFileDataLine, String>(
				new CellDeleteButton()) {

			@Override
			public String getValue(ContentFileDataLine object) {
				return object.getName();
			}
			
		};/*
		buttonColumn.setFieldUpdater(new FieldUpdater<ProblemImagesDataLine, String>() {
			
			@Override
			public void update(int index, ProblemImagesDataLine object, String value) {
				if(!object.getName().equals(""))
					imageDataProvider.deleteRow(object.getName());
				
				clearSelected();
			}
		});*/

		imagedata.addColumn(buttonColumn, "");
		imagedata.setColumnWidth(buttonColumn, 16, Unit.PCT);

		imageDataProvider.addDataDisplay(imagedata);
	}

	/**
	 * @return the selected cell
	 */
	private Element getSelectedFieldElement() {
		if(dataSelectedCell == null)
			return null;
		
		String[] values = dataSelectedCell.split(",");
		if("test".equals(values[0])) {
			for (int i = 0; i < iodata.getRowCount(); i++) {
				
				NodeList<TableCellElement> cells = iodata.getRowElement(i)
						.getCells();
				
				if(cells.getItem(0).getInnerText().equals(values[1])) {
					for (int j = 1; j < cells.getLength()-1; j++)
						if(cells.getItem(j).getInnerText().equals(values[2]))
							return cells.getItem(j);
					break;
				}
				
			}
		}
		else if("image".equals(values[0])) {
			for (int i = 0; i < imagedata.getRowCount(); i++) {
				
				NodeList<TableCellElement> cells = imagedata.getRowElement(i)
						.getCells();
				
				if(cells.getItem(0).getInnerText().equals(values[1]))
					return cells.getItem(0);
				
			}
		}
		else if("skeleton".equals(values[0]) || "program".equals(values[0])) {
			for (int i = 0; i < programdata.getRowCount(); i++) {
				
				NodeList<TableCellElement> cells = programdata.getRowElement(i)
						.getCells();
				
				if(cells.getItem(0).getInnerText().equals(values[1]))
					return cells.getItem(0);
				
			}
		}
		else if("solution".equals(values[0])) {
			for (int i = 0; i < programdata.getRowCount(); i++) {
				
				NodeList<TableCellElement> cells = programdata.getRowElement(i)
						.getCells();
				
				if(cells.getItem(0).getInnerText().equals(values[1]
						.substring(values[1].lastIndexOf("/")+1)))
					return cells.getItem(0);
				
			}
		}
		else {
			switch (values[0]) {
			case "pdf":
				return pdf.getElement();
			case "environment":
				return environment.getElement();
			case "description":
				return description.getElement();
			}
		}
		
		return null;
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		for(ValueChangeHandler<MooshakValue> handler: valueChangeHandlers)
			handler.onValueChange(event);
	}

	public void onFileValueChange(String id,
			ValueChangeEvent<MooshakValue> event) {
		if(event.getValue().getField() == null) {
			if(!event.getValue().getName().equals(""))
				presenter.uploadFile(new String(Base64Coder
						.encode(event.getValue().getContent())).getBytes(), 
						event.getValue().getName(), null, false);
			return;
		}
		
		if(dataSelectedCell.split(",")[0].equals("solution")) {
			presenter.onChangeSolution(dataSelectedCell.split(",")[1], 
					event.getValue());
			return;
		}

		MooshakValue pair = event.getValue();
		presenter.onChange(id,pair);
		
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		valueChangeHandlers.add(handler);
		
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {	
				valueChangeHandlers.remove(handler);
			}
		};
	}
	
	/**
	 * Populate to form's field values with a map indexed by field names
	 * 
	 * @param data a map with the field values indexed by field names
	 */
	@Override
	public void setFieldValues(Map<String,MooshakValue> data) {
		
		for(String fieldName: data.keySet()) {
			
			if(fields.containsKey(fieldName)) {
				if(!fields.get(fieldName).isEditing() && data.get(fieldName) != null)
					fields.get(fieldName).setValue(data.get(fieldName));
			}
		}
	}
	
	/**
	 * Get field values as map indexed by field names
	 * @param data
	 */
	@Override
	public Map<String,MooshakValue> getFieldValues() {
		Map<String,MooshakValue> data = new HashMap<String,MooshakValue>();
		
		for(String fieldName: data.keySet()) {
			data.put(fieldName,fields.get(fieldName).getValue());
		}
		
		return data;
	}
	
	public String getProblemId() {
		return presenter.getProblemId();
	}

	public String getSelectedItem() {
		return dataSelectedCell;
	}
 
	public void setFileName(String name) {
		fileManager.setFilename(name);
	}

	public void changeLanguageAceEditor() {
		fileManager.changeLanguageAceEditor();
	}
	
	/**
	 * Calls presenter when a file is dropped
	 * @param content
	 * @param name
	 */
	public void onFileDropped(byte[] content, String name) {
		String selectedData = dataSelectedCell;
		if(selectedData != null && selectedData.indexOf("solution,") != -1)
			selectedData = "solution," + selectedData.substring(selectedData
					.lastIndexOf("/") + 1);
		
		presenter.uploadFile(content, name, selectedData, true);
		Element selected = getSelectedFieldElement();
		if(selected == null) 
			return;
		
		selected.removeClassName(style.selected());
		dataSelectedCell = null;
		
		fileManager.setFilename("");
		fileManager.setFileContent("".getBytes());
	}
	
	/**
	 * Adds a line with formData to the test list
	 * @param test
	 * @param formDataProvider
	 */
	public ProblemTestsDataLine addTestToList(String test, 
			FormDataProvider formDataProvider) {
		ProblemTestsDataLine line = new ProblemTestsDataLine(test);
		formDataProvider.addFormDataProvider(line);
		formDataProvider.refresh();
		
		ioDataProvider.addIOPair(line);
		
		ioDataProvider.flush();
		ioDataProvider.refresh();
		
		iodata.redraw();
		
		return line;
	}

	public void deleteIODataProviderRow(String id) {
		ioDataProvider.deleteRow(id);
	}
	
	/** Datagrid add and deselect buttons **/

	@UiField
	Button addTest;
	
	@UiHandler(value = { "addTest" })
	void onAddTest(ClickEvent e) {
		if(presenter != null)
			presenter.onAddTest();
	}

	@UiField
	Button addProgram;
	
	@UiHandler(value = { "addProgram" })
	void onAddSkeleton(ClickEvent e) {
		if(presenter != null)
			presenter.onAddSolution();
	}

	@UiField
	Button addImage;
	
	@UiHandler(value = { "addImage" })
	void onAddImage(ClickEvent e) {
		
	}

	@UiField
	Button deselect;
	
	@UiHandler(value = { "deselect" })
	void onDeselect(ClickEvent e) {
		Element selected = getSelectedFieldElement();
		if(selected == null) 
			return;
		
		selected.removeClassName(style.selected());
		dataSelectedCell = null;
		
		fileManager.deselectFile();
	}
	
	@UiField
	Button delEnv;
	
	@UiHandler(value = { "delEnv" })
	void onDelEnv(ClickEvent e) {
		if (environment.getValue().getName() == null)
			return;
		presenter.onRemoveFile("Environment", environment.getValue().getName());
		environment.setValue(new MooshakValue(environment.getValue().getField(), null, null), true);
		clearSelected();
	}
	
	@UiField
	Button delDesc;
	
	@UiHandler(value = { "delDesc" })
	void onDelDesc(ClickEvent e) {
		if (description.getValue().getName() == null)
			return;
		presenter.onRemoveFile("Description", description.getValue().getName());
		description.setValue(new MooshakValue(description.getValue().getField(), null, null), true);
		clearSelected();
	}
	
	@UiField
	Button delPdf;
	
	@UiHandler(value = { "delPdf" })
	void onDelPdf(ClickEvent e) {
		if (pdf.getValue().getName() == null)
			return;
		presenter.onRemoveFile("PDF", pdf.getValue().getName());
		pdf.setValue(new MooshakValue(pdf.getValue().getField(), null, null), true);
		clearSelected();
	}

	public void setImagesDataProvider(FormDataProvider dataProvider) {
		dataProvider.addFormDataProvider(imageDataProvider);
		
		dataProvider.refresh();
	}

	public void setSolutions(MooshakValue value, String id) {
		
		if(value.getField().equalsIgnoreCase("Program")) {
			ProgramFileDataLine line = new ProgramFileDataLine(id, 
					value.getField(), value.getName(), value.getContent());
			
			programDataProvider.addProgramFile(line);
		}
		else {
			for (String name : value.getFileNames()) {
				ProgramFileDataLine line = new ProgramFileDataLine(id + "/" + name, 
						value.getField(), name, value.getFileValue(name).getContent());
				
				programDataProvider.addProgramFile(line);
			}
		}
		
		programDataProvider.refresh();
	}

	public void clearTestProvider() {
		ioDataProvider.removeAllLines();
		iodata.redraw();
	}

	public void deleteProgramDataProviderRow(String id) {
		programDataProvider.deleteRow(id);
		iodata.redraw();
	}

	public void clearProgramDataProvider(String field) {
		programDataProvider.removeAllLines(field);
		iodata.redraw();
	}

	public ProgramFileDataLine addProgramDataToList(String program,
			MooshakObject object) {
		
		MooshakValue value = object.getFieldValue("Skeleton");
		ProgramFileDataLine line = new ProgramFileDataLine(object.getId(), 
				value.getField(), value.getName(), value.getContent());
		
		programDataProvider.addProgramFile(line);
		
		programDataProvider.refresh();
		
		return line;
	}

	public void setSelectedField(String id, MooshakValue value) {

		if(value.getField().toLowerCase().equals("solution")) {
			dataSelectedCell = value.getField().toLowerCase() + ","
					+ id + "/solutions/" + value.getName();

			fileManager.setSelectedField(id+ "/solutions", value.getField());
		}
		else if(value.getField().equals("input") || value.getField().equals("output")) {
			dataSelectedCell = null;
			fileManager.setFileContent("".getBytes());
			fileManager.setFilename("");
		}
		else {
			dataSelectedCell = value.getField().toLowerCase() + ","
					+ value.getName();
			fileManager.setSelectedField(id, value.getField());
		}
		
		updateEditorMode();
	}
	
	
	private static RegExp filePattern = RegExp.compile("(.*/)(?:$|(.+?)(?:(\\.[a-zA-Z0-9]*)))","gm");

	public void addObservation(String obs) {
		MatchResult result = null;
		
		result = filePattern.exec(obs);
		while(result != null) {
			String path = result.getGroup(1);
			obs = obs.replace(path, "");
			result = filePattern.exec(obs);
		}
		
		observations.setText(observations.getText() + "\n" + obs);
		
		scrollBottom(observations.getElement());
	}
	
	private native void scrollBottom(JavaScriptObject ta) /*-{ 
		ta.scrollTop = ta.scrollHeight;
	}-*/;
	
	/**
	 * Is this problem the current?
	 */
	public boolean isShowing() {
		return presenter.isShowing();
	}
	
	/**
	 * Update the mode of the editor
	 */
	public void updateEditorMode() {
		
		Logger.getLogger("").severe("update mode ...");

		if (dataSelectedCell != null) {
		
			if (dataSelectedCell.startsWith("program") || dataSelectedCell.startsWith("solution")) {
				if (editorKind.getSelectedValue() == null || editorKind.getSelectedValue().equals(""))
					fileManager.setEditorMode(EditorMode.CODE);
				else
					fileManager.setEditorMode(EditorMode.valueOf(editorKind.getSelectedValue()));
				return;
			} else if (dataSelectedCell.startsWith("description")) {
				fileManager.setEditorMode(EditorMode.HTML);
				return;
			} else if (dataSelectedCell.startsWith("pdf")) {
				fileManager.setEditorMode(EditorMode.PDF);
				return;
			}
		}
		
		fileManager.setEditorMode(EditorMode.CODE);
	}

	public void createEshu(ConfigInfo configInfo) {
		fileManager.editor.createEshu(configInfo);
	}

	public void setDiagramLanguageOptions(List<SelectableOption> options) {
		fileManager.editor.setDiagramLanguageOptions(options);
	}
}
