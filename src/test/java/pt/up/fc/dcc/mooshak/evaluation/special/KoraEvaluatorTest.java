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

public class KoraEvaluatorTest {
	KoraEvaluator evaluator;
	Set<Path> trash = new HashSet<>();
	
//	@BeforeClass
//	public static void setUpBeforeClass() throws MooshakContentException {
//		PersistentObject.setHome(CustomData.HOME);
//	}
	
	@Before
	public void setUp() throws Exception {
		Evaluator.setTimeout(2);
		evaluator = new KoraEvaluator();
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
	public void EERtest() {
		EvaluationParameters parameters = new EvaluationParameters();
		
		Path solJsonFile = Paths.get("inputs/eer/empresaSolution.eer");
		Path attJsonFile = Paths.get("inputs/eer/empresaAttempt.eer");
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
}
