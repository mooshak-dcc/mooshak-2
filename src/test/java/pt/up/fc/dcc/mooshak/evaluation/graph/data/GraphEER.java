package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;


public class GraphEER {
	public Configs configs = Configs.getDefaultConfigs();
	
	/***Solution*/
	public Node node1S;
	public Node node2S;
	public Node node3S;
	public Node node4S;
	public Node node6S;
	
	public CompositePropertyName propertyCardiName1S;
	public CompositePropertyValue propertyCardiValue1S;
	
	public CompositePropertyName propertyCardiName2S;
	public CompositePropertyValue propertyCardiValue2S;
	
	public CompositePropertyName propertyCardiName3S;
	public CompositePropertyValue propertyCardiValue3S;
	
	public CompositePropertyName propertyCardiName4S;
	public CompositePropertyValue propertyCardiValue4S;
	
	public CompositePropertyName propertyCardiName6S;
	public CompositePropertyValue propertyCardiValue6S;
	
	public SimplePropertyName simplePropertyCardiName1S;
	public SimplePropertyValue simplePropertyCardiValue1S;
	
	public SimplePropertyName simplePropertyCardiName2S;
	public SimplePropertyValue simplePropertyCardiValue2S;
	
	public Map<PropertyName, PropertyValue> propertyCardi1S;
	public Map<PropertyName, PropertyValue> propertyCardi2S;
	public Map<PropertyName, PropertyValue> propertyCardi3S;
	public Map<PropertyName, PropertyValue> propertyCardi4S;
	public Map<PropertyName, PropertyValue> propertyCardi5S;
	public Map<PropertyName, PropertyValue> propertyCardi6S;
	
	
	public Edge edge1S;
	public Edge edge2S;
	public Edge edge3S;
	public Edge edge4S;
	
	
	/**Attempt*/
	public Node node1A;
	public Node node2A;
	public Node node3A;
	public Node node4A;
	public Node node6A;
	
	public CompositePropertyName propertyCardiName1A;
	public CompositePropertyValue propertyCardiValue1A;
	
	public CompositePropertyName propertyCardiName2A;
	public CompositePropertyValue propertyCardiValue2A;
	
	public CompositePropertyName propertyCardiName3A;
	public CompositePropertyValue propertyCardiValue3A;
	
	public CompositePropertyName propertyCardiName4A;
	public CompositePropertyValue propertyCardiValue4A;
	
	public CompositePropertyName propertyCardiName5A;
	public CompositePropertyValue propertyCardiValue5A;
	
	public CompositePropertyName propertyCardiName6A;
	public CompositePropertyValue propertyCardiValue6A;
	
	public SimplePropertyName simplePropertyCardiName1A;
	public SimplePropertyValue simplePropertyCardiValue1A;
	
	public SimplePropertyName simplePropertyCardiName2A;
	public SimplePropertyValue simplePropertyCardiValue2A;
	
	public Map<PropertyName, PropertyValue> propertyCardi1A;
	public Map<PropertyName, PropertyValue> propertyCardi2A;
	public Map<PropertyName, PropertyValue> propertyCardi3A;
	public Map<PropertyName, PropertyValue> propertyCardi4A;
	public Map<PropertyName, PropertyValue> propertyCardi5A;
	public Map<PropertyName, PropertyValue> propertyCardi6A;
	
	public Edge edge1A;
	public Edge edge2A;
	public Edge edge3A;
	public Edge edge4A;
	
	Map<Node, Match> map;

