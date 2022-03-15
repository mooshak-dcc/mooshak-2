/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.feedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.kora.semantics.DifferenceHandler;
import pt.up.fc.dcc.mooshak.evaluation.kora.semantics.FeedbackMessage;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
//import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentConnection;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentSubPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentType;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GObject;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.InDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.OutDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;
import pt.up.fc.dcc.mooshak.evaluation.kora.ParseInfo;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.SummaryFormat;

/**
 * @author hcorreia
 *
 */


public class FeedbackManager {
	
	private LinkedList<FeedbackMessage> pastFeedback;
	List<JSONObject> feedbackSummarize;
	JSONObject jsonFeedback = new JSONObject();
	String textFeedback="";
	static ParseInfo parseInfo=new ParseInfo();
	static Map<String, List<String>> urlMap= new HashMap<String, List<String>>();
	List<FeebackSummarizer> summarizerTypes;
	Map<Node, Double> mapNodesMaxValue = null;
	 
	public FeedbackManager(Map<String, List<String>> urlMap) {
		pastFeedback = new LinkedList<FeedbackMessage>();
		feedbackSummarize = new LinkedList<JSONObject>();
		FeedbackManager.urlMap=urlMap;
	}
	
	
	public enum Lang { EN, PT };
	
	public Lang lang = Lang.EN;
	
	/**
	 * Language in which feedback is provided (EN and PT)  
	 * @return the lang
	 */
	public Lang getLang() {
		return lang;
	}

	/**
	 * Set language in which feedback is provided (EN and PT)  
	 * @param lang the lang to set
	 */
	public void setLang(Lang lang) {
		this.lang = lang;
	}
	
	
	public void loadSummarizerTypes(){
	
		summarizerTypes = Arrays.asList(
//			 new FeedbackTotalError(),
			 new FeedbackInsert(),
			 new FeedbackDelete(),
			 new FeedbackDifferentTypesNodeEdge(),
			 new FeedbackProperty(),
			 new FeedbackCardinality(),
			 new FeedbackDegreeInOut()
			
			);
	}
	
	public void summarize(DifferenceHandler difference) {
		if(difference.totalError()==0){
			this.textFeedback ="Your attempt is correct!";
			return;	
		} 
		jsonFeedback = new JSONObject();
		List<FeedbackMessage> message = new LinkedList<FeedbackMessage>();
		loadSummarizerTypes();
		
		for (FeebackSummarizer summarizer : summarizerTypes) {
			List<FeedbackMessage> list = summarizer.summarize(lang, difference);
			if (list.size() > 0){
				for (FeedbackMessage fm : list)
					{
					
						if (!pastFeedback.contains(fm) ) {
							message.add(fm);
						}
					}
				if(message.size()==0)
					message.add(list.get(list.size()-1));
			}
		}
//		System.out.println("AQUIII124  "  +message);
		
		message.sort(null);
		
//		for(FeedbackMessage f:message)
//			System.out.println(f.getMessage());

		
		FeedbackMessage info= new FeedbackMessage();
		JSONObject json = new JSONObject();
		if(message.size()>0){
//			System.out.println(message);
			 info = message.get(0);
			 pastFeedback.add(info);
//			String text=getInitialText(difference)+info.getMessage();
			 String text=info.getMessage();
			
			 
			json.put("textFeedback", text);
			this.textFeedback = text;
			
			if (info.getIsNode()) {
				json.put("nodes", info.getJson());
				jsonFeedback.put("nodes", new JSONArray().put(info.getJson()));
			} else{
				jsonFeedback.put("edges", new JSONArray().put(info.getJson()));
				json.put("edges", info.getJson());
			}
			
		}
	}
		


	public String getTextFeedback(){
		return this.textFeedback;
	}
	
	public JSONObject getJsonFeedback(){
		return jsonFeedback;
	}
	
	
	
	interface FeebackSummarizer {
		
