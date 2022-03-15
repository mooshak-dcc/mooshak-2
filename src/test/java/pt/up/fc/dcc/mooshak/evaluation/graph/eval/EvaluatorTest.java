package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.GraphEvalException;
import pt.up.fc.dcc.mooshak.evaluation.graph.GraphGenerator;
import pt.up.fc.dcc.mooshak.evaluation.graph.Parser;
import pt.up.fc.dcc.mooshak.evaluation.graph.RelatedGraphs;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentConnection;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentType;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.InDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.OutDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyInsertion;

public class EvaluatorTest {
	Parser parser;
	String solutionPath = "diagrams/en/classDiagram1.dia";
	String path = "diagrams/en/classDiagram1_error";

	@Before
	public void setup() {
		parser = new Parser();
	}

	@Test
	public void testPerms() throws FileNotFoundException {
		Graph solution = new Graph("input.txt");
		Graph attempt = new Graph("input.txt");
		List<List<Integer>> list = new Evaluator(solution, attempt)
				.generatePermutations(3);

		assertEquals("All the permutations are generated", 3 * 2, list.size());
	}

	@Test
	public void testEqualGraphs() throws FileNotFoundException {
		Graph solution = new Graph("input.txt");
		Graph attempt = new Graph("input.txt");
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("There graphs are equal", 100, eval.evaluate().getGrade(),
				0);

	}

	@Test
	public void testOneDifferentNodeProperty() throws FileNotFoundException {
		Graph solution = new Graph("input.txt");
		Graph attempt = new Graph("input1.txt");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		assertEquals(
				"There is one property different in one of the nodes",
				computeGradeByDifferences(solution, attempt,
						e.getDifferences(), 70), e.getGrade(), 0);
	}

	@Test
	public void testMissingAllEdges() throws FileNotFoundException {
		Graph solution = new Graph("input1.txt");
		Graph attempt = new Graph("input3.txt");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		assertEquals(
				"All edges missing",
				computeGradeByDifferences(solution, attempt,
						e.getDifferences(), 70), e.getGrade(), 0);
	}

	@Test
	public void testOneEdgeMore() throws FileNotFoundException {
		Graph solution = new Graph("input2.txt");
		Graph attempt = new Graph("input1.txt");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		assertEquals(
				"There is one more edge",
				computeGradeByDifferences(solution, attempt,
						e.getDifferences(), 70), e.getGrade(), 0);
	}

	@Test
	public void testBothGraphsWithoutEdges() throws FileNotFoundException {
		Graph solution = new Graph("input3.txt");
		Graph attempt = new Graph("input3.txt");
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("There are no edges", 100, eval.evaluate().getGrade(), 0);
	}

	@Test
	public void testGraphsFromQuestions() throws FileNotFoundException,
			GraphEvalException {
		Graph solution = parser.parse(solutionPath);
		Graph attempt = parser.parse(solutionPath);
		Evaluator eval;
		long time1, time2;

		time1 = System.currentTimeMillis();
		eval = new Evaluator(solution, attempt);
		time1 = System.currentTimeMillis() - time1;

		assertEquals("Solution is equal to attempt", 100, eval.evaluate()
				.getGrade(), 0);
		System.out.println("time:" + time1);

		time2 = System.currentTimeMillis();
		eval = new Evaluator(solution, attempt);
		time2 = System.currentTimeMillis() - time2;

		assertEquals("Solution is equal to attempt", 100, eval.evaluate()
				.getGrade(), 0);
	}

	@Test
	public void testGraphsFromQuestionsI() throws FileNotFoundException,
			GraphEvalException {
		Graph solution = parser.parse(solutionPath);
		Graph attempt = parser.parse(path + "1.dia");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		System.out.println(e.getDifferences());
		assertEquals(
				"Wrong answer 1",
				computeGradeByDifferences(solution, attempt,
						e.getDifferences(), 70), e.getGrade(), 0);
	}

	@Test
	public void testGraphsFromQuestionsII() throws FileNotFoundException,
			GraphEvalException {
		Graph solution = parser.parse(solutionPath);
		Graph attempt = parser.parse(path + "2.dia");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		assertEquals(
				"Wrong answer 2",
				computeGradeByDifferences(solution, attempt,
						e.getDifferences(), 70), e.getGrade(), 0);
	}

