package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.GraphGenerator;
import pt.up.fc.dcc.mooshak.evaluation.graph.RelatedGraphs;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class OptimizedMapperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMapperInput3() throws FileNotFoundException {
		testMapper("input.txt", "input.txt");
	}

	@Test
	public void testMapperInput5() throws FileNotFoundException {
		testMapper("input5.txt", "input5.txt");
	}

	private void testMapper(String solutionFilename, String attemptFilename)
			throws FileNotFoundException {

		Graph solution = new Graph(solutionFilename);
		Graph attempt = new Graph(attemptFilename);

		Mapper mapper = new OptimizedMapper(solution, attempt, new CoreMapper(
				solution, attempt,Configs.getDefaultConfigs()));

		int count = 0;
		int lastNodeProximity = Integer.MAX_VALUE;
		for (Map<Node, Match> map : mapper) {
			System.out.println("map " + count);
			int nodeProx = getMapNodeProximity(map);

			assertTrue("Map node proximity must be decreesing",
					nodeProx <= lastNodeProximity);
			lastNodeProximity = nodeProx;

			assertTrue("Maps must be consistent (no duplicate images)",
					isConsistent(map));
			count++;
			if(count > 117)
				break;
		}

		System.out.println(count);
	}

	@Test
	public void checkDecreasingNodeContribution() {
		GraphGenerator g = new GraphGenerator();
//		g.setSeed(-6066797808251394890L);
		for (int i = 0; i < 1; i++) {
			
			g.setParameters(1, 0, 0);
			g.setMinNodes(9);
			g.setMaxNodes(9);
			g.setSeed(-5427698293948948181L);
			Graph original = g.generate();
			RelatedGraphs related = g.generate(original);

			CoreMapper core = new CoreMapper(original, related.getAttempt()
					,Configs.getDefaultConfigs());
			for (RelatedGraphs pair : core) {
				int last = Integer.MAX_VALUE;
				OptimizedMapper mapper = new OptimizedMapper(pair.getSolution(),
						pair.getAttempt(), core);
				int counter = 0;
//				for(Node n : core.bests.keySet())
//					System.out.println(n.getId() + "->" + core.bests.get(n).attempt.getId());
				for (Map<Node, Match> map : mapper) {
//					System.out.println(counter);
					if(counter == 200)
						break;
					int sum = 0;
					for (Node node : map.keySet())
						sum += map.get(node).value;
					System.out.println(sum);
					assertTrue(last >= sum);
					last = sum;
					counter++;
				}
				break;
			}
		}
	}

	private int getMapNodeProximity(Map<Node, Match> map) {
		int nodeProx = 0;
		for (Node node : map.keySet())
			nodeProx += map.get(node).value;

		return nodeProx;
	}

	private boolean isConsistent(Map<Node, Match> map) {
		Set<Match> matches = new HashSet<>();

		for (Match match : map.values()) {
			if (matches.contains(match)) {
				return false;
			} else
				matches.add(match);
		}
		return true;
	}

	@Test
	public void testMapSize() {
		GraphGenerator generator = new GraphGenerator();
		int nTests = 100;
		for (int i = 0; i < nTests; i++) {
			Graph solution = generator.generate();
			generator.setParameters(1, 0, 0);
			Graph attempt = generator.generate(solution).getAttempt();

			CoreMapper core = new CoreMapper(solution, attempt
					,Configs.getDefaultConfigs());

			for (RelatedGraphs pair : core) {
				OptimizedMapper mapper = new OptimizedMapper(pair.getSolution(),
						pair.getAttempt(), core);
				for (Map<Node, Match> map : mapper) {
					assertEquals("Map has correct size", pair.getSolution()
							.getNodes().size(), map.size());
					break;
				}
			}
		}

	}
}
