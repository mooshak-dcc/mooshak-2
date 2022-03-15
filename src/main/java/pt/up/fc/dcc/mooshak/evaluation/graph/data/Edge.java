package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.*;

/**
 * Edge is a class that represents any connection existing in a diagram. It adds
 * to GObject the source and target node.
 * 
 * @author Ruben
 *
 */
public class Edge extends GObject {

	private Node source, target;

	public Edge() {
		this.id = null;
		this.type = null;
		this.properties = new HashMap<>();
		this.indistincts = 1;
		this.name = "";
	}

	public Edge(int i) {
		this.id = "###blank_edge_" + i;
		this.type = null;
		this.properties = new HashMap<>();
		this.indistincts = 1;
		this.name = "";
	}

	public Edge(GObject edge) {
		this.id = edge.id;
		this.type = edge.type;
		this.properties = new HashMap<>(edge.properties);
		this.source = null;
		this.target = null;
		this.indistincts = edge.indistincts;
		this.name = edge.name;
	}

	public Edge(GObject edge, Node source, Node target) {
		this.id = edge.id;
		this.type = edge.type;
		this.properties = edge.properties;
		this.source = source;
		this.target = target;
		this.indistincts = edge.indistincts;
		this.name = edge.name;
	}

	public Edge(String id, String type, Map<PropertyName, PropertyValue> properties, Node source, Node target) {
		this.id = id;
		this.type = type;
		this.properties = properties;
		this.source = source;
		this.target = target;
		this.indistincts = 1;
		this.name = "";
	}

	public Edge(String id, String type, Node source, Node target) {
		this.id = id;
		this.type = type;
		this.properties = new HashMap<>();
		this.source = source;
		this.target = target;
		this.indistincts = 1;
		this.name = "";
	}
	
	public Edge(Edge edge) {
		this.id = edge.id;
		this.type = edge.type;
		this.properties = edge.getProperties();
		this.source = edge.source;
		this.target = edge.target;
		this.indistincts = edge.getIndistincts();
		this.name = edge.name;
	}

	/**
	 * Creates a copy of this edge
	 * 
	 * @param nodes
	 * @return
	 */
	public Edge deepCopy(List<Node> nodes) {
		Edge edge = new Edge();
		edge.id = this.id;
		edge.type = this.type;
		edge.name = this.name;
		int count = 0;
		for (Node node : nodes) {
			if (node.getId().equals(this.source.getId())) {
				edge.source = node;
				count++;
			}
			if (node.getId().equals(this.target.getId())) {
				edge.target = node;
				count++;
			}

			if (count == 2)
				break;
		}

		edge.value = this.value;
		edge.properties = new HashMap<>();
		propertiesDeepCopy(edge, this.properties);
		return edge;
	}

	/**
	 * @return the source
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(Node source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Node target) {
		this.target = target;
	}

	public boolean isNode() {
		return false;
	}

	/**
	 * Convert the edge's information to string
	 */
	public String toString() {
		String phrase = "Edge type : " + type + "\n" + "Start Node : " + source.id + "\n" + "Destination Node : "
				+ target.id + "\n";

		phrase += "Properties:\n";
		Iterator<PropertyName> mapIterator = properties.keySet().iterator();
		PropertyName key;
		while (mapIterator.hasNext()) {
			
			key = mapIterator.next();
			if(properties.get(key)==null){
				continue;
			}
			phrase += key.toString() + properties.get(key).toString() + "\n";
		}
		return phrase;
	}

	/**
	 * Compare type, start and destination nodes and properties between two
	 * edges
	 * 
	 * @param attempt
	 * @return
	 * @throws Exception
	 */
	public GradeWithDifferences compareEdge(Edge attempt, Configs configs, Map<Node, Match> map) {

		GradeWithDifferences grade = new GradeWithDifferences();

		if (this.type.equals(attempt.type))
			grade.setGrade(configs.get(ConfigName.EDGE_TYPE_WEIGHT));
		else
			grade.addDifference(new DifferentType(this, this.type, attempt.type));

		GradeWithDifferences propertiesGrade = compareProperties(this.properties, attempt.getProperties(), configs,map);
		
		grade.update(propertiesGrade);

		int auxGrade = grade.getGrade();
		if (this.indistincts == attempt.indistincts) {
			auxGrade *= this.indistincts;
		}

		else if (this.indistincts < attempt.indistincts) {
			auxGrade = auxGrade * this.indistincts
					- (attempt.indistincts - this.indistincts) * configs.get(ConfigName.NODE_TYPE_WEIGHT);
			grade.addDifference(new EdgeDeletion(attempt, attempt.indistincts - this.indistincts));
		}

		else {
			auxGrade = auxGrade * this.indistincts;
			grade.addDifference(new EdgeInsertion(this, this.indistincts - attempt.indistincts));
		}
		
		
		grade.setGrade(auxGrade);

		if (grade.getGrade() < 0)
			grade.setGrade(0);
		return grade;

	}

