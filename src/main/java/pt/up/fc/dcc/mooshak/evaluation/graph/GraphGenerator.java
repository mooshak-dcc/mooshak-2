package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.*;

public class GraphGenerator {

	private static final int MIN_NODES = 3;
	private static final int MAX_NODES = 20;

	private static final int MIN_EDGES = 2;
	private static final int MAX_EDGES = 10;

	private static final int MIN_NODE_TYPES = 2;
	private static final int MAX_NODE_TYPES = 5;

	private static final int MIN_EDGE_TYPES = 1;
	private static final int MAX_EDGE_TYPES = 5;

	private static final int MIN_PROPERTIES = 0;
	private static final int MAX_PROPERTIES = 10;

	private static final int MIN_VALUE_LENGTH = 1;
	private static final int MAX_VALUE_LENGTH = 10;

	private static final int MIN_CODE_POINT = Character.codePointAt(
			new char[] { ' ' }, 0);
	private static final int MAX_CODE_POINT = Character.codePointAt(
			new char[] { '~' }, 0);

	Random random = new Random();

	int minNodes = MIN_NODES;
	int maxNodes = MAX_NODES;

	int minEdges = MIN_EDGES;
	int maxEdges = MAX_EDGES;

	int minNodeTypes = MIN_NODE_TYPES;
	int maxNodeTypes = MAX_NODE_TYPES;

	int minEdgeTypes = MIN_EDGE_TYPES;
	int maxEdgeTypes = MAX_EDGE_TYPES;

	int minProperties = MIN_PROPERTIES;
	int maxProperties = MAX_PROPERTIES;

	int minValueLength = MIN_VALUE_LENGTH;
	int maxValueLength = MAX_VALUE_LENGTH;

	int minCodePoint = MIN_CODE_POINT;
	int maxCodePoint = MAX_CODE_POINT;

	int nodeCount = 0;
	int edgeCount = 0;

	int changeNodes = 0;
	int changeEdges = 0;
	int changeProperties = 0;

	long seed = -1;

	public GraphGenerator() {

	}

	public GraphGenerator(GraphGenerator generator) {
		super();
		this.random = generator.random;
		this.minNodes = generator.minNodes;
		this.maxNodes = generator.maxNodes;
		this.minEdges = generator.minEdges;
		this.maxEdges = generator.maxEdges;
		this.minNodeTypes = generator.minNodeTypes;
		this.maxNodeTypes = generator.maxNodeTypes;
		this.minEdgeTypes = generator.minEdgeTypes;
		this.maxEdgeTypes = generator.maxEdgeTypes;
		this.minProperties = generator.minProperties;
		this.maxProperties = generator.maxProperties;
		this.minValueLength = generator.minValueLength;
		this.maxValueLength = generator.maxValueLength;
		this.minCodePoint = generator.minCodePoint;
		this.maxCodePoint = generator.maxCodePoint;
		this.nodeCount = generator.nodeCount;
		this.edgeCount = generator.edgeCount;
		this.changeNodes = generator.changeNodes;
		this.changeEdges = generator.changeEdges;
		this.changeProperties = generator.changeProperties;
		this.seed = generator.seed;
	}

	/**
	 * Generates random nodes and edges to construct a graph.
	 * 
	 * @return
	 */
	public Graph generate() {
		nodeCount = 0;
		edgeCount = 0;
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();
		HashMap<Node, Integer> conex = new HashMap<>();
		HashSet<Integer> components = new HashSet<>();

		random.setSeed(consumeSeed());

		int nNodes = nextInt(minNodes, maxNodes);

		setMinEdges(nNodes - 1);
		setMaxEdges((nNodes * (nNodes - 1)) / 2);

		int nEdges = nextInt(minEdges, maxEdges);

		for (int c = 0; c < nNodes; c++)
			nodes.add(createNode());

		if (nodes.size() != 0)
			for (int c = 0; c < nEdges; c++)
				edges.add(createEdge(nodes));

		for (int i = 0; i < nodes.size(); i++) {
			conex.put(nodes.get(i), i);
			components.add(i);
		}

		while (true) {
			components = connectGraph(nodes, edges, conex);
			if (components.size() == 1)
				break;

			int index = 0;
			int startComponent = -1;
			int endComponent = -1;

			for (int i : components) {
				if (index == 0)
					startComponent = i;

				else if (index == 1)
					endComponent = i;

				else
					break;
				index++;
			}

			edges.add(putEdge(conex, startComponent, endComponent, edges));
		}

		return new Graph(nodes, edges, true);
	}

