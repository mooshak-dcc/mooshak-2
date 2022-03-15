package pt.up.fc.dcc.mooshak.evaluation.quiz.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JSONHandlerEditor {
	private static final String QUIZ_NAMESPACE = "http://mooshak.dcc.fc.up.pt/quiz";
	
	
	public static String jsonToXml(String json) {
		
		if (json == null || json.trim().isEmpty())
			return null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    Document doc = null;
		
        try {     
        	
        	dbf.setNamespaceAware(true);
		      DocumentBuilder db = dbf.newDocumentBuilder();
		      doc = db.newDocument();
		      
        	
        	JsonReader jsonReader = Json.createReader(new StringReader(json));
        	System.out.println(jsonReader);
        	JsonObject quizObject= jsonReader.readObject();
        	jsonReader.close();
        	
            
            JsonArray jsonArray =  quizObject.getJsonArray("groups");
           
            Element quizRootElement = doc.createElementNS(QUIZ_NAMESPACE, "quiz");
            quizRootElement.setPrefix("q");
            quizRootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:h", "http://www.w3.org/1999/xhtml");
            quizRootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fun", "http://example.org/xslt/functions");
            quizRootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fn", "http://www.w3.org/2005/xpath-functions");
            
        	doc.appendChild(quizRootElement); // Add Root to Document
        	
        	  JsonObject config=quizObject.getJsonObject("config");   
        	 
        	  
        	  Element configElement = doc.createElementNS(QUIZ_NAMESPACE, "config");
        	  configElement.setPrefix("q");;
              quizRootElement.appendChild(configElement);
              
              configElement.setAttribute("name", config.getString("name"));
              configElement.setAttribute("duration", config.getString("duration"));
              configElement.setAttribute("revision", String.valueOf(config.getBoolean("revision")));
              configElement.setAttribute("shuffle", String.valueOf(config.getBoolean("shuffle")));
    		    
    		    
        	  
            for (int i=0; i<jsonArray.size();i++) {
            	
            	JsonObject group= jsonArray.getJsonObject(i);
            	
            	Element groupElement = doc.createElementNS(QUIZ_NAMESPACE, "group");
            	groupElement.setPrefix("q");;
            	quizRootElement.appendChild(groupElement);
           
      		    groupElement.setAttribute("id","G"+(i+1));
      		    groupElement.setAttribute("name", group.getString("name"));
      		    groupElement.setAttribute("numberQuestion", group.getString("numberQuestion"));
      		    groupElement.setAttribute("score", group.getString("score"));
      		    
      		
      		    groupElement.setAttribute("shuffle",String.valueOf(group.getBoolean("shuffle")));
      		 
            	
            	JsonArray   listQA = group.getJsonArray("listQA");
            	  for (int k=0; k<listQA.size();k++) {
            		  
            		  	JsonObject qa = listQA.getJsonObject(k);
    	            	String   type =qa.getString("type");
		            
		            	JsonArray   answers =  qa.getJsonArray("answer");
            		  
		            	Element qaElement = doc.createElementNS(QUIZ_NAMESPACE, "QA");
		            	qaElement.setPrefix("q");
		 	  		    groupElement.appendChild(qaElement);
		 	  		    qaElement.setAttribute("type",type.toLowerCase());
		 	  		    qaElement.setAttribute("id","G"+(i+1)+"Q"+(k+1));
		 	  		    System.out.println("aqui");
		 	  		    System.out.println(qa.containsKey("shuffle"));
		 	  		   // qaElement.setAttribute("shuffle",String.valueOf(qa.getBoolean("shuffle")));
		 	  		    
		 	  		   
		 	  		    
		 	  		    
		 	  		 String   question = qa.getString("question");
		 	  		    Element qaQuestion = doc.createElementNS(QUIZ_NAMESPACE, "question");
		 	  		    qaQuestion.setPrefix("q");
		 	  		   
		 	  		    
		 	  		    if(!type.toLowerCase().equals("fillintheblank")){
		 	  		    	qaQuestion.appendChild(doc.createTextNode(question));
		 	  		    	qaElement.appendChild(qaQuestion);
//			 	  		
		 	  		    
		 	  		 Element qaAnswer = doc.createElementNS(QUIZ_NAMESPACE, "answer");
		 	  		         qaAnswer.setPrefix("q");
		 	  		    
		            	 for (int j=0; j<answers.size();j++) {
		            		 JsonObject choice = answers.getJsonObject(j);
		            		 Element qaChoice = doc.createElementNS(QUIZ_NAMESPACE, "choice");
		            		 qaChoice.setPrefix("q");
		            	
		                String id= "G"+(i+1)+"Q"+(k+1)+"C"+(j+1);	 
		            	switch (type.toLowerCase()) {
							case "single":
								 createChoiceSingle(qaChoice,choice,doc,id);
								break;
							case "multiple":
								 createChoiceSingle(qaChoice,choice,doc,id);
								break;

							case "shortanswer":
								createChoiceShortAnswer(qaChoice,choice, doc,id);
								break;

							case "matching":
								createChoiceMatching(qaChoice,choice, doc,id);
								break;
								
							case "boolean":
								createChoiceBoolean(qaChoice,choice, doc,id);
								break;
								
							case "numeric":
								createChoiceNumeric(qaChoice,choice, doc,id);
								break;
								
							case "fillintheblank":
								break;

								
							default:
								break;
							}
		            	
		             qaElement.appendChild(qaAnswer);
		           	 qaAnswer.appendChild(qaChoice);
		            		 
		            	 }
		 	  		    }
		            	 
		            	 
		            	 else{
		            		 
				 	  		
		            		 replaceParentese(doc, question,qaQuestion);
				 	  		 qaElement.appendChild(qaQuestion);
		            		
							JsonArray answersArray=  qa.getJsonArray("answer");
						
							 for (int l=0; l<answersArray.size();l++) {
								 Element answerT = doc.createElementNS(QUIZ_NAMESPACE, "answer");
								 answerT.setPrefix("q");
								 qaElement.appendChild(answerT); 
								 JsonObject answersJson = answersArray.getJsonObject(l);
								 JsonArray choicesArray =  answersJson.getJsonArray("answer_");
								 answerT.setAttribute("id", answersJson.getString("id")); 
								 for (int c=0; c<choicesArray.size();c++) {
									 String id= "G"+(i+1)+"Q"+(k+1)+"A"+(l+1)+"C"+(c+1);
									 JsonObject choice_Obje =choicesArray.getJsonObject(c);
									 Element choice_ = doc.createElementNS(QUIZ_NAMESPACE, "choice");
									 choice_.setPrefix("q");
									 answerT.appendChild(choice_);
									 choice_.setAttribute("id",id);
									 choice_.setAttribute("value","false"); 
									 choice_.setAttribute("selected","false");
									 choice_.setAttribute("score",choice_Obje.getString("score"));
									 choice_.appendChild(doc.createTextNode(choice_Obje.getString("text")));
								 }
							 }
		            	 }
		 	  		    
		 	  		 Element reponseElement = doc.createElementNS(QUIZ_NAMESPACE, "response");
		 	  		 reponseElement.setPrefix("q");
		 	  		 qaElement.appendChild(reponseElement);
		 	  		Element valueElement = doc.createElementNS(QUIZ_NAMESPACE, "value");
		 	  		valueElement.setPrefix("q");
		 	  		reponseElement.appendChild(valueElement);
		 	  		valueElement.appendChild(doc.createTextNode("null"));
		            	 
            	  }
            }
            

            
            
//       	 write the content into xml file


            return documentToString(doc);
        } catch ( ParserConfigurationException e) {
			e.printStackTrace();
		}
        return null;
     
	}
	

	private static void replaceParentese(Document doc, String question,Element qaQuestion){
	  
	   String s="";
		for (char ch: question.toCharArray()) {
			
			if(ch=='['){
				qaQuestion.appendChild(doc.createTextNode(s));
				s="";
			}
			
			if(ch==']'){
				
				 Element missingWord = doc.createElementNS(QUIZ_NAMESPACE, "missingWord");
				 missingWord.setPrefix("q");
				 missingWord.appendChild(doc.createTextNode(s));
		  		 qaQuestion.appendChild(missingWord);
		  		 s="";
			}
				if(ch!='[' && ch!=']'){
				s+=ch;
			}
			
		}
		qaQuestion.appendChild(doc.createTextNode(s));
		
		
		
		   
		
	}
	public static void createChoiceSingle(Element qaChoice, JsonObject choice, Document doc, String id){
		
		 	qaChoice.setAttribute("id",id);
		 	qaChoice.setAttribute("score",choice.getString("score"));
		 	qaChoice.setAttribute("selected","false");
		 	qaChoice.appendChild(doc.createTextNode(choice.getString("text")));
		 	
		 	createFeedback(doc, qaChoice, choice);
	}
	
	public static void createChoiceShortAnswer(Element qaChoice,  JsonObject choice, Document doc, String id ){
		System.out.println(12335);
	 	qaChoice.setAttribute("id",id);
	 	qaChoice.setAttribute("score","1");
	 	qaChoice.setAttribute("selected","false");
	 	qaChoice.appendChild(doc.createTextNode(choice.getString("text")));
	 	createFeedback(doc, qaChoice, choice);
}
	
	public static void createChoiceMatching(Element qaChoice,  JsonObject choice, Document doc, String id ){
		
		qaChoice.setAttribute("id",id);
	 	qaChoice.setAttribute("mappedValue",choice.getString("mappedValue")); 
	 	qaChoice.setAttribute("mapKey",choice.getString("key"));
	 	qaChoice.setAttribute("score","1");
	 	//qaChoice.appendChild(doc.createTextNode(choice.getString("text")));
}
	
	public static void createChoiceBoolean(Element qaChoice,  JsonObject choice, Document doc, String id ){
		
		qaChoice.setAttribute("id",id);
	 	qaChoice.setAttribute("value","false"); 
	 	qaChoice.setAttribute("selected","false");
	 	qaChoice.setAttribute("score",choice.getString("score"));
	 	qaChoice.appendChild(doc.createTextNode(choice.getString("bool")));
	 	createFeedback(doc, qaChoice, choice);
	
	}
	
public static void createChoiceNumeric(Element qaChoice,  JsonObject choice, Document doc, String id ){
		
		qaChoice.setAttribute("id",id);
		if(choice.containsKey("low"))
			qaChoice.setAttribute("low",choice.getString("low"));
		if(choice.containsKey("high"))
			qaChoice.setAttribute("high",choice.getString("high"));
		if(choice.containsKey("minimumValue"))
			qaChoice.setAttribute("minimumValue",choice.getString("minimumValue"));
		if(choice.containsKey("minimumValue"))
			qaChoice.setAttribute("maximumValue",choice.getString("maximumValue"));
	 	qaChoice.setAttribute("selected","false");
	 	qaChoice.setAttribute("score",choice.getString("score"));
	 	createFeedback(doc, qaChoice, choice);
	
	}
	

public static void createChoiceFillintheBlank(Element qaAnswer,  JsonObject choice, Document doc  ){
	
	
	JsonArray answers=  choice.getJsonArray("answer_");
	 for (int j=0; j<answers.size();j++) {
		 Element qaChoice = doc.createElementNS(null, "choice");
		 qaAnswer.appendChild(qaChoice);
		 qaChoice.appendChild(qaChoice);
		 qaChoice.setAttribute("id","5656");
		 qaChoice.setAttribute("value","false"); 
		 qaChoice.setAttribute("selected","false");
		 qaChoice.setAttribute("score",choice.getString("score"));
		 qaChoice.appendChild(doc.createTextNode(choice.getString("text")));
		
	 }
}
	
	public static void createFeedback(Document doc, Element qaChoice,  JsonObject choice){
		
		System.out.println(choice);
		if(!choice.getString("feedback").equals("")){
	 		 String   feedbackText = choice.getString("feedback");
	  		 Element feedbackElement = doc.createElementNS(QUIZ_NAMESPACE, "feedback");
	  		 feedbackElement.setPrefix("q");
	  		 feedbackElement.appendChild(doc.createTextNode(feedbackText));
	  		qaChoice.appendChild(feedbackElement);
	  		
		        
	 	}
	}
	
	
	  
	  public  static void  createGroup(Document doc, Element quizElement, JsonObject groupConfig) { 
		  Element group = doc.createElementNS(null, "group"); // Create Root Element
		  System.out.println(quizElement);
		  quizElement.appendChild(group); // Add Root to Document
		  
		  group.setAttribute("id","d2e1");
		  group.setAttribute("name",groupConfig.getString("name"));
		  group.setAttribute("numberQuestion",groupConfig.getString("numberQuestion"));
		  group.setAttribute("score",groupConfig.getString("score"));
		  group.setAttribute("shuffle", String.valueOf(groupConfig.getBoolean("shuffle")));
		  doc.appendChild(quizElement);
		  
	  }

		  public static  String documentToString(Document document) {
		    try {
		      TransformerFactory tf = TransformerFactory.newInstance();
		      Transformer trans = tf.newTransformer();
		      StringWriter sw = new StringWriter();
		      trans.transform(new DOMSource(document), new StreamResult(sw));
		      return sw.toString();
		    } catch (TransformerException tEx) {
		      tEx.printStackTrace();
		    }
		    return null;
		  }
	
	
	
}

