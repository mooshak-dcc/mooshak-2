package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class CompositePropertyValue extends PropertyValue {
	Map<String, String> value;
	private Map<String, String> info;
	
	public CompositePropertyValue() {
		this.value = new HashMap<>();
		this.setInfo(new HashMap<>());
		
		
	}

	public CompositePropertyValue(String key, String value) {
		this.value = new HashMap<>();
		this.value.put(key, value);
		this.setInfo(new HashMap<>());
		
	}
	

	/**
	 * @return the value
	 */
	public Map<String, String> getValue() {
		return value;
	}

	public PropertyValue deepCopy() {
		CompositePropertyValue values = new CompositePropertyValue();
		Iterator<String> iterator = this.value.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			values.add(key, value.get(key));
		}

		return values;
	}

	public void add(String key, String value) {
		this.value.put(key, value);
	}

	public boolean isSimple() {
		return false;
	}

	public String toString() {
		Iterator<String> it = value.keySet().iterator();
		String key, returnString = "";
		while (it.hasNext()) {
			key = it.next();
			returnString += "\n\t" + key + " : " + value.get(key);
		}
		return returnString;
	}
	


	/**
	 * Compare property. Return value goes from 0 to 100.
	 * @param attemptKey 
	 * 
	 * @param property
	 * @return
	 */
	public GradeWithDifferences compareProperty(GObject object,
			PropertyName name, PropertyValue property,Configs configs) {
//		System.out.println(this +" \n"+property+ "\n\n ");
		
		int grade = 0;
		Set<GraphDifference> differences = new HashSet<>();
		List<String> visitedSubProperties = new ArrayList<>();
		ConfigName configName = object.isNode() ? 
				ConfigName.NODE_PROPERTY_WEIGHT : 
					ConfigName.EDGE_PROPERTY_WEIGHT;
		for (String key : this.value.keySet()) {
			String correctValue = this.value.get(key);

			if (((CompositePropertyValue) property).value.containsKey(key) ) {
				visitedSubProperties.add(key);
				String attemptValue = ((CompositePropertyValue) property).value
						.get(key);
				
//				System.out.println("Values "+correctValue +"  "+attemptValue);
				if (correctValue.equals(attemptValue) ){
					grade += configs.get(configName);
				}
				else{
					differences.add(new DifferentSubPropertyValue(object, name,
							this, key, correctValue, attemptValue));
				}
			}

			else {
				differences.add(new SubPropertyInsertion(object, name, this,
						key, correctValue));
			}
		}

		for (String key : ((CompositePropertyValue) property).value.keySet()){
			if (!visitedSubProperties.contains(key)) {
				
				String value = ((CompositePropertyValue) property).value
						.get(key);
				differences.add(new SubPropertyDeletion(object, name, this,
						key, value));
			}
		}
		
//		System.out.println("Grade "+grade);
		if (grade < 0)
			grade = 0;
		return new GradeWithDifferences(differences, grade);
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
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		CompositePropertyValue other = (CompositePropertyValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	

	public Map<String, String> getInfo() {
		return info;
	}

	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	public void addInfo(String key, String value) {
		this.info.put(key, value);
	}
	
	
}