	private HashSet<Integer> connectGraph(List<Node> nodes, List<Edge> edges,
			HashMap<Node, Integer> conex) {

		for (Edge edge : edges) {
			int sourceComponent = conex.get(edge.getSource());
			int targetComponent = conex.get(edge.getTarget());

			for (Node node : nodes) {
				if (conex.get(node) == targetComponent)
					conex.put(node, sourceComponent);
			}
		}

		return new HashSet<>(conex.values());
	}

	/**
	 * Adds a edge between a node which was isolated to another node
	 * 
	 * @param edges
	 * @param source
	 * @param nodes
	 */
	public Edge putEdge(HashMap<Node, Integer> conex, int sourceComponent,
			int targetComponent, List<Edge> edges) {
		List<Node> sourceNodes = new ArrayList<>();
		List<Node> targetNodes = new ArrayList<>();

		for (Node node : conex.keySet()) {
			if (conex.get(node) == sourceComponent)
				sourceNodes.add(node);
			else if (conex.get(node) == targetComponent)
				targetNodes.add(node);
		}

		String id = String.format("edge%03d", edgeCount++);
		String type = nextLabel("edgeType", minEdgeTypes, maxEdgeTypes);
		Node source = sourceNodes.get(nextInt(0, sourceNodes.size()));
		Node target = targetNodes.get(nextInt(0, targetNodes.size()));
		conex.put(target, sourceComponent);

		if (edges.size() >= getMaxEdges())
			removeConnection(edges, source, target, conex);

		return new Edge(id, type, createProperties(), source, target);
	}

	private void removeConnection(List<Edge> edges, Node source, Node target,
			HashMap<Node, Integer> conex) {

		Edge toRemove = null;

		for (Edge e : edges) {
			if (conex.get(e.getSource()).equals(conex.get(e.getTarget()))) {
				toRemove = e;
				break;
			}
		}
		if (toRemove == null)
			System.out.println("D'oh");
		else
			edges.remove(toRemove);

	}

	/**
	 * Set the differences to create a variation graph
	 * 
	 * @param changeNodes
	 * @param changeEdges
	 * @param changeProperties
	 */
	public void setParameters(int changeNodes, int changeEdges,
			int changeProperties) {
		this.changeNodes = changeNodes;
		this.changeEdges = changeEdges;
		this.changeProperties = changeProperties;
	}

	/**
	 * @return the minNodes
	 */
	public int getMinNodes() {
		return minNodes;
	}

	/**
	 * @param minNodes
	 *            the minNodes to set
	 */
	public void setMinNodes(int minNodes) {
		this.minNodes = minNodes;
	}

	/**
	 * @return the maxNodes
	 */
	public int getMaxNodes() {
		return maxNodes;
	}

	/**
	 * @param maxNodes
	 *            the maxNodes to set
	 */
	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	/**
	 * @return the minEdges
	 */
	public int getMinEdges() {
		return minEdges;
	}

	/**
	 * @param minEdges
	 *            the minEdges to set
	 */
	public void setMinEdges(int minEdges) {
		this.minEdges = minEdges;
	}

	/**
	 * @return the maxEdges
	 */
	public int getMaxEdges() {
		return maxEdges;
	}

	/**
	 * @param maxEdges
	 *            the maxEdges to set
	 */
	public void setMaxEdges(int maxEdges) {
		this.maxEdges = maxEdges;
	}

	/**
	 * @return the minNodeTypes
	 */
	public int getMinNodeTypes() {
		return minNodeTypes;
	}

	/**
	 * @param minNodeTypes
	 *            the minNodeTypes to set
	 */
	public void setMinNodeTypes(int minNodeTypes) {
		this.minNodeTypes = minNodeTypes;
	}

	/**
	 * @return the maxNodeTypes
	 */
	public int getMaxNodeTypes() {
		return maxNodeTypes;
	}

	/**
	 * @param maxNodeTypes
	 *            the maxNodeTypes to set
	 */
	public void setMaxNodeTypes(int maxNodeTypes) {
		this.maxNodeTypes = maxNodeTypes;
	}

	/**
	 * @return the minEdgeTypes
	 */
	public int getMinEdgeTypes() {
		return minEdgeTypes;
	}

