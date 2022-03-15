/**

 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;


/**
 * @author User
 *
 */

interface FeebackSummarizer {
	Map<String, List<String>> urlMap= new HashMap<String, List<String>>();
	
	//String  getMessage(Lang lang, DifferenceHandler difference);
}


public  class  FeedbackMessage implements Comparable<FeedbackMessage>,FeebackSummarizer{

	private String message;
	private Integer property;
	private Integer weight;
	private Integer degreeInOut;
	private Boolean isNode;
	private JSONObject json;
	private String id;
	
	public FeedbackMessage() {
		
	}
	
	
	public FeedbackMessage(String message, int property, int weight, int degree, Boolean isNode) {
		super();
		this.message = message;
		this.property = property;
		this.weight = 0;
		this.isNode =isNode;
		this.json= new JSONObject();
		this.id=message;
		this.degreeInOut=degree;
	}
	
	public FeedbackMessage(String message, int property, int weight,int degree,JSONObject json,Boolean isNode ) {
		super();
		this.message = message;
		this.property = property;
		this.weight = weight;
		this.isNode =isNode;
		this.json=json;
		this.id=message;
		this.degreeInOut=degree;
	}
	
	public FeedbackMessage(String message, int property, int weight,JSONObject json,Boolean isNode ) {
		super();
		this.message = message;
		this.property = property;
		this.weight = weight;
		this.isNode =isNode;
		this.json=json;
		this.id=message;
		this.degreeInOut=0;
	}
	
	public FeedbackMessage(String message, int property, int weight,Boolean isNode ) {
		super();
		this.message = message;
		this.property = property;
		this.weight = weight;
		this.isNode =isNode;
		this.id=message;
		this.degreeInOut=0;
		this.json= new JSONObject();
	}



	public int compareTo(FeedbackMessage obj) {
////		if (this == obj) return 0;
////        if (obj == null) return -1;
//		
		if (getClass() != obj.getClass())
			return 1;
		FeedbackMessage other = (FeedbackMessage) obj;
		
		if (property == other.property) {
			if (weight == other.weight) {
			if (degreeInOut < other.getDegreeInOut())
			return 1;
			if (degreeInOut == other.getDegreeInOut())
			return 0;
			return -1;
			}
			return weight - other.weight;
			}
			return property - other.property;


	}
	
	

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the property
	 */
	public int getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(int property) {
		this.property = property;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the degreeInOut
	 */
	public int getDegreeInOut() {
		return degreeInOut;
	}

	/**
	 * @param degreeInOut the degreeInOut to set
	 */
	public void setDegreeInOut(int degreeInOut) {
		this.degreeInOut = degreeInOut;
	}

	/**
	 * @return the isNode
	 */
	public Boolean getIsNode() {
		return isNode;
	}

	/**
	 * @param isNode the isNode to set
	 */
	public void setIsNode(Boolean isNode) {
		this.isNode = isNode;
	}

	/**
	 * @return the json
	 */
	public JSONObject getJson() {
		return json;
	}

	/**
	 * @param json the json to set
	 */
	public void setJson(JSONObject json) {
		this.json = json;
	}



	@Override
	public String toString() {
		return  message;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + property;
		result = prime * result + weight;
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
		FeedbackMessage other = (FeedbackMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (property != other.property)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}



//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}



//	@Override
//	public boolean equals(Object obj) {
//		
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		FeedbackMessage other = (FeedbackMessage) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}

 
	
	
	
	

//	@Override
//	public String toString() {
//		return "FeedbackMessage [message=" + message + ", property=" + property + ", weight=" + weight
//				+ ", degreeInOut=" + degreeInOut + ", isNode=" + isNode + ", json=" + json + "]";
//	}
	
	
	
	
	
	
}
