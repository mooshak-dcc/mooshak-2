package pt.up.fc.dcc.mooshak.client.utils;

public class ProgramError {
	
	public enum Type {
		ERROR, WARNING
	}
	
	private String description;
	private int row;
	private int column;
	private Type type;
	
	public ProgramError() {
	}

	public ProgramError(String description, int row, int column) {
		super();
		this.description = description;
		this.row = row;
		this.column = column;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
}
