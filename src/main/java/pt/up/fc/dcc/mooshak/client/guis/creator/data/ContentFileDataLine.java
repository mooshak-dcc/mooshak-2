package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import com.google.gwt.view.client.ProvidesKey;

import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * Represents a file in a content field in the grid
 * 
 * @author josepaiva
 */
public class ContentFileDataLine {
	
	private CustomLabelPath name = new CustomLabelPath();
	
	public static final ProvidesKey<ContentFileDataLine> KEY_PROVIDER = new ProvidesKey<ContentFileDataLine>() {
		@Override
		public Object getKey(ContentFileDataLine item) {
			return item == null ? null : item.getName();
		}
	};
	
	public ContentFileDataLine() {
		
	}
	
	public ContentFileDataLine(String field, String name, byte[] content) {
		
		this.name.setValue(new MooshakValue(field, name, content));
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.getText();
	}
	
	/**
	 * Returns test object id
	 * @return
	 */
	public String getId() {
		return name.getValuePath();
	}
	
	/**
	 * Returns file value
	 * @return
	 */
	public MooshakValue getValue() {
		return name.getValue();
	}
	
	public void setContent(byte[] content) {
		name.setValue(new MooshakValue(name.getValue().getField(), 
				name.getValue().getName(), content));
	}
}
