package pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class ExamGenerator {
	String pathSolution;
	
	public ExamGenerator(){
	///	this.pathSolution=pathSolution;
	};
	
	public void mooGenerator (String bdXML, String solutionXML) throws MooshakException, TransformerException {
//		Transform factory =  
//				 Transform.newInstance();
			TransformerFactoryImpl fact = new net.sf.saxon.TransformerFactoryImpl();
//		  TransformerFactory fact = new TransformerFactoryImpl().newInstance();
		 if(this.pathSolution == null)
			 this.pathSolution="src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/mooTomoo.xsl";
	        Source xslt = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/mooTomoo.xsl"));
	        Transformer transformer = fact.newTransformer(xslt);
	        System.out.println("PATH EXAM GENERATOR "+ bdXML);
	        //Source text = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/mooInput.xml"));
	        Source text = new StreamSource(new File(bdXML));
//	        transformer.transform(text, new StreamResult(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/output.xml")));
	        transformer.transform(text, new StreamResult(new File(solutionXML)));
		      
	        System.out.println("Done xml");
	}

	
	public String  htmlgenerator (String solutionXML) throws MooshakException, TransformerException {
			
		TransformerFactoryImpl fact = new net.sf.saxon.TransformerFactoryImpl();
		 
	        Source xslt = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/mooToHTML.xsl"));
	        Transformer transformer = fact.newTransformer(xslt);

	       // Source text = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/output.xml"));
	       
	        Source text = new StreamSource(new File(solutionXML)); 
	        //transformer.transform(text, new StreamResult(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/output.html")));
	       
	        StringWriter writer = new StringWriter();
	   	 	StreamResult result = new StreamResult(writer);
	        transformer.transform(text, result);
	        
	        String strResult = writer.toString();
	        
	        System.out.println("Done html");
	        
	        return strResult;
	        
	       
	        
	        
	}
	
	public void analyzer (String submissionPath, String outputPath) throws MooshakException, TransformerException {
//		Transform factory =  
//				 Transform.newInstance();
			TransformerFactoryImpl fact = new net.sf.saxon.TransformerFactoryImpl();
//		  TransformerFactory fact = new TransformerFactoryImpl().newInstance();
		 
	        Source xslt = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/correction.xsl"));
	        Transformer transformer = fact.newTransformer(xslt);

	        
	        Source text = new StreamSource(new File(submissionPath));
	        transformer.transform(text, new StreamResult(new File(outputPath)));
	        
	      //  Source text = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/attempt.xml"));
	       // transformer.transform(text, new StreamResult(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/attemptAnalyzed.xml")));
	        
	        
	        System.out.println("Done revised Analyzer");
	}
	
	public String generateHtmlFinal (String xmlFile) throws MooshakException, TransformerException {
			
//			TransformerFactoryImpl fact = new net.sf.saxon.TransformerFactoryImpl();
//	        
//			Source xslt = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/revised.xsl"));
//	        Transformer transformer = fact.newTransformer(xslt);
//
//	        Source text = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/attemptAnalyzed.xml"));
//	        transformer.transform(text, new StreamResult(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/htmlFinal.html")));
//	        
//	        return "test";
	        
		
		TransformerFactoryImpl fact = new net.sf.saxon.TransformerFactoryImpl();
        
		Source xslt = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/evaluation/quiz/examgenerator/revised.xsl"));
        Transformer transformer = fact.newTransformer(xslt);

//        Source source = new StreamSource(new File("src/main/java/pt/up/fc/dcc/mooshak/shared/quiz/attemptAnalyzed.xml"));
        Source source = new StreamSource(new File(xmlFile));
        
        StringWriter writer = new StringWriter();
   	 	StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        
        String strResult = writer.toString();
        System.out.println(" \n\n\n Done revised");
        return strResult;
	    	    
	}
}
