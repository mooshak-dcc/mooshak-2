/**
 * @author Helder Correia
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentSubPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentType;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.InDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.OutDegreeDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class DifferenceHandler {
	List<NodeInsertion> nodeInsertions;
	List<NodeDeletion> nodeDeletions;
	List<EdgeInsertion> edgeInsertions;
	List<EdgeDeletion> edgeDeletions;
	List<DifferentType> differentNodeTypes;
	List<DifferentType> differentEdgeTypes;
	List<PropertyInsertion> propertyInsertions;
	List<PropertyDeletion> propertyDeletions;
	List<PropertyInsertion> cardinalityInsertions;
	List<PropertyDeletion> cardinalityDeletions;
	List<DifferentPropertyValue> differentPropertyType;
	List<DifferentPropertyValue> differentCardinality;
	List<DifferentSubPropertyValue> differentSubProperty;
	List<OutDegreeDifference> differenceDegreeOut;
	List<InDegreeDifference> differenceDegreeIn;
	List<String> reducibles;
	Evaluation evaluation;
	Graph solution;
	Graph attempt;
	

	public DifferenceHandler(Evaluation evaluation,Graph solution, Graph attempt) {
		this.nodeInsertions = new ArrayList<>();
		this.nodeDeletions = new ArrayList<>();
		this.edgeInsertions = new ArrayList<>();
		this.edgeDeletions = new ArrayList<>();
		this.differentNodeTypes = new ArrayList<>();
		this.differentEdgeTypes = new ArrayList<>();
		this.propertyInsertions = new ArrayList<>();
		this.propertyDeletions = new ArrayList<>();
		this.cardinalityInsertions = new ArrayList<>();
		this.cardinalityDeletions = new ArrayList<>();
		this.differentPropertyType = new ArrayList<>();
		this.differentCardinality = new ArrayList<>();
		this.differentSubProperty = new ArrayList<>();
		this.differenceDegreeOut = new ArrayList<>();
		this.differenceDegreeIn = new ArrayList<>();
		this.reducibles= new ArrayList<>();
		this.evaluation = evaluation;
		insertValue();
		this.solution=solution;
		this.attempt=attempt;
		
		
	}
	
	public DifferenceHandler() {
		this.nodeInsertions = new ArrayList<>();
		this.nodeDeletions = new ArrayList<>();
		this.edgeInsertions = new ArrayList<>();
		this.edgeDeletions = new ArrayList<>();
		this.differentNodeTypes = new ArrayList<>();
		this.differentEdgeTypes = new ArrayList<>();
		this.propertyInsertions = new ArrayList<>();
		this.propertyDeletions = new ArrayList<>();
		this.cardinalityInsertions = new ArrayList<>();
		this.cardinalityDeletions = new ArrayList<>();
		this.differentPropertyType = new ArrayList<>();
		this.differentCardinality = new ArrayList<>();
		this.differentSubProperty = new ArrayList<>();
		this.differenceDegreeOut = new ArrayList<>();
		this.differenceDegreeIn = new ArrayList<>();
		this.reducibles= new ArrayList<>();
		insertValue();

	}
	
	
	
	public void insertValue(){
		this.reducibles.add("Attribute");
		this.reducibles.add("AttributeDerived");
		this.reducibles.add("AttributeMultivalue");
		this.reducibles.add("AttributeKey");
		this.reducibles.add("AttributeKeyWeak");
	}

	public void setDifferences() {
		
//		System.out.println(evaluation.getDifferences());
		
		for (GraphDifference diff : evaluation.getDifferences()) {
			
			if (diff.isNodeInsertion()){
				addNodeInsertion((NodeInsertion) diff);
			}

			else if (diff.isNodeDeletion())
				addNodeDeletion((NodeDeletion) diff);

			else if (diff.isEdgeInsertion())
				addEdgeInsertion((EdgeInsertion) diff);

			else if (diff.isEdgeDeletion())
				addEdgeDeletion((EdgeDeletion) diff);

			else if (diff.isDifferentType()) {
				DifferentType d = (DifferentType) diff;
				if (d.getObject().isNode())
					addDifferentNodeType(d);
				else
					addDifferentEdgeType(d);
			}

			else if (diff.isPropertyInsertion()) {
				PropertyInsertion p = (PropertyInsertion) diff;
				if (p.getObject().isNode()){
					CompositePropertyValue property =(CompositePropertyValue)p.getPropertyValue();
					if(property.getValue().containsKey("type") && reducibles.contains(property.getValue().get("type"))){
						Node node = new Node(property.getInfo().get("id"), property.getValue().get("type"), property.getInfo().get("name"));
						Map<String, Integer> outDegree =new HashMap<String, Integer>();
						outDegree.put("Line", 1);
						node.setOutDegree(outDegree);
						NodeInsertion nodeInsertion = new NodeInsertion(node);
						addNodeInsertion(nodeInsertion);
					}
					else
						addPropertyInsertion(p);
				} 
				
				else{
					SourceTargetPair pair = new SourceTargetPair((Edge)p.getObject());
					Node source2 =	evaluation.getBestMap().get(pair.source).getAttempt();
					Node target2 =   evaluation.getBestMap().get(pair.target).getAttempt();
					p.getObject().setId(getIdNode(p.getObject().getId(),source2.getId(),target2.getId()));
					addCardinalityInsertion(p); 
					
				}
			}
			else if (diff.isPropertyDeletion()) {
				PropertyDeletion p = (PropertyDeletion) diff;
				if (p.getObject().isNode()){
					if (p.getObject().isNode()){
						CompositePropertyValue property =(CompositePropertyValue)p.getPropertyValue();
						if(property.getValue().containsKey("type") && reducibles.contains(property.getValue().get("type"))){
							Node node = new Node(property.getInfo().get("id"), property.getValue().get("type"), property.getInfo().get("name"));
							Map<String, Integer> outDegree =new HashMap<String, Integer>();
							outDegree.put("Line", 1);
							node.setOutDegree(outDegree);
							addNodeDeletion(new NodeDeletion(node));
						} 
					}
						else
							addPropertyDeletion(p);
				}
				else{
					SourceTargetPair pair = new SourceTargetPair((Edge)p.getObject());
					Node source2 =	evaluation.getBestMap().get(pair.source).getAttempt();
					Node target2 =   evaluation.getBestMap().get(pair.target).getAttempt();
					p.getObject().setId(getIdNode(p.getObject().getId(),source2.getId(),target2.getId()));
					addCardinalityDeletion(p);
				}
			}

			else if (diff.isDifferentPropertyValue()) {
				DifferentPropertyValue p = (DifferentPropertyValue) diff;
				if (p.getObject().isNode()) {
					Node nodeAttempt = evaluation.getBestMap().get(p.getObject()).getAttempt();
					PropertyValue wrongProperty;
					PropertyValue correctProperty;
					String wrongTypeSol="";
					String correctProp="";
					
					if(p.getWrongValue().isSimple()){
						wrongProperty=(SimplePropertyValue) p.getWrongValue();
						wrongTypeSol =(String)((SimplePropertyValue) wrongProperty).getValue();
						
						correctProperty=(SimplePropertyValue) p.getCorrectValue();
						correctProp =(String)((SimplePropertyValue) correctProperty).getValue();
					}
					else{
						wrongProperty=(CompositePropertyValue) p.getWrongValue();
						wrongTypeSol =((CompositePropertyValue) wrongProperty).getValue().get("type");
						
						correctProperty=(CompositePropertyValue) p.getCorrectValue();
						correctProp =(String)((CompositePropertyValue) correctProperty).getValue().get("type");
					}
						
					
					for(Entry<PropertyName, PropertyValue> entry: nodeAttempt.getProperties().entrySet()){
						if(entry.getValue().isSimple())
							continue;
						CompositePropertyValue wrongPropertyAttempt =(CompositePropertyValue)entry.getValue();
						
						if(wrongPropertyAttempt.getValue().containsKey("type") && reducibles.contains(wrongPropertyAttempt.getValue().get("type")) ){
							String wrongType=wrongPropertyAttempt.getValue().get("type");
							if(!wrongType.equals(wrongTypeSol))
								continue;
							Node node = new Node(wrongPropertyAttempt.getInfo().get("id"), wrongPropertyAttempt.getValue().get("type"), wrongPropertyAttempt.getInfo().get("name"));
							
							Map<String, Integer> outDegree =new HashMap<String, Integer>();
							outDegree.put("Line", 1);
							node.setOutDegree(outDegree);
							DifferentType d = new DifferentType(node, correctProp , wrongTypeSol);
							addDifferentNodeType(d);
							
						}
					}
				}
					
//					else{
//						
//						SourceTargetPair pair = new SourceTargetPair((Edge)p.getObject());
//						Node source2 =	evaluation.getBestMap().get(pair.source).getAttempt();
//						Node target2 =   evaluation.getBestMap().get(pair.target).getAttempt();
//						p.getObject().setId(getIdNode(p.getObject().getId(),source2.getId(),target2.getId()));
//						addDifferentPropertyType(p);
//						
//						
//						
//					}
				

				else{
					Node source2;
					Node target2;
					Edge edge= (Edge)p.getObject();
					SourceTargetPair pair = new SourceTargetPair((Edge)p.getObject());
					Match source= evaluation.getBestMap().get(pair.source);
					if(source==null)
						source2=edge.getTarget();	
					else
						source2 = source.getAttempt();
					
					Match target= evaluation.getBestMap().get(pair.source);
					if(target==null)
						target2=edge.getTarget();	
					else
						target2 = target.getAttempt();
					
//					 =   evaluation.getBestMap().get(pair.target).getAttempt();
					p.getObject().setId(getIdNode(p.getObject().getId(),source2.getId(),target2.getId()));
					addDifferentPropertyType(p);
					
				}
				
			}
			else if (diff.isDifferentSubPropertyValue()){
				DifferentSubPropertyValue dpv=(DifferentSubPropertyValue) diff;
				if(!dpv.getName().isSimple()){
					CompositePropertyName name= (CompositePropertyName)dpv.getName();
					if(reducibles.contains(name.getType()))
						continue;
				}
				addDifferentSubProperty((DifferentSubPropertyValue) diff);
			}
			else if(diff.isOutDegreeDifference()){
				addDifferentDegreeOut((OutDegreeDifference) diff);
			}
			else if(diff.isInDegreeDifference()){
				addDifferentDegreeIn((InDegreeDifference) diff);
			}
		}

	}
	
	public String getIdNode(String id, String sourceId, String targetId){
			for(Edge edge :this.attempt.getEdges()){
				if(edge.getSource().getId().equals(sourceId) && edge.getTarget().getId().equals(targetId)){
					return edge.getId();
				}
					
			}
			return id;
	}
	
	public void removeDuplicatedDifferences() {
		
		List<GraphDifference> newDifferences = new ArrayList<>(evaluation.getDifferences());
		for (int i = 0; i < newDifferences.size(); i++) {
			GraphDifference diff = newDifferences.get(i);

			if (diff.isEdgeInsertion()) {
				Edge edge = ((EdgeInsertion) diff).getInsertion();
				if (edge.getId().contains("\'")) {
					newDifferences.remove(i);
					i--;
				}
			}
			else if (diff.isEdgeDeletion()) {
				Edge edge = ((EdgeDeletion) diff).getDeletion();
				if (edge.getId().contains("'")) {
					newDifferences.remove(i);
					i--;
				}
			}

			else if (diff.isDifferentType()) {
				DifferentType diffType = ((DifferentType) diff);
				if (!diffType.getObject().isNode()) {
					String correctType = diffType.getSolutionType();
					if (correctType.contains("Spec")) {
						Edge edge = (Edge) diffType.getObject();
						Node source = edge.getSource();
						Node target = edge.getTarget();
						for (int j = 0; j < newDifferences.size(); j++) {
							GraphDifference diff2 = newDifferences.get(j);
							if (diff2.isEdgeDeletion()) {
								Edge edge2 = ((EdgeDeletion) diff2).getDeletion();
								if (source.equals(edge2.getSource()) && target.equals(edge2.getTarget())) {
									newDifferences.remove(j);
									if (j < i)
										i--;
									break;
								}

								else if (source.equals(edge2.getTarget()) && target.equals(edge2.getSource())) {
									newDifferences.remove(j);
									if (j < i)
										i--;
									break;
								}
							}
						}
					}
				}
			}

			else if (diff.isPropertyDeletion()) {
				PropertyDeletion deletion = (PropertyDeletion) diff;
				if (!deletion.getObject().isNode()) {
					Edge edge = (Edge) deletion.getObject();
					if (edge.getId().contains("'")) {
						newDifferences.remove(i);
						i--;
					}
				}
			}

			else if (diff.isPropertyInsertion()) {
				PropertyInsertion insertion = (PropertyInsertion) diff;
				if (!insertion.getObject().isNode()) {
					Edge edge = (Edge) insertion.getObject();
					if (edge.getId().contains("'")) {
						newDifferences.remove(i);
						i--;
					}
				}
			}

			else if (diff.isDifferentPropertyValue()) {
				DifferentPropertyValue wrong = (DifferentPropertyValue) diff;
				if (!wrong.getObject().isNode()) {
					Edge edge = (Edge) wrong.getObject();
					if (edge.getId().contains("'")) {
						newDifferences.remove(i);
						i--;
					}
				}
			}
		}

		evaluation.setDifferences(new HashSet<>(newDifferences));
	}

	
	
	
	public void simplifyDifferences() {
		
		Map<SourceTargetPair, List<GraphDifference>> edgeDifferences = new HashMap<>();
		Map<Node, List<GraphDifference>> propertiesDifferences = new HashMap<>();

		for (GraphDifference diff : evaluation.getDifferences()) {
			if (diff.isEdgeDeletion()) {
				EdgeDeletion d = (EdgeDeletion) diff;
				SourceTargetPair pair = new SourceTargetPair(d.getDeletion());
				List<GraphDifference> list = new ArrayList<>();
				list.add(d);
				if (edgeDifferences.containsKey(pair))
					list.addAll(edgeDifferences.get(pair));
				edgeDifferences.put(pair, list);
			}

			else if (diff.isEdgeInsertion()) {
				EdgeInsertion d = (EdgeInsertion) diff;
				Edge edge=d.getInsertion();
				Node source = edge.getSource();
				Node target = edge.getTarget();
				if(evaluation.getBestMap().containsKey(source)){
					Node source2 =	evaluation.getBestMap().get(source).getAttempt();
					edge.setSource(source2);
					d.getInsertion().setSource(source2);
				}	
				if(evaluation.getBestMap().containsKey(target)){
					Node target2 =   evaluation.getBestMap().get(target).getAttempt();
					edge.setTarget(target2);
					d.getInsertion().setTarget(target2);
				}
				addEdgeInsertion(d);
				
//				SourceTargetPair pair = new SourceTargetPair(d.getInsertion(), evaluation.getBestMap());
//				if (pair.source != null && pair.target != null) {
//					List<GraphDifference> list = new ArrayList<>();
//					list.add(d);
//					if (edgeDifferences.containsKey(pair))
//						list.addAll(edgeDifferences.get(pair));
//					edgeDifferences.put(pair, list);
//				}
			}

			else if (diff.isPropertyInsertion()) {
				PropertyInsertion d = (PropertyInsertion) diff;
				if (d.getObject().isNode()) {
					Node node = (Node) d.getObject();
					List<GraphDifference> list = new ArrayList<>();
					list.add(d);
					if (propertiesDifferences.containsKey(node))
						list.addAll(propertiesDifferences.get(node));
					propertiesDifferences.put(node, list);
				}
			}

			else if (diff.isPropertyDeletion()) {
				PropertyDeletion d = (PropertyDeletion) diff;
				if (d.getObject().isNode()) {
					Node node = (Node) d.getObject();
					List<GraphDifference> list = new ArrayList<>();
					list.add(d);
					if (propertiesDifferences.containsKey(node))
						list.addAll(propertiesDifferences.get(node));
					propertiesDifferences.put(node, list);
				}
			}
		}
		
		createDifferentPropertyValue(propertiesDifferences, evaluation);
		createWrongEdgeType(edgeDifferences, evaluation);
	}
	
	
	
	private void createDifferentPropertyValue(Map<Node, List<GraphDifference>> propertiesDifferences,
			Evaluation evaluation) {
		
		Set<GraphDifference> newDifferences = new HashSet<>(evaluation.getDifferences());
		for (Node node : propertiesDifferences.keySet()) {
			
			Map<GraphDifference, String> wrongTypes = new HashMap<>();
			Map<GraphDifference, String> correctTypes = new HashMap<>();
			for (GraphDifference diff : propertiesDifferences.get(node)) {
				
				if (diff.isPropertyDeletion()) {
					PropertyDeletion p = (PropertyDeletion) diff;
					
					CompositePropertyValue value = (CompositePropertyValue) p.getPropertyValue();
					SimplePropertyValue value2;
					if(p.getPropertyValue().isSimple()){
						 value2 = (SimplePropertyValue) p.getPropertyValue();
						SimplePropertyName name =  (SimplePropertyName) p.getPropertyName();
						
						
						value = new CompositePropertyValue(name.getName(),value2.getValue());
						
						wrongTypes.put(p, name.getName());
					}
					else{

						 value = (CompositePropertyValue) p.getPropertyValue();
						
						}
						for (String name : value.getValue().keySet()) {
//							System.out.println(value.getValue().get(name));
							if (name.equals("type")) {
								
								wrongTypes.put(p, value.getValue().get(name));
							}
						}
					//}
				}

				else if (diff.isPropertyInsertion()) {
					PropertyInsertion p = (PropertyInsertion) diff;

					String type = getTypeOfPropertyInsertion(p);

					if (type != null)
						correctTypes.put(p, type);
					
					  CompositePropertyValue value = (CompositePropertyValue) p
					  .getPropertyValue(); for (String name :
					  value.getValue().keySet()) { if (name.equals("type"))
					  correctTypes.put(p, value.getValue().get(name)); }
					 
				}
				
			}

			Iterator<GraphDifference> exterior = correctTypes.keySet().iterator();
			while (exterior.hasNext()) {
				GraphDifference d1 = exterior.next();
				String type1 = correctTypes.get(d1);
				Iterator<GraphDifference> interior = wrongTypes.keySet().iterator();
				while (interior.hasNext()) {
					GraphDifference d2 = interior.next();
					String type2 = wrongTypes.get(d2);
					if (!type1.equals(type2)) {
						newDifferences.remove(d1);
						newDifferences.remove(d2);
						newDifferences.add(new DifferentPropertyValue(node, new SimplePropertyName("type"),
								new SimplePropertyValue(type1), new SimplePropertyValue(type2)));
						exterior.remove();
						interior.remove();
						break;
					}
				}
			}
		}
		//System.out.println("ryrtrt"+newDifferences);
		this.evaluation.setDifferences(newDifferences); //comentado aquiiiiiiiiiiiiiiiiiii
	}

	
	private void createWrongEdgeType(Map<SourceTargetPair, List<GraphDifference>> edgeDifferences,
			Evaluation evaluation) {
		
		Set<GraphDifference> newDifferences = new HashSet<>(evaluation.getDifferences());
//		System.out.println("newDifferences " + newDifferences);
		for (SourceTargetPair pair : edgeDifferences.keySet()) {
			Map<GraphDifference, String> wrongTypes = new HashMap<>();
			Map<GraphDifference, String> correctTypes = new HashMap<>();

			for (GraphDifference diff : edgeDifferences.get(pair)) {

				if (diff.isEdgeDeletion()) {
					EdgeDeletion p = (EdgeDeletion) diff;
					wrongTypes.put(p, p.getDeletion().getType());

				}

				else if (diff.isEdgeInsertion()) {
					EdgeInsertion p = (EdgeInsertion) diff;
					wrongTypes.put(p, p.getInsertion().getType());
				}
			}

			Iterator<GraphDifference> exterior = correctTypes.keySet().iterator();
			while (exterior.hasNext()) {
				GraphDifference d1 = exterior.next();
				Edge edge;
				if (d1.isEdgeDeletion())
					edge = ((EdgeDeletion) d1).getDeletion();
				else
					edge = ((EdgeInsertion) d1).getInsertion();
				String type1 = correctTypes.get(d1);
				Iterator<GraphDifference> interior = wrongTypes.keySet().iterator();
				while (interior.hasNext()) {
					GraphDifference d2 = interior.next();
					String type2 = wrongTypes.get(d2);

					if (!type1.equals(type2)) {
						newDifferences.remove(d1);
						newDifferences.remove(d2);
						newDifferences.add(new DifferentType(edge, type1, type2));
						exterior.remove();
						interior.remove();
						break;
					}
				}
			}
		}
		this.evaluation.setDifferences(newDifferences);

	}
	
	
	
	String getTypeOfPropertyInsertion(PropertyInsertion p) {
		PropertyValue propertyValue = p.getPropertyValue();

		if (propertyValue instanceof SimplePropertyValue) {
			SimplePropertyValue value = (SimplePropertyValue) propertyValue;

			return value.getValue();
		} else if (propertyValue instanceof CompositePropertyValue) {
			CompositePropertyValue value = (CompositePropertyValue) propertyValue;

			return value.getValue().get("type");
		} else
			throw new RuntimeException("Invalid property value type");
	}


	/**
	 * @return the nodeInsertions
	 */
	public List<NodeInsertion> getNodeInsertions() {
		return nodeInsertions;
	}

	/**
	 * @param nodeInsertions
	 *            the nodeInsertions to set
	 */
	public void setNodeInsertions(List<NodeInsertion> nodeInsertions) {
		this.nodeInsertions = nodeInsertions;
	}

	/**
	 * @return the nodeDeletions
	 */
	public List<NodeDeletion> getNodeDeletions() {
		return nodeDeletions;
	}

	/**
	 * @param nodeDeletions
	 *            the nodeDeletions to set
	 */
	public void setNodeDeletions(List<NodeDeletion> nodeDeletions) {
		this.nodeDeletions = nodeDeletions;
	}

	/**
	 * @return the edgeInsertions
	 */
	public List<EdgeInsertion> getEdgeInsertions() {
		return edgeInsertions;
	}

	/**
	 * @param edgeInsertions
	 *            the edgeInsertions to set
	 */
	public void setEdgeInsertions(List<EdgeInsertion> edgeInsertions) {
		this.edgeInsertions = edgeInsertions;
	}

	/**
	 * @return the edgeDeletions
	 */
	public List<EdgeDeletion> getEdgeDeletions() {
		return edgeDeletions;
	}

	/**
	 * @param edgeDeletions
	 *            the edgeDeletions to set
	 */
	public void setEdgeDeletions(List<EdgeDeletion> edgeDeletions) {
		this.edgeDeletions = edgeDeletions;
	}

	/**
	 * @return the differentNodeTypes
	 */
	public List<DifferentType> getDifferentNodeTypes() {
		return differentNodeTypes;
	}

	/**
	 * @param differentNodeTypes
	 *            the differentNodeTypes to set
	 */
	public void setDifferentNodeTypes(List<DifferentType> differentNodeTypes) {
		this.differentNodeTypes = differentNodeTypes;
	}

	/**
	 * @return the differentEdgeTypes
	 */
	public List<DifferentType> getDifferentEdgeTypes() {
		return differentEdgeTypes;
	}

	/**
	 * @param differentEdgeTypes
	 *            the differentEdgeTypes to set
	 */
	public void setDifferentEdgeTypes(List<DifferentType> differentEdgeTypes) {
		this.differentEdgeTypes = differentEdgeTypes;
	}

	/**
	 * @return the propertyInsertions
	 */
	public List<PropertyInsertion> getPropertyInsertions() {
		return propertyInsertions;
	}

	/**
	 * @param propertyInsertions
	 *            the propertyInsertions to set
	 */
	public void setPropertyInsertions(List<PropertyInsertion> propertyInsertions) {
		this.propertyInsertions = propertyInsertions;
	}

	/**
	 * @return the propertyDeletions
	 */
	public List<PropertyDeletion> getPropertyDeletions() {
		return propertyDeletions;
	}

	/**
	 * @param propertyDeletions
	 *            the propertyDeletions to set
	 */
	public void setPropertyDeletions(List<PropertyDeletion> propertyDeletions) {
		this.propertyDeletions = propertyDeletions;
	}

	/**
	 * @return the cardinalityInsertions
	 */
	public List<PropertyInsertion> getCardinalityInsertions() {
		return cardinalityInsertions;
	}

	/**
	 * @param cardinalityInsertions
	 *            the cardinalityInsertions to set
	 */
	public void setCardinalityInsertions(List<PropertyInsertion> cardinalityInsertions) {
		this.cardinalityInsertions = cardinalityInsertions;
	}

	/**
	 * @return the cardinalityDeletions
	 */
	public List<PropertyDeletion> getCardinalityDeletions() {
		return cardinalityDeletions;
	}

	/**
	 * @param cardinalityDeletions
	 *            the cardinalityDeletions to set
	 */
	public void setCardinalityDeletions(List<PropertyDeletion> cardinalityDeletions) {
		this.cardinalityDeletions = cardinalityDeletions;
	}

	/**
	 * @return the differentPropertyType
	 */
	public List<DifferentPropertyValue> getDifferentPropertyType() {
		return differentPropertyType;
	}

	/**
	 * @param differentPropertyType
	 *            the differentPropertyType to set
	 */
	public void setDifferentPropertyType(List<DifferentPropertyValue> differentPropertyType) {
		this.differentPropertyType = differentPropertyType;
	}

	/**
	 * @return the differentCardinality
	 */
	public List<DifferentPropertyValue> getDifferentCardinality() {
		return differentCardinality;
	}
	
	
	/**
	 * @param differentCardinality
	 *            the differentCardinality to set
	 */
	public void setDifferentCardinality(List<DifferentPropertyValue> differentCardinality) {
		this.differentCardinality = differentCardinality;
	}
	

	/**
	 * @return the differentSubProperty
	 */
	public List<DifferentSubPropertyValue> getDifferentSubProperty() {
		return differentSubProperty;
	}

	/**
	 * @param differentSubProperty the differentSubProperty to set
	 */
	public void setDifferentSubProperty(List<DifferentSubPropertyValue> differentSubProperty) {
		this.differentSubProperty = differentSubProperty;
	}

	void addNodeInsertion(NodeInsertion insertion) {
		this.nodeInsertions.add(insertion);
	}

	void addNodeDeletion(NodeDeletion deletion) {
		this.nodeDeletions.add(deletion);
	}

	void addEdgeInsertion(EdgeInsertion insertion) {
		this.edgeInsertions.add(insertion);
	}

	void addEdgeDeletion(EdgeDeletion deletion) {
		this.edgeDeletions.add(deletion);
	}

	void addDifferentNodeType(DifferentType diff) {
		this.differentNodeTypes.add(diff);
	}

	void addDifferentEdgeType(DifferentType diff) {
		this.differentEdgeTypes.add(diff);
	}

	void addPropertyInsertion(PropertyInsertion insertion) {
		this.propertyInsertions.add(insertion);
	}

	void addPropertyDeletion(PropertyDeletion deletion) {
		this.propertyDeletions.add(deletion);
	}

	void addCardinalityInsertion(PropertyInsertion insertion) {
		this.cardinalityInsertions.add(insertion);
	}

	void addCardinalityDeletion(PropertyDeletion deletion) {
		this.cardinalityDeletions.add(deletion);
	}

	void addDifferentPropertyType(DifferentPropertyValue diff) {
		this.differentPropertyType.add(diff);
	}

	void addDifferentCardinality(DifferentPropertyValue diff) {
		this.differentCardinality.add(diff);
	}

	void addDifferentSubProperty(DifferentSubPropertyValue diff){
		this.differentSubProperty.add(diff);
	}
	
	
	void addDifferentDegreeOut(OutDegreeDifference diff){
		this.differenceDegreeOut.add(diff);
	}
	
	void addDifferentDegreeIn(InDegreeDifference diff){
		this.differenceDegreeIn.add(diff);
	}
	
	

	/**
	 * @return the differenceDegreeOut
	 */
	public List<OutDegreeDifference> getDifferenceDegreeOut() {
		return differenceDegreeOut;
	}

	/**
	 * @param differenceDegreeOut the differenceDegreeOut to set
	 */
	public void setDifferenceDegreeOut(List<OutDegreeDifference> differenceDegreeOut) {
		this.differenceDegreeOut = differenceDegreeOut;
	}

	/**
	 * @return the differenceDegreeIn
	 */
	public List<InDegreeDifference> getDifferenceDegreeIn() {
		return differenceDegreeIn;
	}

	/**
	 * @param differenceDegreeIn the differenceDegreeIn to set
	 */
	public void setDifferenceDegreeIn(List<InDegreeDifference> differenceDegreeIn) {
		this.differenceDegreeIn = differenceDegreeIn;
	}

	/**
	 * @return the list NodeInfo for nodeInsertion
	 */
	public HashMap<String,List<Node>> getMapNodeInsertion( ) {
		
			
		HashMap<String,List<Node>> map = new HashMap<String, List<Node>>();
		List <Node>list;
		for (NodeInsertion node : nodeInsertions){
			list=new LinkedList<Node>();
			if (map.containsKey(node.getInsertion().getType())){
				 list = map.get(node.getInsertion().getType());
			}
			list.add(node.getInsertion());
			map.put(node.getInsertion().getType(),list);
		}
		return map;
	}
	
	
	

	
	/**
	 * @return the list NodeInfo for nodeDeletions
	 */
	public HashMap<String,List<Node>> getMapNodeDeletion( ) {
		
			
		HashMap<String,List<Node>> map = new HashMap<String, List<Node>>();
		List <Node>list;
		for (NodeDeletion node : nodeDeletions){
			list=new LinkedList<Node>();
			if (map.containsKey(node.getDeletion().getType())){
				 list = map.get(node.getDeletion().getType());
			}
			list.add(node.getDeletion());
			map.put(node.getDeletion().getType(),list);
		}
		return map;
	}
	
	/**
	 * @return the list NodeInfo for EdgeInsertion
	 */
	public HashMap<String,List<Edge>> getMapEdgeInsertion( ) {
		
			
		HashMap<String,List<Edge>> map = new HashMap<String, List<Edge>>();
		List<Edge>list;
		for (EdgeInsertion edge : edgeInsertions){
			list=new LinkedList<Edge>();
			if (map.containsKey(edge.getInsertion().getType())){
				 list = map.get(edge.getInsertion().getType());
			}
			list.add(edge.getInsertion());
			map.put(edge.getInsertion().getType(),list);
		}
		return map;
	}
	
	/**
	 * @return the list NodeInfo for EdgeInsertion
	 */
	public HashMap<String,List<Edge>> getMapEdgeDeletion( ) {
		
			
		HashMap<String,List<Edge>> map = new HashMap<String, List<Edge>>();
		List<Edge>list;
		for (EdgeDeletion edge : edgeDeletions){
			list=new LinkedList<Edge>();
			if (map.containsKey(edge.getDeletion().getType())){
				 list = map.get(edge.getDeletion().getType());
			}
			list.add(edge.getDeletion());
			map.put(edge.getDeletion().getType(),list);
		}
		return map;
	}
	
	
	/**
	 * @return the list NodeInfo for PropertyDeletions
	 */
	public HashMap<String,List<PropertyDeletion>> getMapPropertyDeletions( ) {
		
			
		HashMap<String,List<PropertyDeletion>> map = new HashMap<String, List<PropertyDeletion>>();
		List<PropertyDeletion>list;
		for (PropertyDeletion property : propertyDeletions){
			list=new LinkedList<PropertyDeletion>();
			if(property.getObject().isNode()){
				if (map.containsKey(property.getObject().getType())){
					list = map.get(property.getObject().getType());
				}
			}
			list.add(property);
			map.put(property.getObject().getType(),list);
		}
		return map;
	}
	
	
	/**
	 * @return the map with key- string and value list of DifferentType for nodes.
	 */
	public HashMap<String,List<DifferentType>> getMapDifferentType() {
		
			
		HashMap<String,List<DifferentType>> map = new HashMap<String, List<DifferentType>>();
		List<DifferentType>list;
		for (DifferentType property : differentNodeTypes){
			list=new LinkedList<DifferentType>();
			if(property.getObject().isNode()){
				if (map.containsKey(property.getObject().getType())){
					list = map.get(property.getObject().getType());
				}
			}
			list.add(property);
			map.put(property.getObject().getType(),list);
		}
		return map;
	}
	
	
	
	/**
	 * @return the list NodeInfo for PropertyDeletions
	 */
	public HashMap<String,List<DifferentType>> getMapDifferentEdgeType() {
		
			
		HashMap<String,List<DifferentType>> map = new HashMap<String, List<DifferentType>>();
		List<DifferentType>list;
		for (DifferentType property : differentEdgeTypes){
			list=new LinkedList<DifferentType>();
			if(property.getObject().isNode()){
				if (map.containsKey(property.getObject().getType())){
					list = map.get(property.getObject().getType());
				}
			}
			list.add(property);
			map.put(property.getObject().getType(),list);
		}
		return map;
	}
	
	/**
	 * @return the list NodeInfo for PropertyDeletions
	 */
	public HashMap<String,List<PropertyInsertion>> getMapPropertyInsertion() {
		
			
		HashMap<String,List<PropertyInsertion>> map = new HashMap<String, List<PropertyInsertion>>();
		List<PropertyInsertion>list;
		for (PropertyInsertion property : propertyInsertions){
			list=new LinkedList<PropertyInsertion>();
			if(property.getObject().isNode()){
				if (map.containsKey(property.getObject().getType())){
					list = map.get(property.getObject().getType());
				}
			}
			list.add(property);
			map.put(property.getObject().getType(),list);
		}
		return map;
	}
	
	
	
	/**
	 * @return the list NodeInfo for DifferentProperty
	 */
	public HashMap<String,List<DifferentPropertyValue>> getMapDifferentProperty() {
		
			
		HashMap<String,List<DifferentPropertyValue>> map = new HashMap<String, List<DifferentPropertyValue>>();
		List<DifferentPropertyValue>list;
		for (DifferentPropertyValue property : differentPropertyType){
			list=new LinkedList<DifferentPropertyValue>();
			if(property.getObject().isNode()){
				if (map.containsKey(property.getObject().getType())){
					list = map.get(property.getObject().getType());
				}
			}
			list.add(property);
			map.put(property.getObject().getType(),list);
		}
		return map;
	}
	
	
	/**
	 * @return the list NodeInfo for DifferentProperty
	 */
	public HashMap<String,List<DifferentSubPropertyValue>> getMapDifferentSubProperty() {
		
			
		HashMap<String,List<DifferentSubPropertyValue>> map = new HashMap<String, List<DifferentSubPropertyValue>>();
		List<DifferentSubPropertyValue>list;
		for (DifferentSubPropertyValue property : differentSubProperty){
			list=new LinkedList<DifferentSubPropertyValue>();
			if(property.getObject().isNode()){
				if (map.containsKey(property.getObject().getType())){
					list = map.get(property.getObject().getType());
				}
			}
			list.add(property);
			map.put(property.getObject().getType(),list);
		}
		return map;
	}
	
	
	
