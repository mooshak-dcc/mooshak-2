package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Represents a data line of a skeleton
 * 
 * @author josepaiva
 */
public class ProblemSkeletonsDataLine implements HasFormData,
	HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue>{
		
	
	public static final ProvidesKey<ProblemSkeletonsDataLine> KEY_PROVIDER = 
			new ProvidesKey<ProblemSkeletonsDataLine>() {
		@Override
		public Object getKey(ProblemSkeletonsDataLine item) {
			return item == null ? null : item.getName();
		}
	};
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	private String id;
	private CustomLabelPath name = new CustomLabelPath();
	
	public ProblemSkeletonsDataLine() {
		// TODO Auto-generated constructor stub
	}
	
	public ProblemSkeletonsDataLine(String name) {
		this.id = name;
		
		linkFieldsToHandlers();
	}

	private void linkFieldsToHandlers() {
		fields.put("Skeleton", name);
		name.addValueChangeHandler(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.getValue().getName();
	}

	/**
	 * @return the skeleton
	 */
	public MooshakValue getSkeleton() {
		return name.getValue();
	}

	/**
	 * Returns test object id
	 * @return
	 */
	public String getId() {
		return id;
	}

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
	 * Populate to form's field values with a map indexed by field names
	 * 
	 * @param data a map with the field values indexed by field names
	 */
	@Override
	public void setFieldValues(Map<String,MooshakValue> data) {
		
		for(String fieldName: data.keySet()) {
			if(fields.containsKey(fieldName) && data.get(fieldName) != null)
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
			data.put(fieldName,fields.get(fieldName).getValue());
		}
		
		return data;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

}
