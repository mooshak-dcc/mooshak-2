package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject.Processor;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.PathIDs;
import pt.up.fc.dcc.mooshak.client.utils.StringSpaceSeparatedUtils;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Form with fields for editing MooshakObjects.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ObjectForm extends Composite implements HasFormData, 
	HasValueChangeHandlers<MooshakValue>,
	ValueChangeHandler<MooshakValue> {

	private static final int FIELDS_PER_COLUMN = 6;
	private static final int SUB_COLUMNS = 3;

	private final Logger logger = Logger.getLogger("");
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();
	
	private static ObjectFormUiBinder uiBinder = 
			GWT.create(ObjectFormUiBinder.class);

	
	@UiTemplate("ObjectForm.ui.xml")
	interface ObjectFormUiBinder extends UiBinder<Widget, ObjectForm> {
	}

	class ComplementableTextListBox { 
		TextListBox textListBox;
		String complement;
		
		public ComplementableTextListBox(TextListBox testListBox,
				String complement) {
			super();
			this.textListBox = testListBox;
			this.complement = complement;
		}	
	}
	
	private	Map<String,ComplementableTextListBox> complements 
		= new HashMap<String,ComplementableTextListBox>();
	
	private AdminCommandServiceAsync rpc;
	
	private boolean container;
	private String objectId;
	
	@UiField
	Label label;
	
	@UiField
	Grid grid;
	

	public ObjectForm() {
		initWidget(uiBinder.createAndBindUi(this));
		grid.resizeRows(0);
		grid.resizeColumns(0);
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
	
	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		for(ValueChangeHandler<MooshakValue> handler: valueChangeHandlers)
			handler.onValueChange(event);
	}
	
	/**
	 * Create the structure of the form
	 * 
	 * @param fieldDefinitions
	 */
	public void setFieldTypes(List<MooshakField> fieldDefinitions) {
		int size = calculateNumberOfFieldsToShow(fieldDefinitions);
		int columns = SUB_COLUMNS*(size / FIELDS_PER_COLUMN);
		
		if(columns == 0)
			columns = SUB_COLUMNS;
		
		grid.resizeColumns(columns);	
		
		int row = 0;
		int column = 0;
		
		Label spanner = new Label();
		spanner.setSize("1cm", "1cm");
		
		for(MooshakField fieldDefinition: fieldDefinitions) {
			String name = fieldDefinition.getName();
			AttributeType type = fieldDefinition.getType();
			Widget widget = makeFieldWidget(fieldDefinition);
			
			if(ignorableField(type)) {
				// Just ignore
			} else if(type == AttributeType.CONTENT && container) {
				// just ignore
			} else if(widget == null) 
				logger.log(Level.WARNING,"Unknown field type:"+type);
			else if(widget instanceof MooshakWidget) {
				widget.ensureDebugId(name);
				
				MooshakWidget mooshakWidget = (MooshakWidget) widget;
				
				if(column == columns) {
					row++;
					column = 0;
				}
				if(column == 0)
					grid.resizeRows(row+1);
				
				Label label = new Label(StringSpaceSeparatedUtils.splitCamelCase(
						StringSpaceSeparatedUtils.splitUnderscore(name)));
				Widget extra = spanner;
				String tip = fieldDefinition.getTip();
				String help = fieldDefinition.getHelp();
				
				if (type == AttributeType.TEXT && fieldDefinition.getMaxLength() > -1)
					((CustomTextBox) widget).setMaxLength(fieldDefinition.getMaxLength());
				
				if(tip != null && ! "".equals(tip)) {
					label.setTitle(tip);
					widget.setTitle(tip);
				}
				
				if(help != null && ! "".equals(help))
					extra = new HelpButton(help);
				
				grid.setWidget(row, column++, label);
				grid.setWidget(row, column++, widget);
				grid.setWidget(row, column++, extra);
				
				fields.put(name, mooshakWidget);
				mooshakWidget.addValueChangeHandler(this);
			} else {
				String msg ="Field widget with invalid type:"+widget.getClass();
				logger.log(Level.WARNING,msg);
			}
		}
		
		label.removeFromParent();
	}
	
	/**
	 * Calculates the number of fields to be shown (excluding data types)
	 * @param size
	 * @return
	 */
	private int calculateNumberOfFieldsToShow(List<MooshakField> fields) {
		int count = 0;
		for (MooshakField mooshakField : fields) {
			if(!ignorableField(mooshakField.getType()))
				count++;
		}
		
		return count;
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
				MooshakWidget field = fields.get(fieldName);
				if(! field.isEditing()) {
					MooshakValue value = data.get(fieldName);
					if(complements.containsKey(fieldName))
						populateTextListBoxWithRelativeReferences(fieldName,value);
					else
						field.setValue(value);
				}
			}
		}
	}

	/**
	 * List boxes with relative references cannot be populated with items
	 * when they are first created. The items have to be populated for a given
	 * object id. 
	 * @param fieldName
	 * @param value
	 */
	private void populateTextListBoxWithRelativeReferences(
			String fieldName,
			final MooshakValue value) {
		
		String complement = complements.get(fieldName).complement;
		TextListBox textListBox = complements.get(fieldName).textListBox;
		String resolved = PathIDs.normalize(objectId+"/"+complement+"/*");
		List<String> contracted = new ArrayList<String>();
		List<String> expanded = new ArrayList<String>();
		
		contracted.add(resolved);
		expand(contracted,expanded,textListBox,value);
	}
	
	/**
	 * Expand pathnames containing one or more stars ("*") into a list
	 * of pathnames without stars; then populate TextListBox with that list
	 * and set it with the value
	 * 
	 * @param contracted
	 * @param expanded
	 * @param textListBox
	 * @param value
	 */
	private void expand(
			final List<String> contracted,
			final List<String> expanded,
			final TextListBox textListBox,  
			final MooshakValue value) {
		
		if(contracted.size() == 0 ) {
			List<String> items = new ArrayList<String>();
			
			for(String path: expanded)
				items.add(PathIDs.getIdName(path));

			textListBox.resetItemList(items);	
			textListBox.setValue(value);
			
		} else { // contracted.size() > 0
			
			String first = contracted.remove(0);
			int pos = first.indexOf("/*");
			String prefix = first.substring(0,pos);
			final String suffix = first.substring(pos+2);
			
			DataManager.getInstance().getMooshakObject(prefix, new Processor() {

				@Override
				public void process(DataObject dataObject) {
					
					for(String path: dataObject.getData().getChildren()) {
						String fullPath = path + suffix;
						if(fullPath.contains("/*"))
							contracted.add(fullPath);
						else
							expanded.add(fullPath);
					}
					
					expand(contracted,expanded,textListBox,value);
				}
			});
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
	
	/**
	 * Should fields of this type be ignored on forms?
	 * @param type of field
	 * @return {@code true} to ignore; {@code false} otherwise 
	 */
	private boolean ignorableField(AttributeType type) {

		switch(type) {
		case DATA:
		case HIDDEN:
			return true;
		default:
			return false;
		}
	}
			
	
	/**
	 * Make a widget given a field type
	 * @param type
	 * @return
	 */
	private Widget makeFieldWidget(MooshakField field) {
					
			switch(field.getType()) {
			case LABEL:
				return new HtmlFreeLabel(
						getLabelForegroundColor(field.getName()));
			case TEXT:
				return new CustomTextBox();
			case PASSWORD:
				return new CustomPasswordTextBox();
			case LONG_TEXT:
				return new CustomTextArea();
			case DATE:
				return new TextDateBox();
			case LONG:
			case INTEGER:  
				return new IntegerBox();
			case DOUBLE:  
			case FLOAT:  
				return new DoubleBox();
			case PATH:
				TextListBox list = new TextListBox(field.getAlternatives(),false);
				String complement = field.getComplement();
				
				if(complement != null && ! "".equals(complement))
					complements.put(field.getName(),
							new ComplementableTextListBox(list, complement));
				
				return list;
			case MENU:
				return new TextListBox(field.getAlternatives(),false);
			case LIST:
				return new TextListBox(field.getAlternatives(),true);
			case FILE:
				if (field.isQuizEditor())
					return new QuizFileEditorButton(rpc);
				else if (field.getDocSpec() == null)
					return new FileEditorButton(rpc);
				else
					return new XmlFileEditorButton(rpc, field.getDocSpec());
			case COLOR:
				return new ColorPicker();
			case CONTENT:
				return new ContentAttributeManager();
			default:
				return null;
			}
	}
	
	/**
	 * Assign a foreground color depending on widget name
	 * @param name
	 * @return
	 */
	private String getLabelForegroundColor(String name) {
		
		switch(name) {
		case "Fatal":
			return "red";
		case "Warning":
			return "orange";
		default:
			return "black";	
		}
	}
	

	public void setRpcService(AdminCommandServiceAsync rpc) {
		this.rpc = rpc;
	}

	/**
	 * Is this is the editor of a container?
	 * @param contentType
	 */
	public boolean isContainer() {
		return container;
	}
	
	/**
	 * Set if this is the editor of a container 
	 * @param contentType
	 */
	public void setContainer(boolean container) {
		this.container = container;
		
	}

	/**
	 * Set id of object in this form. Needed for expanding 
	 * relative PATHs complements
	 * @param objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
		
	}
	

}