	public GraphEER() {
		
		
			/**SOLUTION*//////////////		
//		Node(String id, String type, Map<PropertyName, PropertyValue> properties) 
		
		HashMap<String, Integer> degree;
		
		
		node1S = new Node("-1","Entity");
		node1S.setName("Node -1 S");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node1S.setInDegree(degree);
		
		node2S = new Node("-2","Entity");
		node2S.setName("Node -2 S");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node2S.setInDegree(degree);
		node2S.setOutDegree(degree);
		
		node3S = new Node("-3","Entity");
		node3S.setName("Node -3 S");
		
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node3S.setOutDegree(degree);
		
		// Edge 1
		
		propertyCardiName1S = new CompositePropertyName("candinality", "cardinality",node1S);
		propertyCardiValue1S = new CompositePropertyValue("value", "1");
		
		 propertyCardiName2S = new CompositePropertyName("candinality", "cardinality",node2S);
		 propertyCardiValue2S = new CompositePropertyValue("value", "M");
		
		
		 propertyCardi1S = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi1S.put(propertyCardiName1S, propertyCardiValue1S);
		
		 propertyCardi2S = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi2S.put(propertyCardiName2S, propertyCardiValue2S);
		
		
		
		 edge1S = new Edge("-4", "Line", node1S, node2S);
		
		edge1S.addProperties(propertyCardi1S);
		edge1S.addProperties(propertyCardi2S);
		
		

		// Edge 2

		 propertyCardiName3S = new CompositePropertyName("candinality", "cardinality",node2S);
		 propertyCardiValue3S = new CompositePropertyValue("value", "1");
		
		 propertyCardiName4S = new CompositePropertyName("candinality", "cardinality",node3S);
		 propertyCardiValue4S = new CompositePropertyValue("value", "M");
		 
		 propertyCardiName6S = new CompositePropertyName("candinality", "cardinality",null);
		 propertyCardiValue6S = new CompositePropertyValue("value", "M");
		
		
	    propertyCardi3S = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi3S.put(propertyCardiName3S, propertyCardiValue3S);
		
		propertyCardi4S = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi4S.put(propertyCardiName4S, propertyCardiValue4S);
		
		propertyCardi6S = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi6S.put(propertyCardiName6S, propertyCardiValue6S);
		
		
		//System.out.println(propertyCardi1);
		
		edge2S = new Edge("-5", "Line", node1S, node2S);
		
		edge2S.addProperties(propertyCardi3S);
		edge2S.addProperties(propertyCardi4S);
		
		edge3S = new Edge("-5", "Line", node1S, node2S);
		
//		edge3S.addProperties(propertyCardi3S);
		edge3S.addProperties(propertyCardi4S);
		
		simplePropertyCardiName1S = new SimplePropertyName("total");
		simplePropertyCardiValue1S= new SimplePropertyValue("true");
		 
		propertyCardi5S = new HashMap<PropertyName, PropertyValue>();
		propertyCardi5S.put(simplePropertyCardiName1S, simplePropertyCardiValue1S);
	
		edge4S = new Edge("-6", "Line", node1S, node2S);
		edge4S.addProperties(propertyCardi5S);
		
		
		node6S = new Node("6","Entity");
		node6S.setName("Node 6 S");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node6S.setOutDegree(degree);
//		node6S.setProperties(propertyCardi6S);
		
		
		/*ATTEMPT***************************************************//////////////		
		
		
		node1A = new Node("1","Entity");
		node1A.setName("Node 1 A");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node1A.setOutDegree(degree);
		
		 node2A = new Node("2","Entity");
		node2A.setName("Node 2 A");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node2A.setInDegree(degree);
		node2A.setOutDegree(degree);
		
		node3A = new Node("3","Entity");
		node3A.setName("Node 3 A");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node3A.setInDegree(degree);
		
		
		
		
		
		// Edge 1
		
		 propertyCardiName1A = new CompositePropertyName("candinality", "cardinality",node1A);
		 propertyCardiValue1A = new CompositePropertyValue("value", "1");
		
		 propertyCardiName2A = new CompositePropertyName("candinality", "cardinality",node2A);
		 propertyCardiValue2A = new CompositePropertyValue("value", "M");
		
		
		 propertyCardi1A = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi1A.put(propertyCardiName1A, propertyCardiValue1A);
		
		 propertyCardi2A = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi2A.put(propertyCardiName2A, propertyCardiValue2A);
		
		//System.out.println(propertyCardi1);
		
		 edge1A = new Edge("-4", "Line", node1A, node2A);
		
		edge1A.addProperties(propertyCardi1A);
		edge1A.addProperties(propertyCardi2A);
		
		

		// Edge 2

		 propertyCardiName3A = new CompositePropertyName("candinality", "cardinality",node2A);
		 propertyCardiValue3A = new CompositePropertyValue("value", "1");
		
		 propertyCardiName4A = new CompositePropertyName("candinality", "cardinality",node3A);
		 propertyCardiValue4A = new CompositePropertyValue("value", "M");
		 
		 propertyCardiName4A = new CompositePropertyName("candinality", "cardinality",node3A);
		 propertyCardiValue4A = new CompositePropertyValue("value", "M");
		 
		 propertyCardiName6A = new CompositePropertyName("candinality", "cardinality",null);
		 propertyCardiValue6A = new CompositePropertyValue("value", "M");
		
		
		propertyCardi3A = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi3A.put(propertyCardiName3A, propertyCardiValue3A);
		
		propertyCardi4A = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi4A.put(propertyCardiName4A, propertyCardiValue4A);
		
		propertyCardi6A = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi6A.put(propertyCardiName6A, propertyCardiValue6A);
		
		 simplePropertyCardiName1A = new SimplePropertyName("total");
		 simplePropertyCardiValue1A= new SimplePropertyValue("true");
		
		//System.out.println(propertyCardi1);
		
		edge2A = new Edge("-5", "Line", node1A, node2A);
		
		edge2A.addProperties(propertyCardi3A);
		edge2A.addProperties(propertyCardi4A);
		
		edge3A = new Edge("-5", "Line", node1A, node2A);
		
		edge3A.addProperties(propertyCardi3A);
//		edge3A.addProperties(propertyCardi4A);
		
		
	
		propertyCardi5A = new  HashMap<PropertyName, PropertyValue>();
		propertyCardi5A.put(simplePropertyCardiName1A, simplePropertyCardiValue1A);
		 
		
		edge4A = new Edge("6", "Line", node1A, node2A);
		edge4A.addProperties(propertyCardi5A);
		
		
		node6A = new Node("6","Entity");
		node6A.setName("Node 6 A");
		degree = new HashMap<String, Integer>();
		degree.put("Line", 1);
		node6A.setInDegree(degree);
		node6A.setProperties(propertyCardi6A);
		
		
		map= new HashMap<>();
		Match match1 = new  Match(node1S, node1A, configs);
		map.put(node1S, match1);
		
		Match match2 = new  Match(node2S, node2A, configs);
		map.put(node2S, match2);
		
		Match match3 = new  Match(node3S, node3A, configs);
		map.put(node3S, match3);
		
		Match match4 = new  Match(node6S, node6A, configs);
		map.put(node6S, match4);
		
	
		
		
	}
	
//	public Node createNode(String id, typeString name){
//		Node node = new Node("1","Entity");
//		node.setName("Node 1 A");
//		return node;
//	}
	
	
//	Node node1A = new Node("1","Entity");
//	node1A.setName("Node 1 A");
//	degree = new HashMap<String, Integer>();
//	degree.put("Line", 1);
//	node1A.setOutDegree(degree);
}


