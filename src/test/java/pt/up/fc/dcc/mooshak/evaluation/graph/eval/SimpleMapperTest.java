package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.SimpleMapper;

public class SimpleMapperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSizeAndFactI() throws FileNotFoundException {
		testSize("input.txt", "input.txt",3,6);
	}
	
	@Test
	public void testSizeAndFactII() throws FileNotFoundException {
		testSize("questionario_sol.txt", "questionario_sol.txt",7,5040);
	}
	
	private void testSize(String solutionFilename, String attemptFilename, 
			int n, int f) 
			throws FileNotFoundException {
		
		Graph solution = new Graph(solutionFilename);
		Graph attempt = new Graph(attemptFilename);
		SimpleMapper mapper = new SimpleMapper(solution, attempt
				,Configs.getDefaultConfigs());
		
		assertEquals(n,mapper.n);
		assertEquals(f,mapper.fact[n]);
	}
	
	@Test
	public void testMapperInput() throws FileNotFoundException {
		testMapper("input.txt", "input.txt");
	}

	@Test
	public void testMapperInputII() throws FileNotFoundException {
		testMapper("questionario_sol.txt", "questionario_sol.txt");
	}

	private void testMapper(String solutionFilename, String attemptFilename)
			throws FileNotFoundException {

		Graph solution = new Graph(solutionFilename);
		Graph attempt = new Graph(attemptFilename);
		SimpleMapper mapper = new SimpleMapper(solution, attempt
				,Configs.getDefaultConfigs());

		int count = 0;
		for (Map<Node, Match> map : mapper) {
			// System.out.println("map " + count);
			assertTrue("Maps must be consistent (no duplicate images)",isConsistent(map));
			count++;
		}
		assertEquals("should be n!",mapper.fact[mapper.n],count);
		
		// System.out.println(count);
	}

	private boolean isConsistent(Map<Node, Match> map) {
		Set<Match> matches = new HashSet<>();

		for (Match match : map.values()) {
			if (matches.contains(match)) {
				/*System.out.print(match.attempt.id + " -> ");
				for(Match m: matches)
					System.out.print("|" + m.attempt.id);
				System.out.println("");*/
				return false;
			} else
				matches.add(match);
		}
		return true;
	}


}
