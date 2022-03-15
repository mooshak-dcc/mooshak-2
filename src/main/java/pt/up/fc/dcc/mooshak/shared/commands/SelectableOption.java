package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Selectable option as a pair of id and label.
 * List of selectable options are typically used for populating menus.
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class SelectableOption implements IsSerializable {
	String id;
	String label;
	
	public SelectableOption() {};
	
	public SelectableOption(String id, String value) {
		super();
		this.id = id;
		this.label = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectableOption other = (SelectableOption) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
}
