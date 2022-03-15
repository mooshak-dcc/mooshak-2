package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.GraphGenerator;
import pt.up.fc.dcc.mooshak.evaluation.graph.RelatedGraphs;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;

public class CoreMapperTest {

	private static final String SOME_TYPE = "T";
	private static final String OTHER_TYPE = "U";

	int nodeCount = 0;
	int edgeCount = 0;

	private Node makeNode(String type) {
		return new Node("node" + (nodeCount++), SOME_TYPE);
	}

	private Edge makeEdge(Node a, Node b, String type) {
		return new Edge("edge" + (edgeCount++), type,
				new HashMap<PropertyName, PropertyValue>(), a, b);
	}

	private Graph makeGraphOneNode() {
		return new Graph(Arrays.asList(makeNode(SOME_TYPE)),
				new ArrayList<Edge>());
	}

	private Graph makeGraphTwoNodes() {
		Node a = makeNode(SOME_TYPE);
		Node b = makeNode(SOME_TYPE);
		Edge e = makeEdge(a, b, SOME_TYPE);

		return new Graph(Arrays.asList(a, b), Arrays.asList(e));
	}

	private Graph makeGraphThreeNodes() {
		Node a = makeNode(SOME_TYPE);
		Node b = makeNode(SOME_TYPE);
		Node c = makeNode(OTHER_TYPE);
		Edge e = makeEdge(a, b, SOME_TYPE);
		Edge f = makeEdge(a, c, SOME_TYPE);

		return new Graph(Arrays.asList(a, b, c), Arrays.asList(e, f));
	}

	@Test
	public void testGraphOneNode() {
		Graph solution = makeGraphOneNode();
		Graph attempt = new Graph(solution);

		attempt.shuffle();
		CoreMapper core = new CoreMapper(solution, attempt
				,Configs.getDefaultConfigs());
		int count = 0;

		for (RelatedGraphs related : core) {
			count++;
			assertEquals(Collections.EMPTY_SET, related.differences);
			assertTrue(related.getSolution().equals(solution));
			assertTrue(related.getAttempt().equals(attempt));
		}

		assertEquals(1, count);
	}

	@Test
	public void testGraphTwoNodesSameType() {
		Graph solution = makeGraphTwoNodes();
		Graph attempt = new Graph(solution);

		attempt.shuffle();
		CoreMapper core = new CoreMapper(solution, attempt
				,Configs.getDefaultConfigs());
		int count = 0;

		for (RelatedGraphs related : core) {
			count++;
			assertEquals(Collections.EMPTY_SET, related.differences);
			assertTrue(related.getSolution().equals(solution));
			assertTrue(related.getAttempt().equals(attempt));
		}

		assertEquals(1, count);
	}

	@Test
	public void testGraphOneAndTwoNodesSameType() {
		Graph solution = makeGraphTwoNodes();
		Graph attempt = makeGraphOneNode();

		attempt.shuffle();
		CoreMapper core = new CoreMapper(solution, attempt
				,Configs.getDefaultConfigs());
		int count = 0;

		for (RelatedGraphs related : core) {
			count++;
			assertEquals(2, related.differences.size());
			System.out.println(core.currentBest);
		}

		assertEquals(2, count);
	}

	@Test
	public void testGraphsTwoAndThreeNodes() {
		Graph solution = makeGraphThreeNodes();
		Graph attempt = makeGraphTwoNodes();

		attempt.shuffle();
		CoreMapper core = new CoreMapper(solution, attempt,
				Configs.getDefaultConfigs());
		int count = 0;

		for (@SuppressWarnings("unused") RelatedGraphs related : core) {
			count++;
			System.out.println(core.currentBest);
		}

		assertEquals(3, count);
	}

	@Test
	public void testReduceGraphs() {
		GraphGenerator generator = new GraphGenerator();
		// generator.setSeed(-628302426554189703L);
		generator.setParameters(1, 0, 0);
		int nTests = 100;
		for (int i = 0; i < nTests; i++) {
			Graph solution = generator.generate();
			Graph attempt = generator.generate(solution).getAttempt();

			CoreMapper core = new CoreMapper(solution, attempt
					,Configs.getDefaultConfigs());
			int nodesDiff = solution.getNodes().size()
					- attempt.getNodes().size();
			System.out.println(solution.getNodes().size() + "|"
					+ attempt.getNodes().size());

			int count = 0;
			for (@SuppressWarnings("unused") RelatedGraphs pair : core) {
				count++;
			}

			if (solution.getNodes().size() > attempt.getNodes().size()) {
				assertEquals("All graphs were generated", fact(solution
						.getNodes().size())
						/ (fact(attempt.getNodes().size()) * fact(nodesDiff)),
						count);
			}

			if (solution.getNodes().size() < attempt.getNodes().size()) {
				assertEquals("All graphs were generated", fact(attempt
						.getNodes().size())
						/ (fact(solution.getNodes().size()) * fact((-1)
								* nodesDiff)), count);
			}
		}
	}

	private long fact(int n) {
		long fact = 1;
		for (int i = 2; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}
}
