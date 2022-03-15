package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentConnection;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentType;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.InDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.OutDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluator;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.NodePair;

public class RelatedGraphsTest {

	@Test
	public void testRelatedGraphs() {
		GraphGenerator g = new GraphGenerator();
		// g.setSeed(-469881550379589788L);
		Graph original;
		g.setParameters(1, 0, 0);
		g.setMinNodes(8);
		g.setMaxNodes(8);
		original = g.generate();
		RelatedGraphs related = g.generate(original);
		Evaluation eval = new Evaluator(original, related.attempt).evaluate();

		Map<NodePair, Integer> count = new HashMap<>();
		for (Edge e : related.attempt.getEdges()) {
			NodePair n = new NodePair(e.getSource(), e.getTarget());
			if (count.containsKey(n))
				count.put(n, count.get(n) + 1);
			else
				count.put(n, 1);

		}

		original.print();

		for (Node n : original.getNodes()) {
			int counter = 0;
			for (Edge e : original.getEdges())
				if (e.getSource().equals(n) || e.getTarget().equals(n))
					counter++;
			System.out.println(n.getId() + "->" + counter);
		}

		related.attempt.print();
		System.out.println(eval.getDifferences());
		System.out.println(related.differences);
		if (eval.isComplete()) {
			assertEquals(related.differences, eval.getDifferences());
		}

		for (Node n : original.getNodes())
			System.out.println(n);
	}

	@Test
	public void testRelatedGraphsEdge() {
		GraphGenerator g = new GraphGenerator();

		g.setSeed(-836611756562812279L);
		Graph original;

		g.setParameters(0, 1, 0);
		g.setMinNodes(10);
		g.setMaxNodes(10);
		original = g.generate();
		RelatedGraphs related = g.generate(original);
		Evaluation eval = new Evaluator(original, related.attempt).evaluate();

		// System.out.println(end-start);
		Map<NodePair, Integer> count = new HashMap<>();
		for (Edge e : related.attempt.getEdges()) {
			NodePair n = new NodePair(e.getSource(), e.getTarget());
			if (count.containsKey(n))
				count.put(n, count.get(n) + 1);
			else
				count.put(n, 1);

		}

		// System.out.println(count);
		original.print();

		for (Node n : original.getNodes()) {
			int counter = 0;
			for (Edge e : original.getEdges())
				if (e.getSource().equals(n) || e.getTarget().equals(n))
					counter++;
			System.out.println(n.getId() + "->" + counter);
		}

		related.attempt.print();
		System.out.println(eval.getDifferences());
		System.out.println(related.differences);
		if (eval.isComplete()) {
			assertEquals(related.differences, eval.getDifferences());
		}

	}

	@Test
	public void testRelatedGraphsGeneration() {
		int nTests = 1000;
		GraphGenerator g = new GraphGenerator();
		Graph original;
		Random rand = new Random();
		for (int i = 0; i < nTests; i++) {
			g.setParameters(rand.nextInt(2), rand.nextInt(2), rand.nextInt(5));
			original = g.generate();
			g.generate(original);
		}
	}

	@Test
	public void testRelatedGraphsGenerationNodes() {
		GraphGenerator g = new GraphGenerator();
		Graph original;
		RelatedGraphs related;
		int nTests = 1000;

		g.setParameters(2, 0, 0);
		for (int i = 0; i < nTests; i++) {
			original = g.generate();
			related = g.generate(original);

			assertTrue(
					"The difference between the number of nodes in original and variation is at max 2",
					Math.abs(original.getNodes().size()
							- related.attempt.getNodes().size()) <= 2);
		}
	}

	@Test
	public void testRelatedGraphsGenerationEdges() {
		GraphGenerator g = new GraphGenerator();
		Graph original;
		RelatedGraphs related;
		int nTests = 1000;

		g.setParameters(0, 2, 0);
		for (int i = 0; i < nTests; i++) {
			original = g.generate();
			related = g.generate(original);

			assertTrue(
					"The difference between the number of edges in original and variation is at max 2",
					Math.abs(original.getEdges().size()
							- related.attempt.getEdges().size()) <= 2);
		}
	}

	@Test
	public void testRelatedGraphsGenerationProperties() {
		GraphGenerator g = new GraphGenerator();
		Graph original;
		RelatedGraphs related;
		int nTests = 1000;

		g.setParameters(0, 0, 10);
		for (int i = 0; i < nTests; i++) {
			original = g.generate();
			related = g.generate(original);

			assertTrue(
					"The difference between the number of properties in original and variation is at max 10",
					Math.abs(original.numberOfProperties()
							- related.attempt.numberOfProperties()) <= 10);
		}
	}

	@Test
	public void testComparingEqualRelatedGraphsOptimized() throws IOException {

		GraphGenerator g = new GraphGenerator();
		Graph original;
		RelatedGraphs related;
		int nTests = 100;

		// COMPARE EQUAL GRAPHS
		g.setParameters(0, 0, 0);
		int nodesFactor = 70;
		for (int nNodes = 2; nNodes < 10; nNodes++) {
			double totalDuration = 0;
			g.setMaxNodes(nNodes);
			g.setMinNodes(nNodes);

			String filename = "results/Optimized_vs_Simple/Optimized/" + nNodes
					+ "Nodes_" + nodesFactor + "-" + (100 - nodesFactor)
					+ ".txt";

			File resultsFile = new File(filename);
			if (!resultsFile.exists()) {
				resultsFile.createNewFile();
			}

			PrintWriter writer = new PrintWriter(filename, "UTF-8");

			for (int i = 0; i < nTests; i++) {
				original = g.generate();
				related = g.generate(original);
				writer.println("Nodes: " + original.getNodes().size());
				writer.println("Edges: " + original.getEdges().size());
				related.attempt.shuffle();
				Configs configs = Configs.getDefaultConfigs();
				configs.setNodeFactor(nodesFactor);
				Evaluator eval = new Evaluator(original, related.attempt,
						configs);
				long startTime = System.currentTimeMillis();
				Evaluation e = eval.evaluate();
				long endTime = System.currentTimeMillis();
				assertTrue("Comparison grade is 100%", e.getGrade() == 100);
				long duration = (endTime - startTime);
				writer.println("Duration: " + duration);
				totalDuration += duration;
			}
			writer.println("Average: " + totalDuration / nTests);
			writer.close();
		}
	}

