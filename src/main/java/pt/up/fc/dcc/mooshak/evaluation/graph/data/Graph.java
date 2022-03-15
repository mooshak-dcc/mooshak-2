package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;

/**
 * Graph is a class to represent a diagram. It groups the information in a set
 * of nodes and a set of edges.
 * 
 * @author Ruben
 *
 */
public class Graph {
	private List<Node> nodes;
	private List<Edge> edges;
	private long maxNodesValue = 0;
	private long maxEdgesValue = 0;
	private int nodesProperties = 0;
	

	public Graph() {
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
	}

	public Graph(Graph graph) {
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.maxNodesValue = graph.getMaxNodesValue();
		this.maxEdgesValue = graph.getMaxEdgesValue();
		computeNodesDegree();
		deepCopy(graph);
	}

	public Graph(List<Node> nodes, List<Edge> edges, boolean init) {
		this.nodes = nodes;
		this.edges = edges;
		computeNodesDegree();
	}

	public Graph(List<Node> nodes, List<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
		computeNodesDegree();
	}

	/**
	 * Gets the max value this graph can get for nodes and for edges.
	 * 
	 * @param configs
	 */
	void getMaxValues(Configs configs) {
		for (Node node : nodes) {
			node.computeMaxValue(configs);
			maxNodesValue += node.getValue();
		}

		for (Edge edge : edges) {
			edge.computeMaxValue(configs);
			maxEdgesValue += edge.getValue();
		}
	}

	/**
	 * Computes the max value this graph can get for nodes and for edges.
	 * 
	 * @param configs
	 */
	public void computeMaxValues(Configs configs) {
		computeNodesDegree();
		maxNodesValue = 0;
		for (Node node : nodes) {
			maxNodesValue += node.computeMaxValue(configs);
			nodesProperties += node.getProperties().size();
		}

		maxEdgesValue = 0;
		for (Edge edge : edges){
			maxEdgesValue += edge.computeMaxValue(configs);
		}
	}

	/**
	 * @return the nodes
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the edges
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * @param edges
	 *            the edges to set
	 */
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	/**
	 * @return the maxNodesValue
	 */
	public long getMaxNodesValue() {
		return maxNodesValue;
	}

	/**
	 * @return the maxEdgesValue
	 */
	public long getMaxEdgesValue() {
		return maxEdgesValue;
	}

	/**
	 * @return the nodesProperties
	 */
	public int getNodesProperties() {
		return nodesProperties;
	}

	/**
	 * It shuffles the set of nodes and the set of edges
	 */
	public void shuffle() {
		Collections.shuffle(nodes);
		Collections.shuffle(edges);
	}

	/**
	 * Creates a copy of the graph given as param to this one
	 * 
	 * @param graph
	 */
	public void deepCopy(Graph graph) {

		for (Node node : graph.nodes)
			this.nodes.add(node.deepCopy());

		for (Edge edge : graph.edges)
			this.edges.add(edge.deepCopy(this.nodes));
	}

	/**
	 * Computes the degrees of the nodes based in the number and type of
	 * connections
	 */
	public void computeNodesDegree() {
		for (Node node : this.nodes) {
			node.setInDegree(new HashMap<String, Integer>());
			node.setOutDegree(new HashMap<String, Integer>());
		}

		for (Edge edge : this.edges) {
			String type = edge.getType();
			Node source = edge.getSource();
			Node target = edge.getTarget();
			int s = 1;
			if (source.getIndistincts() > 1) {
				s = edge.indistincts / source.indistincts;
			} else {
				s = edge.indistincts;
			}

			int t = 1;
			if (target.getIndistincts() > 1) {
				t = edge.indistincts / target.indistincts;
			} else {
				t = edge.indistincts;
			}
			if (source.outDegree.containsKey(type)) {
				int currentDegree = source.outDegree.get(type);
				source.outDegree.put(type, currentDegree + s);

			} else {
				source.outDegree.put(type, s);
			}

			if (target.inDegree.containsKey(type)) {
				int currentDegree = target.inDegree.get(type);
				target.inDegree.put(type, currentDegree + t);

			} else {
				target.inDegree.put(type, t);
			}
		}

	}

	public void computeNodesDegree(List<Node> nodes, Set<GraphDifference> diff) {
		computeNodesDegree();

		for (Node node : this.nodes) {
			for (Node n : nodes) {
				if (node.getId().equals(n.getId())) {
					GradeWithDifferences grade = n.compareDegrees(node, Configs.getDefaultConfigs());
					diff.addAll(grade.getDifferences());
					break;
				}
			}
		}

	}