	/**
	 * @param minEdgeTypes
	 *            the minEdgeTypes to set
	 */
	public void setMinEdgeTypes(int minEdgeTypes) {
		this.minEdgeTypes = minEdgeTypes;
	}

	/**
	 * @return the maxEdgeTypes
	 */
	public int getMaxEdgeTypes() {
		return maxEdgeTypes;
	}

	/**
	 * @param maxEdgeTypes
	 *            the maxEdgeTypes to set
	 */
	public void setMaxEdgeTypes(int maxEdgeTypes) {
		this.maxEdgeTypes = maxEdgeTypes;
	}

	/**
	 * @return the minProperties
	 */
	public int getMinProperties() {
		return minProperties;
	}

	/**
	 * @param minProperties
	 *            the minProperties to set
	 */
	public void setMinProperties(int minProperties) {
		this.minProperties = minProperties;
	}

	/**
	 * @return the maxProperties
	 */
	public int getMaxProperties() {
		return maxProperties;
	}

	/**
	 * @param maxProperties
	 *            the maxProperties to set
	 */
	public void setMaxProperties(int maxProperties) {
		this.maxProperties = maxProperties;
	}

	/**
	 * @param seed
	 *            the seed to set
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

	/**
	 * Consumes the seed. If it is given one it uses it, otherwise generates a
	 * random one.
	 * 
	 * @return
	 */
	private long consumeSeed() {
		long nextSeed;

		if (seed == -1)
			nextSeed = random.nextLong();
		else
			nextSeed = seed;
		seed = -1;

		System.out.println("seed:" + nextSeed);

		return nextSeed;
	}

	/**
	 * Creates an edge with random data
	 * 
	 * @param nodes
	 * @return
	 */
	private Edge createEdge(List<Node> nodes) {
		String id = String.format("edge%03d", edgeCount++);
		String type = nextLabel("edgeType", minEdgeTypes, maxEdgeTypes);
		Node source = nodes.get(nextInt(0, nodes.size()));
		Node target = nodes.get(nextInt(0, nodes.size()));
		return new Edge(id, type, createProperties(), source, target);
	}

	/**
	 * Creates a node with random data
	 * 
	 * @return
	 */
	private Node createNode() {
		String id = String.format("node%03d", nodeCount++);
		String type = nextLabel("nodeType", minNodeTypes, maxNodeTypes);
		return new Node(id, type, createProperties());
	}

	/**
	 * Creates a property with random data
	 * 
	 * @return
	 */
	private Map<PropertyName, PropertyValue> createProperties() {
		Map<PropertyName, PropertyValue> map = new HashMap<>();
		int nProperties = nextInt(minProperties, maxProperties);
		for (int c = 0; c < nProperties; c++) {
			String name = nextLabel("property", c, c);
			String value = nextValue();
			map.put(new SimplePropertyName(name),
					new SimplePropertyValue(value));
		}
		return map;
	}

	/**
	 * Generates a random string
	 * 
	 * @return
	 */
	private String nextValue() {
		StringBuilder builder = new StringBuilder();
		int length = nextInt(minValueLength, maxValueLength);

		for (int c = 0; c < length; c++)
			builder.append(Character
					.toChars(nextInt(minCodePoint, maxCodePoint)));

		return builder.toString();
	}

	/**
	 * Generates a random label with an int between a and b
	 * 
	 * @param prefix
	 * @param a
	 * @param b
	 * @return
	 */
	private String nextLabel(String prefix, int a, int b) {
		return String.format("%s%03d", prefix, nextInt(a, b));
	}

	/**
	 * Generates a random int between a and b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int nextInt(int a, int b) {
		if (a == b)
			return a;
		return a + random.nextInt(b - a);
	}

	/**
	 * Changes the values for min and max nodes, edges and properties based on
	 * setted parameters
	 * 
	 * @param original
	 */
	public void updateValues(GraphGenerator generator, Graph original) {
		int nNodes = original.getNodes().size();
		int nEdges = original.getEdges().size();
		int nProperties = original.numberOfProperties();

		// update nodes related values
		int minNodes = nNodes - changeNodes;
		int maxNodes = nNodes + changeNodes + 1;
		if (minNodes < 0)
			minNodes = 0;
		generator.setMinNodes(minNodes);
		generator.setMaxNodes(maxNodes);

		// update edges related values
		int minEdges = nEdges - changeEdges;
		int maxEdges = nEdges + changeEdges + 1;
		if (minEdges < 0)
			minEdges = 0;
		generator.setMinEdges(minEdges);
		generator.setMaxEdges(maxEdges);

		// update properties related values
		int minProperties = nProperties - changeProperties;
		int maxProperties = nProperties + changeProperties + 1;

		if (minProperties < 0)
			minProperties = 0;
		generator.setMinProperties(minProperties);
		generator.setMaxProperties(maxProperties);
	}

