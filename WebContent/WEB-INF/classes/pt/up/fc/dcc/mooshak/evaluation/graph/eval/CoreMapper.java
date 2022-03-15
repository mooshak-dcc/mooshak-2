package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import pt.up.fc.dcc.mooshak.evaluation.graph.RelatedGraphs;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;

/**
 * CoreMapper is a class that handle the mapping process
 * @author Ruben
 *
 */
public class CoreMapper implements Iterable<RelatedGraphs> {
	Configs configs;
	boolean isReduced;
	Map<Node, Match> bests = new HashMap<>();
	Map<Node, Integer> attemptBests = new HashMap<>();
	Map<Node, Integer> bestsDifference = new HashMap<>();
	Map<Node, Integer> attemptsDifference = new HashMap<>();
	List<Alternative> alternatives = new ArrayList<>();
	private Graph solution;
	private Graph attempt;
	int currentBest = 0;
	List<Alternative> newAlternatives = new ArrayList<>();
	Map<Node, Match> newBests;
	Map<Node, Integer> newBestsDifference = new HashMap<>();

	CoreMapper(Graph solution, Graph attempt, Configs configs) {
		init(solution, attempt, false, configs, "UML");
	}

	CoreMapper(Graph solution, Graph attempt, boolean isReduced, Configs configs) {
		init(solution, attempt, isReduced, configs, "UML");
	}
	
	CoreMapper(Graph solution, Graph attempt, boolean isReduced, Configs configs, String type) {
		init(solution, attempt, isReduced, configs, type);
//		System.out.println(attempt);
//		System.out.println("\n\n");
//		System.out.println(solution);
	}

	Comparator<Alternative> comparator = new Comparator<Alternative>() {

		@Override
		public int compare(Alternative arg0, Alternative arg1) {
			if (arg0.getDelta() == arg1.getDelta()
					&& arg0.getSolution().equals(arg1.getSolution())) {
				return computeDiferenciator(arg1) - computeDiferenciator(arg0);
			}

			return arg0.getDelta() - arg1.getDelta();
		}

		private int computeDiferenciator(Alternative arg0) {

			if (arg0.match.tiebreak != -1)
				return arg0.match.tiebreak;

			Node solutionNode = arg0.getSolution();
			Node attemptNode = arg0.getMatch().getAttempt();
			arg0.match.tiebreak = 0;
			for (Edge solutionEdge : solution.getEdges()) {
				int max = 0;
				if (solutionEdge.getSource().equals(solutionNode)) {
					Node solutionTarget = solutionEdge.getTarget();
					for (Edge attemptEdge : attempt.getEdges()) {
						if (attemptEdge.getSource().equals(attemptNode)) {
							Node attemptTarget = attemptEdge.getTarget();
							int value = solutionTarget.compareNode(
									attemptTarget, configs).getGrade();
							if (value > max) {
								max = value;
							}
						}
					}
				}

				else if (solutionEdge.getTarget().equals(solutionNode)) {
					Node solutionSource = solutionEdge.getSource();
					for (Edge attemptEdge : attempt.getEdges()) {
						if (attemptEdge.getTarget().equals(attemptNode)) {
							Node attemptSource = attemptEdge.getSource();
							int value = solutionSource.compareNode(
									attemptSource, solution.getEdges(),
									attempt.getEdges(), configs).getGrade();
							if (value > max)
								max = value;
						}
					}
				}

				arg0.match.tiebreak += max;
			}
			return arg0.match.tiebreak;
		}

	};

	void init(final Graph solution, final Graph attempt, boolean isReduced,
			final Configs configs, String type) {
		this.solution = solution;
		this.attempt = attempt;
		this.isReduced = isReduced;
		this.configs = configs;
		Map<Integer, Integer> diffsCount = new HashMap<>();
		Match match;
		for (Node solutionNode : solution.getNodes()) {
			Match bestMatch = null;
			int bestValue = Integer.MAX_VALUE;

			for (Node attemptNode : attempt.getNodes()) {

				if (isReduced)
					match = new Match(solutionNode, attemptNode,
							solution.getEdges(), attempt.getEdges(), configs);
				else
					match = new Match(solutionNode, attemptNode, configs);

				int currentDifference = solutionNode.getValue() - match.value;

				int count = 0;
				if (diffsCount.containsKey(currentDifference))
					count = diffsCount.get(currentDifference);

				diffsCount.put(currentDifference, (++count));

				if (currentDifference <= bestValue) {
					bestMatch = match;
					bestValue = currentDifference;
				}

				if (attemptBests.containsKey(match.attempt)) {
					if (currentDifference <= attemptsDifference
							.get(match.attempt)) {
						attemptBests.put(match.attempt, match.value);
						attemptsDifference
								.put(match.attempt, currentDifference);
					}

				} else {
					attemptBests.put(match.attempt, match.value);
					attemptsDifference.put(match.attempt, currentDifference);
				}

				alternatives.add(new Alternative(solutionNode, match));
			}

			bestsDifference.put(solutionNode, bestValue);
			bests.put(solutionNode, bestMatch);
		}

		if (type.equals("EER")) {
			handleEER();
		}

		else {
			handleRegular();
		}

	}

