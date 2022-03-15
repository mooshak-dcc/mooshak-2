package pt.up.fc.dcc.mooshak.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator.ExamGenerator;
import pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator.GetMark;
import pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator.JSONHandler;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Manages all Quiz requests. This class ignores types from any particular
 * communication layer, such as GWT RPC or Jersey
 * 
 * @author helder correia <code>hppc25@gmail.com</code>
 */
public class QuizManager extends Manager {
	
	private static QuizManager quizManager = null;

	/**
	 * Get a single instance of this class
	 * 
	 * @return {@link QuizManager} 
	 * @throws MooshakException
	 */
	public static QuizManager getInstance() {

		if (quizManager == null)
			quizManager = new QuizManager();
		return quizManager;
	}

	private QuizManager() {
	}

	/**************************************************************
	 *                        Methods                             
	 * @throws IOException 
	 * @throws TransformerException *
	 **************************************************************/
	
	public String getHTML(Session session, String problemId) throws MooshakException, IOException, TransformerException{
		
		 System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		 
		 
		 Contest contest = session.getContest(); 
		 Problems problems = contest.open("problems");
		 Problem problem = problems.open(problemId);
		 Collection<Path> solutions = problem.getSolutions();
		
		String solutionXML="";
	
		solutions = problem.getSolutions();
		
		for(Path path: solutions) {
			solutionXML = PersistentCore.getAbsoluteFile(path).toString();
		}

		String bdXML = PersistentCore.getAbsoluteFile(problem.getQuiz()).toString();	
		 
		ExamGenerator eg= new ExamGenerator();
		eg.mooGenerator(bdXML,solutionXML);
		eg.htmlgenerator(solutionXML);
		
		return eg.htmlgenerator(solutionXML);
	}
	
	
	public String analyzerQuiz(Session session, String submissionId) throws MooshakException {
		
		Contest contest = session.getContest(); 
		Submissions submissions = contest.open("submissions");
		Submission submission= submissions.open(submissionId);
		
		
		// Problems problems = contest.open("problems");
		// Problem problem = problems.open(problemId);
		
		 
		 
		 Collection<Path> solutions = submission.getProblem().getSolutions();
		 
			
			String solutionXML="";
			
			
//			Problems problems = submission.getProblem();
			Problem problem = submission.getProblem();
			String bdQuiz= PersistentCore.getAbsoluteFile(problem.getQuiz()).toString();
			String submissionPathFolder =  PersistentCore.getAbsoluteFile(submission.getAbsoluteFile()).toString();
			//String json =  PersistentCore.getAbsoluteFile(submission.re(submission.getProgram())).toString();
			String json =  PersistentCore.getAbsoluteFile(submission.getProgram()).toString();
			
			String attemptXML =submissionPathFolder+"/attempt.xml";
			String analyzer = submissionPathFolder+"/analyzer.xml";
					//PersistentCore.getAbsoluteFile(submission.getProgram().getFileName()).toString();
			System.out.println("BDQUIZ");
			System.out.println(bdQuiz);
			System.out.println();
			System.out.println(submissionPathFolder);
			System.out.println();
			System.out.println(json);
			System.out.println();
			
			
			
			
			

			JSONHandler jsonHandler = new JSONHandler(bdQuiz);
			jsonHandler.update(json,attemptXML );
			
			
			//String bdXML = PersistentCore.getAbsoluteFile(problem.getQuiz()).toString();
		
			ExamGenerator eg= new ExamGenerator();	
		
		
		
		try {
			eg.analyzer(attemptXML,analyzer); 
			String mark = GetMark.get(analyzer);
			System.out.println("mark "+mark);
			int markInt= Integer.parseInt(mark);
			submission.setMark(markInt);
			System.out.println("markInt "+markInt);
			return eg.generateHtmlFinal(analyzer);
		} catch (TransformerException e) {
			System.out.println("ERROR: "+ e.toString());
		}
		
		
		
		return "";
	}
	
//	public String analyzerAttempt(Session session, String submissionId) throws MooshakException{
//		Contest contest = session.getContest(); 
//		Submissions submissions = contest.open("submissions");
//		Submission submission= submissions.open(submissionId);
//	}
}