//	public HashMap<String,List<Edge>> getMapEdgeDeletion( ) {
//		
//		
//		HashMap<String,List<Edge>> map = new HashMap<String, List<Edge>>();
//		List<Edge>list;
//		for (EdgeDeletion edge : edgeDeletions){
//			list=new LinkedList<Edge>();
//			if (map.containsKey(edge.getDeletion().getType())){
//				 list = map.get(edge.getDeletion().getType());
//			}
//			list.add(edge.getDeletion());
//			map.put(edge.getDeletion().getType(),list);
//		}
//		return map;
//	}

	public String getNameInsertion (String type){
		String result="[ ";
		Map<Node, Match> map = evaluation.getBestMap();
		for( Entry<Node, Match> entry : map.entrySet())
			if(entry.getKey().getType().equals(type))
				result+= entry.getKey().getName()+", ";
		
		int len=nodeInsertions.size(), index=0;
		for(NodeInsertion node: nodeInsertions){
			result +=node.getInsertion().getName();
			if(index+1<len)
				result+=", ";
			index++;
		}
		
		 result+="]";
		 return result;
	}


	public String getNameDeletion (String type){
		String result="[ ";
		Map<Node, Match> map = evaluation.getBestMap();
		for( Entry<Node, Match> entry : map.entrySet())
			if(entry.getKey().getType().equals(type))
				result+= entry.getKey().getType()+", ";
		
		int len=nodeDeletions.size(), index=0;
		for(NodeDeletion node: nodeDeletions){
			result +=node.getDeletion().getName();
			if(index+1<len)
				result+=", ";
			index++;
		}
		 result+="]";
		 return result;
	}

	
	public boolean isEmptyNodeIsertion(){
		return nodeInsertions.size()==0;
	}
	
	public boolean isEmptyEdgeIsertion(){
		return edgeInsertions.size()==0;
	}
	
	public boolean isEmptyNodeDeletion(){
		return nodeDeletions.size()==0;
	}
	
	public boolean isEmptyEdgeDeletion(){
		return edgeDeletions.size()==0;
	}
	
	public boolean isEmptyDifferentNodeTypes(){
		return differentNodeTypes.size()==0;
	}
	public boolean isEmptyDifferentEdgeTypes(){
		return differentEdgeTypes.size()==0;
	}
	
	public boolean isEmptyDifferenceDegreeOut(){
		return differenceDegreeOut.size()==0;
	}
	
	public boolean isEmptyDifferenceDegreeIn(){
		return differenceDegreeIn.size()==0;
	}
	
	public boolean isEmptyDifferenceDegree(){
		return isEmptyDifferenceDegreeOut() && isEmptyDifferenceDegreeIn();
	}
	
	public boolean isEmptyDifferentTypes(){
		return isEmptyDifferentNodeTypes() && isEmptyDifferentEdgeTypes() ;
	}
	
	public int totalError(){
		return edgeDeletions.size()+nodeDeletions.size() + nodeInsertions.size()+edgeInsertions.size() + differentNodeTypes.size() 
				+ differentEdgeTypes.size()+ cardinalityInsertions.size() +cardinalityDeletions.size() + differentCardinality.size() 
				+ differentPropertyType.size() + propertyDeletions.size()+ propertyInsertions.size()+differentSubProperty.size() 
				+totalErrorDegree(); 
	}
	
