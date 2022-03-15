package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.EDGE_PROPERTY_PENALTY;
import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.NODE_PROPERTY_PENALTY;

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

/**
 * Node class represents any nodes of a diagram. It adds to the GObject just the
 * feature related to the number and type of connections that are connected to
 * this node.
 * 
 * @author Ruben
 *
 */
public class Node extends GObject {

	Map<String, Integer> inDegree;
	Map<String, Integer> outDegree;

	public Node() {
		this.id = null;
		this.type = null;
		this.inDegree = null;
		this.outDegree = null;
		this.indistincts = 1;
		this.name = "";
	}

	public Node(Node node) {
		this.id = node.id;
		this.type = node.type;
		this.properties = new HashMap<>(node.properties);
		this.inDegree = new HashMap<>(node.inDegree);
		this.outDegree = new HashMap<>(node.outDegree);
		this.indistincts = node.indistincts;
		this.name = node.name;
		;
	}

	public Node(GObject node) {
		this.id = node.id;
		this.type = node.type;
		this.properties = new HashMap<>(node.properties);
		this.inDegree = new HashMap<>();
		this.outDegree = new HashMap<>();
		this.indistincts = node.indistincts;
		this.name = node.name;
	}

	public Node(String id, String type, Map<PropertyName, PropertyValue> properties) {
		this.id = id;
		this.type = type;
		this.properties = properties;
		this.inDegree = new HashMap<>();
		this.outDegree = new HashMap<>();
		this.indistincts = 1;
		this.name = "";
	}

	public Node(String type) {
		this.id = "";
		this.type = type;
		this.properties = new HashMap<>();
		this.inDegree = new HashMap<>();
		this.outDegree = new HashMap<>();
		this.indistincts = 1;
		this.name = "";
	}

	public Node(String id, String type) {
		this.id = id;
		this.type = type;
		this.properties = new HashMap<>();
		this.inDegree = new HashMap<>();
		this.outDegree = new HashMap<>();
		this.indistincts = 1;
		this.name = "";
	}
	
	public Node(String id, String type, String name) {
		this.id = id;
		this.type = type;
		this.properties = new HashMap<>();
		this.inDegree = new HashMap<>();
		this.outDegree = new HashMap<>();
		this.indistincts = 1;
		this.name = name;
	}

	/**
	 * @return the inDegree
	 */
	public Map<String, Integer> getInDegree() {
		return inDegree;
	}

	/**
	 * @param inDegree
	 *            the inDegree to set
	 */
	public void setInDegree(Map<String, Integer> inDegree) {
		this.inDegree = new HashMap<String, Integer>(inDegree);
	}

	/**
	 * @return the outDegree
	 */
	public Map<String, Integer> getOutDegree() {
		return outDegree;
	}

	/**
	 * @param outDegree
	 *            the outDegree to set
	 */
	public void setOutDegree(Map<String, Integer> outDegree) {
		this.outDegree = new HashMap<String, Integer>(outDegree);
	}

	/**
	 * Creates a exact copy of this node
	 * 
	 * @return
	 */
	public Node deepCopy() {
		Node node = new Node();
		node.id = this.id;
		node.type = this.type;
		node.properties = new HashMap<>();
		node.value = this.value;
		node.inDegree = new HashMap<>(this.inDegree);
		node.outDegree = new HashMap<>(this.outDegree);
		node.name = this.name;
		propertiesDeepCopy(node, this.properties);
		return node;
	}