	/**
	 * Creates a RelatedGraph object which has an original graph and a variation
	 * graph, and the set of differences between them
	 * 
	 * @param original
	 * @return
	 */
	public RelatedGraphs generate(Graph original) {
		GraphGenerator newGenerator = new GraphGenerator(this);
		updateValues(newGenerator, original);
		Graph variation = new Graph(original);
		RelatedGraphs related = new RelatedGraphs(original, variation);
		int nNodesOriginal = original.getNodes().size();
		int nNodesVariation = nextInt(newGenerator.minNodes,
				newGenerator.maxNodes);

		if (nNodesVariation < nNodesOriginal) {
			removeNodes(related, nNodesOriginal, nNodesVariation);
			clearEdges(related);
		}

		else if (nNodesVariation > nNodesOriginal)
			addNodes(related, nNodesOriginal, nNodesVariation);

		int nEdgesVariation = nextInt(newGenerator.minEdges,
				newGenerator.maxEdges);

		// int nEdgesOriginal = variation.getEdges().size();
		int nEdgesOriginal = original.getEdges().size();
	
		if (nEdgesVariation < nEdgesOriginal)
			removeEdges(related, nEdgesOriginal, nEdgesVariation);

		else if (nEdgesVariation > nEdgesOriginal && nNodesVariation > 0)
			addEdges(related, nEdgesOriginal, nEdgesVariation);

		int nPropertiesVariation = nextInt(newGenerator.minProperties,
				newGenerator.maxProperties);
		int nPropertiesOriginal = related.solution.numberOfProperties();
		if (nPropertiesVariation > nPropertiesOriginal)
			addProperties(related, nPropertiesOriginal, nPropertiesVariation);

		else if (nPropertiesVariation < nPropertiesOriginal)
			removeProperties(related, nPropertiesOriginal, nPropertiesVariation);
		related.attempt.computeNodesDegree(original.getNodes(), related.differences);
		related.attempt.shuffle();
		return related;
	}

	/**
	 * Removes a property from variation graph
	 * 
	 * @param related
	 * @param nPropertiesOriginal
	 * @param nPropertiesVariation
	 */
	private void removeProperties(RelatedGraphs related,
			int nPropertiesOriginal, int nPropertiesVariation) {

		int diff = nPropertiesOriginal - nPropertiesVariation;
		int nodesProperties = 0;
		int edgesProperties = 0;

		if (related.attempt.getNodes().size() > 0) {
			if (related.attempt.getEdges().size() > 0) {
				nodesProperties = nextInt(0, diff + 1);
				edgesProperties = diff - nodesProperties;
			}

			else {
				nodesProperties = diff;
			}
		}

		for (int i = 0; i < nodesProperties; i++) {
			int maxSize = Math.min(related.solution.getNodes().size(), related.attempt.getNodes().size());
			int nodeIndex = nextInt(0, maxSize);
			Node node = related.attempt.getNodes().get(nodeIndex);
			int propertyIndex = nextInt(0, node.getProperties().size());
			Iterator<PropertyName> names = node.getProperties().keySet().iterator();

			for (int j = 0; j < node.getProperties().size(); j++) {
				PropertyName name = names.next();
				if (j == propertyIndex) {
					PropertyValue value = node.getProperties().get(name);
					Node auxNode = null;
					for(Node n : related.solution.getNodes()){
						if(n.getId().equals(node.getId())){
							auxNode = n;
							break;
						}
					}
					related.addDifference(new PropertyInsertion(auxNode, name,
							value));
					node.getProperties().remove(name);
					break;
				}
			}
		}

		if (related.attempt.getEdges().size() != 0) {
			for (int i = 0; i < edgesProperties; i++) {
				int maxSize = Math.min(related.solution.getEdges().size(), related.attempt.getEdges().size());
				int edgeIndex = nextInt(0, maxSize);
				Edge edge = related.attempt.getEdges().get(edgeIndex);
				int propertyIndex = nextInt(0, edge.getProperties().size());
				Iterator<PropertyName> names = edge.getProperties().keySet()
						.iterator();

				for (int j = 0; j < edge.getProperties().size(); j++) {
					PropertyName name = names.next();
					if (j == propertyIndex) {
						PropertyValue value = edge.getProperties().get(name);
						Edge auxEdge = null;
						for(Edge e : related.solution.getEdges()){
							if(e.getId().equals(edge.getId())){
								auxEdge = e;
								break;
							}
						}
						related.addDifference(new PropertyInsertion(auxEdge,
								name, value));
						edge.getProperties().remove(name);
						break;
					}
				}
			}
		}

	}

