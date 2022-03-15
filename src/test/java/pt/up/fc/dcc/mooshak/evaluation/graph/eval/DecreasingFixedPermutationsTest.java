package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Alternative;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.DecreasingFixedPermutations;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class DecreasingFixedPermutationsTest {

	@Test
	public void testPermutationsForEqualValues() {
		List<Node> nodes = new ArrayList<Node>();
		List<Match> matches = new ArrayList<Match>();
		int missingNodes = 1;
		int nNodes = 20;
		for (int i = 0; i < nNodes; i++)
			nodes.add(new Node("id_" + i, "type" + i % 2));
		Map<Node, Match> bests = new HashMap<>();

		for (Node n1 : nodes)
			for (Node n2 : nodes) {
				Match k = new Match(n1, n2,Configs.getDefaultConfigs());
				matches.add(k);
				bests.put(n1, k);
			}

		for (Match m : bests.values()) {
			m.value = nextInt(0, 0);
		}

		List<Match> bestMatches = new ArrayList<>(bests.values());

		DecreasingFixedPermutations<Match> permutations = new DecreasingFixedPermutations<Match>(
				bestMatches, missingNodes);
		checkIncreasingSums(permutations);
		show(permutations);
		checkAllPermutationsWereGenerated(permutations, nNodes, missingNodes);

	}

	@Test
	public void testPermutationsForRandomValues() {
		List<Node> nodes = new ArrayList<Node>();
		List<Match> matches = new ArrayList<Match>();
		int missingNodes = 1;
		int nNodes = 20;
		for (int i = 0; i < nNodes; i++)
			nodes.add(new Node("id_" + i, "type" + i % 2));

		Map<Node, Match> bests = new HashMap<>();

		for (Node n1 : nodes)
			for (Node n2 : nodes) {
				Match k = new Match(n1, n2,Configs.getDefaultConfigs());
				matches.add(k);
				bests.put(n1, k);
			}

		for (Match m : bests.values()) {
			m.value = nextInt(0, 100);
		}

		List<Match> bestMatches = new ArrayList<>(bests.values());

		DecreasingFixedPermutations<Match> permutations = new DecreasingFixedPermutations<Match>(
				bestMatches, missingNodes);
		checkIncreasingSums(permutations);
		show(permutations);
		checkAllPermutationsWereGenerated(permutations, nNodes, missingNodes);

	}

	@Test
	public void testPermutationsForRandomValuesAlternative() {
		List<Node> nodes = new ArrayList<Node>();
		int missingNodes = 1;
		int nNodes = 20;
		for (int i = 0; i < nNodes; i++)
			nodes.add(new Node("id_" + i, "type" + i % 2));

		List<Alternative> alternatives = new ArrayList<>();

		for (int i = 0; i < nodes.size(); i++) {
			Node n1 = nodes.get(i);
			alternatives.add(new Alternative(n1, 
					new Match(n1, n1,Configs.getDefaultConfigs())));
		}

		for (Alternative a : alternatives) {
			a.delta = 100 - nextInt(0, 100);
		}

		Collections.sort(alternatives);

		for (Alternative a : alternatives)
			System.out.println(a.getIntegerValue());

		DecreasingFixedPermutations<Alternative> permutations = new DecreasingFixedPermutations<>(
				alternatives, missingNodes);

		showAlternative(permutations);
		checkAllPermutationsWereGenerated(permutations, nNodes, missingNodes);
		checkIncreasingSumsAlternative(permutations);

		showAlternative(permutations);
		// checkAllPermutationsWereGenerated(permutations, nNodes,
		// missingNodes);

	}

	private void showAlternative(
			DecreasingFixedPermutations<Alternative> permutations) {

		for (List<Alternative> seq : permutations)
			System.out.println(String.format("%3d %s", sumAlternative(seq),
					toStringAlternative(seq)));
		System.out.println("");
	}

	private void checkIncreasingSumsAlternative(
			DecreasingFixedPermutations<Alternative> permutations) {
		int prev = 0;
		for (List<Alternative> seq : permutations) {
			int sum = sumAlternative(seq);
			assertTrue(sum >= prev);
			prev = sum;
		}
	}

	private void checkIncreasingSums(
			DecreasingFixedPermutations<Match> permutations) {
		int prev = 0;
		for (List<Match> seq : permutations) {
			int sum = sum(seq);
			assertTrue(sum >= prev);
			prev = sum;
		}
	}

	private void checkAllPermutationsWereGenerated(
			DecreasingFixedPermutations<?> permutations, int nodes, int missing) {
		int counter = 0;
		for (@SuppressWarnings("unused") List<?> seq : permutations)
			counter++;
		assertEquals("All the permutations were generated", fact(nodes)
				/ (fact(missing) * fact(nodes - missing)), counter);
	}

	private long fact(int n) {
		long fact = 1;
		for (int i = 1; i <= n; i++)
			fact *= i;
		System.out.println(fact);
		return fact;
	}

	private int nextInt(int i, int j) {
		Random random = new Random();
		if (i == j)
			return i;
		return i + random.nextInt(j + 1 - i);
	}

	private void show(DecreasingFixedPermutations<Match> adder) {

		for (List<Match> seq : adder)
			System.out
					.println(String.format("%3d %s", sum(seq), toString(seq)));
		System.out.println("");
	}

	private String toString(List<Match> seq) {
		String string = "[";
		for (Match m : seq) {
			string += m.getIntegerValue() + ",";
		}
		return string + "]";
	}

	private String toStringAlternative(List<Alternative> seq) {
		String string = "[";
		for (Alternative m : seq) {
			string += m.getIntegerValue() + ",";
		}
		return string + "]";
	}

	private int sumAlternative(List<Alternative> seq) {
		int sum = 0;

		for (Alternative value : seq)
			sum += value.getIntegerValue();

		return sum;
	}

	private int sum(List<Match> seq) {
		int sum = 0;

		for (Match value : seq)
			sum += value.getIntegerValue();

		return sum;
	}

	@Test
	public void checkAverageAndStandardDeviation() {
		Map<Integer, Integer> diffsCount = new HashMap<>();
		int nElements = 0;
		int values[] = new int[] { 2, 4, 4, 4, 5, 5, 7, 9 };

		for (int diff : values) {
			nElements++;
			int count = 0;
			if (diffsCount.containsKey(diff)) {
				count = diffsCount.get(diff);
			}

			diffsCount.put(diff, (++count));

		}

		int sum = 0;
		for (int diff : diffsCount.keySet())
			sum += diff * diffsCount.get(diff);

		double average = sum / (double) nElements;

		double sum2 = 0;
		for (int diff : diffsCount.keySet()){
			double value = diffsCount.get(diff) * (diff - average) * (diff - average);
			sum2 += value;
			System.out.println(value);
		}

		double variation = Math.sqrt(sum2 / (nElements));

		System.out.println("M�dia:" + average + "\nDesvio:" + variation);
	}
}