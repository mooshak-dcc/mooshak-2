package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomTextBox;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.Filenames;
import pt.up.fc.dcc.mooshak.client.widgets.DropFileSupportHandler;
import pt.up.fc.dcc.mooshak.client.widgets.TabbedMultipleEditor;
import pt.up.fc.dcc.mooshak.client.widgets.TabbedMultipleEditor.EditorMode;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * A composite widget to collect a local file content. Files can be selected
 * using a file selector or drag-and drop.
 * 
 * @author josepaiva
 * @since 2014-07-15
 *
 */
public class DropFileManager extends DropFileSupportHandler implements MooshakWidget,
	HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue>,
	HasFormData {

	private static DropFileManagerUiBinder uiBinder = GWT
			.create(DropFileManagerUiBinder.class);

	@UiTemplate("DropFileManager.ui.xml")
	interface DropFileManagerUiBinder extends UiBinder<Widget, DropFileManager> {
	}
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();
	
	private String field = null;
	private String id;

	@UiField
	CustomTextBox filename;

	@UiField
	TabbedMultipleEditor editor;

	@UiField
	FileUpload fileUpload;
	
	private MooshakValue value;
	
	private ProblemForm form;

	public DropFileManager() {
		initWidget(uiBinder.createAndBindUi(this));
		
		linkFieldsToHandler();

		filename.addHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				changeLanguageAceEditor();
			}
		}, BlurEvent.getType());
		
		setEditorMode(EditorMode.CODE);
	}

	private void linkFieldsToHandler() {
		filename.addValueChangeHandler(this);
		editor.addValueChangeHandler(this);
	}

	/**
	 * Form to call when a file is dropped
	 * @param problemForm
	 */
	public void setAssociatedForm(ProblemForm problemForm) {
		form = problemForm;
		
		editor.setProblemId(form.getProblemId());
	}

	/**
	 * Changes the editor language based in file name
	 */
	public void changeLanguageAceEditor() {
		
		switch (Filenames.getExtension(filename.getValue().getSimple())) {
		case "c":
		case "cpp":
			editor.setMode(AceEditorMode.C_CPP);
			break;
		case "pl":
			editor.setMode(AceEditorMode.PERL);
			break;
		case "py":
			editor.setMode(AceEditorMode.PYTHON);
			break;
		case "pas":
		case "p":
		case "pascal":
			editor.setMode(AceEditorMode.PASCAL);
			break;
		case "java":
			editor.setMode(AceEditorMode.JAVA);
			break;
		case "html":
			editor.setMode(AceEditorMode.HTML);
			break;
		case "json":
			editor.setMode(AceEditorMode.JSON);
			break;
		case "sql":
			editor.setMode(AceEditorMode.SQL);
			break;

		default:
			editor.setMode(AceEditorMode.TEXT);
			break;
		}
	}
	
	/**
	 * Sets editor mode
	 */
	public void setEditorMode(EditorMode mode) {
		editor.setMode(mode);
	}

	public String getProgramCode() {
		return editor.getText();
	}

	public String getProgramName() {
		return filename.getValue().getName();
	}

	/** Drag and drop **/

	public void setFileContent(byte[] content) {
		editor.setValue(new MooshakValue(field, filename.getValue().getSimple(),
				content));
	}
 
	public void setFilename(String filename) {
		this.filename.setValue(new MooshakValue(field, filename));
		changeLanguageAceEditor();
	}

	@Override
	public void onFileDropped(String content, String name) {
		byte[] result = Base64Coder.decode(content);
		if(form != null) {
			form.onFileDropped(result, name);
		}
	}
	
	/**
	 * Receive load notification to set JS event handlers
	 */
	protected void onLoad() {
		final int id;

		synchronized (pool) {
			pool.add(this);
			id = pool.size() - 1;
		}

		newFileContentJS(id, fileUpload.getElement(), filename.getElement(),
				editor.getElement(), RootPanel.getBodyElement());

		RootPanel.get().addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				if (!form.isShowing())
					return;
				
				// stop default behaviour
				event.preventDefault();
				event.stopPropagation();

				// starts the fetching, reading and callbacks
				dropHandlerSupport(event.getDataTransfer(), id);
			}
		}, DropEvent.getType());
	}

	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field, getProgramName(), 
				getProgramCode().getBytes());
	}
	
	public void setValue(String id, MooshakValue value) {
		this.id = id;
		setValue(value);
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		this.value = value;
		field = value.getField();
		fields.put(field, filename);
		fields.put(field, editor);
		
		filename.setValue(new MooshakValue(field, value.getName()),
				fireEvents);
		
		changeLanguageAceEditor();
		
		editor.setValue(new MooshakValue(field, value.getName(),
				value.getContent()));
	}

	@Override
	public boolean isEditing() {
		return editor.isEditing() || filename.isEditing();
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		ValueChangeEvent<MooshakValue> eventToSend;
		MooshakValue value = event.getValue();
		
		if(value.isSimpleValue())
			if (field.equalsIgnoreCase("pdf"))
				eventToSend = new ValueChangeEvent<MooshakValue>(new MooshakValue(field, 
						value.getSimple(), this.value.getContent())){};
			else
				eventToSend = new ValueChangeEvent<MooshakValue>(new MooshakValue(field, 
						value.getSimple(), editor.getValue().getContent())){};
		else
			eventToSend = new ValueChangeEvent<MooshakValue>(new MooshakValue(field, 
					filename.getValue().getSimple(), value.getContent())){};
		
		form.onFileValueChange(id, eventToSend);
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
			if(fields.containsKey(fieldName))
				if(!isEditing() && fieldName.equals(field))
					fields.get(fieldName).setValue(data.get(fieldName));
		}
	}
	
	/**
	 * Get field values as map indexed by field names
	 * @param data
	 */
	@Override
	public Map<String,MooshakValue> getFieldValues() {
		Map<String,MooshakValue> data = new HashMap<String,MooshakValue>();
		
		for(String fieldName: fields.keySet()) {
			if(fieldName.equals(field))
				data.put(fieldName,fields.get(fieldName).getValue());
		}
		
		return data;
	}
	
	public void deselectFile() {
		field = null;
		id = null;
		setEditorMode(EditorMode.CODE);
		setFilename("");
		setFileContent("".getBytes());
	}

	public void setSelectedField(String id, String field) {
		this.id = id;
		this.field = field;
		editor.setField(field);
	}

}