	private void addProperties(RelatedGraphs related, int nPropertiesOriginal,
			int nPropertiesVariation) {
		int diff = nPropertiesVariation - nPropertiesOriginal;
		int nodesProperties = 0;
		int edgesProperties = 0;

		if (related.attempt.getNodes().size() > 0) {
			if (related.attempt.getEdges().size() > 0) {
				nodesProperties = nextInt(0, diff + 1);
				edgesProperties = diff - nodesProperties;
			}

			else {
				nodesProperties = diff;
			}
		}

		for (int i = 0; i < nodesProperties; i++) {
			int maxSize = Math.min(related.solution.getNodes().size(), related.attempt.getNodes().size());
			int nodeIndex = nextInt(0, maxSize);
			Node node = related.attempt.getNodes().get(nodeIndex);
			SimplePropertyName name = new SimplePropertyName(nextValue());
			SimplePropertyValue value = new SimplePropertyValue(nextValue());
			Node auxNode = null;
			for(Node n : related.solution.getNodes()){
				if(n.getId().equals(node.getId())){
					auxNode = n;
					break;
				}
			}
			related.addDifference(new PropertyDeletion(auxNode, name, value));
			node.addProperty(name, value);
		}

		for (int i = 0; i < edgesProperties; i++) {
			int maxSize = Math.min(related.solution.getEdges().size(), related.attempt.getEdges().size());
			int edgeIndex = nextInt(0, maxSize);
			Edge edge = related.attempt.getEdges().get(edgeIndex);
			SimplePropertyName name = new SimplePropertyName(nextValue());
			SimplePropertyValue value = new SimplePropertyValue(nextValue());
			Edge auxEdge = null;
			for(Edge e : related.solution.getEdges()){
				if(e.getId().equals(edge.getId())){
					auxEdge = e;
					break;
				}
			}
			related.addDifference(new PropertyDeletion(auxEdge, name, value));
			edge.addProperty(name, value);
		}
	}

	private void addEdges(RelatedGraphs related, int nEdgesOriginal,
			int nEdgesVariation) {

		for (int i = 0; i < nEdgesVariation - nEdgesOriginal; i++) {
			Edge edge = createEdge(related.attempt.getNodes());
			related.attempt.getEdges().add(edge);
			related.addDifference(new EdgeDeletion(edge));
		}

	}

	private void removeEdges(RelatedGraphs related, int nEdgesOriginal,
			int nEdgesVariation) {
		int limit = nEdgesOriginal - nEdgesVariation;
		if (limit >= related.attempt.getEdges().size()){
			limit = related.attempt.getEdges().size();
		}
		for (int i = 0; i < limit; i++) {
			int index = nextInt(0, related.attempt.getEdges().size());
			Edge edge = related.attempt.getEdges().remove(index);
			related.addDifference(new EdgeInsertion(edge));
		}

	}

	private void clearEdges(RelatedGraphs related) {
		for (Edge edge : related.solution.getEdges()) {
			if (!related.attempt.getNodes().contains(edge.getSource())
					|| !related.attempt.getNodes().contains(edge.getTarget())) {
				related.attempt.getEdges().remove(edge);
				related.addDifference(new EdgeInsertion(edge));
			}
		}

	}

	private void addNodes(RelatedGraphs related, int nNodesOriginal,
			int nNodesVariation) {

		for (int i = 0; i < nNodesVariation - nNodesOriginal; i++) {
			Node node = createNode();
			related.attempt.getNodes().add(node);
			related.addDifference(new NodeDeletion(node));
		}

	}

	private void removeNodes(RelatedGraphs related, int nNodesOriginal,
			int nNodesVariation) {
		for (int i = 0; i < nNodesOriginal - nNodesVariation; i++) {
			int index = nextInt(0, related.attempt.getNodes().size());
			Node node = related.attempt.getNodes().remove(index);
			related.addDifference(new NodeInsertion(node));
		}

	}

}
