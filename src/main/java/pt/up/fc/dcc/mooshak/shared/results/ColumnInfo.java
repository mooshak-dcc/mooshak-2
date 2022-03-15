package pt.up.fc.dcc.mooshak.shared.results;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Information a listing column
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ColumnInfo implements IsSerializable {
	
	private static final int DEFAULT_COLUMN_WIDTH = 20;

	public enum ColumnType implements IsSerializable { LABEL, TIME,
		CLASSIFICATION, FLAG, RANK, TEAM, PROBLEM };
	
	private String name;
	private int size = ColumnInfo.DEFAULT_COLUMN_WIDTH;
	private ColumnType type = ColumnType.LABEL;
	private String color = null;
	
	/**
	 * Create a list of column information with default data
	 * for given column names
	 *  
	 * @param names
	 * @return
	 */
	public static List<ColumnInfo> addColumns(String... names) {
		List<ColumnInfo> infos = new ArrayList<ColumnInfo>();
				
		addColumns(infos,names);
		
		return infos;
	}
	
	/**
	 * Add to  list of column information  default data
	 * for given column names
	 * 
	 * @param infos
	 * @param names
	 */
	public static void addColumns(List<ColumnInfo> infos,String... names) {
		
		addColumns(infos,DEFAULT_COLUMN_WIDTH,names);
	}
	
	/**
	 * Add to  list of column information  default data
	 * for given column names
	 * 
	 * @param infos
	 * @param names
	 */
	public static void addColumns(List<ColumnInfo> infos,int size, 
			String... names) {
		
		for(String name: names)
			infos.add(new ColumnInfo(name,size));
	}
	
	
	
	public ColumnInfo(String name, int size, ColumnType type, String color) {
		super();
		this.name = name;
		this.size = size;
		this.type = type;
		this.color = color;
	}
	
	public ColumnInfo(String name, int size, ColumnType type) {
		super();
		this.name = name;
		this.size = size;
		this.type = type;
	}
	
	public ColumnInfo(String name, int size) {
		super();
		this.name = name;
		this.size = size;
	}
	
	public ColumnInfo(String name) {
		super();
		this.name = name;
	}
	
	public ColumnInfo() {}

	/**
	 * Get name of this column
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of this column
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get size of this column
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Set size of this column
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Get type of this column. Simple columns have type LABEL.
	 * Special columns contain FLAG or ORDER
	 * @return the type
	 */
	public ColumnType getType() {
		return type;
	}

	/**
	 * Set type of this column. Simple columns have type LABEL.
	 * Special columns contain FLAG or ORDER
	 * @param type the type to set
	 */
	public void setType(ColumnType type) {
		this.type = type;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ColumnInfo [name=" + name + ", size=" + size + ", type=" + type
				+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnInfo other = (ColumnInfo) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size != other.size)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
    
}
