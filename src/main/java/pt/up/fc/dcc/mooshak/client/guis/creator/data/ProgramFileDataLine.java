package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.view.client.ProvidesKey;

public class ProgramFileDataLine {

	private String id;
	private CustomLabelPath name = new CustomLabelPath();
	private String type;

	public static final ProvidesKey<ProgramFileDataLine> KEY_PROVIDER = new ProvidesKey<ProgramFileDataLine>() {
		@Override
		public Object getKey(ProgramFileDataLine item) {
			return item == null ? null : item.getName();
		}
	};

	public ProgramFileDataLine() {

	}

	public ProgramFileDataLine(String id, String field, String name, byte[] content) {
		this.id = id;
		this.name.setValue(new MooshakValue(field, name, content));
		
		switch (field.toLowerCase()) {
		case "solution":
			type = "Solution";
			break;
		case "skeleton":
			type = "Skeleton";
			break;

		default:
			type = "Program";
			break;
		}

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.getText();
	}

	/**
	 * @return the name
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns object id
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns file value
	 * @return
	 */
	public MooshakValue getValue() {
		return name.getValue();
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setValue(MooshakValue value) {
		name.setValue(value);
		
		switch (name.getValue().getField().toLowerCase()) {
		case "solution":
			type = "Solution";
			break;
		case "skeleton":
			type = "Skeleton";
			break;

		default:
			type = "Program";
			break;
		}
	}
	
	public void setContent(byte[] content) {
		name.setValue(new MooshakValue(name.getValue().getField(), 
				name.getValue().getName(), content));
	}


}
