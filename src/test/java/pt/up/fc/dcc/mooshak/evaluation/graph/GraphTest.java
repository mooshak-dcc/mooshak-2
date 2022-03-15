package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;

public class GraphTest {
	/**
	 * Testing the graph construction through string
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testScanner() throws FileNotFoundException {
		Graph graph = new Graph("input.txt");
		System.out.print(graph.toString());
		// Testing graph size
		assertEquals("Graph has 2 nodes", 3, graph.getNodes().size());
		assertEquals("Graph has 2 edges", 2, graph.getEdges().size());

		// Testing nodes types
		assertTrue("Node 0 has type Class",
				graph.getNodes().get(0).getType().equals("Class"));
		assertTrue("Node 1 has type Interface",
				graph.getNodes().get(1).getType().equals("Interface"));

		// Testing edges types
		assertTrue("Edge 0 has type Association",
				graph.getEdges().get(0).getType().equals("Association"));
		assertTrue("Edge 1 has type Generalization",
				graph.getEdges().get(1).getType().equals("Generalization"));

		// Testing number of properties in each node
		assertEquals("Node 0 has 2 properties",
				graph.getNodes().get(0).getProperties().size(), 2);
		assertEquals("Node 1 has 1 property",
				graph.getNodes().get(1).getProperties().size(), 1);

		// Testing edges sources
		assertEquals(
				"Checking if the source of the first edge is the first node",
				graph.getEdges().get(0).getSource(), graph.getNodes().get(0));
		assertEquals(
				"Checking if the source of the second edge is the first node",
				graph.getEdges().get(1).getSource(), graph.getNodes().get(0));

		// Testing edges targets
		assertEquals(
				"Checking if the target of the first edge is the second node",
				graph.getEdges().get(0).getTarget(), graph.getNodes().get(1));
		assertEquals(
				"Checking if the target of the second edge is the first node",
				graph.getEdges().get(1).getTarget(), graph.getNodes().get(0));
	}

}