	@Test
	public void testOptimizedChangingFactorsWithDifferences()
			throws IOException {

		GraphGenerator g = new GraphGenerator();
		//g.setSeed(-34747410152528959L);
		Graph original;
		RelatedGraphs related;
		int nTests = 100;
		// COMPARE EQUAL GRAPHS
		int nodeDifferences = 1;
		int edgeDifferences = 0;
		g.setParameters(nodeDifferences, edgeDifferences, 0);
		int nodeFactor = 70;
		int edgeFactor = 100 - nodeFactor;
		for (int nNodes = nodeDifferences + 2; nNodes < 31; nNodes++) {
			double totalDuration = 0;
			int notCompleted = 0;
			// g.setMaxProperties(0);
			// g.setMinProperties(0);
			g.setMinEdges(edgeDifferences);
			g.setMaxNodes(nNodes);
			g.setMinNodes(nNodes);
			// String filename = "results/Nodes" + nodeFactor + "_Edges"
			String filename = "results/DifferentGraphs/NodesDifferences"
					+ nodeDifferences + "/Nodes" + nodeFactor + "-Edges"
					+ edgeFactor + "/" + nNodes + "Nodes.txt";

			File resultsFile = new File(filename);
			if (!resultsFile.exists()) {
				resultsFile.createNewFile();
			}
			// g.setSeed(-4366461159098355137L);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			System.out.println("\t\t\t\t\t\t\t\t#############################"
					+ nNodes);
			for (int i = 0; i < nTests; i++) {
				// System.out.println("*************************************");
				original = g.generate();
				related = g.generate(original);
				// System.out.println(nNodes + " | " +
				// original.getNodes().size() + " | " +
				// related.attempt.getNodes().size());
				writer.println("Nodes: " + original.getNodes().size());
				writer.println("Edges: " + original.getEdges().size());
				writer.println("Nodes: " + related.attempt.getNodes().size());
				writer.println("Edges: " + related.attempt.getEdges().size());
				Set<String> types = new HashSet<String>();
				for (Node n : original.getNodes()) {
					types.add(n.getType());
				}
				writer.print("Tipos: " + types.size() + "/");
				types = new HashSet<String>();
				for (Node n : related.attempt.getNodes()) {
					types.add(n.getType());
				}
				writer.println(types.size());

				System.gc();
				long startTime = System.currentTimeMillis();
				Configs configs = Configs.getDefaultConfigs();
				configs.setNodeFactor(nodeFactor);
				Evaluator eval = new Evaluator(original, related.attempt,
						configs);
				Evaluation e = eval.evaluate();
				long endTime = System.currentTimeMillis();

				if (e.isComplete()) {
					if (!related.differences.equals(e.getDifferences())) {
						System.out.println(">>>>>>>>>>>>>>>>>>");
						original.print();
						related.attempt.print();
						System.out.println(e.getDifferences() + "\n"
								+ related.differences);
					}
					assertTrue(e.getGrade() >= 0);
					assertTrue(e.getGrade() <= 100);
					assertEquals(
							computeGradeByDifferences(original,
									related.attempt, related.differences,
									nodeFactor), e.getGrade(), 0);
					assertEquals(
							computeGradeByDifferences(original,
									related.attempt, related.differences,
									nodeFactor),
							computeGradeByDifferences(original,
									related.attempt, e.getDifferences(),
									nodeFactor), 0);

					long duration = (endTime - startTime);
					System.out.println("Dura��o (ms): " + duration);
					writer.println("Duration: " + duration + " | Grade: "
							+ e.getGrade());
					totalDuration += duration;
				}

				else {
					// original.print();
					// related.attempt.print();
					writer.println("Not completed with correct grade?: "
							+ (e.getGrade() == computeGradeByDifferences(
									original, related.attempt,
									related.differences, nodeFactor)));
					writer.println("Differences are equal?: "
							+ e.getDifferences().equals(related.differences));
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
					notCompleted++;
				}

			}
			writer.println("Not completed: " + notCompleted);
			writer.println("Average: " + totalDuration
					/ (nTests - notCompleted));
			writer.close();
		}
	}

	@Test
	public void testUseCaseSimulation() throws IOException {

		GraphGenerator g = new GraphGenerator();
		//g.setSeed(7767769735384306887L);
		Graph original;
		RelatedGraphs related;
		int nTests = 100;
		int nodeDifferences = 1;
		int edgeDifferences = 0;
		g.setParameters(nodeDifferences, edgeDifferences, 0);
		int nodeFactor = 70;
		int edgeFactor = 100 - nodeFactor;
		for (int nNodes = nodeDifferences + 2; nNodes < 31; nNodes++) {
			g.setMaxProperties(0);
			g.setMinProperties(0);
			g.setMaxEdgeTypes(2);
			g.setMaxNodeTypes(2);
			g.setMinEdges(edgeDifferences);
			g.setMaxNodes(nNodes);
			g.setMinNodes(nNodes);
			// String filename = "results/Nodes" + nodeFactor + "_Edges"
			String filename = "results/DifferentGraphs/NodesDifferences"
					+ nodeDifferences + "/Nodes" + nodeFactor + "-Edges"
					+ edgeFactor + "/" + nNodes + "Nodes.txt";

			File resultsFile = new File(filename);
			if (!resultsFile.exists()) {
				resultsFile.createNewFile();
			}
			System.out.println("\t\t\t\t\t\t\t\t#############################"
					+ nNodes);
			for (int i = 0; i < nTests; i++) {
				original = g.generate();
				related = g.generate(original);

				System.gc();
				long startTime = System.currentTimeMillis();
				Configs configs = Configs.getDefaultConfigs();
				configs.setNodeFactor(nodeFactor);

				Evaluator eval = new Evaluator();
				Evaluation x = new Evaluation();
				x.setDifferences(computeValues(related.getDifferences(),
						original, related.attempt));
//				System.out.println(x.getDifferences());
				eval.handleDifferences(x);
				eval = new Evaluator(original, related.attempt,
						configs);
				Graph newOriginal = eval.solution;
				Graph newAttempt = eval.attempt;

				Evaluation e = eval.evaluate();
				long endTime = System.currentTimeMillis();

				if (e.isComplete()) {
					double grade1 = computeGradeByDifferencesNoProperties(
							newOriginal, newAttempt, e.getDifferences(), 70);
					double grade2 = computeGradeByDifferencesNoProperties(
							original, related.attempt, x.getDifferences(), 70);

					assertEquals(grade1, grade2,0); 
//						System.out.println(grade1 + "\t" + e.getDifferences());
//						System.out.println(grade2 + "\t" + x.getDifferences());
//					}

					// assertTrue(areEquivalent(e.getDifferences(),
					// x.getDifferences()));

					assertTrue(e.getGrade() >= 0);
					assertTrue(e.getGrade() <= 100);
					// assertEquals(
					// computeGradeByDifferences(original,
					// related.attempt, related.differences,
					// nodeFactor), e.getGrade(), 0);
					// assertEquals(
					// computeGradeByDifferences(original,
					// related.attempt, related.differences,
					// nodeFactor),
					// computeGradeByDifferences(original,
					// related.attempt, e.getDifferences(),
					// nodeFactor), 0);

					long duration = (endTime - startTime);
					System.out.println("Dura��o (ms): " + duration);
				}

				else {
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
				}

			}
		}
	}

	// private void simplifyDifferences(Graph solution, Graph attempt,
	// Set<GraphDifference> differences) {
	// Set<GraphDifference> newDifferences = new HashSet<>();
	// boolean used;
	// for (GraphDifference diff : differences) {
	// used = false;
	// if (diff.isNodeInsertion()) {
	// Node insertion = ((NodeInsertion) diff).getInsertion();
	// int quantity = ((NodeInsertion) diff).getQuantity();
	// for (GraphDifference diff2 : newDifferences) {
	// if (diff2.isNodeInsertion()) {
	// NodeInsertion nodeInsertion = (NodeInsertion) diff2;
	// if (GraphSimplifier.isIndistinct(solution, insertion,
	// nodeInsertion.getInsertion())) {
	// newDifferences.remove(diff2);
	// nodeInsertion.setQuantity(nodeInsertion
	// .getQuantity() + quantity);
	// newDifferences.add(nodeInsertion);
	// used = true;
	// break;
	// }
	// }
	// }
	// if (!used) {
	// newDifferences.add(diff);
	// }
	// }
	//
	// else if (diff.isNodeDeletion()) {
	// Node deletion = ((NodeDeletion) diff).getDeletion();
	// int quantity = ((NodeDeletion) diff).getQuantity();
	// for (GraphDifference diff2 : newDifferences) {
	// if (diff2.isNodeDeletion()) {
	// NodeDeletion nodeDeletion = (NodeDeletion) diff2;
	// if (GraphSimplifier.isIndistinct(attempt, deletion,
	// nodeDeletion.getDeletion())) {
	// newDifferences.remove(diff2);
	// nodeDeletion.setQuantity(nodeDeletion.getQuantity()
	// + quantity);
	// newDifferences.add(nodeDeletion);
	// used = true;
	// break;
	// }
	// }
	// }
	// if (!used) {
	// newDifferences.add(diff);
	// }
	// }
	//
	// else if (diff.isEdgeDeletion()) {
	// Edge deletion = ((EdgeDeletion) diff).getDeletion();
	// int quantity = ((EdgeDeletion) diff).getQuantity();
	// for (GraphDifference diff2 : newDifferences) {
	// if (diff2.isEdgeDeletion()) {
	// EdgeDeletion edgeDeletion = (EdgeDeletion) diff2;
	// Edge goal = edgeDeletion.getDeletion();
	// if (deletion.getSource().equals(goal.getSource())) {
	// if (GraphSimplifier.isIndistinct(attempt,
	// deletion.getTarget(), goal.getTarget())) {
	// newDifferences.remove(diff2);
	// edgeDeletion.setQuantity(edgeDeletion
	// .getQuantity() + quantity);
	// newDifferences.add(edgeDeletion);
	// used = true;
	// break;
	//
	// }
	// }
	//
	// else if (deletion.getTarget().equals(goal.getTarget())) {
	// if (GraphSimplifier.isIndistinct(attempt,
	// deletion.getTarget(), goal.getTarget())) {
	// newDifferences.remove(diff2);
	// edgeDeletion.setQuantity(edgeDeletion
	// .getQuantity() + quantity);
	// newDifferences.add(edgeDeletion);
	// used = true;
	// break;
	//
	// }
	// }
	// }
	// }
	// if (!used) {
	// newDifferences.add(diff);
	// }
	// }
	//
	// else if (diff.isEdgeInsertion()) {
	// Edge insertion = ((EdgeInsertion) diff).getInsertion();
	// int quantity = ((EdgeInsertion) diff).getQuantity();
	// for (GraphDifference diff2 : newDifferences) {
	// if (diff2.isEdgeInsertion()) {
	// EdgeInsertion edgeInsertion = (EdgeInsertion) diff2;
	// Edge goal = edgeInsertion.getInsertion();
	// if (insertion.getSource().equals(goal.getSource())
	// || insertion.getTarget().equals(
	// goal.getTarget())) {
	// if (GraphSimplifier.isIndistinct(solution,
	// insertion.getTarget(), goal.getTarget())) {
	// newDifferences.remove(diff2);
	// edgeInsertion.setQuantity(edgeInsertion
	// .getQuantity() + quantity);
	// newDifferences.add(edgeInsertion);
	// used = true;
	// break;
	//
	// }
	// } else if (insertion.getTarget().equals(
	// goal.getTarget())) {
	// if (GraphSimplifier.isIndistinct(solution,
	// insertion.getSource(), goal.getSource())) {
	// newDifferences.remove(diff2);
	// edgeInsertion.setQuantity(edgeInsertion
	// .getQuantity() + quantity);
	// newDifferences.add(edgeInsertion);
	// used = true;
	// break;
	//
	// }
	// }
	// }
	// }
	// if (!used) {
	// newDifferences.add(diff);
	// }
	// }
	// differences = newDifferences;
	// }
	//
	// // System.out.println("old\n" + differences + "\nnew\n" +
	// // newDifferences);
	// }

	// private boolean areEquivalent(Set<GraphDifference> differences,
	// Set<GraphDifference> differences2) {
	//
	// System.out.println(differences + "\n\n" + differences2);
	// if (differences.size() != differences2.size())
	// return false;
	//
	// List<GraphDifference> visited = new ArrayList<>();
	//
	// for (GraphDifference diff : differences) {
	// for (GraphDifference diff2 : differences2) {
	// if (!visited.contains(diff2)) {
	// if (diff.isNodeDeletion() && diff2.isNodeDeletion()) {
	// NodeDeletion deletion1 = (NodeDeletion) diff;
	// NodeDeletion deletion2 = (NodeDeletion) diff2;
	//
	// if (deletion1.getDeletion().getValue()
	// / deletion1.getDeletion().getIndistincts()
	// * deletion1.getQuantity() == deletion2
	// .getDeletion().getValue()
	// / deletion2.getDeletion().getIndistincts()
	// * deletion2.getQuantity()) {
	// visited.add(diff2);
	// break;
	// }
	// }
	//
	// else if (diff.isNodeInsertion() && diff2.isNodeInsertion()) {
	// NodeInsertion insertion1 = (NodeInsertion) diff;
	// NodeInsertion insertion2 = (NodeInsertion) diff2;
	// System.out.println(insertion1.getInsertion().getValue()
	// * insertion1.getQuantity() + "|"
	// + insertion2.getInsertion().getValue()
	// * insertion2.getQuantity());
	// if (insertion1.getInsertion().getValue()
	// / insertion1.getInsertion().getIndistincts()
	// * insertion1.getQuantity() == insertion2
	// .getInsertion().getValue()
	// / insertion2.getInsertion().getIndistincts()
	// * insertion2.getQuantity()) {
	// visited.add(diff2);
	// break;
	// }
	// }
	//
	// else if (diff.isEdgeDeletion() && diff2.isEdgeDeletion()) {
	// EdgeDeletion deletion1 = (EdgeDeletion) diff;
	// EdgeDeletion deletion2 = (EdgeDeletion) diff2;
	//
	// if (deletion1.getDeletion().getValue()
	// / deletion1.getDeletion().getIndistincts()
	// * deletion1.getQuantity() == deletion2
	// .getDeletion().getValue()
	// / deletion2.getDeletion().getIndistincts()
	// * deletion2.getQuantity()) {
	// visited.add(diff2);
	// break;
	// }
	// }
	//
	// else if (diff.isEdgeInsertion() && diff2.isEdgeInsertion()) {
	// EdgeInsertion insertion1 = (EdgeInsertion) diff;
	// EdgeInsertion insertion2 = (EdgeInsertion) diff2;
	//
	// if (insertion1.getInsertion().getValue()
	// / insertion1.getInsertion().getIndistincts()
	// * insertion1.getQuantity() == insertion2
	// .getInsertion().getValue()
	// / insertion2.getInsertion().getIndistincts()
	// * insertion2.getQuantity()) {
	// visited.add(diff2);
	// break;
	// }
	// }
	// }
	// }
	// }
	// System.out.println(visited.size() + "|" + differences.size());
	// return visited.size() == differences.size();
	// }

	private Set<GraphDifference> computeValues(
			Set<GraphDifference> differences, Graph original, Graph attempt) {
		Configs configs = Configs.getDefaultConfigs();
		Set<GraphDifference> newDifferences = new HashSet<>();
		for (GraphDifference diff : differences) {
			if (diff.isNodeDeletion()) {
				((NodeDeletion) diff).getDeletion().computeMaxValue(configs);
				newDifferences.add(copy(diff, original, attempt));
			} else if (diff.isNodeInsertion()) {
				((NodeInsertion) diff).getInsertion().computeMaxValue(configs);
				newDifferences.add(copy(diff, original, attempt));
			} else if (diff.isEdgeDeletion()) {
				((EdgeDeletion) diff).getDeletion().computeMaxValue(configs);
				newDifferences.add(copy(diff, original, attempt));
			} else if (diff.isEdgeInsertion()) {
				((EdgeInsertion) diff).getInsertion().computeMaxValue(configs);
				newDifferences.add(copy(diff, original, attempt));
			}

		}
		return newDifferences;
	}

	private GraphDifference copy(GraphDifference diff, Graph original,
			Graph attempt) {
		if (diff.isNodeDeletion()) {
			NodeDeletion deletion = ((NodeDeletion) diff);
			return new NodeDeletion(deletion.getDeletion().deepCopy(),
					deletion.getQuantity());
		} else if (diff.isNodeInsertion()) {
			NodeInsertion insertion = ((NodeInsertion) diff);
			return new NodeInsertion(insertion.getInsertion().deepCopy(),
					insertion.getQuantity());
		} else if (diff.isEdgeDeletion()) {
			EdgeDeletion deletion = ((EdgeDeletion) diff);
			return new EdgeDeletion(deletion.getDeletion().deepCopy(
					attempt.getNodes()), deletion.getQuantity());
		} else if (diff.isEdgeInsertion()) {
			EdgeInsertion insertion = ((EdgeInsertion) diff);
			return new EdgeInsertion(insertion.getInsertion().deepCopy(
					original.getNodes()), insertion.getQuantity());
		}
		return null;
	}

	@Test
	public void testSimulateSpecificUseCase() {
		GraphGenerator g = new GraphGenerator();
		g.setSeed(6453420660110538087L);
		g.setMaxProperties(0);
		g.setMinProperties(0);
		g.setMaxEdgeTypes(2);
		g.setMaxNodeTypes(2);
		g.setMinEdges(0);
		g.setMaxNodes(6);
		g.setMinNodes(6);
		g.setParameters(1, 0, 0);
		Graph original = g.generate();
		RelatedGraphs related = g.generate(original);
		original.print();
		related.attempt.print();
		Configs configs = Configs.getDefaultConfigs();
		configs.setNodeFactor(70);
		for (Node node : original.getNodes()) {
			node.computeMaxValue(configs);
			System.out.println(node.getValue() + "/" + node.getIndistincts()
					+ "=" + node.getValue() / node.getIndistincts());
			System.out.println(node);
		}

		for (Edge edge : original.getEdges()) {
			edge.computeMaxValue(configs);
			System.out.println(edge.getValue() + "/" + edge.getIndistincts()
					+ "=" + edge.getValue() / edge.getIndistincts());
			System.out.println(edge);
		}

		Evaluator eval = new Evaluator();
		Evaluation x = new Evaluation();
		x.setDifferences(computeValues(related.getDifferences(), original, related.attempt));
		eval.handleDifferences(x);
		eval = new Evaluator(original, related.attempt, configs);
		Graph newOriginal = eval.solution;
		Graph newAttempt = eval.attempt;
		
		// simplifyDifferences(original, related.attempt, x.getDifferences());
		Evaluation e = eval.evaluate();
		System.out.println("******************************");

		for (Node node : newAttempt.getNodes()) {
			System.out.println(node.getValue() + "/" + node.getIndistincts()
					+ "=" + node.getValue() / node.getIndistincts());
			System.out.println(node);
		}

		for (Edge edge : newAttempt.getEdges()) {
			edge.computeMaxValue(configs);
			System.out.println(edge.getValue() + "/" + edge.getIndistincts()
					+ "=" + edge.getValue() / edge.getIndistincts());
			System.out.println(edge);
		}

		System.out.println("-----------------------------------\n"
				+ x.getDifferences() + "\n" + e.getDifferences());
		System.out.println(computeGradeByDifferencesNoProperties(newOriginal,
				newAttempt, e.getDifferences(), 70));
		System.out.println(computeGradeByDifferencesNoProperties(newOriginal,
				newAttempt, x.getDifferences(), 70));
		newOriginal.print();
		newAttempt.print();
		System.out.println("-------------------------------");
		for (Node n : e.getBestMap().keySet()) {
			System.out.println(n.getId() + "->"
					+ e.getBestMap().get(n).getAttempt().getId());
		}

	}

	@Test
	public void testGraphGenerationBySeed() {
		long seed = 1402584175176261137L;
		int nodes = 14;
		GraphGenerator g = new GraphGenerator();
		Graph original;
		g.setSeed(seed);
		g.setMaxProperties(0);
		g.setMaxNodeTypes(1);
		g.setMinNodeTypes(1);
		g.setMaxNodes(nodes);
		g.setMinNodes(nodes);

		original = g.generate();

		for (Edge e : original.getEdges()) {
			System.out.println(e.getType());
		}

		// System.out.println("*************************");
		// for (Node n : original.getNodes())
		// for (Node k : related.variation.getNodes())
		// System.out.println(n.id + " -> " + k.id + " : "
		// + n.compareNode(k));
		System.out.println(original.getNodes().size() + " / "
				+ original.getEdges().size());

		Evaluator eval = new Evaluator(original, original);
		eval.evaluate();
	}

	@Test
	public void testChangingTypesOptimized() throws IOException {

		GraphGenerator g = new GraphGenerator();
		Graph original;
		RelatedGraphs related;
		int nTests = 100;
		int nTypes = 2;
		int nodesFactor = 70;
		// COMPARE EQUAL GRAPHS
		g.setParameters(0, 0, 0);
		// g.setSeed(-1729044474452063022L);
		int totalNotCompleted = 0;
		for (int nNodes = 1; nNodes < 10; nNodes++) {
			int notCompleted = 0;
			System.out.println("\n######" + nNodes + "NODES#####");
			double totalDuration = 0;
			g.setMaxProperties(0);
			g.setMaxNodeTypes(nTypes);
			g.setMinNodeTypes(1);
			g.setMaxNodes(nNodes);
			g.setMinNodes(nNodes);

			String filename = "results/Types/Types (" + nTypes + ")/Nodes_"
					+ nNodes + "Nodes.txt";

			File resultsFile = new File(filename);
			if (!resultsFile.exists()) {
				resultsFile.createNewFile();
			}

			PrintWriter writer = new PrintWriter(filename, "UTF-8");

			for (int i = 0; i < nTests; i++) {
				original = g.generate();
				int count = 0;
				Set<String> types = new HashSet<String>();
				for (Node n : original.getNodes()) {
					count += n.getProperties().size();
					types.add(n.getType());
				}

				assertEquals(0, count);
				assertTrue(types.size() <= nTypes);
				related = g.generate(original);
				writer.println("Nodes: " + original.getNodes().size());
				writer.println("Edges: " + original.getEdges().size());
				System.gc();
				long startTime = System.currentTimeMillis();
				Configs configs = Configs.getDefaultConfigs();
				configs.setNodeFactor(nodesFactor);
				Evaluator eval = new Evaluator(original, related.attempt,
						configs);

				Evaluation e = eval.evaluate();
				long endTime = System.currentTimeMillis();

				if (e.isComplete()) {
					long duration = (endTime - startTime);
					assertTrue("Comparison grade is 100%", e.getGrade() == 100);
					writer.println("Duration: " + duration);
					totalDuration += duration;
				}

				else {
					writer.println(e.getGrade() == 100);
					// writer.println("Not completed with grade: " +
					// e.getGrade());
					notCompleted++;
				}

			}
			totalNotCompleted += notCompleted;
			writer.println("Average: " + totalDuration
					/ (nTests - notCompleted));
			writer.println("Total percentage of failures: "
					+ (double) (100 * totalNotCompleted)
					/ (double) (nTests * nNodes) + "%");
			writer.println("Not completed: " + notCompleted);
			writer.close();
		}
	}

	@Test
	public void testOptimizedNoProperties() throws IOException {

		GraphGenerator g = new GraphGenerator();
		Graph original;
		RelatedGraphs related;
		int nTests = 30;
		g.setMaxProperties(0);
		g.setMinProperties(0);

		// COMPARE EQUAL GRAPHS
		g.setParameters(0, 0, 0);
		int nodeFactor = 80;
		int edgeFactor = 100 - nodeFactor;
		// g.setSeed(1963182298872304645L);
		for (int nNodes = 1; nNodes < 20; nNodes++) {
			double totalDuration = 0;

			g.setMaxNodes(nNodes);
			g.setMinNodes(nNodes);

			String filename = "results/NoProperties/Nodes" + nodeFactor
					+ "_Edges" + edgeFactor + "/" + nNodes + "Nodes.txt";

			File resultsFile = new File(filename);
			if (!resultsFile.exists()) {
				resultsFile.createNewFile();
			}
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for (int i = 0; i < nTests; i++) {
				original = g.generate();
				related = g.generate(original);
				writer.println("Nodes: " + original.getNodes().size());
				writer.println("Edges: " + original.getEdges().size());
				writer.println("Nodes: " + related.attempt.getNodes().size());
				writer.println("Edges: " + related.attempt.getEdges().size());
				long startTime = System.currentTimeMillis();
				Configs configs = Configs.getDefaultConfigs();
				configs.setNodeFactor(nodeFactor);
				Evaluator eval = new Evaluator(original, related.attempt,
						configs);
				Evaluation e = eval.evaluate();
				writer.println("Grade: " + e.getGrade());

				assertTrue("Differences are equal",
						e.getDifferences().equals(related.differences));

				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime);
				writer.println("Duration: " + duration);
				totalDuration += duration;
			}
			writer.println("Average: " + totalDuration / nTests);
			writer.close();
		}
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
				int quantity = ((NodeInsertion) diff).getQuantity();
				int tempGrade = node.getValue() - quantity
						* (node.getValue() / node.getIndistincts());

				nodesGradeMap.put(node, tempGrade);

			} else if (diff.isNodeDeletion()) {
				nodesGrade -= 10 * ((NodeDeletion) diff).getQuantity();
			}

			else if (diff.isEdgeInsertion()) {
				Edge edge = ((EdgeInsertion) diff).getInsertion();
				int quantity = ((EdgeInsertion) diff).getQuantity();

				edgesGradeMap.put(edge, edge.getValue()
						- (edge.getValue() / edge.getIndistincts()) * quantity);

			} else if (diff.isEdgeDeletion()) {
				edgesGrade -= 10;
			}

			else if (diff.isPropertyInsertion()) {
				PropertyInsertion insertion = (PropertyInsertion) diff;
				int factor = insertion.getObject().getIndistincts();

				int penalty = 2 * factor;
				if (!insertion.getPropertyName().isSimple())
					penalty = ((CompositePropertyValue) insertion
							.getPropertyValue()).getValue().size() * 2 * factor;

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
				int factor = deletion.getObject().getIndistincts();

				int penalty = 2 * factor;

				if (deletion.getPropertyName().isSimple())
					penalty = 2 * factor;

				else
					penalty = ((CompositePropertyValue) deletion
							.getPropertyValue()).getValue().size() * 2 * factor;

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
					nodesGradeMap.put(node,
							currentNodeGrade -= (10 * node.getIndistincts()));
				} else {
					Edge edge = (Edge) type.getObject();
					int currentEdgeGrade = edgesGradeMap.get(edge);
					edgesGradeMap.put(edge,
							currentEdgeGrade -= (10 * edge.getIndistincts()));
				}
			}

			else if (diff.isInDegreeDifference()) {
				InDegreeDifference degree = (InDegreeDifference) diff;
				int penalty = 0;
				for (DifferentConnection con : degree.getDifferences()) {
					penalty += Math.abs(con.getCorrectDegree()
							- con.getWrongDegree());
				}
				Node node = degree.getNode();
				int currentNodeGrade = nodesGradeMap.get(node);
				nodesGradeMap.put(node,
						currentNodeGrade -= (penalty * node.getIndistincts()));
			}

			else if (diff.isOutDegreeDifference()) {
				OutDegreeDifference degree = (OutDegreeDifference) diff;
				int penalty = 0;
				for (DifferentConnection con : degree.getDifferences()) {
					penalty += Math.abs(con.getCorrectDegree()
							- con.getWrongDegree());
				}
				Node node = degree.getNode();
				int currentNodeGrade = nodesGradeMap.get(node);
				nodesGradeMap.put(node,
						currentNodeGrade -= (penalty * node.getIndistincts()));
			}

			else if (diff.isDifferentPropertyValue()) {
				DifferentPropertyValue property = (DifferentPropertyValue) diff;
				if (property.getObject().isNode()) {
					Node node = (Node) property.getObject();
					int currentNodeGrade = nodesGradeMap.get(node);
					nodesGradeMap.put(node,
							currentNodeGrade -= (2 * node.getIndistincts()));
				} else {
					Edge edge = (Edge) property.getObject();
					int currentEdgeGrade = edgesGradeMap.get(edge);
					edgesGradeMap.put(edge,
							currentEdgeGrade -= (2 * edge.getIndistincts()));
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

	private double computeGradeByDifferencesNoProperties(Graph solution,
			Graph attempt, Set<GraphDifference> differences, int nodesFactor) {

		double nodesGrade = solution.getMaxNodesValue();
		double edgesGrade = solution.getMaxEdgesValue();
		// System.out.println("1." + nodesGrade + "|" + edgesGrade);

		for (GraphDifference diff : differences) {
			if (diff.isNodeInsertion()) {
				int quantity = ((NodeInsertion) diff).getQuantity();

				int penalty = 10 * quantity;
				nodesGrade -= penalty;

			} else if (diff.isNodeDeletion()) {
				nodesGrade -= 10 * ((NodeDeletion) diff).getQuantity();
			}

			else if (diff.isEdgeInsertion()) {
				int quantity = ((EdgeInsertion) diff).getQuantity();

				edgesGrade -= 10 * quantity;

			} else if (diff.isEdgeDeletion()) {
				edgesGrade -= 10;
			}

		}
		// System.out.println("2." + nodesGrade + "|" + edgesGrade);
		nodesGrade *= nodesFactor;
		if (nodesGrade == 0 && solution.getMaxNodesValue() == 0)
			nodesGrade = nodesFactor;
		else
			nodesGrade /= (double) solution.getMaxNodesValue();

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

	@Test
	public void readTypesResults() throws IOException {
		File resultsFile = new File("results/storedResults");
		if (!resultsFile.exists()) {
			resultsFile.createNewFile();
		}

		PrintWriter writer = new PrintWriter("results/storedResults", "UTF-8");
		int trues = 0;
		int falses = 0;
		int maxNodes = 31;
		for (int nTypes = 5; nTypes < 6; nTypes++) {
			writer.println("##############" + nTypes + " TYPES##############");
			if (nTypes == 3)
				maxNodes = 31;

			for (int nNodes = 2; nNodes < maxNodes; nNodes++) {
				String filename = "results/Types/Types (" + nTypes + ")/Nodes_"
						+ nNodes + "Nodes.txt";
				File readFile = new File(filename);
				Scanner sc = new Scanner(readFile);

				while (sc.hasNextLine()) {
					String aux = sc.nextLine();
					if (aux.contains("true"))
						trues++;
					if (aux.contains("false"))
						falses++;
					if (aux.contains("Average:"))
						writer.println(aux);
					if (aux.contains("Not completed:"))
						writer.println(aux);
				}
				sc.close();

			}
		}
		writer.println("true:" + trues);
		writer.println("false:" + falses);
		writer.close();
	}

	@Test
	public void readOptimizedResults() throws IOException {
		String type = "Optimized";
		String fileName = "results/storedResults" + type + "Version";

		File resultsFile = new File(fileName);
		if (!resultsFile.exists()) {
			resultsFile.createNewFile();
		}

		PrintWriter writer = new PrintWriter(fileName, "UTF-8");

		for (int nNodes = 1; nNodes < 31; nNodes++) {
			String filename = "results/Optimized_vs_Simple/" + type + "/"
					+ nNodes + "Nodes_90-10.txt";
			File readFile = new File(filename);
			Scanner sc = new Scanner(readFile);
			String durationsToStore = "";
			while (sc.hasNextLine()) {
				String aux = sc.nextLine();
				if (aux.contains("Average:"))
					durationsToStore = aux;
			}
			sc.close();
			writer.println(durationsToStore);

		}
		writer.close();
	}

	@Test
	public void readNodeDifferencesResults() throws IOException {
		String fileName = "results/storedResultsNodeDifferences";

		File resultsFile = new File(fileName);
		if (!resultsFile.exists()) {
			resultsFile.createNewFile();
		}
		int totalIncomplete = 0;
		int totalTrues = 0;
		int totalFalses = 0;
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		for (int nodeDiff = 1; nodeDiff < 2; nodeDiff++) {
			writer.println("##########" + nodeDiff);
			for (int nNodes = nodeDiff + 1; nNodes < 31; nNodes++) {
				if (nodeDiff == 1 && nNodes == 1)
					continue;
				writer.println("\t\t\t\t###########" + nNodes + "nodes");
				int trueValue = 0;
				int falseValue = 0;
				String filename = "results/DifferentGraphs/NodesDifferences"
						+ nodeDiff + "/Nodes70-Edges30/" + nNodes + "Nodes.txt";
				File readFile = new File(filename);
				Scanner sc = new Scanner(readFile);
				while (sc.hasNextLine()) {
					String aux = sc.nextLine();
					if (aux.contains("Average:"))
						writer.println(aux);
					if (aux.contains("Not completed:"))
						writer.println(aux);
					if (aux.contains("true"))
						trueValue++;
					if (aux.contains("false"))
						falseValue++;
				}
				totalTrues += (trueValue / 2);
				totalFalses += (falseValue / 2);
				sc.close();
				writer.println("Trues: " + (trueValue / 2) + "\nFalses: "
						+ (falseValue / 2));
			}
		}
		totalIncomplete = totalFalses + totalTrues;
		writer.println("--------------------------\nIncompletes:"
				+ totalIncomplete + "|Trues:" + totalTrues + "|Falses:"
				+ totalFalses);
		writer.close();
	}

	@Test
	public void readChangingFactorsResults() throws IOException {
		String fileName = "results/storedChangingFactorResults";

		File resultsFile = new File(fileName);
		if (!resultsFile.exists()) {
			resultsFile.createNewFile();
		}

		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		int nodesDifference = 2;

		for (int nodesFactor = 70; nodesFactor < 75; nodesFactor += 10) {
			int edgesFactor = 100 - nodesFactor;
			writer.println("#######" + nodesFactor + "-" + edgesFactor);
			for (int nNodes = 3; nNodes < 31; nNodes++) {
				String filename = "results/DifferentGraphs/NodesDifferences"
						+ nodesDifference + "/Nodes" + nodesFactor + "-Edges"
						+ edgesFactor + "/" + nNodes + "Nodes.txt";
				File readFile = new File(filename);
				Scanner sc = new Scanner(readFile);
				while (sc.hasNextLine()) {
					String aux = sc.nextLine();
					if (aux.contains("Average:"))
						writer.println(aux);
					if (aux.contains("Not completed:"))
						writer.println(aux);
				}
				sc.close();

			}
		}
		writer.close();
	}

	void graphSignature(Graph graph) {
		Map<String, Integer> count = new HashMap<>();

		List<String> keys = new ArrayList<>(count.keySet());
		Collections.sort(keys);

		int perm = 1;
		for (String sig : keys) {
			int n = count.get(sig);
			System.out.println(sig + ":" + n);
			perm *= (int) Math.pow(n, n);
		}
		System.out.println("\t\t\t\tperm:" + perm);

	}

	@Test
	public void testSpecificCase() throws IOException {

		GraphGenerator g = new GraphGenerator();
		g.setSeed(3186170627718605402L);
		Graph original;
		RelatedGraphs related;
		int nodeDifferences = 1;
		int edgeDifferences = 0;
		g.setParameters(nodeDifferences, edgeDifferences, 0);
		int nodeFactor = 70;

		g.setMinEdges(edgeDifferences);
		g.setMaxNodes(2);
		g.setMinNodes(2);

		original = g.generate();
		related = g.generate(original);

		System.gc();
		Configs configs = Configs.getDefaultConfigs();
		configs.setNodeFactor(nodeFactor);
		Evaluator eval = new Evaluator(original, related.attempt, configs);

		for (Node n : original.getNodes())
			for (Node n1 : related.attempt.getNodes())
				System.out.println(n.getId() + "(" + n.getValue() + ")->"
						+ n1.getId() + "(" + n1.getValue() + ") :"
						+ n.compareNode(n1, configs).getGrade());

		for (Node n : original.getNodes())
			System.out.println(n);
		Evaluation e = eval.evaluate();

		if (e.isComplete()) {
			if (!related.differences.equals(e.getDifferences())) {
				System.out.println(">>>>>>>>>>>>>>>>>>");
				original.print();
				related.attempt.print();
				System.out.println(e.getDifferences() + "\n"
						+ related.differences);
			}

			assertTrue(e.getGrade() >= 0);
			assertTrue(e.getGrade() <= 100);
			System.out.println(e.getNodesGrade());
			System.out.println(e.getEdgesGrade());
			assertEquals(
					computeGradeByDifferences(original, related.attempt,
							related.differences, nodeFactor), e.getGrade(), 0);
			assertEquals(
					computeGradeByDifferences(original, related.attempt,
							related.differences, nodeFactor),
					computeGradeByDifferences(original, related.attempt,
							e.getDifferences(), nodeFactor), 0);

		}

		else {
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
		}

	}

}