	/**
	 * Compare this node with an attempt and grade it
	 * 
	 * @param attempt
	 * @return
	 */
	public GradeWithDifferences compareNode(Node attempt, Configs configs) {
		GradeWithDifferences grade = new GradeWithDifferences();

		if (this.type.equals(attempt.type))
			grade.setGrade(configs.get(ConfigName.NODE_TYPE_WEIGHT));
		else
			grade.getDifferences().add(new DifferentType(this, this.type, attempt.type));
//		GradeWithDifferences degreeGrade = compareDegrees(attempt, configs);
//		grade.update(degreeGrade);

		GradeWithDifferences propertiesGrade = compareProperties(this.properties, attempt.properties, configs,null);
		grade.update(propertiesGrade);

		int auxGrade = grade.getGrade();
		if (this.indistincts == attempt.indistincts) {
			auxGrade *= this.indistincts;
		}

		else if (this.indistincts < attempt.indistincts) {
			auxGrade = auxGrade * this.indistincts
					- (attempt.indistincts - this.indistincts) * configs.get(ConfigName.NODE_TYPE_WEIGHT);
			grade.addDifference(new NodeDeletion(attempt, attempt.indistincts - this.indistincts));
		}

		else {
			auxGrade = auxGrade * this.indistincts;
			grade.addDifference(new NodeInsertion(this, this.indistincts - attempt.indistincts));
		}

		grade.setGrade(auxGrade);

		if (grade.getGrade() < 0)
			grade.setGrade(0);

		return grade;
	}

	/**
	 * Compare this node with an attempt and grade it
	 * 
	 * @param attempt
	 * @return
	 */
	public GradeWithDifferences compareNode(Node attempt, List<Edge> solutionEdges, List<Edge> attemptEdges,
			Configs configs) {
		GradeWithDifferences grade = new GradeWithDifferences();
		if (this.type.equals(attempt.type)){
			grade.setGrade(configs.get(ConfigName.NODE_TYPE_WEIGHT));
		}

		else
			grade.getDifferences().add(new DifferentType(this, this.type, attempt.type));
				
		GradeWithDifferences degreeGrade = compareDegrees(attempt, configs);
		grade.update(degreeGrade);
		int auxGrade = grade.getGrade();
		if (this.indistincts == attempt.indistincts) {
				
			auxGrade *= this.indistincts;
		}

		else if (this.indistincts < attempt.indistincts) {
			
			auxGrade = auxGrade * this.indistincts
					- (attempt.indistincts - this.indistincts) * configs.get(ConfigName.NODE_TYPE_WEIGHT);
			int difference = attempt.indistincts - this.indistincts;
			grade.addDifference(new NodeDeletion(attempt, difference));
			for (Edge edge : attemptEdges) {
				int d;
				if (edge.getSource().equals(attempt) || edge.getTarget().equals(attempt)) {
					d = difference * edge.getIndistincts() / attempt.getIndistincts();

					grade.addDifference(new EdgeDeletion(edge, d));
				}
			}
		}

		else {
			
			auxGrade = auxGrade * this.indistincts;
			int difference = this.indistincts - attempt.indistincts;
			grade.addDifference(new NodeInsertion(this, difference));
			for (Edge edge : solutionEdges) {
				int d;
				if (edge.getSource().equals(this) || edge.getTarget().equals(this)) {
					d = difference * edge.getIndistincts() / this.getIndistincts();
					grade.addDifference(new EdgeInsertion(edge, d));
				}
			}
		}

		grade.setGrade(auxGrade);

		if (grade.getGrade() < 0)
			grade.setGrade(0);

		return grade;
	}

