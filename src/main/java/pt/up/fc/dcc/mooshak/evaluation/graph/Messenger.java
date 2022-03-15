package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentPropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.DifferentType;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class Messenger {

	Graph solution;
	Graph attempt;
	String feedback;
	JSONObject jsonFeedback;
	private JSONArray jsonNodes;
	private JSONArray jsonEdges;
	Evaluation evaluation;
	boolean giveFeedback;
	// Map with each attempt node and max comparison value
	Map<Node, Double> mapNodesMaxValue = null;

	public Messenger(Graph solution, Graph attempt, Evaluation evaluation, String type, boolean giveFeedback) {
		this.solution = solution;
		this.attempt = attempt;
		this.evaluation = evaluation;
		this.giveFeedback = giveFeedback;
		this.jsonFeedback = new JSONObject();
		this.jsonNodes = new JSONArray();
		this.jsonEdges = new JSONArray();
		this.feedback = "";
		try {
			handleEERMessage();
			// this.jsonFeedback.put("note", this.feedback);
			this.jsonFeedback.put("nodes", this.jsonNodes);
			this.jsonFeedback.put("edges", this.jsonEdges);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Messenger(Graph solution, Graph attempt, Evaluation evaluation, boolean giveFeedback, String type) {
		this.solution = solution;
		this.attempt = attempt;
		this.evaluation = evaluation;
		this.giveFeedback = giveFeedback;
		if (type.equals("EER"))
			try {
				handleEERMessage();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public Messenger(int exitCode, String message) {
		this.feedback += message;
		// System.exit(exitCode); // cannot exit on a application server
	}

	public Messenger(Graph solution, Graph attempt, Evaluation evaluation, String type) {
		new Messenger(solution, attempt, evaluation, type, true);
	}

	/**
	 * Get textual feedback
	 * 
	 * @return
	 */
	public String getTextualFeedback() {
		return feedback;
	}

	/**
	 * Get structured feedback as a JSON object
	 * 
	 * @return
	 */
	public JSONObject getJsonFeedback() {
		return jsonFeedback;
	}

	private void handleEERMessage() throws JSONException {
		if (evaluation.isComplete())
			handleCompleteEEREvaluation();
		else
			handleIncompleteEEREvaluation();
	}

	private void handleCompleteEEREvaluation() throws JSONException {
		if (evaluation.getGrade() == 100) {
			this.feedback += "Your attempt is correct!";
			// System.exit(0);
		}

		else {
			this.feedback += "Your attempt is " + (int) evaluation.getGrade() + "% close to the solution.\n";
			removeDuplicatedDifferences(evaluation);
			simplifyDifferences(evaluation);
			DifferenceHandler differences = groupDifferences();
			String hints;
			hints = handleNodesDifferences(differences);
			if (!hints.equals("")) {
				this.feedback += hints;
				// System.exit(2);
			}

			hints = handleAttributesDifferences(differences);
			if (!hints.equals("")) {
				this.feedback += hints;
				// System.exit(2);
			}

			hints = handleEdgesDifferences(differences);
			if (!hints.equals("")) {
				this.feedback += hints;
				// System.exit(2);
			}

			hints = handleCardinalityDifferences(differences);
			if (!hints.equals("")) {
				this.feedback += hints;
				// System.exit(2);
			}
		}

	}

	private void handleIncompleteEEREvaluation() throws JSONException {
		int totalSolutionNodes = 0;
		int totalSolutionEdges = 0;
		int solutionSpecializations = 0;
		int attemptsSpecializations = 0;
		Map<String, Integer> solutionNodesByType = new HashMap<>();
		Map<String, Integer> attemptNodesByType = new HashMap<>();
		Map<String, Integer> solutionEdgesByType = new HashMap<>();
		Map<String, Integer> attemptEdgesByType = new HashMap<>();

		// Map with each attempt node and max comparison value - initialization
		if (attempt.getNodes().size() > solution.getNodes().size()) {
			mapNodesMaxValue = new HashMap<>();
			for (Node attemptNode : attempt.getNodes()) {
				double maxValue = attemptNode.getMaxComparisonValue(solution.getNodes());
				mapNodesMaxValue.put(attemptNode, maxValue);
			}
		}

		String nodesPhrases = "";
		String attributesPhrases = "";
		String edgesPhrases = "";

		for (Node node : solution.getNodes()) {
			int simpleAttributes = node.getProperties().size();
			totalSolutionNodes += simpleAttributes + 1;
			if (solutionNodesByType.containsKey("attribute"))
				simpleAttributes += solutionNodesByType.get("attribute");
			solutionNodesByType.put("attribute", simpleAttributes);

			int thisType = 1;
			if (solutionNodesByType.containsKey(node.getType()))
				thisType += solutionNodesByType.get(node.getType());
			solutionNodesByType.put(node.getType(), thisType);
		}

		for (Edge edge : solution.getEdges()) {

			totalSolutionEdges++;
			if (edge.getType().contains("Spec"))
				solutionSpecializations++;

			int thisType = 1;
			if (solutionEdgesByType.containsKey(edge.getType()))
				thisType += solutionEdgesByType.get(edge.getType());
			solutionEdgesByType.put(edge.getType(), thisType);
		}

		for (Node node : attempt.getNodes()) {
			int simpleAttributes = node.getProperties().size();
			if (attemptNodesByType.containsKey("attribute"))
				simpleAttributes += attemptNodesByType.get("attribute");
			attemptNodesByType.put("attribute", simpleAttributes);

			int thisType = 1;
			if (attemptNodesByType.containsKey(node.getType()))
				thisType += attemptNodesByType.get(node.getType());
			attemptNodesByType.put(node.getType(), thisType);
		}

		for (Edge edge : attempt.getEdges()) {
			if (edge.getType().contains("Spec"))
				attemptsSpecializations++;

			int thisType = 1;
			if (attemptEdgesByType.containsKey(edge.getType()))
				thisType += attemptEdgesByType.get(edge.getType());
			attemptEdgesByType.put(edge.getType(), thisType);
		}

		int nodesGrade = totalSolutionNodes;
		boolean putAttributes = false;
		for (String type : solutionNodesByType.keySet()) {
			if (type.equals("attribute"))
				putAttributes = true;
			if (attemptNodesByType.containsKey(type)) {
				int diff = solutionNodesByType.get(type) - attemptNodesByType.get(type);
				nodesGrade -= Math.abs(diff);
				if (diff > 0) {
					if (putAttributes) {
						attributesPhrases += ("add " + diff + " node(s) from type: " + type + "\n");
					} else {
						String suggestion = nameSuggestion(solution, attempt, type);
						if (!suggestion.equals("[]"))
							nodesPhrases += "add: " + type + " (" + diff + " time(s)). Suggestion: " + suggestion;
						else
							nodesPhrases += ("add " + diff + " node(s) from type: " + type);

						nodesPhrases += "\n";
					}
					writeJsonNodesDifferences(type, diff, "insert");
				}

				else if (diff < 0) {
					if (putAttributes) {
						attributesPhrases += ("remove " + -diff + " node(s) from type: " + type + "\n");
					} else {
						String suggestion = nameSuggestion(attempt, solution, type);
						if (!suggestion.equals("[]"))
							nodesPhrases += "remove: " + type + " (" + -diff + " time(s)). Suggestion: " + suggestion;
						else
							nodesPhrases += ("remove " + -diff + " node(s) from type: " + type);
						nodesPhrases += "\n";
					}
					writeJsonNodesDifferences(type, -diff, "delete");
				}
			} else {
				nodesGrade -= solutionNodesByType.get(type);
				if (putAttributes) {
					attributesPhrases += ("add " + solutionNodesByType.get(type) + " node(s) from type: " + type
							+ "\n");
				} else {
					String suggestion = nameSuggestion(solution, attempt, type);
					if (!suggestion.equals("[]")) {
						nodesPhrases += "add: " + type + " (" + solutionNodesByType.get(type)
								+ " time(s)). Suggestion: " + suggestion;
					} else {
						nodesPhrases += ("add " + solutionNodesByType.get(type) + " node(s) from type: " + type);
					}
					nodesPhrases += "\n";
				}
				writeJsonNodesDifferences(type, solutionNodesByType.get(type), "insert");
			}
			putAttributes = false;
		}

		for (String type : attemptNodesByType.keySet()) {
			if (type.equals("attribute"))
				putAttributes = true;
			if (!solutionNodesByType.containsKey(type)) {
				nodesGrade -= attemptNodesByType.get(type);
				if (putAttributes) {
					attributesPhrases += ("remove " + attemptNodesByType.get(type) + " node(s) from type: " + type
							+ "\n");
				} else {
					String suggestion = nameSuggestion(attempt, solution, type);
					if (!suggestion.equals("[]")) {
						nodesPhrases += "remove: " + type + " (" + attemptNodesByType.get(type)
								+ " time(s)). Suggestion: " + suggestion;
					} else {
						nodesPhrases += ("remove " + attemptNodesByType.get(type) + " node(s) from type: " + type);
					}
					nodesPhrases += "\n";
				}
				writeJsonNodesDifferences(type, attemptNodesByType.get(type), "delete");
			}
			putAttributes = false;
		}

		int edgesGrade = totalSolutionEdges;
		for (String type : solutionEdgesByType.keySet()) {
			if (!type.contains("Spec")) {
				if (attemptEdgesByType.containsKey(type)) {

					int diff = solutionEdgesByType.get(type) - attemptEdgesByType.get(type);
					edgesGrade -= Math.abs(diff / 2);
					if (diff > 0) {
						
						if(diff==1)
						{
							edgesPhrases += ("add " + diff  + " edge(s) from type: " + type + "\n");
							writeJsonEdgesDifferences(type, diff, "insert");
							
						}
						else{
							edgesPhrases += ("add " + diff / 2 + " edge(s) from type: " + type + "\n");
							writeJsonEdgesDifferences(type, diff / 2, "insert");
						}
					}

					else if (diff < 0) {
						if(diff==1){
							edgesPhrases += ("remove " + -diff / 2 + " edge(s) from type: " + type + "\n");
							writeJsonEdgesDifferences(type, -diff / 2, "delete");
						}
						
						edgesPhrases += ("remove " + -diff / 2 + " edge(s) from type: " + type + "\n");
						writeJsonEdgesDifferences(type, -diff / 2, "delete");
					}
				} else {
					edgesGrade -= solutionEdgesByType.get(type);
					edgesPhrases += ("add " + solutionEdgesByType.get(type) + " edge(s) from type: " + type + "\n");
					writeJsonEdgesDifferences(type, solutionEdgesByType.get(type), "insert");
				}
			}
		}

		for (String type : attemptEdgesByType.keySet()) {
			if (!solutionEdgesByType.containsKey(type) && !type.contains("Spec")) {
				edgesGrade -= attemptEdgesByType.get(type);
				edgesPhrases += ("remove " + attemptEdgesByType.get(type) / 2 + " edge(s) from type: " + type + "\n");
				writeJsonEdgesDifferences(type, attemptEdgesByType.get(type) / 2, "delete");
			}
		}

		if (solutionSpecializations > attemptsSpecializations) {
			edgesGrade -= (solutionSpecializations - attemptsSpecializations);
			edgesPhrases += ("add " + (solutionSpecializations - attemptsSpecializations)
					+ " node(s) to represent specialization.\n");
		}

		else if (solutionSpecializations < attemptsSpecializations) {
			edgesGrade -= (attemptsSpecializations - solutionSpecializations);
			edgesPhrases += ("You can try " + -(solutionSpecializations - attemptsSpecializations)
					+ " node(s) to represent specialization.\n");
		}

		// this.feedback +=nodesGrade + "|" + totalSolutionNodes + "|"
		// + edgesGrade + "|" + totalSolutionEdges);
		if (evaluation.getGrade() < 0) {
			if (nodesGrade < 0)
				nodesGrade = 0;
			if (edgesGrade < 0)
				edgesGrade = 0;
			double grade = 70 * (nodesGrade / (double) totalSolutionNodes)
					+ 30 * (edgesGrade / (double) totalSolutionEdges);
			grade = 90 * grade / 100;
			evaluation.setGrade(grade);
		}
		this.feedback += "Your attempt is, at least, " + (int) evaluation.getGrade() + "% close to the solution.\n";

		// if (!giveFeedback)
		// System.exit(2);
		if (!nodesPhrases.equals("")) {
			this.feedback += "Hints: You can try to:\n" + nodesPhrases;
			// System.exit(2);
		}

		if (!attributesPhrases.equals("")) {
			this.feedback += "Hints: You can try to:\n" + attributesPhrases;
			// System.exit(2);
		}

		else if (!edgesPhrases.equals("")) {
			this.feedback += "Hints: You can try to:\n" + edgesPhrases;
			// System.exit(2);
		}

		this.feedback = "WARNING: These hints might not be exactly correct!\n" + this.feedback;
	}

	private void writeJsonEdgesDifferences(String fullType, int size, String temporaryType) throws JSONException {
		String types[] = fullType.split(" ");
		for (int i = 0; i < size; i++) {
			JSONObject edgetemporary = new JSONObject();
			edgetemporary.put("type", types[0]);
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				JSONObject property = new JSONObject();

				// property.put(types[j], true);
				property.put("name", types[j]);
				property.put("value", true);

				properties.put(property);
			}
			edgetemporary.put("temporary", temporaryType);
			edgetemporary.put("features", properties);
			this.jsonEdges.put(edgetemporary);
		}

	}

	private void writeJsonNodesDifferences(String fullType, int size, String temporaryType) throws JSONException {
		String types[] = fullType.split(" ");
		for (int i = 0; i < size; i++) {
			Node nodeToRemove = null;
			if (temporaryType.equals("delete")) {
				nodeToRemove = getPossibleNodeToRemove(types[0]);
			}

			JSONObject nodetemporary = new JSONObject();
			
			if (nodeToRemove != null) {
				nodetemporary.put("label", nodeToRemove.getName());
				nodetemporary.put("id", Integer.parseInt(nodeToRemove.getId()));
			}

			nodetemporary.put("type", types[0]);
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				JSONObject property = new JSONObject();

				// property.put(types[j], true);
				property.put("name", types[j]);
				property.put("value", true);

				properties.put(property);
			}
			nodetemporary.put("temporary", temporaryType);
			nodetemporary.put("features", properties);
			this.jsonNodes.put(nodetemporary);
		}

	}

	private Node getPossibleNodeToRemove(String type) {
		if (mapNodesMaxValue != null) {
			Iterator<Node> itAttemptNodes = mapNodesMaxValue.keySet().iterator();
			while (itAttemptNodes.hasNext()) {
				Node node = itAttemptNodes.next();
				// Check if it has the same type of the nodes to remove
				if (type.equals(node.getType().split(" ")[0])) {
					double maxValue = mapNodesMaxValue.get(node);
					if (maxValue < 0.3) {
						itAttemptNodes.remove();
						return node;
					}
				}
			}
		}
		return null;
	}

	private String handleNodesDifferences(DifferenceHandler differences) throws JSONException {
		String hints = "";
		for (NodeInsertion insertion : differences.getNodeInsertions()) {
			Node node = insertion.getInsertion();
			String phrase;
			if (!node.getType().contains("EER"))
				phrase = "It's missing one node from type: " + node.getType() + " which has "
						+ node.getProperties().size()
						+ " simple attribute(s). (Hint - you can try to add some related to this: "
						+ node.getName().toUpperCase() + ")";
			else
				phrase = "It's missing one node from type: " + node.getType();
			hints += phrase + "\n";
			if(node.getId().matches("-.+-.+"))
				continue;
			JSONObject nodeInsertion = createJsonNodeTemporary(node, "insert");
			this.jsonNodes.put(nodeInsertion);
		}

		for (NodeDeletion deletion : differences.getNodeDeletions()) {
			Node node = deletion.getDeletion();
			String phrase;
			if (!node.getType().contains("EER"))
				phrase = ("Your node " + node.getName() + " and its respective attribute(s) should be removed");
			else
				phrase = "A node from type: " + node.getType() + " should be removed";
			hints += phrase + "\n";
			JSONObject nodeDeletion = createJsonNodeTemporary(node, "delete");
			this.jsonNodes.put(nodeDeletion);
		}

		for (DifferentType difType : differences.getDifferentNodeTypes()) {
			Node node = (Node) difType.getObject();
			Node attemptNode = evaluation.getBestMap().get(node).getAttempt();
			String fullName = attemptNode.getName();
			String name = "";
			if (fullName.equals("")) {
				name = "There's one node that";
			} else {
				name = "The node " + fullName;
			}

			hints += (name + " has type " + difType.getAttemptType() + " but should be " + difType.getSolutionType())
					+ "\n";
			JSONObject nodeModification = createJsonNodeTemporary(node, "modify");
			this.jsonNodes.put(nodeModification);
		}
		return hints;
	}

	private JSONObject createJsonNodeTemporary(Node node, String temporary) throws JSONException {
		JSONObject jsonTemporaryNode = new JSONObject();
		String types[] = node.getType().split(" ");
			jsonTemporaryNode.put("id", Integer.parseInt(node.getId()));
		jsonTemporaryNode.put("id", Integer.parseInt(node.getId()));
		jsonTemporaryNode.put("type", types[0]);
		jsonTemporaryNode.put("label", node.getName());
		JSONArray properties = new JSONArray();
		Map<PropertyName, PropertyValue> mapProperties = node.getProperties();
		for (PropertyName propertyname : mapProperties.keySet()) {
			if (propertyname.isSimple()) {
				String name = ((SimplePropertyName) propertyname).getName();
				String value = ((SimplePropertyValue) mapProperties.get(propertyname)).getValue();
				JSONObject property = new JSONObject();

				// property.put(name, value);
				property.put("name", name);
				if ("true".equals(value))
					property.put("value", true);
				else if ("false".equals(value))
					property.put("value", false);
				else
					property.put("value", value);

				properties.put(property);
			}
		}

		for (int i = 1; i < types.length; i++) {
			if (!types[i].equals("-") && !types[i].equalsIgnoreCase("composite")) {
				JSONObject property = new JSONObject();

				// property.put(types[i], true);

				property.put("name", types[i]);
				property.put("value", true);

				properties.put(property);
			}
		}

		jsonTemporaryNode.put("temporary", temporary);
		jsonTemporaryNode.put("features", properties);
		return jsonTemporaryNode;
	}

	private String handleEdgesDifferences(DifferenceHandler differences) throws JSONException {
		String hints = "";
		Map<String, Integer> edgeInsertion = new HashMap<>();
		for (EdgeInsertion insertion : differences.getEdgeInsertions()) {
			Edge edge = insertion.getInsertion();
			String type = edge.getType();
			int increment = 1;
			
			if (edgeInsertion.containsKey(type))
				increment += edgeInsertion.get(edge.getType());
			edgeInsertion.put(edge.getType(), increment);
			
			String types[] = type.split(" "); 
			for (int i = 0; i < edgeInsertion.get(type); i++) {
				JSONObject jsonInsertion = new JSONObject();

				jsonInsertion.put("type", types[0]);
				if(edge.getSource().getId().matches("-.+-.+") ||
						edge.getTarget().getId().matches("-.+-.+"))
					continue;
				jsonInsertion.put("source", 
						Integer.parseInt(edge.getSource().getId()));
				jsonInsertion.put("target", 
						Integer.parseInt(edge.getTarget().getId()));
				
				JSONArray properties = new JSONArray();
				for (int j = 1; j < types.length; j++) {
					JSONObject property = new JSONObject();

					property.put("name", types[j]);
					property.put("value", true);

					properties.put(property);
				}
				jsonInsertion.put("temporary", "insert");
				jsonInsertion.put("features", properties);
				
				this.jsonEdges.put(jsonInsertion);
			}
			
		}

		for (String type : edgeInsertion.keySet()) {
			hints += "It's missing " + edgeInsertion.get(type) + " connection(s) from type " + type
					+ "(between Entities, Relationships, Composite Attributes or any EER special element).";

			
			if (type.contains("Spec"))
				hints += " Don't forget that you have to use a specialization node and two connections to do it.";
			hints += "\n";
		}

		for (EdgeDeletion deletion : differences.getEdgeDeletions()) {
			Edge edge = deletion.getDeletion();
			String name1 = edge.getSource().getName();
			if (name1.equals("")) {
				name1 = "a node from type " + edge.getSource().getType();
			}

			String name2 = edge.getTarget().getName();
			if (name2.equals("")) {
				name2 = "a node from type " + edge.getTarget().getType();
			}
			hints += ("Your edge that connects " + name1 + " to " + name2 + " should be removed") + "\n";
			JSONObject jsonDeletion = new JSONObject();
			String types[] = edge.getType().split(" ");
			jsonDeletion.put("id", Integer.parseInt(edge.getId()));
			jsonDeletion.put("type", types[0]);
			jsonDeletion.put("source", 
					Integer.parseInt(edge.getSource().getId()));
			jsonDeletion.put("target", 
					Integer.parseInt(edge.getTarget().getId()));
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				JSONObject property = new JSONObject();

				// property.put(types[j], true);
				property.put("name", types[j]);
				property.put("value", true);

				properties.put(property);
			}
			jsonDeletion.put("temporary", "delete");
			jsonDeletion.put("features", properties);
			this.jsonEdges.put(jsonDeletion);
		}

		for (DifferentType difType : differences.getDifferentEdgeTypes()) {
			Edge edge = (Edge) difType.getObject();
			Node source = edge.getSource();
			Node target = edge.getTarget();
			source = evaluation.getBestMap().get(source).getAttempt();
			target = evaluation.getBestMap().get(target).getAttempt();
			String sourceName = "";
			String targetName = "";
			if (source.getName().equals(""))
				sourceName = "from type " + source.getType();
			else {
				String fullName = source.getName();
				sourceName = fullName;
			}

			if (target.getName().equals(""))
				targetName = "from type " + target.getType();
			else {
				String fullName = target.getName();
				targetName = fullName;
			}

			hints += ("There is an edge that connects a node " + sourceName + " to a node " + targetName
					+ " that has type " + difType.getAttemptType() + " but should be from type "
					+ difType.getSolutionType()) + "\n";

			JSONObject jsonModification = new JSONObject();
			String types[] = edge.getType().split(" ");

			String id = edge.getId();
			if(id.endsWith("'"))
				id = "-"+id.replace("'", "");
			
			jsonModification.put("id", Integer.parseInt(id));
			
			jsonModification.put("id", edge.getId());
			jsonModification.put("type", types[0]);
			jsonModification.put("source", Integer.parseInt(source.getId()));
			jsonModification.put("target", Integer.parseInt(target.getId()));
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				JSONObject property = new JSONObject();

				// property.put(types[j], true);
				property.put("name", types[j]);
				property.put("value", true);

				properties.put(property);
			}
			jsonModification.put("temporary", "modify");
			jsonModification.put("features", properties);
			this.jsonEdges.put(jsonModification);
		}

		return hints;
	}

	private String handleAttributesDifferences(DifferenceHandler differences) throws JSONException {
		String hints = "";
		List<AttributeCounter> counter = new ArrayList<>();

		for (PropertyInsertion insertion : differences.getAttributeInsertions()) {
			// CompositePropertyValue value = (CompositePropertyValue)
			// insertion.getPropertyValue();
			// String type = value.getValue().get("type");

			String type = getTypeOfPropertyInsertion(insertion);

			Node solutionNode = (Node) insertion.getObject();
			Node attemptNode = evaluation.getBestMap().get(solutionNode).getAttempt();

			AttributeCounter att = new AttributeCounter(attemptNode, type);
			if (!counter.contains(att))
				counter.add(att);
			else {
				for (int i = 0; i < counter.size(); i++) {
					if (counter.get(i).equals(att)) {
						counter.get(i).counter++;
						break;
					}
				}
			}

		}

		for (AttributeCounter count : counter) {
			String nodeName = count.node.getName();
			hints += "There is missing" + count.counter + " node(s) from type: " + count.attributeType
					+ " related to node " + nodeName + "\n";

			for (int i = 0; i < count.counter; i++) {
				JSONObject nodeInsertion = new JSONObject();
				String types[] = count.attributeType.split(" ");
				nodeInsertion.put("type", types[0]);
				nodeInsertion.put("label", nodeName);
				JSONArray properties = new JSONArray();
				for (int j = 1; j < types.length; j++) {
					if (!types[j].equals("-") && !types[j].equalsIgnoreCase("composite")) {
						JSONObject property = new JSONObject();

						// property.put(types[j], true);
						property.put("name", types[j]);
						property.put("value", true);

						properties.put(property);
					}
				}
				nodeInsertion.put("temporary", "insert");
				nodeInsertion.put("features", properties);
				this.jsonNodes.put(nodeInsertion);
			}
		}

		for (PropertyDeletion deletion : differences.getAttributeDeletions()) {
			CompositePropertyName compositeName = (CompositePropertyName) deletion.getPropertyName();
			String id = compositeName.getName();// compositeName.getName().substring(1,compositeName.getName().length()
												// - 1);
			Node solutionNode = (Node) deletion.getObject();
			Node attemptNode = evaluation.getBestMap().get(solutionNode).getAttempt();
			String nodeName = attemptNode.getName();
			hints += ("The attribute with id " + id + " related to node " + nodeName + " should be removed.") + "\n";

			JSONObject nodeDeletion = new JSONObject();
			String types[] = compositeName.getType().split(" ");
			nodeDeletion.put("id", Integer.parseInt(id));
			nodeDeletion.put("type", types[0]);
			nodeDeletion.put("label", nodeName);
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				if (!types[j].equals("-") && !types[j].equalsIgnoreCase("composite")) {
					JSONObject property = new JSONObject();

					// property.put(types[j], true);
					property.put("name", types[j]);
					property.put("value", true);

					properties.put(property);
				}
			}
			nodeDeletion.put("temporary", "delete");
			nodeDeletion.put("features", properties);
			this.jsonNodes.put(nodeDeletion);
		}

		Map<Node, List<DifferentPropertyValue>> differentTypeProperties = new HashMap<>();
		for (DifferentPropertyValue difValue : differences.getDifferentAttributeType()) {
			Node node = (Node) difValue.getObject();
			List<DifferentPropertyValue> list = new ArrayList<>();
			list.add(difValue);
			if (differentTypeProperties.containsKey(node))
				list.addAll(differentTypeProperties.get(node));
			differentTypeProperties.put(node, list);
		}

		for (Node node : differentTypeProperties.keySet()) {
			String wrongTypes = "";
			String correctTypes = "";
			for (DifferentPropertyValue p : differentTypeProperties.get(node)) {
				wrongTypes += ((SimplePropertyValue) p.getWrongValue()).getValue() + "; ";
				correctTypes += ((SimplePropertyValue) p.getCorrectValue()).getValue() + "; ";
			}
			Node attemptNode = evaluation.getBestMap().get(node).getAttempt();
			String name = attemptNode.getName();
			this.feedback += "The node " + name + " have attributes with wrong type:\n\tWrong type(s): " + wrongTypes
					+ "\n\tCorrect type(s): " + correctTypes;

		}

		return hints;
	}

	private String handleCardinalityDifferences(DifferenceHandler differences) {
		String hints = "";
		Set<Node> missingCardinality = new HashSet<>();
		Set<Node> extraCardinality = new HashSet<>();
		for (PropertyInsertion insertion : differences.getCardinalityInsertions()) {
			PropertyName name = insertion.getPropertyName();
			SimplePropertyName simple = (SimplePropertyName) name;
			if (simple.getName().equals("cardinality")) {
				Edge edge = (Edge) insertion.getObject();
				if (edge.getSource().getType().contains("Relationship"))
					missingCardinality.add(edge.getSource());
				else if (edge.getTarget().getType().contains("Relationship"))
					missingCardinality.add(edge.getTarget());
			}
		}

		for (PropertyDeletion deletion : differences.getCardinalityDeletions()) {
			PropertyName name = deletion.getPropertyName();
			SimplePropertyName simple = (SimplePropertyName) name;
			if (simple.getName().equals("cardinality")) {
				Edge edge = (Edge) deletion.getObject();
				if (edge.getSource().getType().contains("Relationship"))
					extraCardinality.add(edge.getSource());
				else if (edge.getTarget().getType().contains("Relationship"))
					extraCardinality.add(edge.getTarget());
			}
		}

		if (!missingCardinality.isEmpty()) {
			hints += ("There is (are) missing the cardinality in the following relationship(s): ");
			String phrase = "";
			for (Node node : missingCardinality) {
				Node attemptNode = evaluation.getBestMap().get(node).getAttempt();
				phrase += attemptNode.getName() + ",";
			}

			hints += (phrase.substring(0, phrase.length() - 1)) + "\n";
		}

		if (!extraCardinality.isEmpty()) {
			hints += ("There is (are) cardinality in the following relationship(s) and should be removed: ");
			String phrase = "";
			for (Node node : extraCardinality) {
				Node attemptNode = evaluation.getBestMap().get(node).getAttempt();
				phrase += attemptNode.getName() + ",";
			}

			hints += (phrase.substring(0, phrase.length() - 1)) + "\n";
		}

		for (DifferentPropertyValue difValue : differences.getDifferentCardinality()) {
			Edge edge = (Edge) difValue.getObject();
			Node source = evaluation.getBestMap().get(edge.getSource()).getAttempt();
			Node target = evaluation.getBestMap().get(edge.getTarget()).getAttempt();
			String sourceName = source.getName();
			String targetName = target.getName();

			if (sourceName.equals(""))
				sourceName = "a node from type " + source.getType();

			if (targetName.equals(""))
				targetName = "a node from type " + target.getType();

			hints += ("The edge between " + sourceName + " and " + targetName + " has the wrong cardinality") + "\n";

		}

		return hints;
	}

	private void removeDuplicatedDifferences(Evaluation evaluation) {
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

	private void simplifyDifferences(Evaluation evaluation) {
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
				SourceTargetPair pair = new SourceTargetPair(d.getInsertion(), evaluation.getBestMap());
				if (pair.source != null && pair.target != null) {
					List<GraphDifference> list = new ArrayList<>();
					list.add(d);
					if (edgeDifferences.containsKey(pair))
						list.addAll(edgeDifferences.get(pair));
					edgeDifferences.put(pair, list);
				}
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

	private void createWrongEdgeType(Map<SourceTargetPair, List<GraphDifference>> edgeDifferences,
			Evaluation evaluation) {
		Set<GraphDifference> newDifferences = new HashSet<>(evaluation.getDifferences());

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
		evaluation.setDifferences(newDifferences);

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
					for (String name : value.getValue().keySet()) {
						if (name.equals("type")) {
							wrongTypes.put(p, value.getValue().get(name));
						}
					}
				}

				else if (diff.isPropertyInsertion()) {
					PropertyInsertion p = (PropertyInsertion) diff;

					String type = getTypeOfPropertyInsertion(p);

					if (type != null)
						correctTypes.put(p, type);
					/*
					 * CompositePropertyValue value = (CompositePropertyValue) p
					 * .getPropertyValue(); for (String name :
					 * value.getValue().keySet()) { if (name.equals("type"))
					 * correctTypes.put(p, value.getValue().get(name)); }
					 */
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
		evaluation.setDifferences(newDifferences);
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

	private DifferenceHandler groupDifferences() {
		DifferenceHandler differences = new DifferenceHandler();

		for (GraphDifference diff : evaluation.getDifferences()) {
			if (diff.isNodeInsertion())
				differences.addNodeInsertion((NodeInsertion) diff);

			else if (diff.isNodeDeletion())
				differences.addNodeDeletion((NodeDeletion) diff);

			else if (diff.isEdgeInsertion())
				differences.addEdgeInsertion((EdgeInsertion) diff);

			else if (diff.isEdgeDeletion())
				differences.addEdgeDeletion((EdgeDeletion) diff);

			else if (diff.isDifferentType()) {
				DifferentType d = (DifferentType) diff;
				if (d.getObject().isNode())
					differences.addDifferentNodeType(d);
				else
					differences.addDifferentEdgeType(d);
			}

			else if (diff.isPropertyInsertion()) {
				PropertyInsertion p = (PropertyInsertion) diff;
				if (p.getObject().isNode())
					differences.addAttributeInsertion(p);
				else
					differences.addCardinalityInsertion(p);
			}

			else if (diff.isPropertyDeletion()) {
				PropertyDeletion p = (PropertyDeletion) diff;
				if (p.getObject().isNode())
					differences.addAttributeDeletion(p);
				else
					differences.addCardinalityDeletion(p);
			}

			else if (diff.isDifferentPropertyValue()) {
				DifferentPropertyValue p = (DifferentPropertyValue) diff;
				if (p.getObject().isNode()) {
					differences.addDifferentAttributeType(p);
					;
				} else
					differences.addDifferentCardinality(p);
			}
		}

		return differences;
	}

	private String nameSuggestion(Graph solution, Graph attempt, String type) {
		String suggestion = "[";
		List<String> solutionNames = new ArrayList<>();
		List<String> attemptNames = new ArrayList<>();

		for (Node node : solution.getNodes()) {
			if (node.getType().equals(type)) {
				String name;
				if (!type.contains("EER")) {
					if (type.contains("Composite"))
						name = Normalizer.normalize(node.getName(), Form.NFD)
								.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					else
						name = Normalizer.normalize(node.getName().toUpperCase(Locale.ENGLISH), Form.NFD)
								.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					solutionNames.add(name);
				}
			}
		}

		for (Node node : attempt.getNodes()) {
			if (node.getType().equals(type)) {
				String name;
				if (!type.contains("EER")) {
					if (type.contains("Composite"))
						name = Normalizer.normalize(node.getName(), Form.NFD)
								.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
					else
						name = Normalizer.normalize(node.getName().toUpperCase(Locale.ENGLISH), Form.NFD)
								.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

					attemptNames.add(name);
				}
			}
		}

		for (String name : solutionNames) {
			if (!attemptNames.contains(name))
				suggestion += name + ",";
		}
		if (suggestion.equals("["))
			suggestion = "[]";
		else
			suggestion = suggestion.substring(0, suggestion.length() - 1) + "]";
		return suggestion;
	}

}

class AttributeCounter {
	Node node;
	String attributeType;
	int counter;

	AttributeCounter(Node node, String attributeType) {
		this.node = node;
		this.attributeType = attributeType;
		this.counter = 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeType == null) ? 0 : attributeType.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeCounter other = (AttributeCounter) obj;
		if (attributeType == null) {
			if (other.attributeType != null)
				return false;
		} else if (!attributeType.equals(other.attributeType))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

}

class DifferenceHandler {
	List<NodeInsertion> nodeInsertions;
	List<NodeDeletion> nodeDeletions;
	List<EdgeInsertion> edgeInsertions;
	List<EdgeDeletion> edgeDeletions;
	List<DifferentType> differentNodeTypes;
	List<DifferentType> differentEdgeTypes;
	List<PropertyInsertion> attributeInsertions;
	List<PropertyDeletion> attributeDeletions;
	List<PropertyInsertion> cardinalityInsertions;
	List<PropertyDeletion> cardinalityDeletions;
	List<DifferentPropertyValue> differentAttributeType;
	List<DifferentPropertyValue> differentCardinality;

	public DifferenceHandler() {
		this.nodeInsertions = new ArrayList<>();
		this.nodeDeletions = new ArrayList<>();
		this.edgeInsertions = new ArrayList<>();
		this.edgeDeletions = new ArrayList<>();
		this.differentNodeTypes = new ArrayList<>();
		this.differentEdgeTypes = new ArrayList<>();
		this.attributeInsertions = new ArrayList<>();
		this.attributeDeletions = new ArrayList<>();
		this.cardinalityInsertions = new ArrayList<>();
		this.cardinalityDeletions = new ArrayList<>();
		this.differentAttributeType = new ArrayList<>();
		this.differentCardinality = new ArrayList<>();
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
	 * @return the attributeInsertions
	 */
	public List<PropertyInsertion> getAttributeInsertions() {
		return attributeInsertions;
	}

	/**
	 * @param attributeInsertions
	 *            the attributeInsertions to set
	 */
	public void setAttributeInsertions(List<PropertyInsertion> attributeInsertions) {
		this.attributeInsertions = attributeInsertions;
	}

	/**
	 * @return the attributeDeletions
	 */
	public List<PropertyDeletion> getAttributeDeletions() {
		return attributeDeletions;
	}

	/**
	 * @param attributeDeletions
	 *            the attributeDeletions to set
	 */
	public void setAttributeDeletions(List<PropertyDeletion> attributeDeletions) {
		this.attributeDeletions = attributeDeletions;
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
	 * @return the differentAttributeType
	 */
	public List<DifferentPropertyValue> getDifferentAttributeType() {
		return differentAttributeType;
	}

	/**
	 * @param differentAttributeType
	 *            the differentAttributeType to set
	 */
	public void setDifferentAttributeType(List<DifferentPropertyValue> differentAttributeType) {
		this.differentAttributeType = differentAttributeType;
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

	void addAttributeInsertion(PropertyInsertion insertion) {
		this.attributeInsertions.add(insertion);
	}

	void addAttributeDeletion(PropertyDeletion deletion) {
		this.attributeDeletions.add(deletion);
	}

	void addCardinalityInsertion(PropertyInsertion insertion) {
		this.cardinalityInsertions.add(insertion);
	}

	void addCardinalityDeletion(PropertyDeletion deletion) {
		this.cardinalityDeletions.add(deletion);
	}

	void addDifferentAttributeType(DifferentPropertyValue diff) {
		this.differentAttributeType.add(diff);
	}

	void addDifferentCardinality(DifferentPropertyValue diff) {
		this.differentCardinality.add(diff);
	}
}

class SourceTargetPair {
	Node source;
	Node target;

	public SourceTargetPair(Edge edge) {
		this.source = edge.getSource();
		this.target = edge.getTarget();
	}

	public SourceTargetPair(Edge edge, Map<Node, Match> bests) {
		Match aux = bests.get(edge.getSource());
		if (aux == null)
			this.source = null;
		else
			this.source = aux.getAttempt();
		aux = bests.get(edge.getTarget());
		if (aux == null)
			this.target = null;
		else
			this.target = aux.getAttempt();
	}

	public SourceTargetPair(Node source, Node target) {
		this.source = source;
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceTargetPair other = (SourceTargetPair) obj;
		if (source.equals(other.source) && target.equals(other.target))
			return true;
		if (source.equals(other.target) && target.equals(other.source))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SourceTargetPair [source=" + source.getId() + ", target=" + target.getId() + "]";
	}

}