	private void handleRegular() {
		Iterator<Alternative> iterator = alternatives.iterator();
		while (iterator.hasNext()) {
			Alternative alternative = iterator.next();
			if (alternative.delta == bestsDifference.get(alternative.solution)) {

				if (alternative.match.attempt.equals(bests
						.get(alternative.solution).attempt))
					iterator.remove();

			} else {
				alternative.delta = bests.get(alternative.solution).value
						- alternative.match.value;
			}
		}

		Collections.sort(alternatives);
	}

	private void handleEER() {
		
		Iterator<Alternative> iterator = alternatives.iterator();
		while (iterator.hasNext()) {
			Alternative alternative = iterator.next();
			// if (alternative.delta ==
			// bestsDifference.get(alternative.solution)) {

			// if (alternative.match.attempt.equals(bests
			// .get(alternative.solution).attempt))
			// iterator.remove();

			// } else {
			alternative.delta = bests.get(alternative.solution).value
					- alternative.match.value;
			// }
		}

		Collections.sort(alternatives, comparator);

		iterator = alternatives.iterator();
		bests = new HashMap<Node, Match>();
		List<Alternative> bestList = new ArrayList<>();
		while (iterator.hasNext()) {
			Alternative alternative = iterator.next();
			if (!bests.containsKey(alternative.solution)) {
				bests.put(alternative.solution, alternative.match);
				bestList.add(alternative);
				// iterator.remove();
			}

			if (bests.size() == solution.getNodes().size())
				break;
		}

		for (int i = 0; i < bestList.size(); i++) {
			Alternative alt1 = bestList.get(i);
			for (int j = i + 1; j < bestList.size(); j++) {
				Alternative alt2 = bestList.get(j);
				if (alt1.match.attempt.equals(alt2.match.attempt)
						&& alt1.delta == alt2.delta) {
					if (alt1.match.tiebreak > alt2.match.tiebreak) {
						changeBest(bestList, j, alt2);
					} else {
						changeBest(bestList, i, alt1);

					}
					i = -1;
					break;
				}
			}
		}

		for (Alternative alternative : bestList) {
			alternatives.remove(alternative);
			bests.put(alternative.solution, alternative.match);
		}

	}

	private void changeBest(List<Alternative> bestList, int j,
			Alternative alternative) {
		boolean getNext = false;

		bestList.remove(j);
		for (Alternative alt : alternatives) {
			if (getNext) {
				if (alt.solution.equals(alternative.solution)) {
					bestList.add(alt);
					break;
				}
			}

			if (alt.equals(alternative)) {
				getNext = true;
			}
		}

		Collections.sort(bestList, comparator);
	}

	/**
	 * Remove some bests from original bests
	 * 
	 * @return the bests for the given graphs
	 */

	public Map<Node, Match> getBests(Graph solution, Graph attempt) {
		newBests = new HashMap<>();

		for (Node solutionNode : solution.getNodes()) {
			Node attemptNode = bests.get(solutionNode).attempt;

			if (attempt.getNodes().contains(attemptNode))
				newBests.put(solutionNode, bests.get(solutionNode));
			else
				for (Alternative alt : alternatives)
					if (alt.solution.equals(solutionNode)
							&& attempt.getNodes().contains(alt.match.attempt)) {
						newBests.put(solutionNode, alt.match);
					}
		}
		return newBests;
	}

	/**
	 * @return the bests
	 */
	public Map<Node, Match> getBests() {
		return bests;
	}

	/**
	 * @return the attemptBests
	 */
	public Map<Node, Integer> getAttemptBests() {
		return attemptBests;
	}

	/**
	 * Remove some alternatives from original alternatives
	 * 
	 * @return the alternatives for the given graphs
	 */
	public List<Alternative> getAlternatives(Graph solution, Graph attempt) {
		List<Alternative> newAlternatives = new ArrayList<>(alternatives);
		for (Alternative alt : alternatives) {
			if (!solution.getNodes().contains(alt.solution)
					|| !attempt.getNodes().contains(alt.match.attempt)) {
				newAlternatives.remove(alt);
			}

		}

		// for(Alternative alt : newAlternatives)
		// System.out.println(alt.solution.getId()+alt.solution.getName() + "->"
		// +
		// alt.match.attempt.getId()+alt.match.attempt.getName()+":"+alt.delta+"=>"+alt.match.tiebreak);

		return newAlternatives;
	}

