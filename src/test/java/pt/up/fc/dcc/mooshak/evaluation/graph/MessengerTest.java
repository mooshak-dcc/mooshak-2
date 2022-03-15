package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class MessengerTest {

	@Test
	public void test() {
		Node node1 = new Node("tipo1");
		Node node2 = new Node("tipo2");
		Node node3 = node1.deepCopy();
		
		SourceTargetPair pair1 = new SourceTargetPair(node1, node2);
		SourceTargetPair pair2 = new SourceTargetPair(node2, node1);
		SourceTargetPair pair3 = new SourceTargetPair(node1, node1);
		SourceTargetPair pair4 = new SourceTargetPair(node1, null);
		SourceTargetPair pair5 = new SourceTargetPair(node3, node2);
		
		assertTrue(pair1.equals(pair1));
		assertTrue(pair1.equals(pair2));
		assertFalse(pair1.equals(pair3));
		assertFalse(pair1.equals(pair4));
		assertTrue(pair2.equals(pair1));
		assertTrue(pair4.equals(pair4));
		assertTrue(pair5.equals(pair2));
		
	}

}