	@Test
	public void testGraphsFromQuestionsIII() throws FileNotFoundException,
			GraphEvalException {
		Graph solution = parser.parse(solutionPath);
		Graph attempt = parser.parse(path + "3.dia");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		assertEquals("Wrong answer 3", computeGradeByDifferences(solution, attempt,
				e.getDifferences(), 70), e.getGrade(), 0);
	}

	@Test
	public void testGraphsFromQuestionsIV() throws FileNotFoundException,
			GraphEvalException {
		Graph solution = parser.parse(solutionPath);
		Graph attempt = parser.parse(path + "4.dia");
		Evaluator eval = new Evaluator(solution, attempt);
		Evaluation e = eval.evaluate();
		assertEquals("Wrong answer 4", computeGradeByDifferences(solution, attempt,
				e.getDifferences(), 70), e.getGrade(), 0);
	}

	long maxNodesGrade, maxEdgesGrade, totalNodePenalty;

	@Test
	public void testPruning() {
		GraphGenerator g = new GraphGenerator();
		for (int i = 0; i < 50; i++) {
			Graph solution = g.generate();
			solution.computeMaxValues(Configs.getDefaultConfigs());
			g.setParameters(2, 0, 0);
			RelatedGraphs related = g.generate(solution);
			related.getAttempt().computeMaxValues(Configs.getDefaultConfigs());
			if (solution.getNodes().size() < related.getAttempt().getNodes()
					.size()) {
				maxNodesGrade = solution.getMaxNodesValue();
			} else {
				maxNodesGrade = related.getAttempt().getMaxNodesValue();
			}

			if (solution.getEdges().size() < related.getAttempt().getEdges()
					.size()) {
				maxEdgesGrade = solution.getMaxEdgesValue();
			} else {
				maxEdgesGrade = related.getAttempt().getMaxEdgesValue();
			}

			CoreMapper core = new CoreMapper(solution, related.getAttempt(),
					Configs.getDefaultConfigs());
			double oldMax = Double.MAX_VALUE;
			for (RelatedGraphs pair : core) {
				int nodesPenalty = computeNodePenalty(pair.differences);

				double maxGrade = computeCurrentMaxGrade(solution,
						nodesPenalty, 0);

				assertTrue(maxGrade <= oldMax);
				oldMax = maxGrade;

			}
		}
	}

	private double computeCurrentMaxGrade(Graph solution, long nodesPenalty,
			long edgesPenalty) {
		Configs configs = Configs.getDefaultConfigs();
		double nodesGrade = maxNodesGrade - nodesPenalty;
		if (nodesGrade < 0)
			nodesGrade = 0;
		double edgesGrade = maxEdgesGrade - edgesPenalty;
		if (edgesGrade < 0)
			edgesGrade = 0;

		nodesGrade = nodesGrade * configs.getNodeFactor()
				/ (double) solution.getMaxNodesValue();
		edgesGrade = edgesGrade * configs.getEdgeFactor()
				/ (double) solution.getMaxEdgesValue();

		double grade = nodesGrade + edgesGrade;
		return grade;
	}

	private int computeNodePenalty(Set<GraphDifference> differences) {
		int extraNodes = 0;
		int penalty = 0;
		totalNodePenalty = 0;
		for (GraphDifference diff : differences) {
			if (diff.isNodeDeletion())
				extraNodes++;

			else if (diff.isNodeInsertion()) {
				// totalNodePenalty += ((NodeInsertion) diff).getInsertion()
				// .getValue();
			}
		}

		penalty += extraNodes * 10; // 5 penalty pts for extra node
		totalNodePenalty += penalty;
		return penalty;
	}

