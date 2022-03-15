package pt.up.fc.dcc.mooshak.evaluation.kora.feedback;

import static org.junit.Assert.*;


import org.junit.Test; 
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.evaluation.kora.EvaluationController;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;
import pt.up.fc.dcc.mooshak.evaluation.kora.semantics.EvaluationWithGrade;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class FeedbackManagerTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void summarizeTest() throws MooshakException {

		Configuration fileConfig= new Configuration("inputs/casouso/CasoUsoConfig.xml");
		String attemptPath = "inputs/casouso/attemptCasoUso.json";
		String solutionPath = "inputs/casouso/SolutionCasoUso.json";
		Diagram diagram = fileConfig.getDL2().getDiagram();
		EvaluationController ec = new EvaluationController(solutionPath, attemptPath,diagram);
		
		
		Team team = new Team();
		team.setName("team");
		team.setPassword("plain");
		team.setEmail("email@fc.up.pt");
		
		Problem probleam = new Problem();
		probleam.setName("graph");
		probleam.setDifficulty(null);
		probleam.setTitle("title");	
		
		//Key key = new Key(team, probleam); 
		
		FeedbackManager fm= new FeedbackManager(diagram.getUrlMap());
		ec.generateFeedback(fm);
//		fm.setLang(fm.lang.PT);
//		fm.summarize(key,evalInfo);
//		System.out.println(); 
//		fm.summarize(key,evalInfo);
//		System.out.println();
//		fm.summarize(key,evalInfo);
		//fm.summarize(key,evalInfo);
		//System.out.println(ec.solution);
		
		EvaluationWithGrade evaluation = new EvaluationWithGrade(ec.solution, ec.attempt, ec.evaluation);
		fm.summarize(evaluation.getDifferences());
		System.out.println(fm.getTextFeedback());
		System.out.println(fm.getJsonFeedback());
		System.out.println();
		
		fm.summarize(evaluation.getDifferences());
		System.out.println(fm.getTextFeedback());
		System.out.println(fm.getJsonFeedback());
		System.out.println();
		
		fm.summarize(evaluation.getDifferences());
		System.out.println(fm.getTextFeedback());
		System.out.println(fm.getJsonFeedback());
		System.out.println();
		
		fm.summarize(evaluation.getDifferences());
		System.out.println(fm.getTextFeedback());
		System.out.println(fm.getJsonFeedback());
		System.out.println();
		
		
//		fm.summarize(key,evalInfo);
//		System.out.println(fm.getTextualFeeback());
//		System.out.println(fm.getJsonFeedback());
//		System.out.println();
//		
//		fm.summarize(key,evalInfo);
//		System.out.println(fm.getTextualFeeback());
//		System.out.println(fm.getJsonFeedback());
//		
//		
//		fm.summarize(key,evalInfo);
//		System.out.println(fm.getTextualFeeback());
//		System.out.println(fm.getJsonFeedback());
		
	
	}
	
	@Test
	public void getStringMessageTest() throws MooshakException {
		
		
		Configuration fileConfig= new Configuration("inputs/casouso/CasoUsoConfig.xml");
		Diagram diagram = fileConfig.getDL2().getDiagram();
		
		FeedbackManager fm= new FeedbackManager(diagram.getUrlMap());
		String result=fm.getStringMessage(1, "insertNode", "1","Attribute");
		System.out.println(result);
		
		 assertEquals("1;insertNode;1;Attribute",result);
	}
	

}
