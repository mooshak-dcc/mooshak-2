package pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.saxon.TransformerFactoryImpl;

public class JSONHandler {
	private  Document doc; 
	private XPath xpath;
	private LinkedList<String> listGroup;
	public JSONHandler(String filepath) {
		 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//		docFactory.setValidating(false);
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			setDoc(docBuilder.parse(filepath));
			XPathFactory factory = XPathFactory.newInstance();
			setXpath(factory.newXPath());
			
			
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean isGroupExist(String id) throws XPathExpressionException{
		String expression_ = "/quiz/group/@id='"+id+"'";
		return (Boolean) xpath.evaluate(expression_, doc, XPathConstants.BOOLEAN);
	}
	
	public boolean isQAExist(String groupID,  String questionId) throws XPathExpressionException{
		String expression_ = "/quiz/group[@id='"+groupID+"']/QA/@id='"+questionId+"'";
		return (Boolean) xpath.evaluate(expression_, doc, XPathConstants.BOOLEAN);
	}
	
	public boolean isChoiceExist(String groupID,  String QAId, String optionId) throws XPathExpressionException{
		String expression_ = "/quiz/group[@id='"+groupID+"']/QA[@id='"+QAId+"']/answer/choice/@id='"+optionId+"'";
		return (Boolean) xpath.evaluate(expression_, doc, XPathConstants.BOOLEAN);
	}
	
	
	public Node  getGroup(String id) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+id+"']";
		return (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
	}
	
	public Node  getQA(String groupId, String qaId) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']";
		return(Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
	}
	
public boolean hasNodeResponse(String groupID,  String QAId) throws XPathExpressionException{
	String expression_ = "/quiz/group[@id='"+groupID+"']/QA[@id='"+QAId+"']/response/value";
	Node node =(Node) xpath.evaluate(expression_, doc, XPathConstants.NODE);;
	return node!=null;
	}


	public void updateSingleMultiple(String groupId, String qaId, String id, String value) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/answer/choice[@id='"+id+"']";
		Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		node.getAttributes().getNamedItem("selected").setNodeValue(value);
	}
	
	public void updateBoolean(String groupId, String qaId, String id, String value) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/answer/choice[@id='"+id+"']";
		Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		
//		System.out.println("groupId "+ groupId +" qaId "+ qaId+ " id "+ id +" value "+ value);
//		System.out.println("Node "+node);
		
		node.getAttributes().getNamedItem("selected").setNodeValue(value);
	}
	
	
	public void updateMatchingChoice(String groupId, String qaId, String id, String value) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/answer/choice[@id='"+id+"']";
		Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		node.getAttributes().getNamedItem("selected").setNodeValue(value);
		
	}
	
	
	public void updateFillInTheBlank(String groupId, String qaId, String id, String value) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/answer[@id='"+id+"']/choice[@id='"+value+"']";
		Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		node.getAttributes().getNamedItem("selected").setNodeValue("true");
		
	}
	
	public void updateNumeric(String groupId, String qaId, String id, String value) throws XPathExpressionException{
//		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/answer/choice[@id='"+id+"']";
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/response/value";
		
		Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		
		node.setTextContent(value);
	}
	
	public void updateEssayChoice(String groupId, String qaId, String id, String value) throws XPathExpressionException{
		String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/response/value";
		Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		node.setTextContent(value);
	}
	
	public void updateShortAnswer(String groupId, String qaId, String id, String value) throws XPathExpressionException{
		
		if(hasNodeResponse( groupId, qaId )){
			String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']/response/value";
			Node node= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
			node.setTextContent(value);
		}
		else{
			String expression = "/quiz/group[@id='"+groupId+"']/QA[@id='"+qaId+"']";
			Node qa= (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
			
			Element response = doc.createElement("response");
			Element value_ = doc.createElement("value");
			value_.appendChild(doc.createTextNode(value));
			response.appendChild(value_);
			qa.appendChild(response);
			
			
		}
		
	}
	
	public void updateMoo(String type, String groupId, String qaId, String id, String value) throws XPathExpressionException{
		switch (type) {
		case "single":
			updateSingleMultiple(groupId,qaId,id,value);
			break;
		case "boolean":
			updateBoolean(groupId,qaId,id,value);		
			break;
		case "multiple":
			updateSingleMultiple(groupId,qaId,id,value);
			break;
		case "shortAnswer":
			updateShortAnswer(groupId,qaId,id,value);
			break;
		case "numeric":
			updateNumeric(groupId,qaId,id,value);
			break;
		case "fillInTheBlank":
			updateFillInTheBlank(groupId,qaId,id,value);
			break;
		case "essay":
			updateEssayChoice(groupId,qaId,id,value);
			break;

		default:
			break;
		}
	}
	
	
	public void update(String filepathJson, String outputFile){
		listGroup = new LinkedList<>();
		
        try (
        		InputStream fis = new FileInputStream(filepathJson);
        		JsonReader reader = Json.createReader(fis);
        		) {     
        	
        	
            JsonArray jsonArray =  reader.readArray();

            JsonArray groupsJson = jsonArray;
            for (int i=0; i<groupsJson.size();i++) {
            	JsonObject group_ = groupsJson.getJsonObject(i);
            	String   groupID =  group_.getString("groupID");
            	listGroup.push(groupID);
            	JsonArray   questions = group_.getJsonArray("qa");
            	
            	  for (int k=0; k<questions.size();k++) {
            		  	JsonObject elem = questions.getJsonObject(k);
		            	String   type = elem.getString("type");
		            	String   qaId = elem.getString("qaId");
		            	markNode(groupID,qaId);
		            	JsonArray   value  = elem.getJsonArray("value");
		            	
		            	 for (int j=0; j<value.size();j++) {
		            		 JsonObject val = value.getJsonObject(j);
		            		 String   inputId  = val.getString("inputId");
		            		 String   valueLocal  = (String) (val.get("value")+"");
		            		 updateMoo(type,groupID,qaId,inputId,valueLocal );
		            	 }
            	  }
            }
            
            for (String _idGroup : listGroup) {
     			updateQAGroup(_idGroup);
    		}
            
//       	 write the content into xml file
        TransformerFactoryImpl fact = new net.sf.saxon.TransformerFactoryImpl();
// 		TransformerFactory transformerFactory = TransformerFactory.newInstance();
 		 
 		Transformer transformer = fact.newTransformer();
 		DOMSource source = new DOMSource(doc);
 		StreamResult result = new StreamResult(new File(outputFile));
 		transformer.transform(source, result);
 		
            
       
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
			e.printStackTrace();
        } catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	public void markNode(String groupId, String qaId){
		Node node;
		try {
			node = getQA(groupId,qaId);
			((Element)node).setAttribute("selected", "yes");
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}
	}
	
	public void updateQAGroup(String id){
		
		try {
			Node group=this.getGroup(id);
			
			NodeList nl = group.getChildNodes();
			
			LinkedList<Node> listToRemove = new LinkedList<Node>();
			 if (nl != null) {
			        int length = nl.getLength();
			        for (int i = 0; i < length; i++) {
			            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
			                Element el = (Element) nl.item(i);
			                if (el.getNodeName().contains("q:QA") ) {
			                	if(el.hasAttribute("selected")){
			                		el.removeAttribute("selected");
			                	}
			                	else{
			                		listToRemove.push(el);
			                	}
			                }
			            }
			        }
			        
			        for (Node node : listToRemove) {
						group.removeChild(node);
					}
			 }        
			
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*********** getters and setters********/

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public XPath getXpath() {
		return xpath;
	}

	public void setXpath(XPath xpath) {
		this.xpath = xpath;
	}
	

}

