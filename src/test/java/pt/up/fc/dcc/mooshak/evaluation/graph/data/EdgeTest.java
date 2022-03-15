package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class EdgeTest {
	int id = 0;
	Node node1S = new Node("-1","Class");
	Node node2S = new Node("-2","CLass");
	Node node1A = new Node("1","Class");
	Node node2A = new Node("2","Class");
	Node node3A = new Node("3","Entity");
	Node node4A = new Node("4","Entity");
	
	Map<Node, Match> map= new HashMap<>();
	Match match1 = new  Match(node1S, node1A, Configs.getDefaultConfigs());
	

	@Test
	public void testCompareTwoIdenticalEdges() {
		System.out.println(System.getProperty("user.dir"));
		map.put(node1S, match1);
		
		Edge solution = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1S,
				node2S);
		Edge attempt = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1A,
				node2A);

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute",
				"age",node1S);
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute",
				"age",node1A);
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);
		
		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());
		assertEquals("Comparison between two identical Edges", solution.getValue(), solution
				.compareEdge(attempt,Configs.getDefaultConfigs(),map)
					.getGrade());
	}

	@Test
	public void testCompareTwoEdgesWithSameType() {
		map.put(node1S, match1);
		Edge solution = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1S,
				node2S);
		Edge attempt = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1A,
				node2A);

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute",
				"age",node1S);
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);
		
		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());
	
		assertEquals(
				"Comparison between two different Edges with same type but without identical properties",
				18, solution.compareEdge(attempt,Configs.getDefaultConfigs(),map)
					.getGrade());
	}

	@Test
	public void testCompareTwoDifferentTypeEdges() {
		map.put(node1S, match1);
		
		Edge solution = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1S,
				node2S);
		Edge attempt = new Edge("id" + (id++), "UML - Generalization",
				new HashMap<PropertyName, PropertyValue>(), node1A,
				node2A);

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute",
				"age,node1S");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute",
				"age,node1A");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);
		
		assertEquals(
				"Comparison between two Edges with different types but same properties",
				0, solution.compareEdge(attempt,Configs.getDefaultConfigs(),map)
					.getGrade());
	}

	@Test
	public void testCompareTwoCompletlyDifferentEdges() {
		map.put(node1S, match1);
		
		Edge solution = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1S,
				node2S);
		Edge attempt = new Edge("id" + (id++), "UML - Generalization",
				new HashMap<PropertyName, PropertyValue>(), node4A,
				node3A);

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute",
				"age",node1S);
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		assertEquals("Comparison between two different Edges", 0, solution
				.compareEdge(attempt,Configs.getDefaultConfigs(),map)
					.getGrade());
	}

	@Test
	public void testCompareSimplePropertiesDifference() {
		map.put(node1S, match1);
		
		Edge solution = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1S,
				node2S);
		Edge attempt = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1A,
				node2A);

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("2");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute",
				"age",node1S);
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute",
				"idade",node1A);
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);
		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());
		assertEquals(
				"Comparison between two Edges with one different simple property",
				solution.getValue() - 4, solution.compareEdge(attempt,Configs.getDefaultConfigs(),map)
					.getGrade());
	}

	@Test
	public void testCompareCompositePropertiesDifference() {
		map.put(node1S, match1);
		
		Edge solution = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1S,
				node2S);
		Edge attempt = new Edge("id" + (id++), "UML - Association",
				new HashMap<PropertyName, PropertyValue>(), node1A,
				node2A);
		
		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute",
				"age",node1S);
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "float");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute",
				"idade",node1A);
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);

		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());
		
		assertEquals(
				"Comparison between two Edges with one different composite property",
				solution.getValue() - 4, solution.compareEdge(attempt,Configs.getDefaultConfigs(),map)
					.getGrade());
	}

}