	/**
	 * Compares the connections that are attached in this node with the ones
	 * attached to node attempt
	 * 
	 * @param attempt
	 * @param configs
	 * @return
	 */
	public GradeWithDifferences compareDegrees(Node attempt, Configs configs) {
		GradeWithDifferences grade = new GradeWithDifferences();
		int commonInDegreeGrade = 0;
		Set<DifferentConnection> diffs = new HashSet<>();

		for (String type : this.inDegree.keySet()) {
			int typeConnections = this.inDegree.get(type);
			if (attempt.inDegree.containsKey(type)) {
				int attemptTypeConnections = attempt.inDegree.get(type);
				commonInDegreeGrade += typeConnections - Math.abs(typeConnections - attemptTypeConnections);
				if (typeConnections != attemptTypeConnections) {
					diffs.add(new DifferentConnection(type, typeConnections, attemptTypeConnections));
				}
			} else {
				diffs.add(new DifferentConnection(type, typeConnections, 0));
			}
		}

		for (String type : attempt.inDegree.keySet()) {
			if (!this.inDegree.containsKey(type)) {
				commonInDegreeGrade -= attempt.inDegree.get(type);
				diffs.add(new DifferentConnection(type, 0, attempt.inDegree.get(type)));

			}
		}
		if (!diffs.isEmpty()) {
			grade.getDifferences().add(new InDegreeDifference(this, diffs));
		}
		grade.addGrade(configs.get(ConfigName.NODE_DEGREE_WEIGHT) * commonInDegreeGrade);
		int commonOutDegreeGrade = 0;
		diffs = new HashSet<>();

		for (String type : this.outDegree.keySet()) {
			int typeConnections = this.outDegree.get(type);
			if (attempt.outDegree.containsKey(type)) {
				int attemptTypeConnections = attempt.outDegree.get(type);
				commonOutDegreeGrade += typeConnections - Math.abs(typeConnections - attemptTypeConnections);
				if (typeConnections != attemptTypeConnections) {
					diffs.add(new DifferentConnection(type, typeConnections, attemptTypeConnections));
				}
			} else {
				diffs.add(new DifferentConnection(type, typeConnections, 0));
			}
		}

		for (String type : attempt.outDegree.keySet()) {
			if (!this.outDegree.containsKey(type)) {
				commonOutDegreeGrade -= attempt.outDegree.get(type);
				diffs.add(new DifferentConnection(type, 0, attempt.outDegree.get(type)));

			}
		}
		if (!diffs.isEmpty()) {
			grade.getDifferences().add(new OutDegreeDifference(this, diffs));
		}
		grade.addGrade(configs.get(ConfigName.NODE_DEGREE_WEIGHT) * commonOutDegreeGrade);
		return grade;
	}

	/**
	 * Returns if the object is a node.
	 */
	public boolean isNode() {
		return true;
	}

	/**
	 * Convert to string the node's information
	 */
	public String toString() {
		String phrase = "\nNode id : " + id + "\nNode name: " + name + "\nNode type : " + type
				+ "\nNumber of properties : " + properties.size() + "\n";

		Iterator<PropertyName> mapIterator = properties.keySet().iterator();
		PropertyName key;
		while (mapIterator.hasNext()) {
			key = mapIterator.next();
			phrase += key.toString() + properties.get(key).toString() + "\n";
		}

		phrase += "InDegree: " + inDegree + "\nOutDegree: " + outDegree;
		return phrase;
	}
	
	/**
	 * Increments the counter of similar nodes.
	 */
	public void incrementIndistinct() {
		this.indistincts++;
	}

	public double getMaxComparisonValue(List<Node> solutionNodes) {
		double maxComparison = 0;
		for(Node node : solutionNodes) {
			double currentValue = this.compareNode(node, Configs.getDefaultConfigs()).getGrade() / (double) this.computeMaxValue(Configs.getDefaultConfigs());
			maxComparison = maxComparison > currentValue ? maxComparison : currentValue;  
		}
		return maxComparison;
	}
	
	/**
	 * 
	 * @return true if node is diconnected
	 */
	public boolean isDisconnected(){
		if(this.inDegree.size() == 0 && this.outDegree.size()==0 && !this.getType().equals("Rectangle"))
			return true;
		return false;
	}

	
	
	public int getTotalDegreeIn(){
		int total=0;
		for(Entry<String, Integer> typeIn : this.inDegree.entrySet()){
			total += typeIn.getValue();
		}
		return total;
	}
	
	public int getTotalDegreeOut(){
		int total=0;
		for(Entry<String, Integer> typeOut : this.outDegree.entrySet()){
			total += typeOut.getValue();
		}
		return total;
	}
	
	public int getTotalDegreeInOut(){
		return getTotalDegreeIn() + getTotalDegreeOut();
	}
	
	
	
	
	
