package pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class GetMark {
	
	public static String get(String filepath){
		
		
		
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//		docFactory.setValidating(false);
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			
			Document doc=docBuilder.parse(filepath); 
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath=factory.newXPath();
			
			String expression = "/quiz/config/@mark";
			//String expression = "sum(/quiz/group/@score)";
			try {
				return (String) xpath.evaluate(expression, doc, XPathConstants.STRING);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			
			
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
        return null;
     
	}
	



		 
		  
		
}

