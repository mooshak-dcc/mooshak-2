package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Data provider to back the table of images
 * 
 * @author josepaiva
 */
public class ContentDataProvider extends ListDataProvider<ContentFileDataLine>
	implements HasFormData, HasValueChangeHandlers<MooshakValue>, 
	ValueChangeHandler<MooshakValue> {

	public static final int LINES = 2;

	private static final Map<String, ContentDataProvider> providers = new HashMap<String, ContentDataProvider>();
	
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	/**
	 * Get a IODataProvider for a given problem
	 * 
	 * @param problem
	 * @return
	 */
	public static ContentDataProvider getDataProvider(String problem) {
		ContentDataProvider provider = null;
		if (providers.containsKey(problem))
			provider = providers.get(problem);
		else {
			provider = new ContentDataProvider();
			providers.put(problem, provider);
		}
		return provider;
	}

	public ContentDataProvider() {

		List<ContentFileDataLine> ioDataList = getList();
		for (int count = 0; count < LINES; count++) {
			ioDataList.add(new ContentFileDataLine());
		}
		
	}
	
	/**
	 * Adds a line replacing an existing empty or not
	 * @param newLine
	 */
	public void addFile(ContentFileDataLine newLine) {
		
		ContentFileDataLine line;
		
		if((line = findLineWithName(newLine.getName())) != null) {
			int pos = getList().indexOf(line);
			getList().remove(pos);
			getList().add(pos, newLine);
		}
		else if((line = getEmptyLine()) == null)
			getList().add(newLine);
		else {
			int pos = getList().indexOf(line);
			getList().remove(pos);
			getList().add(pos, newLine);
		}
		
	}

	private ContentFileDataLine findLineWithName(String name) {
		for (ContentFileDataLine line : getList()) {
			if(name.equals(line.getName()))
				return line;
		}
		return null;
	}

	/**
	 * Returns an empty line
	 * @return
	 */
	private ContentFileDataLine getEmptyLine() {
		for (ContentFileDataLine line : getList()) {
			if("".equals(line.getName()) || line.getName() == null)
				return line;
		}
		
		return null;
	}

	/**
	 * Deletes the row with given id
	 * @param rowId
	 */
	public void deleteRow(String rowId) {
		for (ContentFileDataLine ioPair : getList()) {
			if(rowId.equals(ioPair.getName())) {
				getList().remove(ioPair);
				break;
			}
		}
		
		fillWithEmptyLines();
	}

	private void fillWithEmptyLines() {
		for (int i = getList().size(); i < LINES; i++)
			getList().add(new ContentFileDataLine());
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		for(ValueChangeHandler<MooshakValue> handler: valueChangeHandlers)
			handler.onValueChange(event);
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
	public void setFieldValues(Map<String, MooshakValue> data) {
		getList().clear();
		
		for (String fieldName : data.keySet()) {
			if(fieldName.equals("Image")
					|| fieldName.equals("Solution")) {
				for (String file : data.get(fieldName).getFileNames()) {
					ContentFileDataLine line = new ContentFileDataLine(
							fieldName, file, 
							data.get(fieldName).getContent(file));
					addFile(line);
				}
			}
		}
		
		fillWithEmptyLines();
	}

	@Override
	public Map<String, MooshakValue> getFieldValues() {
		return null;
	}
	
	

}