		List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences);
	}


	
	/**
	 *  Observations of worst classification (higher severity)
	 */
	/**
	 *  Observations of worst classification (higher severity)
	 */
	static class FeedbackInsert implements FeebackSummarizer {
		
		List<FeedbackMessage> summary = new ArrayList<>();
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {

			setFeedbackNode(lang,differences);
			setFeedbackEdge(lang,differences);
			
			return summary;
		}
		
		private void setFeedbackNode(Lang lang, DifferenceHandler differences) {
			
			if(differences.isEmptyNodeIsertion()) return;
			String text1="";
			
			HashMap<String,List<Node>> mapNodes=differences.getMapNodeInsertion();
//			if(mapNodes.size()>1)
				for(Entry<String, List<Node>> entry : mapNodes.entrySet()){
					
					text1=getStringMessage(1,"insertNode",entry.getValue().size(),entry.getKey());
					summary.add( new FeedbackMessage(text1, 1, 10,getDegreebyType(entry.getValue()), true)); 
					
					text1=getStringMessage(1,"insertNode1",entry.getValue().size(),entry.getKey(),differences.getNameInsertion(entry.getKey()),getURL(entry.getKey()));
					summary.add( new FeedbackMessage(text1, 2, 20,getDegreebyType(entry.getValue()), true)); 
					
				}
			
			List<NodeInsertion> nodes = differences.getNodeInsertions();
			for(NodeInsertion node: nodes){
				setFeedbackNode(lang,node,differences.getNameInsertion(node.getInsertion().getType()));
			}
			
		}
		
		private void setFeedbackNode(Lang lang, NodeInsertion node ,String names) {
			
			JSONObject jsonNode = parseInfo.createJsonNodeTemporary(node.getInsertion(), "insert");
			int degree=node.getInsertion().getTotalDegreeInOut();
			String text1=getStringMessage(1,"insertNode2",node.getInsertion().getType(), names); 
			summary.add(new FeedbackMessage(text1, 2, 30,degree,jsonNode, true));
			
			 text1=getStringMessage(1,"insertNode3",node.getInsertion().getType(), names,
						node.getInsertion().getlDegreeInOut().size());
			summary.add(new FeedbackMessage(text1, 2, 40,degree,jsonNode, true));

			 
			text1=getStringMessage(1,"insertNode4",node.getInsertion().getType(), names,
						node.getInsertion().getlDegreeInOut().size(),getURL(node.getInsertion().getType()));
			 summary.add( new FeedbackMessage(text1, 2, 50,degree, true)); 
		}
			
			
			private void setFeedbackEdge(Lang lang, DifferenceHandler differences) {
				
				List<EdgeInsertion> edges = differences.getEdgeInsertions();
				
				for(EdgeInsertion edge: edges){
					setFeedbackEdge(lang,edge);
				}
				
				if(getSizeEdgeInsertion(edges)<=1) return;
				
				String text1=getStringMessage(1,"insertEdge",differences.getEdgeInsertions().size());
				summary.add( new FeedbackMessage(text1, 5, 10,0, false)); 
				
				HashMap<String,List<Edge>> mapEdges=differences.getMapEdgeInsertion();
				for(Entry<String, List<Edge>> entry : mapEdges.entrySet()){
					text1=getStringMessage(1,"insertEdge1",entry.getValue().size(),entry.getKey(),getUrlInfo(urlMap.get(entry.getKey())));
					summary.add( new FeedbackMessage(text1, 5, 20,entry.getValue().size(), false)); 
					
				}
			
			
		}
			
			
		
		private void setFeedbackEdge(Lang lang, EdgeInsertion edgeInsertion) {
			JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edgeInsertion.getInsertion(), "insert");
			
			
			if(edgeInsertion.getInsertion().getType().equals("include")){
				String text1=getStringMessage(1,"includeNode", edgeInsertion.getInsertion().getTarget().getType(), edgeInsertion.getInsertion().getSource().getType(),
						edgeInsertion.getInsertion().getTarget().getType());
				summary.add(new FeedbackMessage(text1, 5, 40, 0,jsonEdge,true));
				return;
			}
			
			String text1=getStringMessage(1,"insertEdge2",edgeInsertion.getInsertion().getType(), edgeInsertion.getInsertion().getSource().getType(),
					edgeInsertion.getInsertion().getTarget().getType());
			summary.add(new FeedbackMessage(text1, 2, 40,0, jsonEdge,false));
			
			 text1=getStringMessage(1,"insertEdge3",edgeInsertion.getInsertion().getType(), edgeInsertion.getInsertion().getSource().getName(),
						edgeInsertion.getInsertion().getSource().getType(), edgeInsertion.getInsertion().getTarget().getName(), edgeInsertion.getInsertion().getTarget().getType());
			 summary.add(new FeedbackMessage(text1, 3, 50, 0,jsonEdge,false));

			 text1=getStringMessage(1,"insertEdge4",edgeInsertion.getInsertion().getType(), edgeInsertion.getInsertion().getSource().getName(),
						edgeInsertion.getInsertion().getSource().getType(), edgeInsertion.getInsertion().getTarget().getName(), edgeInsertion.getInsertion().getTarget().getType(),getURL(edgeInsertion.getInsertion().getType()));
			 
			 summary.add(new FeedbackMessage(text1, 5, 60, 0,false)); 
			 
		
			
		}
		
		public int getSizeEdgeInsertion(List<EdgeInsertion> edgeInsertions){
			int count=0;
			for(EdgeInsertion edge : edgeInsertions)
				if(!edge.getInsertion().getType().equals("include"))
					count++;
			return count;
		}	

		private int getDegreebyType(List<Node> nodes ){
			int value=0;
			for(Node node: nodes){
				value+=node.getTotalDegreeInOut();
			}
			return value;
		}
		
	}
	
	/**
	 *  Observations of worst classification (higher severity)
	 */
	static class FeedbackDelete implements FeebackSummarizer {
		List<FeedbackMessage> summary = new ArrayList<>();
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {
			
			setFeedbackNode(lang,differences);
			setFeedbackEdge(lang,differences);
			
			
			return summary;
			
		}
		
		private void setFeedbackNode(Lang lang, DifferenceHandler differences) {
			if(differences.isEmptyNodeDeletion()) return;
			
			
			
			
			HashMap<String,List<Node>> mapNodes=differences.getMapNodeDeletion();
//			if(mapNodes.size()>1)
//			System.out.println("mapNodes "+ mapNodes.size());
				for(Entry<String, List<Node>> entry : mapNodes.entrySet()){
					String text1=getStringMessage(2,"deleteNode1",entry.getValue().size(),entry.getKey(),getURL(entry.getKey()));
					summary.add( new FeedbackMessage(text1, 2, 20, entry.getValue().size(),true));
					
				}
			
			List<NodeDeletion> nodes = differences.getNodeDeletions();
			for(NodeDeletion node: nodes){
				setFeedbackNode(lang,node,differences.getNameDeletion(node.getDeletion().getType()));
			}
		}
		
		
		
		private void setFeedbackNode(Lang lang, NodeDeletion node, String names) {
			int degree=node.getDeletion().getTotalDegreeInOut();
			JSONObject jsonNode = parseInfo.createJsonNodeTemporary(node.getDeletion(), "delete");
			
			String  text1=getStringMessage(2,"deleteNode2",node.getDeletion().getType(), names,getURL(node.getDeletion().getType()));
			summary.add(new FeedbackMessage(text1, 2, 40, degree,jsonNode, true));
			
			text1=getStringMessage(2,"deleteNode3",node.getDeletion().getType(), names, node.getDeletion().getlDegreeInOut().size());
			summary.add(new FeedbackMessage(text1, 2, 50, degree,jsonNode,true));
			
			
			text1=getStringMessage(2,"deleteNode4",node.getDeletion().getType(), names, node.getDeletion().getlDegreeInOut().size(),getURL(node.getDeletion().getType()));
			summary.add(new FeedbackMessage(text1, 4, 60,degree,jsonNode, true)); 
		}
		
		private void setFeedbackEdge(Lang lang, DifferenceHandler differences) {
			List<EdgeDeletion> edges = differences.getEdgeDeletions();
			for(EdgeDeletion edge: edges){
				setFeedbackEdge(lang,edge);
			}
			
			if(getSizeEdgeDeletion(edges)<=1) return ;
				
			String  text1=getStringMessage(2,"deleteEdge",differences.getEdgeDeletions().size());
			summary.add( new FeedbackMessage(text1, 6, 10,0, false)); 
			
			
			HashMap<String,List<Edge>> mapEdges=differences.getMapEdgeDeletion();
			for(Entry<String, List<Edge>> entry : mapEdges.entrySet()){
				
				text1=getStringMessage(2,"deleteEdge1",entry.getValue().size(),entry.getKey(),getURL(entry.getKey()));
				summary.add( new FeedbackMessage(text1, 6, 20, entry.getValue().size(),false)); 
				text1+=getURL(entry.getKey());
			}
		
		
		}

		private void setFeedbackEdge(Lang lang, EdgeDeletion edge) {
			JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge.getDeletion(), "delete");
			
			if(edge.getDeletion().getType().equals("include")){
				String text1=getStringMessage(2,"includeDeletion",edge.getDeletion().getSource().getType(), 
						edge.getDeletion().getTarget().getType());
				summary.add(new FeedbackMessage(text1, 5, 40, jsonEdge,true));
				return;
			}
			
			String text1=getStringMessage(2,"deleteEdge2",edge.getDeletion().getType(), edge.getDeletion().getSource().getType(), edge.getDeletion().getTarget().getType());
			summary.add(new FeedbackMessage(text1, 6, 40,jsonEdge, false));
			
			text1=getStringMessage(2,"deleteEdge3",edge.getDeletion().getType(), edge.getDeletion().getSource().getName(), edge.getDeletion().getSource().getType(), edge.getDeletion().getTarget().getName(),edge.getDeletion().getTarget().getType());
			summary.add(new FeedbackMessage(text1, 6, 50, jsonEdge,false));
			
			
			 if(!edge.getDeletion().getType().equals("include")){
				 text1=getStringMessage(2,"deleteEdge3",edge.getDeletion().getType(), edge.getDeletion().getSource().getName(),
						 edge.getDeletion().getSource().getType(), edge.getDeletion().getTarget().getName(), edge.getDeletion().getTarget().getType(),getURL(edge.getDeletion().getType()));
				 summary.add(new FeedbackMessage(text1, 6, 60, false));
			}
		}
		
		public int getSizeEdgeDeletion(List<EdgeDeletion> edgeDeletions){
			int count=0;
			for(EdgeDeletion edge : edgeDeletions)
				if(!edge.getDeletion().getType().equals("include"))
					count++;
			return count;
		}
		
		
	}
	
	
	
	
	
	
	/**
	 *  Observations of worst classification (higher severity)
	 */
	static class FeedbackDifferentTypesNodeEdge implements FeebackSummarizer {
		List<FeedbackMessage> summary = new ArrayList<>();
		DifferenceHandler differences;
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {
			this.differences=differences;
			if(differences.isEmptyDifferentTypes()) return summary;
			List<DifferentType> nodes = differences.getDifferentNodeTypes();
			List<DifferentType> edges = differences.getDifferentEdgeTypes();
			
			if(nodes.size()>1){
				String text1=getStringMessage(3,"differentType1",nodes.size());
				summary.add( new FeedbackMessage(text1, 1, 10, true)); 
			}
			if(edges.size()>1){
				String text1=getStringMessage(3,"differentType2",nodes.size());
				summary.add( new FeedbackMessage(text1, 1, 10, false));
				
			}
			for(DifferentType node: nodes){
				setFeedbackNode(lang,node);
			}
			for(DifferentType edge: edges){
				setFeedbackEdge(lang,edge);
			}
			
			return summary;
		}
		
		
		
		private void setFeedbackNode(Lang lang, DifferentType differentType) {
			Node node =getPossibleNodeToRemove(differences, (Node)differentType.getObject());
			
			int degree=node.getTotalDegreeInOut();
			JSONObject jsonNode = parseInfo.createJsonNodeTemporary(node, "differentType");
			String text1=getStringMessage(3,"differentNodeType",node.getName(), node.getType());
			summary.add(new FeedbackMessage(text1, 7, 10,degree, jsonNode, true));
			
			text1=getStringMessage(3,"differentNodeType1",node.getName(), node.getType(), differentType.getSolutionType(),getURL(node.getType()));
			summary.add(new FeedbackMessage(text1, 7, 20, degree,jsonNode, true));

			
		}
		
		private void setFeedbackEdge(Lang lang, DifferentType differentType) {
			Edge edge=(Edge)differentType.getObject();
			
			JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge, "DifferentType");
			
			String text1=getStringMessage(3,"differentEdgeType",edge.getType(), edge.getSource().getType(), edge.getTarget().getType());
			summary.add(new FeedbackMessage(text1, 8, 30, jsonEdge, false));
			
			text1=getStringMessage(3,"differentEdgeType",edge.getType(),edge.getSource().getType(), edge.getTarget().getType(),differentType.getSolutionType(),getURL(edge.getType()));
			summary.add(new FeedbackMessage(text1, 8, 40, jsonEdge, false));
			
		}
		
	}
	
	
	
	static class FeedbackCardinality implements FeebackSummarizer {
		List<FeedbackMessage> summary = new ArrayList<>();
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {
			
			List<DifferentPropertyValue> mapPropriedady=differences.getDifferentCardinality();
			if(differences.totalErrorCardinality()<1) return summary;
				
//				String text1=getStringMessage(4,"wrongCardinality",differences.totalErrorCardinality());
//				summary.add( new FeedbackMessage(text1, 10, 0, false));
			String text1;
				
			for(DifferentPropertyValue entry : mapPropriedady){
				if(!entry.getObject().isNode()){
					
					Edge edge = (Edge) entry.getObject();
					Node source = differences.getEvaluation().getBestMap().get(edge.getSource()).getAttempt();
					Node target = differences.getEvaluation().getBestMap().get(edge.getTarget()).getAttempt();
					
					String sourceName = source.getName();
					String targetName = target.getName();
					
					JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge, "property");
					text1=getStringMessage(4,"wrongCardinality1",edge.getSource().getType(),edge.getTarget().getType(),entry.getName());
					summary.add( new FeedbackMessage(text1, 10, 20, 0,false));
					text1=getStringMessage(4,"wrongCardinality2",edge.getSource().getType(), sourceName, edge.getTarget().getType(), targetName, entry.getName());
					summary.add( new FeedbackMessage(text1, 10, 20,0,jsonEdge, false));
					
					text1=getStringMessage(4,"wrongCardinality3",edge.getSource().getType(), sourceName, edge.getTarget().getType(), targetName, entry.getName(), entry.getCorrectValueString(), entry.getWrongValueString());
					summary.add( new FeedbackMessage(text1, 10, 30, 0,jsonEdge,false));
				}
			}
			
			List<PropertyDeletion> cardinalityDeletions=differences.getCardinalityDeletions();
			for(PropertyDeletion entry : cardinalityDeletions){
				
					Edge edge = (Edge) entry.getObject();
					Node source = differences.getEvaluation().getBestMap().get(edge.getSource()).getAttempt();
					Node target = differences.getEvaluation().getBestMap().get(edge.getTarget()).getAttempt();
					
					String sourceName = source.getName();
					String targetName = target.getName();
					
					
					if(entry.getPropertyName().isSimple()){
						SimplePropertyName  propName=(SimplePropertyName) entry.getPropertyName(); 
						JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge, "property");
						text1=getStringMessage(4,"deleteCardinality",propName.getName(), edge.getType(), edge.getSource().getType(),edge.getTarget().getType());
						summary.add( new FeedbackMessage(text1, 10, 20,0, jsonEdge,false));
						text1=getStringMessage(4,"deleteCardinality1",propName.getName(), edge.getType(), edge.getSource().getType(), sourceName, edge.getTarget().getType(), targetName, entry.getObject().getName());
						summary.add( new FeedbackMessage(text1, 10, 30,0,jsonEdge, false));}
					else{
						CompositePropertyName propNameComp= (CompositePropertyName) entry.getPropertyName(); 
						JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge, "property");
						text1=getStringMessage(4,"deleteCardinality",propNameComp.getName(), edge.getType(), edge.getSource().getType(),edge.getTarget().getType());
						summary.add( new FeedbackMessage(text1, 10, 20,0, jsonEdge,false));
						text1=getStringMessage(4,"deleteCardinality1",propNameComp.getName(), edge.getType(), edge.getSource().getType(), sourceName, edge.getTarget().getType(), targetName, entry.getObject().getName());
						summary.add( new FeedbackMessage(text1, 10, 30,0,jsonEdge, false));
					}
			
			}
			
			List<PropertyInsertion> cardinalityInsertions=differences.getCardinalityInsertions();
			for(PropertyInsertion entry : cardinalityInsertions){
				
					Edge edge = (Edge) entry.getObject();
					Node source = differences.getEvaluation().getBestMap().get(edge.getSource()).getAttempt();
					Node target = differences.getEvaluation().getBestMap().get(edge.getTarget()).getAttempt();
					
					CompositePropertyName propName=(CompositePropertyName) entry.getPropertyName(); 
					JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge, "property");
					text1=getStringMessage(4,"insertCardinality",propName.getName(),edge.getType(), edge.getSource().getType(),edge.getTarget().getType());
					summary.add( new FeedbackMessage(text1, 10, 20, 0,jsonEdge,false));
					text1=getStringMessage(4,"insertCardinality1",propName.getName(),edge.getType(), edge.getSource().getType(), source.getName(), edge.getTarget().getType(), target.getName(), entry.getObject().getName());
					summary.add( new FeedbackMessage(text1, 10, 30, 0,jsonEdge,false));
					
			}
			
			return summary;
			
		}
		

	}
	
	
	/**
	 *  Observations of worst classification (higher severity)
	 */
	static class FeedbackProperty implements FeebackSummarizer {
		List<FeedbackMessage> summary = new ArrayList<>();
		
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {
			
			setFeedbackNode(lang,differences);
//			setFeedbackPropertyGeneric(lang,differences);
			
			
			return summary;
			
		}
		
		private void setFeedbackNode(Lang lang, DifferenceHandler differences) {
			
			List<DifferentPropertyValue> differentProperties = differences.getDifferentPropertyType();
			for(DifferentPropertyValue diffProperty: differentProperties){
				setFeedbackDifferentProperty(lang,diffProperty,differences);
			}
			
			
			List<PropertyInsertion> propertyInsertions = differences.getPropertyInsertions();
			for(PropertyInsertion diffProperty: propertyInsertions){
				setFeedbackPropertyInsertion(lang,diffProperty);
			}
			
			List<PropertyDeletion> propertyDeletion = differences.getPropertyDeletions();
			for(PropertyDeletion diffProperty: propertyDeletion){
				setFeedbackPropertyDeletion(lang,diffProperty);
			}
			
			
			List<DifferentSubPropertyValue> differentSubProperties = differences.getDifferentSubProperty();
			for(DifferentSubPropertyValue diffProperty: differentSubProperties){
				setFeedbackDifferentProperty(lang,diffProperty);
			}
			
			
		}
		
		
		
		private void setFeedbackDifferentProperty(Lang lang, DifferentPropertyValue diffProperty,DifferenceHandler differences) {
			String text1, propName=diffProperty.getNameString();
//			if(!diffProperty.getName().isSimple()){
//				propName=((CompositePropertyName) diffProperty.getName()).getName();
//			}
//			else{
//				propName=diffProperty.getName().toString();
//			}
			
			if(diffProperty.getObject().isNode()){
				Node node = (Node)diffProperty.getObject();
				int degree=node.getTotalDegreeInOut();
				JSONObject jsonNode = parseInfo.createJsonNodeTemporary(node, "property");
				text1=getStringMessage(4,"modifyProperty2",node.getType(), propName);
				summary.add(new FeedbackMessage(text1, 10, 30,degree, jsonNode, true));
				text1=getStringMessage(4,"modifyProperty4",node.getType(),node.getName(),propName
						,diffProperty.getWrongValueString(),diffProperty.getCorrectValueString() );
				summary.add(new FeedbackMessage(text1, 10, 40,degree, jsonNode, true));
			}
			else{
				
				Edge edge = (Edge)diffProperty.getObject();
				Node source = edge.getSource();
				Node target = edge.getTarget();
				
				if(differences.getEvaluation().getBestMap().get(edge.getSource())!=null)
					source=	differences.getEvaluation().getBestMap().get(edge.getSource()).getAttempt();
				if(differences.getEvaluation().getBestMap().get(edge.getTarget())!=null)
					target=	differences.getEvaluation().getBestMap().get(edge.getTarget()).getAttempt();
				
//				 differences.getEvaluation().getBestMap().get(edge.getTarget()).getAttempt();
				String sourceName = source.getName();
				String targetName = target.getName();
				
					
				JSONObject jsonEdge = parseInfo.createJsonEdgeTemporary(edge, "property");
//				text1=getStringMessage(4,"modifyProperty3",edge.getType(), edge.getSource().getType(),edge.getTarget().getType(),propName);
//				summary.add(new FeedbackMessage(text1, 10, 30,0, false));
				text1=getStringMessage(4,"modifyProperty5",edge.getType(), edge.getSource().getType(), sourceName, edge.getTarget().getType(), targetName, propName);
				summary.add(new FeedbackMessage(text1, 10, 40,0, jsonEdge, false)); 
				text1=getStringMessage(4,"modifyProperty6",edge.getType(), edge.getSource().getType(), sourceName, edge.getTarget().getType(), targetName, propName,diffProperty.getWrongValueString(),diffProperty.getCorrectValueString());
				summary.add(new FeedbackMessage(text1, 10, 50,0, jsonEdge, false));
				
//				HashMap<String, List<DifferentPropertyValue>> mapDifferentProperty = differences.getMapDifferentProperty();
////				System.out.println(mapDifferentProperty);
//				for (Entry<String, List<DifferentPropertyValue>> entry : mapDifferentProperty.entrySet()) {
////					String text12 = SummaryFormat.MODIFY_PROPERTY1.format(lang, entry.getValue().size(), entry.getKey());
//					text1=getStringMessage(4,"modifyProperty1",entry.getValue().size(), entry.getKey(),"total");
//					summary.add(new FeedbackMessage(text1, 10, 10, true));
//				}
				
				
			}
			
			
			
			
		}
		
		private void setFeedbackDifferentProperty(Lang lang, DifferentSubPropertyValue diffProperty) {
			
			
			GObject element;
			int degree=0;
			JSONObject jsonElement =  new JSONObject();
			if(diffProperty.getObject().isNode()){
					element= (Edge)diffProperty.getObject();
					degree=(int)((Node) element).getTotalDegreeInOut();
					jsonElement = parseInfo.createJsonNodeTemporary((Node) element, "property");
			}
			
			else{
				 element=(Edge)diffProperty.getObject();
				 jsonElement = parseInfo.createJsonEdgeTemporary((Edge) element, "property");
				 
			}
			
			String text1, propName=diffProperty.getNameString();
//			
			
//			if(!diffProperty.getName().isSimple()){
//				propName=((CompositePropertyName) diffProperty.getName()).getName();
//
//			}
//			else{
//				propName=diffProperty.getName().toString();
//			}
			
			text1=getStringMessage(4,"modifyProperty4",element.getType(),element.getName(), propName,diffProperty.getKey(),diffProperty.getWrongValue(),diffProperty.getCorrectValue());
			summary.add(new FeedbackMessage(text1, 10, 30,degree, jsonElement, true));			
			
		}
		
		private void setFeedbackPropertyInsertion(Lang lang, PropertyInsertion diffProperty) {
			Node node = (Node)diffProperty.getObject();
			int degree=node.getTotalDegreeInOut();
			JSONObject jsonNode = parseInfo.createJsonNodeTemporary(node, "property");
			String text1="",propName=diffProperty.getNameString();
			
//			if(!diffProperty.getPropertyName().isSimple()){
//				 propName=((CompositePropertyName) diffProperty.getPropertyName()).toString();
//			}
//			else{
//				propName=diffProperty.getPropertyName().toString();
//			}
			
			
			text1=getStringMessage(4,"insertProperty",propName, node.getType(),node.getType().toString());
			summary.add(new FeedbackMessage(text1, 10, 30, degree, jsonNode, true));
		}
		
		
		private void setFeedbackPropertyDeletion(Lang lang, PropertyDeletion diffProperty) {
			Node node = (Node)diffProperty.getObject();
			int degree=node.getTotalDegreeInOut();
			JSONObject jsonNode = parseInfo.createJsonNodeTemporary(node, "property");
			
			String text1;
			if(!diffProperty.getPropertyName().isSimple()){
//				CompositePropertyName name=(CompositePropertyName) diffProperty.getPropertyName().toString();
//				CompositePropertyValue value = (CompositePropertyValue) diffProperty.getPropertyValue();
				text1=getStringMessage(4,"deleteProperty",node.getType(),node.getName(),diffProperty.getPropertyName().toString());
				
			}
			else{
				SimplePropertyName name =(SimplePropertyName) diffProperty.getPropertyName();
				text1=getStringMessage(4,"deleteProperty",node.getType(),node.getName(),name.getName());
			}
			
			summary.add(new FeedbackMessage(text1, 10, 30,degree, jsonNode, true));
			
		}
		
		
		private void setFeedbackPropertyGeneric(Lang lang, DifferenceHandler differences) {
			//VERIFICAR E ELIMINAR DEPOIS SELIMINAR DEPOIS
			int totalErroProp = differences.totalErrorProperty();
			if (totalErroProp > 1) {
				String text1 = SummaryFormat.MODIFY_PROPERTY.format(lang, totalErroProp);
				summary.add(new FeedbackMessage(text1, 1, 0, true));

			} else
				return;
			HashMap<String, List<DifferentPropertyValue>> mapDifferentProperty = differences.getMapDifferentProperty();
			for (Entry<String, List<DifferentPropertyValue>> entry : mapDifferentProperty.entrySet()) {
				String text1 = SummaryFormat.MODIFY_PROPERTY1.format(lang, entry.getValue().size(), entry.getKey());
				summary.add(new FeedbackMessage(text1, 10, 10, true));
			}

			HashMap<String, List<PropertyInsertion>> mapDifferentPropertyInsertion = differences
					.getMapPropertyInsertion();
			for (Entry<String, List<PropertyInsertion>> entry : mapDifferentPropertyInsertion.entrySet()) {
				String text1 = SummaryFormat.MODIFY_PROPERTY1.format(lang, entry.getValue().size(), entry.getKey());
				summary.add(new FeedbackMessage(text1, 10, 10, true));
			}

			HashMap<String, List<PropertyDeletion>> mapDifferentPropertyDeletion = differences
					.getMapPropertyDeletions();
			for (Entry<String, List<PropertyDeletion>> entry : mapDifferentPropertyDeletion.entrySet()) {
				String text1 = SummaryFormat.MODIFY_PROPERTY1.format(lang, entry.getValue().size(), entry.getKey());
				summary.add(new FeedbackMessage(text1, 10, 10, true));
			}

			HashMap<String, List<DifferentSubPropertyValue>> mapDifferentSubProperty = differences
					.getMapDifferentSubProperty();
			for (Entry<String, List<DifferentSubPropertyValue>> entry : mapDifferentSubProperty.entrySet()) {
				String text1 = SummaryFormat.MODIFY_PROPERTY1.format(lang, entry.getValue().size(), entry.getKey());
				summary.add(new FeedbackMessage(text1, 10, 10, true));
			}


		}
		
		
	}
	
	

	static class FeedbackTotalError implements FeebackSummarizer {
		List<FeedbackMessage> summary = new ArrayList<>();
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {
			if(differences.totalError() !=0){
				String text1=SummaryFormat.TOTAL_ERROR.format(lang,differences.totalError());
				summary.add( new FeedbackMessage(text1, 0, 0, true)); 
			}
			return summary;
			
		}
		
	}
	
	static class FeedbackDegreeInOut implements FeebackSummarizer {
		List<FeedbackMessage> summary = new ArrayList<>();
		Map<Node, Double> mapNodesMaxValue = null;
		
		@Override
		public List<FeedbackMessage> summarize(Lang lang, DifferenceHandler differences) {
			if(differences.isEmptyDifferenceDegree() ) return summary;
			
			List<OutDegreeDifference> nodeDegreeOut = differences.getDifferenceDegreeOut();
			List<InDegreeDifference> nodeDegreeIn = differences.getDifferenceDegreeIn();
			for(OutDegreeDifference diff: nodeDegreeOut){
				Node node =getPossibleNodeToRemove(differences, diff.getNode());
				
				Set<DifferentConnection> diffConnection =diff.getDifferences();
				for(DifferentConnection df : diffConnection){
					String text1=SummaryFormat.INVALID_DEGREE.format(lang,node.getName(), node.getType(),df.getWrongDegree(),df.getType(),df.getCorrectDegree());
					summary.add( new FeedbackMessage(text1, 12, 0, true));
				}
			}
			
			for(InDegreeDifference diff: nodeDegreeIn){
				Node node = diff.getNode();
				Set<DifferentConnection> diffConnection =diff.getDifferences();
				for(DifferentConnection df : diffConnection){
					String text1=SummaryFormat.INVALID_DEGREE.format(lang,node.getName(), node.getType(),df.getWrongDegree(),df.getType(),df.getCorrectDegree());
					summary.add( new FeedbackMessage(text1, 12, 0, true));
				}
			}
				
			return summary;
			
		}
		
	}
	
	private static Node getPossibleNodeToRemove(DifferenceHandler differences, Node node) {
		Map<Node, Match> mapNodes=differences.getEvaluation().getBestMap();
		if(mapNodes.containsKey(node))
			return mapNodes.get(node).getAttempt();
		return node;
	}
	