	/**
	 * It sums the number of properties in all graph's objects.
	 * @return
	 */
	public int numberOfProperties() {
		int count = 0;
		for (Node node : nodes)
			count += node.properties.size();

		for (Edge edge : edges)
			count += edge.properties.size();

		return count;
	}

	/**
	 * Converts the graph's information to string
	 */
	public String toString() {
		String phrase = "Number of nodes : " + nodes.size() + "\n" + "Number of edges : " + edges.size() + "\n\n";

		for (Node n : nodes)
			phrase += n.toString() + "\n";
		phrase += "\n";

		for (Edge e : edges)
			phrase += e.toString() + "\n";

		return phrase;
	}

	/**
	 * Graph construction through primitive file
	 * 
	 * @param graph
	 * @throws FileNotFoundException
	 */
	public Graph(String name) throws FileNotFoundException {
		this.nodes = new ArrayList<Node>();
		this.edges = new ArrayList<Edge>();

		String fileName = "inputs" + File.separator + name;
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);

		while (scanner.hasNext()) {
			readObjects(scanner);
		}

		scanner.close();
	}

	/**
	 * Read primitive file and creates graph
	 * @param scanner
	 */
	void readObjects(Scanner scanner) {
		GObject object = new GObject(scanner.next(), scanner.next());

		int nProperties = scanner.nextInt();
		scanner.nextLine();

		for (int i = 0; i < nProperties; i++)
			readProperties(scanner, object);

		String diff = scanner.nextLine();
		String[] parts = diff.split(" ");

		if (parts.length == 1) {
			this.nodes.add(new Node(object));
		}

		else if (parts.length == 3) {
			this.edges.add(new Edge(object, findNode(parts[1]), findNode(parts[2])));
		}
	}

	/**
	 * Returns the node with the given id
	 * @param id
	 * @return
	 */
	Node findNode(String id) {
		for (Node n : nodes)
			if (n.hasId(id))
				return n;
		return null;
	}

	/**
	 * 
	 * @param scanner
	 * @param object
	 */
	void readProperties(Scanner scanner, GObject object) {
		int compositeProperties;
		SimplePropertyName spn;
		SimplePropertyValue spv;
		CompositePropertyName cpn;
		CompositePropertyValue cpv;
		if (scanner.next().equals("S")) {
			spn = new SimplePropertyName(scanner.next());
			spv = new SimplePropertyValue(scanner.next());
			scanner.nextLine();
			object.addProperty(spn, spv);
		}

		else {
			compositeProperties = scanner.nextInt();
			cpn = new CompositePropertyName(scanner.next(), scanner.next());
			cpv = new CompositePropertyValue();
			scanner.nextLine();
			for (int c = 0; c < compositeProperties; c++) {
				cpv.add(scanner.next(), scanner.next());
				scanner.nextLine();
			}
			object.addProperty(cpn, cpv);
		}
	}

	/**
	 * It prints the information about the graph
	 */
	public void print() {
		String text = "Nodes: [";
		for (Node n : nodes) {
			text += n.name + ",";
		}

		text = text.substring(0, text.length() - 1) + "]\nEdges:\n";

		for (Edge e : edges) {
			String name1 = e.getSource().name;
			if (name1.equals(""))
				name1 = e.getSource().type;

			String name2 = e.getTarget().name;
			if (name2.equals(""))
				name2 = e.getTarget().type;
			text += name1 + "->" + name2 + "\n";
		}

		System.out.print(text);
	}
	
	
	public void bfs(){
		if(nodes.isEmpty() || edges.isEmpty()) return ;
		
		List<Edge> edgesAdJ=this.edges;
		List<Node> nodesWhite = this.nodes;
		List<Node> nodesBlack = new LinkedList<Node>();
		List<Node> nodesGray = new LinkedList<Node>();
		
		nodesGray.add(nodesWhite.remove(0));
		while(!nodesGray.isEmpty()){
			Node n = nodesGray.remove(0);
			for(Edge e :edgesAdJ){
				if(e.getSource().equals(n) ){
					if(!nodesBlack.contains(e.getTarget()))
						nodesGray.add(e.getTarget());
						//edgesAdJ.remove(e);
				}
				
				else if(e.getTarget().equals(n)){
					if(!nodesBlack.contains(e.getSource()))
						nodesGray.add(e.getSource());
						//edgesAdJ.remove(e);
				}
			}
			
			nodesBlack.add(n);
			nodesWhite.remove(n);
			
			
		}
		System.out.println(nodesWhite.size() +"  nodewhite");
		System.out.println(nodesGray.size() +"  nodegray");
		System.out.println(nodesBlack.size() + " nodeback");
		
		
	}

}