	private double computeGradeByDifferences(Graph solution, Graph attempt,
			Set<GraphDifference> differences, int nodesFactor) {

		double nodesGrade = 0;
		double edgesGrade = 0;

		Map<Node, Integer> nodesGradeMap = new HashMap<>();
		for (Node n : solution.getNodes())
			nodesGradeMap.put(n, n.getValue());

		Map<Edge, Integer> edgesGradeMap = new HashMap<>();
		for (Edge n : solution.getEdges())
			edgesGradeMap.put(n, n.getValue());

		for (GraphDifference diff : differences) {
			if (diff.isNodeInsertion()) {
				Node node = ((NodeInsertion) diff).getInsertion();
				nodesGradeMap.put(node, 0);

			} else if (diff.isNodeDeletion()) {
				nodesGrade -= 10;
			}

			else if (diff.isEdgeInsertion()) {
				Edge edge = ((EdgeInsertion) diff).getInsertion();
				edgesGradeMap.put(edge, 0);

			} else if (diff.isEdgeDeletion()) {
				edgesGrade -= 10;
			}

			else if (diff.isPropertyInsertion()) {
				PropertyInsertion insertion = (PropertyInsertion) diff;
				int penalty = 2;

				if (!insertion.getPropertyName().isSimple())
					penalty = ((CompositePropertyValue) insertion
							.getPropertyValue()).getValue().size() * 2;

				if (insertion.getObject().isNode()) {
					Node node = (Node) insertion.getObject();
					int currentNodeGrade = nodesGradeMap.get(node);
					nodesGradeMap.put(node, currentNodeGrade -= penalty);
				}

				else {
					Edge edge = (Edge) insertion.getObject();
					int currentEdgeGrade = edgesGradeMap.get(edge);
					edgesGradeMap.put(edge, currentEdgeGrade -= penalty);
				}
			}

			else if (diff.isPropertyDeletion()) {
				PropertyDeletion deletion = (PropertyDeletion) diff;
				int penalty = 2;

				if (deletion.getPropertyName().isSimple())
					penalty = 2;

				else
					penalty = ((CompositePropertyValue) deletion
							.getPropertyValue()).getValue().size() * 2;

				if (deletion.getObject().isNode())
					nodesGrade -= penalty;
				else
					edgesGrade -= penalty;
			}

			else if (diff.isDifferentType()) {
				DifferentType type = (DifferentType) diff;
				if (type.getObject().isNode()) {
					Node node = (Node) type.getObject();
					int currentNodeGrade = nodesGradeMap.get(node);
					nodesGradeMap.put(node, currentNodeGrade -= 10);
				} else {
					Edge edge = (Edge) type.getObject();
					int currentEdgeGrade = edgesGradeMap.get(edge);
					edgesGradeMap.put(edge, currentEdgeGrade -= 10);
				}
			}

			else if (diff.isInDegreeDifference()) {
				InDegreeDifference degree = (InDegreeDifference) diff;
				int penalty = 0;
				for(DifferentConnection con : degree.getDifferences()){
					penalty += con.getCorrectDegree() - Math.abs(con.getCorrectDegree() - con.getWrongDegree());
				}
				Node node = degree.getNode();
				int currentNodeGrade = nodesGradeMap.get(node);
				nodesGradeMap.put(node, currentNodeGrade -= penalty);
			}

			else if (diff.isOutDegreeDifference()) {
				OutDegreeDifference degree = (OutDegreeDifference) diff;
				int penalty = 0;
				for(DifferentConnection con : degree.getDifferences()){
					penalty += con.getCorrectDegree() - Math.abs(con.getCorrectDegree() - con.getWrongDegree());
				}
				Node node = degree.getNode();
				int currentNodeGrade = nodesGradeMap.get(node);
				nodesGradeMap.put(node, currentNodeGrade -= penalty);
			}
			
			else if (diff.isDifferentPropertyValue()) {
				DifferentPropertyValue property = (DifferentPropertyValue) diff;
				if (property.getObject().isNode()) {
					Node node = (Node) property.getObject();
					int currentNodeGrade = nodesGradeMap.get(node);
					nodesGradeMap.put(node, currentNodeGrade -= 2);
				} else {
					Edge edge = (Edge) property.getObject();
					int currentEdgeGrade = edgesGradeMap.get(edge);
					edgesGradeMap.put(edge, currentEdgeGrade -= 2);
				}
			}

		}

		for (Node n : nodesGradeMap.keySet()) {
			if (nodesGradeMap.get(n) < 0)
				continue;
			nodesGrade += nodesGradeMap.get(n);
		}

		nodesGrade *= nodesFactor;
		if (nodesGrade == 0 && solution.getMaxNodesValue() == 0)
			nodesGrade = nodesFactor;
		else
			nodesGrade /= (double) solution.getMaxNodesValue();

		for (Edge e : edgesGradeMap.keySet()) {
			if (edgesGradeMap.get(e) < 0)
				continue;
			edgesGrade += edgesGradeMap.get(e);
		}

		edgesGrade *= (100 - nodesFactor);
		if (edgesGrade == 0 && solution.getMaxEdgesValue() == 0)
			edgesGrade = 100 - nodesFactor;
		else
			edgesGrade /= (double) solution.getMaxEdgesValue();

		if (nodesGrade < 0)
			nodesGrade = 0;

		if (edgesGrade < 0)
			edgesGrade = 0;

		double grade = nodesGrade + edgesGrade;
		return grade;
	}
}