//	private static Edge getPossibleEdgeToRemove(DifferenceHandler differences, Edge edge) {
//		Map<Edge, Match> mapNodes=differences.getEvaluation().getBestMap();
//		if(mapNodes.containsKey(edge))
//			return mapNodes.get(edge).getAttempt();
//		return edge;
//	}
	
	public String  messageTotalError(Lang lang, DifferenceHandler differences) {
		String summary ="";
		int totalErrorEdge=differences.totalErrorEdge();
		int totalErrorCard = differences.totalErrorCardinality();
		int totalErrorNode = differences.totalErrorNode();
		
		summary=SummaryFormat.TOTAL_ERROR.format(lang,differences.totalError());
		
		if(totalErrorNode>0)
			summary+=SummaryFormat.TOTAL_ERROR_NODE.format(lang,totalErrorNode);
		if(totalErrorEdge>0)
			summary+=SummaryFormat.TOTAL_ERROR_EDGE.format(lang,totalErrorEdge);
		if(totalErrorCard>0)
			summary+=SummaryFormat.TOTAL_ERROR_EDGE.format(lang,totalErrorCard);
//					summary=SummaryFormat.TOTAL_ERROR.format(lang,differences.totalError(),differences.totalErrorNode(),differences.totalErrorEdge(),differences.totalErrorCardinality());
				
		return summary +"\n";
	}


	
	
	 public static String getURL(String type){
		 List<String> listUrl = new LinkedList<>();
		 if(urlMap.containsKey(type))
			 listUrl=urlMap.get(type);
		 return getUrlInfo(listUrl);
	 }
	
	
	@Override
	public String toString() {
		return "FeedbackManager [jsonFeedback=" + jsonFeedback + ", textFeedback=" + textFeedback + ", lang=" + lang
				+ "]";
	}
	
	
//	private static String getString(List<String> summary){
//		String text="";
//		for(String string : su9mmary){
//			text+=string+"\n";
//			
//			}
//		return text;
//	} 
	
	private static String getUrlInfo(List<String> listURL){
		String text="Info: ";
		for(String url : listURL)
			text+="<a target=\"blank\" href=\" "+url+" \">info</a>  ";
		return text;
	} 
	
	public static String getStringMessage(int type, String typeMessage, Object...args){
		 String message="";
		 for(Object s : args)
			 message+=";"+s;
		return type+";"+typeMessage+message;
		
	}
	
}
