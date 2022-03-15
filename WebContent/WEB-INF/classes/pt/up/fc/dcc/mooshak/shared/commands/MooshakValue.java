package pt.up.fc.dcc.mooshak.shared.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A field and its value, either a string or a file content (including its name)
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class MooshakValue implements IsSerializable {
	
	String field;
	Value value;
	
	enum MooshakValueType { SIMPLE, FILE, FILES }
	
	interface Value { 
		MooshakValueType getType();
	}; 
	
	/**
	 * A simple value is just a string,
	 * used for any value that may be serialized to a short string
	 *
	 * @author José Paulo Leal <zp@dcc.fc.up.pt>
	 */
	static class SimpleValue implements Value, IsSerializable {
		String simple;
		
		public SimpleValue() {
			super();
		}
		
		public SimpleValue(String simple) {
			super();
			this.simple = simple;
		}

		public MooshakValueType getType() {
			return MooshakValueType.SIMPLE;
		}
		
		/**
		 * @return the simple
		 */
		public String getSimple() {
			return simple;
		}

		/**
		 * @param simple the simple to set
		 */
		public void setSimple(String simple) {
			this.simple = simple;
		}
		
	}
	
	/**
	 * File values represent data that is kept on files on server side.
	 * Files are represented by a name (from which a type may be derived)
	 * and an array of bytes.
	 *
	 * @author José Paulo Leal <zp@dcc.fc.up.pt>
	 */
	public static class FileValue implements Value, IsSerializable {
		String name;
		byte[] content;
		
		public FileValue() {
			super();
		}
		
		public FileValue(String name, byte[] content) {
			super();
			this.name = name;
			this.content = content;
		}

		public MooshakValueType getType() {
			return MooshakValueType.FILE;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the content
		 */
		public byte[] getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(byte[] content) {
			this.content = content;
		}
	}
	
	static class MultipleFileValue implements Value, IsSerializable {
		Map<String,FileValue> files = new HashMap<String,FileValue>();
		
		public MultipleFileValue() {
			super();
		}
		
		public void addFileValue(FileValue value) {
			files.put(value.getName(),value);
		}
		
		public FileValue getFileValue(String name) {
			return files.get(name);
		}
		
		public void addFileValue(String name,byte[] content) {
			files.put(name,new FileValue(name,content));
		}
		
		public boolean contains(String name) {
			return files.containsKey(name);
		}

		public void removeFileValue(String name) {
			files.remove(name);
		}
		
		public Set<String> getFileNames() {
			return files.keySet();
		}
		
		public byte[] getContent(String name) {
			return files.get(name).getContent();
		}
		
		@Override
		public MooshakValueType getType() {
			return MooshakValueType.FILES;
		}
		
	}
	
	
	/**
	 * Default empty constructor for serialization purposes
	 */
	public MooshakValue() {
		super();
	}
	
	/**
	 * Create a MooshakValue from a simple string value
	 * @param field
	 * @param value
	 */
	public MooshakValue(String field, String value) {
		super();
		this.field = field;
		this.value = new SimpleValue(value);
	}
	
	/**
	 * Create a MooshakValue from a file name and content
	 * @param field
	 * @param name
	 * @param content
	 */
	public MooshakValue(String field,String name, byte[] content) {
		super();
		this.field = field;
		this.value = new FileValue(name,content);
	}
	
	/**
	 * Create a MooshakValue from multiple files name and their contents
	 * @param field
	 */
	public MooshakValue(String field) {
		super();
		this.field = field;
		this.value = new MultipleFileValue();
	}

	/**
	 * Get field name
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	/**
	 * Set field name
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
	
	/**
	 * Get field's value; may be either {@code SimpleValue} or {@code FileValue}
	 * @return the value
	 */
	public Value getValue() {
		return value;
	}
	/**
	 * Set this field's value; may be either {@code SimpleValue} or {@code FileValue}
	 * @param value the value to set
	 */
	public void setValue(Value value) {
		this.value = value;
	}
	
	/**
	 * Return {@true} if this is a simple field; {@false} otherwise
	 * @return
	 */
	public boolean isSimpleValue() {
		return value.getType() == MooshakValueType.SIMPLE;
	}
	
	/**
	 * Get value (as a String) if field refers to simple field; null otherwise
	 * @return
	 */	
	public String getSimple() {
		if(value.getType() == MooshakValueType.SIMPLE )
			return ((SimpleValue) value).getSimple();
		else 
			return  null;
	}
	
	/**
	 * Get file name (as a String) if field refers to a file; null otherwise
	 * @return
	 */	
	public String getName() {
		if(value.getType() == MooshakValueType.FILE)
			return ((FileValue) value).getName(); 
		else
			return  null;
	}

	/**
	 * Get file content (as a byte[]) if field refers to a file; null otherwise
	 * @return
	 */
	public byte[] getContent() {
		
		if(value.getType() == MooshakValueType.FILE)
			return ((FileValue) value).getContent();
		else
			return  null; 
	}
	
	public Set<String> getFileNames() {
		if(value.getType() == MooshakValueType.FILES)
			return ((MultipleFileValue) value).getFileNames();
		else
			return null;
	}
	
	/**
	 * Get content of given file name in a multiple file 
	 * @param name of file
	 * @return
	 */
	public byte[] getContent(String name) {
		if(value.getType() == MooshakValueType.FILES)
			return ((MultipleFileValue) value).getContent(name);
		else
			return null;
	}

	/**
	 * Add a MooshakValue containing a file to this value
	 * @param file
	 */
	public void addFileValue(MooshakValue file) {
		if(value.getType() == MooshakValueType.FILES 
				&& file.value.getType() == MooshakValueType.FILE)
			((MultipleFileValue) value).addFileValue((FileValue) file.value);
	}

	/**
	 * Remove file in a MultipleFileValue
	 * @param file
	 */
	public void removeFile(String file) {
		if(value.getType() == MooshakValueType.FILES)
			((MultipleFileValue) value).removeFileValue(file);
	}
	
	/**
	 * Get a MooshakValue containing a file with given name
	 * @param name
	 * @return
	 */
	public FileValue getFileValue(String name) {
		
		if(value.getType() == MooshakValueType.FILES)
			return ((MultipleFileValue) value).getFileValue(name);
		else 
			return null;
		
	}
}