	/**
	 * @return the alternatives
	 */
	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	@Override
	public Iterator<RelatedGraphs> iterator() {

		int nodesDifference = solution.getNodes().size()
				- attempt.getNodes().size();

		if (nodesDifference == 0) {
			return Arrays.asList(new RelatedGraphs(solution, attempt))
					.iterator();

		} else if (nodesDifference > 0) {
			return changeSolutionsIterator(nodesDifference);

		}

		else {
			return changeAttemptsIterator(nodesDifference);
		}

	}

	private Iterator<RelatedGraphs> changeAttemptsIterator(int nodesDifference) {
		List<Match> bestMatches = new ArrayList<>();
		int max = 0;
		for (Node node : attemptBests.keySet()) {
			bestMatches.add(new Match(node, attemptBests.get(node)));
			max += attemptBests.get(node);
		}
		final int maxBests = max;
		final IncreasingFixedPermutations<Match> permutations = new IncreasingFixedPermutations<Match>(
				bestMatches, (-1) * nodesDifference);
		return new Iterator<RelatedGraphs>() {
			Iterator<List<Match>> iterator = permutations.iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public RelatedGraphs next() {
				currentBest = maxBests;
				Graph changedGraph;
				List<Node> changedNodes = new ArrayList<>(attempt.getNodes());
				List<Edge> changedEdges = new ArrayList<>(attempt.getEdges());
				List<Node> removedNodes = new ArrayList<>();
				Set<GraphDifference> differences = new HashSet<>();
				for (Match match : iterator.next()) {
					removedNodes.add(match.attempt);
					changedNodes.remove(match.attempt);
					differences.add(new NodeDeletion(match.attempt,
							match.attempt.getIndistincts()));
					currentBest -= match.value;
				}

				for (Edge edge : attempt.getEdges()) {
					if (removedNodes.contains(edge.getSource())
							|| removedNodes.contains(edge.getTarget())) {
						changedEdges.remove(edge);
						differences.add(new EdgeDeletion(edge, edge
								.getIndistincts()));
					}

				}

				changedGraph = new Graph(changedNodes, changedEdges);
			
				return new RelatedGraphs(solution, changedGraph, differences);
			}

		};
	}

	private Iterator<RelatedGraphs> changeSolutionsIterator(int nodesDifference) {
		
		List<Alternative> bestAlternatives = new ArrayList<>();
		int max = 0;

		for (Node node : bests.keySet()) {
			
			Alternative alt = new Alternative(node, bests.get(node));
			alt.delta = 1000 - alt.delta;// this.solution.getMaxNodesValue() -
											// alt.delta; // alt.match.value;
			bestAlternatives.add(alt);
			max += bests.get(node).value;
			
		}

		// for(Alternative alt : bestAlternatives){
		// System.out.println(alt.solution.getName() + "->" +
		// alt.match.attempt.getName() + ":" + alt.delta);
		// }

		final int maxBests = max;
		final IncreasingFixedPermutations<Alternative> permutations = new IncreasingFixedPermutations<Alternative>(
				bestAlternatives, nodesDifference);

		return new Iterator<RelatedGraphs>() {
			// int counter = 1;
			Iterator<List<Alternative>> iterator = permutations.iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public RelatedGraphs next() {
				currentBest = maxBests;
				Graph changedGraph;
				List<Node> changedNodes = new ArrayList<>(solution.getNodes());
				List<Edge> changedEdges = new ArrayList<>(solution.getEdges());
				List<Node> removedNodes = new ArrayList<>();
				Set<GraphDifference> differences = new HashSet<>();
				// System.out.println("##########"+(counter++));
				for (Alternative alt : iterator.next()) {
					// System.out.println(alt.solution.getId() +
					// alt.solution.getName());
					removedNodes.add(alt.solution);
					changedNodes.remove(alt.solution);
					differences.add(new NodeInsertion(alt.solution,
							alt.solution.getIndistincts()));
					currentBest -= alt.match.value;
				}

				for (Edge edge : solution.getEdges()) {
					if (removedNodes.contains(edge.getSource())
							|| removedNodes.contains(edge.getTarget())) {
						changedEdges.remove(edge);
						differences.add(new EdgeInsertion(edge, edge
								.getIndistincts()));
					}
				}

				changedGraph = new Graph(changedNodes, changedEdges);
				return new RelatedGraphs(changedGraph, attempt, differences);
			}
		};
	}

}