	/**
	 * Creates a reverse edge of this one. It changes the source with the target
	 * and their respective multiplicity
	 * 
	 * @return
	 */
	public Edge reverse() {
		Edge edge = new Edge();
		edge.setType(this.type);
		edge.setId(this.id + "'");
		edge.setSource(this.target);
		edge.setTarget(this.source);
		propertiesDeepCopy(edge, this.properties);

		SimplePropertyValue multA, multB;
		SimplePropertyName nameA = new SimplePropertyName("cardinalitySource");
		SimplePropertyName nameB = new SimplePropertyName("cardinalityTarget");
		Map<PropertyName, PropertyValue> properties = edge.getProperties();
		if (properties.containsKey(nameA)) {
			multB = (SimplePropertyValue) properties.get(nameA);
			multA = (SimplePropertyValue) properties.get(nameB);
			properties.put(nameA, multA);
			properties.put(nameB, multB);
		}
		return edge;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	
	public boolean equals2(Object obj, Map<Node, Match> map) {
		
		Node s = map.get(getSource()).getAttempt();
		Node t = map.get(getTarget()).getAttempt();
		
		if (this == obj)
			return true;
		
//		if (!super.equals(obj))
//			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		
		if (s == null) {
			if (other.source != null)
				return false;
		} /*else if (!source.equals(other.source))
			return false;*/
		if (t == null) {
			if (other.target != null)
				return false;
		}/* else if (!target.equals(other.target))
			return false;*/
		
		if (t.equals(other.source) && s.equals(other.target)){
		
			return true;
		}
		else if (t.equals(other.target) && s.equals(other.source)){
			return true;
		
		}
		return false;
		
	}
	
	
	/**
	 * Compare a set of solution properties with another set of properties
	 * 
	 * @param solutionProperties
	 * @param attemptProperties
	 * @return
	 */
	public GradeWithDifferences compareProperties(Map<PropertyName, PropertyValue> solutionProperties,
			Map<PropertyName, PropertyValue> attemptProperties, Configs configs, Map<Node, Match> map) {

		GradeWithDifferences grade = new GradeWithDifferences();
		List<PropertyName> compositeMapped = new ArrayList<>();
		List<PropertyName> attemptsVisited = new ArrayList<>();
		
//		Map<PropertyName, PropertyValue> solutionProperties2 = new HashMap<PropertyName, PropertyValue>(); 
		
//		for (Entry<PropertyName, PropertyValue> entry : solutionProperties.entrySet()) {
//			solutionProperties2.put(entry.getKey(), entry.getValue());
//		}
//		
//		Map<PropertyName, PropertyValue> attemptProperties2= new HashMap<PropertyName, PropertyValue>();
//		
//		for (Entry<PropertyName, PropertyValue> entry : attemptProperties.entrySet()) {
//			attemptProperties2.put(entry.getKey(), entry.getValue());
//		}
		
		
		
		
		
		for (PropertyName key : solutionProperties.keySet()) {
			PropertyValue value = solutionProperties.get(key);
			
			
			if (key.isSimple()) {
				if (attemptProperties.containsKey(key)) {
					grade.update(value.compareProperty(this, key, attemptProperties.get(key), configs));
					attemptsVisited.add(key);
				} else
					grade.getDifferences().add(new PropertyInsertion(this, key, value));
			}

			else {
				GradeWithDifferences bestGrade = new GradeWithDifferences();
				PropertyName name = null;
				
				for (PropertyName attemptKey : attemptProperties.keySet()) {
					
//					if(map==null && attemptsVisited.contains(attemptKey)){
//						continue;
//					}
					
					if (map!=null &&((containsPropertyName(attemptsVisited , attemptKey,map)) || attemptKey.isSimple())){
						continue;
					}
					
					
					if (key.equals(attemptKey,map)) {
						CompositePropertyValue solutionValue = (CompositePropertyValue) solutionProperties.get(key);
						CompositePropertyValue attemptValue = (CompositePropertyValue) attemptProperties
								.get(attemptKey);
						if(solutionValue==null)
							continue;
						GradeWithDifferences currentGrade = solutionValue.compareProperty(this, key, attemptValue,
								configs);
						
				
						if(currentGrade.getGrade() > bestGrade.getGrade()){
							name = attemptKey;
		//						attemptsVisited.add(name);
							bestGrade = currentGrade;
							if(currentGrade.getDifferences().size()>0)
								grade.getDifferences().addAll(currentGrade.getDifferences());
//							solutionProperties2.remove(key);
//							attemptProperties2.remove(attemptKey);
							
					}

					}
				}

				if (name != null) {
					grade.update(bestGrade);
					attemptsVisited.add(name);
				} else{
					grade.getDifferences().add(new PropertyInsertion(this, key, value));
				}
				compositeMapped.add(key);
			}

		}
		ConfigName configName = isNode() ? NODE_PROPERTY_PENALTY : EDGE_PROPERTY_PENALTY;
		for (PropertyName key : attemptProperties.keySet()) {
			if (!attemptsVisited.contains(key)) { //if (!attemptsVisited.contains(key)
				int penalty = configs.get(configName);
				if (!key.isSimple())
					penalty = configs.get(configName) * ((CompositePropertyValue) attemptProperties.get(key)).value.size();
				grade.applyPenalty(penalty);
				grade.getDifferences().add(new PropertyDeletion(this, key, attemptProperties.get(key)));
			}
		}

		return grade;
	}
	
	
	private boolean containsPropertyName(List<PropertyName> list, PropertyName propertyName, Map<Node, Match> map){
		for(PropertyName pn:list){
			if(pn.equals(propertyName,map))
				return true;
		}
		
		return false;
	}
	

}