//	public int totalError(){
//		return totalErrorCardinality() + totalErrorEdge()+ totalErrorNode(); 
//	}
	
	public int totalErrorEdge(){
	return edgeDeletions.size()+edgeInsertions.size() + differentEdgeTypes.size();
		

	}
	
	public int totalErrorNode(){
		if( nodeDeletions.size() ==0 && nodeInsertions.size() ==0 && differentNodeTypes.size() ==0)
			return totalErrorDegree();
		return nodeDeletions.size() + nodeInsertions.size()+  differentNodeTypes.size();
		

	}
	
	public int totalErrorProperty(){
		return differentPropertyType.size() + propertyDeletions.size() + propertyInsertions.size()+differentSubProperty.size() ; 
	}
	
	public int totalErrorCardinality(){
		return  cardinalityInsertions.size() +cardinalityDeletions.size() + differentCardinality.size();
	}
	

	public int totalErrorDegree(){
		return differenceDegreeIn.size()+differenceDegreeOut.size();
	}
	
//	@Override
//	public String toString() {
//		return "DifferenceHandler [nodeInsertions=" + nodeInsertions + ", nodeDeletions=" + nodeDeletions
//				+ ", edgeInsertions=" + edgeInsertions + ", edgeDeletions=" + edgeDeletions + ", differentNodeTypes="
//				+ differentNodeTypes + ", differentEdgeTypes=" + differentEdgeTypes + ", propertyInsertions="
//				+ propertyInsertions + ", propertyDeletions=" + propertyDeletions + ", cardinalityInsertions="
//				+ cardinalityInsertions + ", cardinalityDeletions=" + cardinalityDeletions + ", differentPropertyType="
//				+ differentPropertyType + ", differentCardinality=" + differentCardinality + ", differentSubProperty="+ differentSubProperty+"]";
//	}
	

	/**
	 * @return the evaluation
	 */
	public Evaluation getEvaluation() {
		return evaluation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DifferenceHandler [nodeInsertions=" + nodeInsertions + ", nodeDeletions=" + nodeDeletions
				+ ", edgeInsertions=" + edgeInsertions + ", edgeDeletions=" + edgeDeletions + ", differentNodeTypes="
				+ differentNodeTypes + ", differentEdgeTypes=" + differentEdgeTypes + ", propertyInsertions="
				+ propertyInsertions + ", propertyDeletions=" + propertyDeletions + ", cardinalityInsertions="
				+ cardinalityInsertions + ", cardinalityDeletions=" + cardinalityDeletions + ", differentPropertyType="
				+ differentPropertyType + ", differentCardinality=" + differentCardinality + ", differentSubProperty="
				+ differentSubProperty + ", differenceDegreeOut=" + differenceDegreeOut + ", differenceDegreeIn="
				+ differenceDegreeIn + "]";
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}
	
	
	
}