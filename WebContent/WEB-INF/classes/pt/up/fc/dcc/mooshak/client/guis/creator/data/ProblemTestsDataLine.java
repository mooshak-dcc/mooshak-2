package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ProvidesKey;

import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.form.admin.HtmlFreeLabel;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * Represents a test in the grid
 * 
 * @author josepaiva
 */
public class ProblemTestsDataLine implements HasFormData,
		HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue>{
	
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	private CustomLabelPath name = new CustomLabelPath();
	private CustomLabelPath input = new CustomLabelPath();
	private CustomLabelPath output = new CustomLabelPath();
	private HtmlFreeLabel timeout = new HtmlFreeLabel();
	private HtmlFreeLabel result = new HtmlFreeLabel();
	private String solutionErrors = "";
	
	public static final ProvidesKey<ProblemTestsDataLine> KEY_PROVIDER = new ProvidesKey<ProblemTestsDataLine>() {
		@Override
		public Object getKey(ProblemTestsDataLine item) {
			return item == null ? null : item.getName();
		}
	};
	
	public ProblemTestsDataLine() {
		
	}
	
	public ProblemTestsDataLine(String name) {
		this.name.setText(name);
		
		linkFieldsToHandlers();
	}

	private void linkFieldsToHandlers() {
		fields.put("output", output);
		output.addValueChangeHandler(this);

		fields.put("input", input);
		input.addValueChangeHandler(this);

		fields.put("Timeout", timeout);
		timeout.addValueChangeHandler(this);

		fields.put("Result", result);
		result.addValueChangeHandler(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.getText();
	}

	/**
	 * @return the input
	 */
	public CustomLabelPath getInput() {
		return input;
	}

	/**
	 * @return the output
	 */
	public CustomLabelPath getOutput() {
		return output;
	}

	/**
	 * @return the result
	 */
	public HtmlFreeLabel getResult() {
		return result;
	}

	/**
	 * @return the timeout
	 */
	public HtmlFreeLabel getTimeout() {
		return timeout;
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
	 * Populate to form's field values with a map indexed by field names
	 * 
	 * @param data a map with the field values indexed by field names
	 */
	@Override
	public void setFieldValues(Map<String,MooshakValue> data) {
		
		for(String fieldName: data.keySet()) {
			if(fields.containsKey(fieldName) && data.get(fieldName) != null)
				fields.get(fieldName).setValue(data.get(fieldName));
			else if(fieldName.equalsIgnoreCase("SolutionErrors"))
				solutionErrors = data.get(fieldName).getSimple();
				
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

	/**
	 * Returns test object id
	 * @return
	 */
	public String getId() {
		return name.getValuePath();
	}

	/**
	 * Returns solutionErrors
	 * @return
	 */
	public String getSolutionErrors() {
		return solutionErrors;
	}
}
