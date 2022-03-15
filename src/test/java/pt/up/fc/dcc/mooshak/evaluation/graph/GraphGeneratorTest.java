package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class GraphGeneratorTest {

	@Test
	public void testSetters() {
		GraphGenerator g = new GraphGenerator();
		int numberOfTests = 1000;
		
		int minNodes = 1;
		int maxNodes = 7;
		g.setMinNodes(minNodes);
		g.setMaxNodes(maxNodes);
				
		int minProperties = 5;
		g.setMinProperties(minProperties);
		int maxProperties = 5;
		g.setMaxProperties(maxProperties);
		
		int maxNodeTypes = 5;
		
		g.setSeed(2785436205623037834L);
		for (int i = 0; i < numberOfTests; i++) {
			Graph graph = g.generate();
			assertTrue("All graphs have at least minNodes", graph.getNodes().size() >= minNodes);			
			assertTrue("All graphs have at maximum maxNodes", graph.getNodes().size() <= maxNodes);
			
			assertTrue("All graphs have at least minEdges", graph.getEdges().size() >= g.getMinEdges());
			assertTrue("All graphs have at maximum maxEdges", graph.getEdges().size() <= g.getMaxEdges());
		
			HashSet<String> nodeTypes = new HashSet<>();
			for(Node n : graph.getNodes()){
				assertTrue("All nodes have at maximum maxProperties", n.getProperties().size() <= maxProperties);
				assertTrue("All nodes have at least minProperties", n.getProperties().size() >= minProperties);
				nodeTypes.add(n.getType());
			}
			
			assertTrue("There are at maximum maxTypes types", nodeTypes.size() <= maxNodeTypes);
			
			for(Edge e : graph.getEdges()){
				assertTrue("All edges have at maximum maxProperties", e.getProperties().size() <= maxProperties);
				assertTrue("All edges have at least minProperties", e.getProperties().size() >= minProperties);	
			}
		}
	}
	
	@Test
	public void testGraphConnections() {
		GraphGenerator g = new GraphGenerator();
		
		int numberOfTests = 1000;
		//g.setSeed(1438546381526296700L);
		for (int i = 0; i < numberOfTests; i++) {
			Graph graph = g.generate();
			HashMap<Node, Integer> conex = new HashMap<>();
			for(int j = 0; j < graph.getNodes().size(); j++)
				conex.put(graph.getNodes().get(j),j);
			
			for(Edge edge : graph.getEdges()){
				int sourceComponent = conex.get(edge.getSource());
				int targetComponent = conex.get(edge.getTarget());
				
				for(Node node : graph.getNodes()){
					if(conex.get(node) == targetComponent)
						conex.put(node, sourceComponent);
				}
			}
			assertTrue("The graph is conex",
						new HashSet<>(conex.values()).size() == 1);
			
		}

	}

}
