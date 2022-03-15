package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GradeWithDifferences;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;

public class NodeTest {

	@Test
	public void testCompareTwoIdenticalNodes() {
		System.out.println(System.getProperty("user.dir"));
		Node solution = new Node("UML - Class");
		Node attempt = new Node("UML - Class");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);

		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());

		assertEquals("Comparison between two identical nodes", solution.getValue(),
				solution.compareNode(attempt, Configs.getDefaultConfigs()).getGrade());
	}

	@Test
	public void testCompareTwoNodesWithSameType() {
		Node solution = new Node("UML - Class");
		Node attempt = new Node("UML - Class");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());

		assertEquals("Comparison between two different nodes with same type but without identical properties",
				Configs.getDefaultConfigs().get(ConfigName.NODE_TYPE_WEIGHT),
				solution.compareNode(attempt, Configs.getDefaultConfigs()).getGrade(), 0);
	}

	@Test
	public void testCompareTwoDifferentTypeNodes() {
		Node solution = new Node("UML - Class");
		Node attempt = new Node("UML - Interface");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute", "idade");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);
		assertEquals("Comparison between two nodes with different types but same properties", 6,
				solution.compareNode(attempt, Configs.getDefaultConfigs()).getGrade());
	}

	@Test
	public void testCompareTwoCompletlyDifferentNodes() {
		Node solution = new Node("UML - Class");
		Node attempt = new Node("UML - Interface");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		assertEquals("Comparison between two different nodes", 0,
				solution.compareNode(attempt, Configs.getDefaultConfigs()).getGrade());
	}

	@Test
	public void testCompareSimplePropertiesDifference() {
		Node solution = new Node("UML - Class");
		Node attempt = new Node("UML - Class");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("2");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute", "idade");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);

		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());

		assertEquals("Comparison between two nodes with one different simple property", solution.getValue() - 2,
				solution.compareNode(attempt, Configs.getDefaultConfigs()).getGrade());
	}

	@Test
	public void testCompareCompositePropertiesDifference() {
		Node solution = new Node("UML - Class");
		Node attempt = new Node("UML - Class");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "float");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute", "idade");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);

		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());

		assertEquals("Comparison between two nodes with one different composite subproperty", solution.getValue() - 2,
				solution.compareNode(attempt, Configs.getDefaultConfigs()).getGrade());
	}

	@Test
	public void testChangingDegrees() {
		Node solution = new Node("id000", "UML - Class");
		Node attempt = new Node("id001", "UML - Class");

		SimplePropertyName spn1 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv1 = new SimplePropertyValue("1");

		SimplePropertyName spn2 = new SimplePropertyName("multiplicity");
		SimplePropertyValue spv2 = new SimplePropertyValue("1");

		CompositePropertyName cpn1 = new CompositePropertyName("uml-attribute", "age");
		CompositePropertyValue cpv1 = new CompositePropertyValue();
		cpv1.add("type", "int");
		cpv1.add("visibility", "public");

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute", "idade");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		solution.addProperty(spn1, spv1);
		solution.addProperty(cpn1, cpv1);

		attempt.addProperty(cpn2, cpv2);
		attempt.addProperty(spn2, spv2);

		Map<String, Integer> degree = new HashMap<>();
		degree.put("type1", 2);
		degree.put("type2", 2);
		degree.put("type4", 5);

		solution.setInDegree(degree);
		solution.setOutDegree(degree);

		Map<String, Integer> degree2 = new HashMap<>();
		degree2.put("type1", 2);
		degree2.put("type2", 1);
		degree2.put("type3", 1);
		attempt.setInDegree(degree2);
		attempt.setOutDegree(degree2);

		solution.computeMaxValue(Configs.getDefaultConfigs());
		attempt.computeMaxValue(Configs.getDefaultConfigs());

		GradeWithDifferences grade = solution.compareNode(attempt, Configs.getDefaultConfigs());

		System.out.println(grade.getDifferences());
		assertEquals("Comparison between two nodes with different degrees", solution.getValue() - 14, grade.getGrade());
	}

	@Test
	public void testMaxValue() {
		testEqualNodes();
		testNodesWithDifferentTypes();
		testNodesWithDifferentProperties();
	}

	private void testNodesWithDifferentProperties() {
		Node solution = new Node("id000", "UML - Class");
		Node attempt = new Node("id001", "UML - Class");

		solution.addProperty(new SimplePropertyName("abc"), new SimplePropertyValue("xpto"));
		solution.addProperty(new SimplePropertyName("def"), new SimplePropertyValue("1234"));

		attempt.addProperty(new SimplePropertyName("abc"), new SimplePropertyValue("xpto"));
		attempt.addProperty(new SimplePropertyName("def"), new SimplePropertyValue("xpto"));

		List<Node> solutionNodes = new ArrayList<>();
		solutionNodes.add(solution);

		double maxValue = attempt.getMaxComparisonValue(solutionNodes);
		System.out.println(maxValue);
		assertTrue("maxValue must be between 0 and 1", maxValue >= 0 && maxValue <= 1);
		assertEquals("maxValue of nodes with different properties", 12/14d, maxValue, 0);
	}

	private void testNodesWithDifferentTypes() {
		Node solution = new Node("id000", "UML - Class");
		Node attempt = new Node("id001", "UML");

		solution.addProperty(new SimplePropertyName("abc"), new SimplePropertyValue("xpto"));
		solution.addProperty(new SimplePropertyName("def"), new SimplePropertyValue("1234"));

		attempt.addProperty(new SimplePropertyName("abc"), new SimplePropertyValue("xpto"));
		attempt.addProperty(new SimplePropertyName("def"), new SimplePropertyValue("1234"));

		List<Node> solutionNodes = new ArrayList<>();
		solutionNodes.add(solution);

		double maxValue = attempt.getMaxComparisonValue(solutionNodes);
		System.out.println(maxValue);
		assertTrue("maxValue must be between 0 and 1", maxValue >= 0 && maxValue <= 1);
		assertEquals("maxValue of nodes with different types", 4/14d, maxValue, 0);
	}

	private void testEqualNodes() {
		Node solution = new Node("id000", "UML - Class");
		Node attempt = new Node("id001", "UML - Class");

		solution.addProperty(new SimplePropertyName("abc"), new SimplePropertyValue("xpto"));
		solution.addProperty(new SimplePropertyName("def"), new SimplePropertyValue("1234"));

		attempt.addProperty(new SimplePropertyName("abc"), new SimplePropertyValue("xpto"));
		attempt.addProperty(new SimplePropertyName("def"), new SimplePropertyValue("1234"));

		List<Node> solutionNodes = new ArrayList<>();
		solutionNodes.add(solution);

		double maxValue = attempt.getMaxComparisonValue(solutionNodes);
		System.out.println(maxValue);
		assertTrue("maxValue must be between 0 and 1", maxValue >= 0 && maxValue <= 1);
		assertEquals("maxValue must be 1 (equal nodes)", 1, maxValue, 0);
	}
}