	public int getTotalDegreeIn(String type){
		int total=0;
		for(Entry<String, Integer> typeIn : this.inDegree.entrySet()){
			if(typeIn.getKey().equals(type))
				total += typeIn.getValue();
		}
		return total;
	}
	
	public int getTotalDegreeOut(String type){
		int total=0;
		for(Entry<String, Integer> typeOut : this.outDegree.entrySet()){
			if(typeOut.getKey().equals(type))
			total += typeOut.getValue();
		}
		return total;
	}
	
	public int getTotalDegreeInOut(String type){
		return getTotalDegreeIn(type) + getTotalDegreeOut(type);
	}
	
	public Map<String, Integer> getlDegreeInOut() {
		Map<String, Integer> degreeInOut= new HashMap<String, Integer>();
		
		degreeInOut= this.inDegree;
		
		for(Entry<String, Integer> typeOut : this.outDegree.entrySet()){
			if(degreeInOut.containsKey(typeOut.getKey())){
					degreeInOut.put(typeOut.getKey(), typeOut.getValue()+degreeInOut.get(typeOut.getKey()));
			}
			else
				degreeInOut.put(typeOut.getKey(),typeOut.getValue());
		}
		return degreeInOut;
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
		List<PropertyName> attemptsVisited = new ArrayList<>();
		
		Map<PropertyName, PropertyValue> solutionProperties2 = new HashMap<PropertyName, PropertyValue>(); 
		
		for (Entry<PropertyName, PropertyValue> entry : solutionProperties.entrySet()) {
			solutionProperties2.put(entry.getKey(), entry.getValue());
		}
		
		Map<PropertyName, PropertyValue> attemptProperties2= new HashMap<PropertyName, PropertyValue>();
		
		for (Entry<PropertyName, PropertyValue> entry : attemptProperties.entrySet()) {
			attemptProperties2.put(entry.getKey(), entry.getValue());
		}
		
		
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
				PropertyName pn = null;
				for (Entry<PropertyName, PropertyValue> entry : attemptProperties2.entrySet())
					if (entry.getKey().equals(key)) {
						GradeWithDifferences currentGrade = value.compareProperty(this, key, entry.getValue(), configs);
						if (currentGrade.getGrade() > 0) {
							grade.update(currentGrade);
							pn = entry.getKey();
							solutionProperties2.remove(key);
						}
						if (currentGrade.getDifferences().size() > 0)
							grade.getDifferences().addAll(currentGrade.getDifferences());
						break;
					}

				if (pn != null)
					attemptProperties2.remove(pn);
				if (attemptProperties2.size() == 0)
					break;
			}

		}
			
		ConfigName configName = isNode() ? NODE_PROPERTY_PENALTY : EDGE_PROPERTY_PENALTY;
		for (PropertyName key : solutionProperties2.keySet()) {
				int penalty = configs.get(configName);
				if (!key.isSimple())
					penalty = configs.get(configName) * ((CompositePropertyValue) solutionProperties2.get(key)).value.size();
//				grade.applyPenalty(penalty);
				
				if(attemptProperties2.size()>0){
					PropertyValue value1=null;
					for (PropertyName key2 : attemptProperties2.keySet()) {
						value1=attemptProperties2.remove(key2);
						break;
					}
					grade.getDifferences().add(new DifferentPropertyValue(this, key, solutionProperties2.get(key),value1));	
				}
				else
					grade.getDifferences().add(new PropertyInsertion(this, key, solutionProperties2.get(key)));
		}
		
		for (PropertyName key : attemptProperties2.keySet()) {
				int penalty = configs.get(configName);
				if (!key.isSimple())
					penalty = configs.get(configName) * ((CompositePropertyValue) attemptProperties2.get(key)).value.size();
				grade.applyPenalty(penalty);
				grade.getDifferences().add(new PropertyDeletion(this, key, attemptProperties2.get(key)));
		}

		return grade;
	}

	
	
	
}
