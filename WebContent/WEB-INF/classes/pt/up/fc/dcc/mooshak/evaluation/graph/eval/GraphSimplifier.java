package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class GraphSimplifier {
	static Configs configs;

	@SuppressWarnings("static-access")
	GraphSimplifier(Configs configs) {
		this.configs = configs;
	}

	int countComponents;

	public Graph reduceGraph(Graph graph) {
		Map<Node, Integer> nodesComponent = new HashMap<>();
		Map<Integer, List<Edge>> edgesComponent = new HashMap<>();
		int limit = graph.getNodes().size();
		countComponents = 0;
		for (int i = 0; i < limit; i++) {
			for (int j = i + 1; j < limit; j++) {
				Node node1 = graph.getNodes().get(i);
				Node node2 = graph.getNodes().get(j);
				int comparisonValue = node1.compareNode(node2, configs)
						.getGrade();
				if (comparisonValue == node1.getValue()
						&& comparisonValue == node2.getValue()) {
					if (isIndistinct(graph, node1, node2))
						addNodesToComponent(nodesComponent, edgesComponent,
								node1, node2);
				}
			}
		}

		return createNewGraph(graph, nodesComponent, edgesComponent);
	}

	private Graph createNewGraph(Graph graph,
			Map<Node, Integer> nodesComponent, Map<Integer, List<Edge>> edgesComponent) {
		
		Map<Integer,Node> newGroupNodes = new HashMap<>();
		List<Node> simpleNodes = new ArrayList<>();
		List<Integer> componentsVisited = new ArrayList<>();
		for(Node node : graph.getNodes()){
			if(nodesComponent.containsKey(node)){
				int component = nodesComponent.get(node);
				if(componentsVisited.contains(component)){
					Node newNode = newGroupNodes.get(component);
					newNode.setId(newNode.getId()+node.getId());
					newNode.incrementIndistinct();
				}
				else{
					Node newNode = new Node(node);
					componentsVisited.add(component);
					newGroupNodes.put(component, newNode);
				}
			}
			else{
				simpleNodes.add(node);
			}
		}
		
		List<Node> graphNodes = new ArrayList<>();
		graphNodes.addAll(simpleNodes);
		graphNodes.addAll(newGroupNodes.values());
		
		List<Edge> graphEdges = new ArrayList<>();
		
		for(int component : edgesComponent.keySet()){
			List<Edge> edges = edgesComponent.get(component);
			for(Edge edge : edges){
				Node target = edge.getTarget();
				Node source = edge.getSource();
								
				if(nodesComponent.containsKey(target) && nodesComponent.get(target) == component){
					edge.setTarget(newGroupNodes.get(component));
					continue;
				}
				
				else if(nodesComponent.containsKey(source) &&
						nodesComponent.get(edge.getSource()) == component)
					edge.setSource(newGroupNodes.get(component));
			}
			
			for(int i = 0; i < edges.size(); i++){
				Edge edge1 = edges.get(i);
				String id = edge1.getId();
				for(int j = i; j < edges.size(); j++){
					Edge edge2 = edges.get(j);
					if(!(edge1.getType().equals(edge2.getType()) &&
							edge1.getTarget().equals(edge2.getTarget()) &&
							edge1.getSource().equals(edge2.getSource())))
						continue;
					edge2.setId(id);
				}
			}
			
			Set<Edge> edgeSet = new HashSet<>(edges);
			for (Edge edge : edgeSet) {
				edge.setIndistincts(edge.getSource().getIndistincts() * edge.getTarget().getIndistincts());
			}
			
			
			Iterator<Edge> edgeIterator = edgeSet.iterator();
			while(edgeIterator.hasNext()){
				Edge edge = edgeIterator.next();
				if(graphNodes.contains(edge.getSource()) && graphNodes.contains(edge.getTarget()))
					continue;
				edgeIterator.remove();
			}
			graphEdges.addAll(edgeSet);
			
		}

		for(Edge edge : graph.getEdges()){
			if(simpleNodes.contains(edge.getSource()) && simpleNodes.contains(edge.getTarget()))
				graphEdges.add(edge);
		}
		
		return new Graph(graphNodes, graphEdges);
	}

	private void addNodesToComponent(Map<Node, Integer> nodesComponent,
			Map<Integer, List<Edge>> edgesComponent, Node node1, Node node2) {
		if (nodesComponent.containsKey(node1)) {
			if (!nodesComponent.containsKey(node2)) {
				nodesComponent.put(node2, nodesComponent.get(node1));
			} else {
				changeComponentNumber(nodesComponent, edgesComponent, node1,
						node2);
				countComponents++;
			}
		}

		else if (nodesComponent.containsKey(node2)) {
			if (!nodesComponent.containsKey(node1)) {
				nodesComponent.put(node1, nodesComponent.get(node2));
			} else {
				changeComponentNumber(nodesComponent, edgesComponent, node1,
						node2);
				countComponents++;
			}
		}

		else {
			nodesComponent.put(node1, countComponents);
			nodesComponent.put(node2, countComponents);
			edgesComponent.put(countComponents, edgesToAdd);
			countComponents++;
		}

	}

	private void changeComponentNumber(Map<Node, Integer> nodesComponent,
			Map<Integer, List<Edge>> edgesComponent, Node node1, Node node2) {
		int node1Component = nodesComponent.get(node1);
		int node2Component = nodesComponent.get(node2);
		for (Node node : nodesComponent.keySet()) {
			if (nodesComponent.get(node) == node1Component
					|| nodesComponent.get(node) == node2Component)
				nodesComponent.put(node, countComponents);
		}

		List<Edge> edges = edgesComponent.get(node1Component);
		edgesComponent.put(countComponents, edges);
		edgesComponent.remove(node1Component);
		edgesComponent.remove(node2Component);
	}

	static List<Edge> edgesToAdd;

	static public boolean isIndistinct(Graph graph, Node node1, Node node2) {
		edgesToAdd = new ArrayList<>();
		int limit = graph.getEdges().size();
		int maxEdges = 0;
		int equalConnections = 0;
		for (String type : node1.getInDegree().keySet()) {
			maxEdges += node1.getInDegree().get(type);
		}

		for (String type : node1.getOutDegree().keySet()) {
			maxEdges += node1.getOutDegree().get(type);
		}

		List<Edge> choosenEdges = new ArrayList<>();
		for (int i = 0; i < limit; i++) {
			for (int j = i + 1; j < limit; j++) {
				Edge edge1 = graph.getEdges().get(i);
				Edge edge2 = graph.getEdges().get(j);
				if(choosenEdges.contains(edge1)) break;
				if(choosenEdges.contains(edge2)) continue;
				
				if (checkConnection(node1, node2, edge1, edge2)) {
					equalConnections++;
					choosenEdges.add(edge1);
					choosenEdges.add(edge2);
					edgesToAdd.add(edge2);
					edgesToAdd.add(edge1);
					break;
				}
			}
		}

		if (equalConnections == maxEdges)
			return true;
		else
			return false;
	}

	private static boolean checkConnection(Node node1, Node node2, Edge edge1,
			Edge edge2) {
		int comparisonValue = edge1.compareEdge(edge2, configs,null ).getGrade();
		
		
		if (comparisonValue != edge1.getValue()
				|| comparisonValue != edge2.getValue())
			return false;

		Node target1 = edge1.getTarget();
		Node target2 = edge2.getTarget();

		Node source1 = edge1.getSource();
		Node source2 = edge2.getSource();

		if (!target1.equals(node1) && !target2.equals(node1)
				&& !source1.equals(node1) && !source2.equals(node1))
			return false;

		if (!target1.equals(node2) && !target2.equals(node2)
				&& !source1.equals(node2) && !source2.equals(node2))
			return false;

		if (target1.equals(node1)) {
			if (!target2.equals(node2))
				return false;
			else if (source1.equals(source2))
				return true;

			else
				return false;

		}

		else if (target2.equals(node1)) {
			if (!target1.equals(node2))
				return false;
			else if (source1.equals(source2))
				return true;
			else
				return false;
		}

		else if (source1.equals(node1)) {
			if (!source2.equals(node2))
				return false;
			else if (target1.equals(target2))
				return true;
			else
				return false;
		}

		else if (source2.equals(node1)) {
			if (!source1.equals(node2))
				return false;
			else if (target1.equals(target2))
				return true;
			else
				return false;
		}

		return false;
	}
}
