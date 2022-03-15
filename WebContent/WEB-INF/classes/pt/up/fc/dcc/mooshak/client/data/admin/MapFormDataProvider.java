package pt.up.fc.dcc.mooshak.client.data.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * Implementation of FormDataProvider backed by a Map
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class MapFormDataProvider implements HasFormData, FormDataProvider {
	
	private Map<String,MooshakValue> data = new HashMap<String,MooshakValue>();

	private List<HasFormData> forms = new ArrayList<HasFormData>();
	
	/**
	 * @see addFormDataProvider
	 */
	@Override
	public void addFormDataProvider(HasFormData form) {
		forms.add(form);
	}

	/**
	 * @see removeFormDataProvider
	 */
	@Override
	public void removeFormDataProvider(HasFormData form) {
		forms.remove(form);	
	}

	/**
	 * @see getFieldValues
	 */
	@Override
	public Map<String, MooshakValue> getFieldValues() {
		return this.data;
	}
	
	/**
	 * @see setFieldValues()
	 */
	@Override
	public void setFieldValues(Map<String, MooshakValue> data) {
		this.data = data;
		refresh();
	}

	/**
	 * @see HasFormdata#refresh()
	 */
	@Override
	public void refresh() {
		for(HasFormData form: forms)
			form.setFieldValues(data);
	}
	
}
