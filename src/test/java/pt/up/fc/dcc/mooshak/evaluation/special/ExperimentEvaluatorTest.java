package pt.up.fc.dcc.mooshak.evaluation.special;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluator;

public class ExperimentEvaluatorTest {
	ExperimentEvaluator evaluator;
	Set<Path> trash = new HashSet<>();
	
//	@BeforeClass
//	public static void setUpBeforeClass() throws MooshakContentException {
//		PersistentObject.setHome(CustomData.HOME);
//	}
	
	@Before
	public void setUp() throws Exception {
		Evaluator.setTimeout(2);
		evaluator = new ExperimentEvaluator();
	}
	
	@After
	public void cleanUp() throws IOException {
		
		for(Path path: trash)
			Files.deleteIfExists(path);
	}
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void CasoUsoGetSolutionTest() {
		EvaluationParameters parameters = new EvaluationParameters();
		
		Path solJsonFile = Paths.get("inputs/casouso/attemptCasoUso.json");
		Set<Path> solutions = new HashSet<>();
		
		solutions.add(solJsonFile);
		
		parameters.setProgram(solJsonFile);
		parameters.setSolutions(solutions);
		
	
		
		try {
			evaluator.evaluate(parameters);
		} catch (MooshakEvaluationException cause) {
			fail("Unexpected exception");
		}	
	}
	
	
	@Test
	public void CasoUsotest() {
		EvaluationParameters parameters = new EvaluationParameters();
		
		Path solJsonFile = Paths.get("inputs/casouso/SolutionCasoUso.json");
		Path attJsonFile = Paths.get("inputs/casouso/attemptCasoUso.json");
		Set<Path> solutions = new HashSet<>();
		
		solutions.add(solJsonFile);
		
		parameters.setProgram(attJsonFile);
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
		} catch (MooshakEvaluationException cause) {
			fail("Unexpected exception");
		}	
		
		
		
	}
	
	@Test
	public void EERtest1() {
		EvaluationParameters parameters = new EvaluationParameters();
		
		Path solJsonFile = Paths.get("inputs/eer/empresaSolution.eer");
		Path attJsonFile = Paths.get("inputs/eer/empresaAttempt.eer");
		Set<Path> solutions = new HashSet<>();
		solutions.add(solJsonFile);
		
		Problem problem = new Problem();
		problem.setName("E");
		
		Team team1 = new Team();
		team1.setName("Aluno1");
		
		Team team2 = new Team();
		team2.setName("Aluno2");
		System.out.println("***********************************");
		
		System.out.println("team1 "+team1.hashCode());
		System.out.println("team2 "+team2.hashCode());
		System.out.println("probleam "+problem.hashCode());
		
		
		
		parameters.setTeam(team1);
		parameters.setProblem(problem);
		parameters.setProgram(attJsonFile);
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
		} catch (MooshakEvaluationException cause) {
			fail("Unexpected exception");
		}	
		
	}
	
	

	@Test
	public void EERtest2() {
		EvaluationParameters parameters = new EvaluationParameters();
		
		Path solJsonFile = Paths.get("inputs/eer/empresaSolution.eer");
		Path attJsonFile = Paths.get("inputs/eer/empresaAttempt.eer");
		Set<Path> solutions = new HashSet<>();
		solutions.add(solJsonFile);
		
		Problem problem = new Problem();
		problem.setName("C");
		
		Team team1 = new Team();
		team1.setName("BB");
		
		System.out.println("***********************************");
		
		System.out.println("team1 "+team1.hashCode());
		System.out.println("probleam "+problem.hashCode());
		
		
		
		parameters.setTeam(team1);
		parameters.setProblem(problem);
		parameters.setProgram(attJsonFile);
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
		} catch (MooshakEvaluationException cause) {
			fail("Unexpected exception");
		}	
		
	}
}
