package pt.up.fc.dcc.mooshak.shared.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Data transfer object representing a type of persistent objects
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class MooshakType implements IsSerializable {
	
	String type;
	String contentType = null;

	List<MooshakField> fields = new ArrayList<MooshakField>();	
	List<MooshakMethod> methods = new ArrayList<MooshakMethod>();	
	
	public MooshakType() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Get name of this type 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get name of this type
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the name of the type of objects contained in the type,
	 * if this is a container type; {@code null} otherwise.
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Set the name of the type of objects contained in the type,
	 * if this is a container type; defaults to {@code null}.
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * Get list of fields  of this type 
	 * @return the list fields of this type
	 */
	public List<MooshakField> getFields() {
		return fields;
	}

	/**
	 * Set list of fields of this type
	 * @param fields the list of fields to set
	 */
	public void setFields(List<MooshakField> fields) {
		this.fields = fields;
	}

	/**
	 * Get methods defined on this persistent object
	 * @return the methods
	 */
	public List<MooshakMethod> getMethods() {
		return methods;
	}

	/**
	 * Set methods on this type of persistent object
	 * @param methods the methods to set
	 */
	public void setMethods(List<MooshakMethod> operations) {
		this.methods = operations;
	}

	public static class MooshakTypeComponent implements IsSerializable {
		String name = "";
		String tip = "";
		String help = "";
		int maxLength = -1;
		/**
		 * Get name of this component, field or method 
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * Set name of this component, field or method
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * Get a tip on the use of this component, field or method
		 * @return the tip
		 */
		public String getTip() {
			return tip;
		}
		/**
		 * Set a tip on the use of this component, field or method
		 * @param tip the tip to set
		 */
		public void setTip(String tip) {
			this.tip = tip;
		}
		/**
		 * Get a help text on the use of this component, field or method
		 * @return the help
		 */
		public String getHelp() {
			return help;
		}
		/**
		 * Set a tip on the use of this component, field or method
		 * @param help the help to set
		 */
		public void setHelp(String help) {
			this.help = help;
		}
		/**
		 * @return the maxLength
		 */
		public int getMaxLength() {
			return maxLength;
		}
		/**
		 * @param maxLength the maxLength to set
		 */
		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((help == null) ? 0 : help.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((tip == null) ? 0 : tip.hashCode());
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
			MooshakTypeComponent other = (MooshakTypeComponent) obj;
			if (help == null) {
				if (other.help != null)
					return false;
			} else if (!help.equals(other.help))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (tip == null) {
				if (other.tip != null)
					return false;
			} else if (!tip.equals(other.tip))
				return false;
			return true;
		}

	}
	
	/**
	 * An attribute from a persistent object
	 */
	public static class MooshakField extends MooshakTypeComponent {
		AttributeType type;
		List<String> alternatives = null;
		String complement = null;
		String docSpec = null;
		boolean quizEditor = false;
		boolean isBase64 = false;
		
		public MooshakField() {}
		
		public MooshakField(String name, AttributeType type) {
			super();
			this.name = name;
			this.type = type;
		}
		
		/**
		 * Get type of this field field
		 * @return the type
		 */
		public AttributeType getType() {
			return type;
		}
		/**
		 * Set type of the field
		 * @param type the type to set
		 */
		public void setType(AttributeType type) {
			this.type = type;
		}
		/**
		 * Get list of alternatives to this value
		 * @return the alternatives
		 */
		public List<String> getAlternatives() {
			return alternatives;
		}
		/**
		 * Set list of alternatives to this value
		 * @param alternatives the alternatives to set
		 */
		public void setAlternatives(List<String> alternatives) {
			this.alternatives = alternatives;
		}
		/**
		 * Get the complement of PATH field (e.g. "../../problems")
		 * @return the complement
		 */
		public String getComplement() {
			return complement;
		}

		/**
		 * Set the complement of PATH field (e.g. "../../problems")
		 * @param complement the complement to set
		 */
		public void setComplement(String complement) {
			this.complement = complement;
		}
		
		/**
		 * @return the docSpec
		 */
		public String getDocSpec() {
			return docSpec;
		}

		/**
		 * @param docSpec the docSpec to set
		 */
		public void setDocSpec(String docSpec) {
			this.docSpec = docSpec;
		}

		/**
		 * @return the quizEditor
		 */
		public boolean isQuizEditor() {
			return quizEditor;
		}

		/**
		 * @param quizEditor the quizEditor to set
		 */
		public void setQuizEditor(boolean quizEditor) {
			this.quizEditor = quizEditor;
		}

		/**
		 * Is the value encoded in base64?
		 * @return the isBase64
		 */
		public boolean isBase64() {
			return isBase64;
		}
		/**
		 * Define if this value is set in base 64
		 * @param isBase64 the isBase64 to set
		 */
		public void setBase64(boolean isBase64) {
			this.isBase64 = isBase64;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((alternatives == null) ? 0 : alternatives.hashCode());
			result = prime * result + (isBase64 ? 1231 : 1237);
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
			MooshakField other = (MooshakField) obj;
			if (alternatives == null) {
				if (other.alternatives != null)
					return false;
			} else if (!alternatives.equals(other.alternatives))
				return false;
			if (isBase64 != other.isBase64)
				return false;
			if (type != other.type)
				return false;
			return true;
		}
		
	}
	
	/**
	 * An operation on a persistent object
	 */
	public static class MooshakMethod extends MooshakTypeComponent {
		String name;
		String category;
		boolean inputable;
		boolean showable;
		boolean updateEvents;
		
		public MooshakMethod() {}
		
		public MooshakMethod(String name, String category, 
				boolean inputable, boolean showable,
				boolean updateEvents) {
			this.name = name;
			this.category = category;
			this.inputable = inputable;
			this.showable = showable;
			this.updateEvents = updateEvents;
		}
		
		/**
		 * Get the name of this operation
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * Set the name of this operation
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * get the category (menu name) of this operation
		 * @return the category
		 */
		public String getCategory() {
			return category;
		}
		/**
		 * Set a category (menu name) to this operation 
		 * @param category the category to set
		 */
		public void setCategory(String category) {
			this.category = category;
		}
		/**
		 * Can this operation receive input from a dialog box?
		 * @return the inputable
		 */
		public boolean isInputable() {
			return inputable;
		}
		/**
		 * Control in this operation can receive input from a dialog box
		 * @param inputable the inputable to set
		 */
		public void setInputable(boolean inputable) {
			this.inputable = inputable;
		}
		
		/**
		 * Can this operation is be shown on menu?
		 * @return the showable
		 */
		public boolean isShowable() {
			return showable;
		}

		/**
		 *  Control if this operation is to be shown on menu
		 * @param showable the showable to set
		 */
		public void setShowable(boolean showable) {
			this.showable = showable;
		}
		
		/**
		 * Is to update events immediately after execution?
		 * @return the updateEvents
		 */
		public boolean isUpdateEvents() {
			return updateEvents;
		}

		/**
		 *  Control if this operation is to update events immediately
		 * @param updateEvents the updateEvents to set
		 */
		public void setUpdateEvents(boolean updateEvents) {
			this.updateEvents = updateEvents;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result
					+ ((category == null) ? 0 : category.hashCode());
			result = prime * result + (inputable ? 1231 : 1237);
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			MooshakMethod other = (MooshakMethod) obj;
			if (category == null) {
				if (other.category != null)
					return false;
			} else if (!category.equals(other.category))
				return false;
			if (inputable != other.inputable)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MooshakMethod [name=" + name + ", category=" + category
					+ ", inputable=" + inputable + "]";
		}
		
		
	}
	
}
