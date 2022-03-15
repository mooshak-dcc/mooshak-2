package pt.up.fc.dcc.mooshak.server.commands;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import pt.up.fc.dcc.mooshak.client.services.QuizCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator.ExamGenerator;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Service implementation for Quiz commands
 * 
 * @author Helder Correia <code>hppc25@gmail.com</code>
 */
public class QuizCommandServiceImpl extends CommandService implements QuizCommandService {
	private static final long serialVersionUID = 1L;

	

	public String getQuizHTML(String input) throws MooshakException {
		
		
		
//		String text = quizManager.getHTML(); 
		
		
		
		// Verify that the input is valid. 
//		if (!FieldVerifier.isValidName(input)) {
//			// If the input is not valid, throw an IllegalArgumentException back to
//			// the client.
//			throw new IllegalArgumentException("Name must be at least 4 characters long");
//		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
//		input = escapeHtml(text);
//		userAgent = escapeHtml(userAgent);

//		return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
//				+ userAgent;
		return getHtml(input);
//				+ userAgent;
	}
	
	private String getHtml(String problemId){
		String html="";
		Session session = getSession();
		 
		try {
			html = quizManager.getHTML(session, problemId);
		} catch (MooshakException | IOException | TransformerException e) {
			e.printStackTrace();
		} 
		return html;
	}


	@Override
	public String getQuizHTMLFinal(String name) throws MooshakException {
		
		Session session = getSession();
		ExamGenerator eg= new ExamGenerator();	
		
		return quizManager.analyzerQuiz(session,name);
		
//		try {
//			return eg.generateHtmlFinal("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/attemptAnalyzed.xml");
//		} catch (TransformerException e) {
//			e.printStackTrace();
//		}
		
		
		
		//return "";
	}
	
}
