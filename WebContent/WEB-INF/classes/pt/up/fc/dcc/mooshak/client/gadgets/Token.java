package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

/**
 * A token containing information on ids and names Token are stringified to be
 * saved in GWT history
 */
public class Token {

	private static final String SEP = ":";

	String id = "";
	String name = "";
	ResourceType type;
	String link = "";
	String language = "";

	public Token() {
	}

	public Token(String token) {
		if (token != null)
			parse(token);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param name
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the type
	 */
	public ResourceType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ResourceType type) {
		this.type = type;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param name
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		if (type != null && type.equals(ResourceType.PROBLEM)) {
			if (id != null && id.indexOf("/") != -1)
				return id.substring(id.lastIndexOf("/") + 1);
			else
				return id;
		}

		return null;
	}

	/**
	 * Stringification compatible with parse (used in constructor)
	 */
	public String toString() {
		String typeValue = "";

		if (type != null)
			typeValue = type.toString();

		return Base64Coder.encodeString(id) + SEP + name + SEP + typeValue;
	}

	/**
	 * Parse a token stringification
	 * 
	 * @param token
	 */
	protected void parse(String token) {
		String[] args = token.split(SEP);

		if (args.length > 0)
			id = Base64Coder.decodeString(args[0]);
		if (args.length > 1)
			name = args[1];
		if (args.length > 2 && args[2].length() > 0)
			type = Enum.valueOf(ResourceType.class, args[2]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		Token other = (Token) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
