package pt.up.fc.dcc.mooshak.evaluation.kora;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.evaluation.kora.EvaluationController;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.FeedbackManager;

public class EvaluationControllerTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}


	@Test
	public void TestExtension() throws MooshakException, FileNotFoundException{
		
		Configuration fileConfig= new Configuration("inputs/class/ClassConfig.xml");
		String attemptPath = "inputs/casouso/CasoUso.umluc";
		String solutionPath = "inputs/casouso/SolutionCasoUso.json";
				
		Diagram diagram = fileConfig.getDL2().getDiagram();
		EvaluationController ec = new EvaluationController(solutionPath, attemptPath,diagram);
		
		//ec.evaluation();
		System.out.println("Error syntaxe: " +ec.evaluationSyntax());
		System.out.println(ec.getTextFeedback());
		System.out.println(ec.getJsonFeedback());
		
	}
	
	@Test
	public void EvaluationController() throws MooshakException, FileNotFoundException{

//		Configuration to use case
//		Configuration fileConfig= new Configuration("inputs/casouso/CasoUsoConfig.xml");
//		String attemptPath = "inputs/casouso/attemptCasoUso.json";
//		String solutionPath = "inputs/casouso/SolutionCasoUso.json";
		
		Configuration fileConfig= new Configuration("inputs/class/class.xml");
		String attemptPath = "inputs/casouso/attemptCasoUso.json";
		String solutionPath = "inputs/casouso/SolutionCasoUso.json";
				
		Diagram diagram = fileConfig.getDL2().getDiagram();
		EvaluationController ec = new EvaluationController(solutionPath, attemptPath,diagram);
		
		//ec.evaluation();
		System.out.println("Error syntaxe: " +ec.evaluationSyntax());
		System.out.println(ec.getTextFeedback());
		System.out.println(ec.getJsonFeedback());
		System.out.println();
		
		FeedbackManager fm= new FeedbackManager(diagram.getUrlMap());
		ec.generateFeedback(fm);
		
		System.out.println(ec.getTextFeedback());
		System.out.println(ec.getJsonFeedback());
		
		
		ec.generateFeedback(fm);
		System.out.println();
		System.out.println(ec.getTextFeedback());
		System.out.println(ec.getJsonFeedback());
		
		
		
//		for(NodeInfo entry : evalInfo.getNodesInfo())
//			System.out.println(entry.getType() +"  : "+entry.getCode());
//		
//		for(EdgeInfo entry : evalInfo.getEdgesInfo())
//			System.out.println(entry.getType() +"  : "+entry.getCode());
//		
//		
//		System.out.println(evalInfo.getUrlMap());
//		for(Entry<String, String> entry: evalInfo.getUrlMap().entrySet()){
//			System.out.println(entry.getKey());
//			System.out.println(entry.getValue());
//			//System.out.println();
//		}
		//System.out.println(evalInfo.getErrorRate());
		
		
	}


	@Test
	public void EvaluationControllerEER() throws MooshakException, FileNotFoundException{

		
		Configuration fileConfig= new Configuration("inputs/eer/eer2.xml");
		
//		String attemptPath = "inputs/eer/eerTest.eer"; 
//		String solutionPath = "inputs/eer/eerInput.eer";
		
		String attemptPath = "inputs/eer/empresaAttempt.eer";
		String solutionPath = "inputs/eer/empresaSolution.eer";
		
		//***CASO DE USO
		//String attemptPath = "inputs/fcup3.json";
		//String solutionPath = "inputs/fcup2.json";
				
		Diagram diagram = fileConfig.getDL2().getDiagram();
		EvaluationController ec = new EvaluationController(solutionPath, attemptPath,diagram);
		
		
		//ec.evaluation();
//		System.out.println("Error syntaxe: " +ec.evaluationSyntax());
//		System.out.println(ec.getTextFeedback());
//		System.out.println("GRADE "+ec.getGrade());
//		System.out.println(ec.getJsonFeedback());
		System.out.println();
	
		FeedbackManager fm= new FeedbackManager(diagram.getUrlMap());
		ec.generateFeedback(fm);
//		System.out.println("BM "+ec.getEvaluation().getBestMap());
		System.out.println("Grade : "+ ec.getGrade());
		
		
//		System.out.println(ec.getTextFeedback());
////		System.out.println(ec.getJsonFeedback());
//		
//		System.out.println();
		
//		ec.generateFeedback(fm);
////		System.out.println();
//		System.out.println(ec.getTextFeedback());
////		System.out.println(ec.getJsonFeedback());
//		
//		System.out.println();
		System.out.println(ec.getTextFeedback());
		
		System.out.println();

//		ec.generateFeedback(fm);
////		System.out.println();
//		System.out.println(ec.getTextFeedback());
		
	}



}
