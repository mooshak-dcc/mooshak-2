package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;
import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.evaluation.kora.EvaluationController;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.FeedbackManager;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class GObjectTest {
	private static final int MIN_CODE_POINT = Character.codePointAt(
			new char[] { ' ' }, 0);
	private static final int MAX_CODE_POINT = Character.codePointAt(
			new char[] { '~' }, 0);
	private int id = 0;

	
	
	
	@Test
	public void testcomparePropertiesProperties() throws MooshakException, FileNotFoundException{

		
		Configuration fileConfig= new Configuration("inputs/eer/eer2.xml");
		
		
		String attemptPath = "inputs/eer/empresaAttempt.eer";
		String solutionPath = "inputs/eer/empresaSolution.eer";
				
		Diagram diagram = fileConfig.getDL2().getDiagram();
		
		EvaluationController ec = new EvaluationController(solutionPath, attemptPath,diagram);
		
		FeedbackManager fm= new FeedbackManager(diagram.getUrlMap());
		ec.generateFeedback(fm);
		Map<Node, Match> map =ec.getEvaluation().getBestMap();
		
		System.out.println(ec.solution);
		ec.attempt.getEdges().get(0);
		assertEquals("a","a");
//		int simpleProperties = 3;
//		int compositeProperties = 3;
//		int subProperties = 2;
//		Node node = createNode(simpleProperties, compositeProperties,
//				subProperties);
//
//		int expected = 10 + simpleProperties * 2 + compositeProperties
//				* subProperties * 2;
//		int real = node.computeMaxValue(Configs.getDefaultConfigs());
//		assertEquals(expected, real);
//
//		Node variation = new Node(node);
//
//		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute",
//				"idade");
//		CompositePropertyValue cpv2 = new CompositePropertyValue();
//		cpv2.add("type", "int");
//		cpv2.add("visibility", "public");
//
//		variation.addProperty(cpn2, cpv2);
//		assertEquals(
//				"Comparison between two nodes - Adding one composite property",
//				node.getValue() - 4, node.compareNode(variation,Configs.getDefaultConfigs())
//					.getGrade());
	}
	
	@Test
	public void testAddingProperties() {

		int simpleProperties = 4;
		int compositeProperties = 4;
		int subProperties = 2;
		Node node = createNode(simpleProperties, compositeProperties,
				subProperties);

		int expected = 10 + simpleProperties * 2 + compositeProperties
				* subProperties * 2;
		int real = node.computeMaxValue(Configs.getDefaultConfigs());
		System.out.println(real + " "+ expected);
		assertEquals(expected, real);

		Node variation = new Node(node);

		CompositePropertyName cpn2 = new CompositePropertyName("uml-attribute",
				"idade");
		CompositePropertyValue cpv2 = new CompositePropertyValue();
		cpv2.add("type", "int");
		cpv2.add("visibility", "public");

		variation.addProperty(cpn2, cpv2);
		
		assertEquals(
				"Comparison between two nodes - Adding one composite property",
				node.getValue() - 4, node.compareNode(variation,Configs.getDefaultConfigs())
					.getGrade());
	}

	@Test
	public void testRemovingProperties() {

		int simpleProperties = 3;
		int compositeProperties = 3;
		int subProperties = 2;
		Node node = createNode(simpleProperties, compositeProperties,
				subProperties);

		int expected = 10 + simpleProperties * 2 + compositeProperties
				* subProperties * 2;
		int real = node.computeMaxValue(Configs.getDefaultConfigs());
		assertEquals(expected, real);

		Node variation = new Node(node);

		Iterator<PropertyName> iterator = variation.properties.keySet()
				.iterator();
		while (iterator.hasNext()) {
			PropertyName name = iterator.next();
			if (!name.isSimple()){
				iterator.remove();
			}
		}

		assertEquals(
				"Comparison between two nodes - Removing all composite properties",
				node.getValue() - 2 * compositeProperties * subProperties, node.compareNode(variation,Configs.getDefaultConfigs())
					.getGrade());
	}

	@Test
	public void testComputeNodeMaxValue() {
		int simpleProperties = 3;
		int compositeProperties = 3;
		int subProperties = 2;
		Node node = createNode(simpleProperties, compositeProperties,
				subProperties);

		int expected = 10 + simpleProperties * 2 + compositeProperties
				* subProperties * 2;
		int real = node.computeMaxValue(Configs.getDefaultConfigs());
		assertEquals(expected, real);
	}

	@Test
	public void testComputeEdgeMaxValue() {
		int simpleProperties = 2;
		int compositeProperties = 4;
		int subProperties = 1;
		Edge edge = createEdge(simpleProperties, compositeProperties,
				subProperties);

		int expected = 10 + simpleProperties * 2 + compositeProperties
				* subProperties * 2;
		int real = edge.computeMaxValue(Configs.getDefaultConfigs());
		assertEquals(expected, real);
	}

	@Test
	public void testComputeRandomMaxValue() {
		int nTests = 100;
		for (int i = 0; i < nTests; i++) {
			int simpleProperties = nextInt(0, 5);
			int compositeProperties = nextInt(0, 5);
			int subProperties = nextInt(0, 5);

			int expected = 10 + simpleProperties * 2 + compositeProperties
					* subProperties * 2;
			int real;
			int isNode = nextInt(0, 1);
			if (isNode == 1) {
				Node node = createNode(simpleProperties, compositeProperties,
						subProperties);
				real = node.computeMaxValue(Configs.getDefaultConfigs());
			} else {
				Edge edge = createEdge(simpleProperties, compositeProperties,
						subProperties);
				real = edge.computeMaxValue(Configs.getDefaultConfigs());
			}

			assertEquals(expected, real);
		}
	}

	
	
	@Test
	public void testComputeGraphMaxValue() {
		int nTests = 1000;
		List<Node> nodes = new LinkedList<>();
		List<Edge> edges = new LinkedList<>();
		int totalNodes = 0;
		int totalEdges = 0;
		for (int i = 0; i < nTests; i++) {
			int simpleProperties = nextInt(0, 5);
			int compositeProperties = nextInt(0, 5);
			int subProperties = nextInt(0, 5);

			int expected = 10 + simpleProperties * 2 + compositeProperties
					* subProperties * 2;
			int real;
			int isNode = nextInt(0, 1);
			if (isNode == 1) {
				Node node = createNode(simpleProperties, compositeProperties,
						subProperties);
				real = node.computeMaxValue(Configs.getDefaultConfigs());
				nodes.add(node);
				totalNodes += real;
			} else {
				Edge edge = createEdge(simpleProperties, compositeProperties,
						subProperties);
				real = edge.computeMaxValue(Configs.getDefaultConfigs());
				edges.add(edge);
				totalEdges += real;
			}
			assertEquals("Object value", expected, real);
			Graph graph = new Graph(nodes, edges);
			graph.computeMaxValues(Configs.getDefaultConfigs());

			assertEquals("Edges total", totalEdges, graph.getMaxEdgesValue());
			assertEquals("Nodes total", totalNodes, graph.getMaxNodesValue());
		}
	}

	private Node createNode(int simpleProperties, int compositeProperties,
			int subProperties) {
		GObject object = createObject(simpleProperties, compositeProperties,
				subProperties);
		Node node = new Node(object);
		return node;
	}

	private Edge createEdge(int simpleProperties, int compositeProperties,
			int subProperties) {
		GObject object = createObject(simpleProperties, compositeProperties,
				subProperties);
		Edge edge = new Edge(object);
		edge.setSource(createNode(0, 0, 0));
		edge.setTarget(createNode(0, 0, 0));
		return edge;
	}

	private GObject createObject(int simpleProperties, int compositeProperties,
			int subProperties) {
		GObject object = new GObject();
		object.setId(getId());
		object.setType(getType());
		object.setProperties(createProperties(simpleProperties,
				compositeProperties, subProperties));
		return object;
	}

	private Map<PropertyName, PropertyValue> createProperties(
			int simpleProperties, int compositeProperties, int subProperties) {
		Map<PropertyName, PropertyValue> properties = new HashMap<>();
		for (int i = 0; i < simpleProperties; i++) {
			createSimpleProperty(properties);
		}

		for (int i = 0; i < compositeProperties; i++) {
			createCompositeProperty(properties, subProperties);
		}
		return properties;
	}

	private void createCompositeProperty(
			Map<PropertyName, PropertyValue> properties, int subProperties) {
		CompositePropertyName name;
		do {
			name = new CompositePropertyName(nextString(), nextString());
		} while (properties.containsKey(name));

		CompositePropertyValue values = new CompositePropertyValue();
		for (int i = 0; i < subProperties; i++) {
			String valueName;
			do {
				valueName = nextString();
			} while (values.value.containsKey(valueName));

			values.value.put(valueName, nextString());
		}

		properties.put(name, values);
	}

	private void createSimpleProperty(
			Map<PropertyName, PropertyValue> properties) {
		SimplePropertyName name;
		do {
			name = new SimplePropertyName(nextString());
		} while (properties.containsKey(name));

		SimplePropertyValue value = new SimplePropertyValue(nextString());
		properties.put(name, value);
	}

	private String getId() {
		return "id" + id++;
	}

	private String getType() {
		return nextLabel("type", 0, 4);
	}

	private String nextString() {
		StringBuilder builder = new StringBuilder();
		int length = nextInt(0, 5);

		for (int c = 0; c < length; c++)
			builder.append(Character.toChars(nextInt(MIN_CODE_POINT,
					MAX_CODE_POINT)));

		return builder.toString();
	}

	private String nextLabel(String prefix, int a, int b) {
		return String.format("%s%03d", prefix, nextInt(a, b));
	}

	private int nextInt(int a, int b) {
		if (a == b)
			return a;
		Random random = new Random();
		return a + random.nextInt(b - a + 1);
	}
}
