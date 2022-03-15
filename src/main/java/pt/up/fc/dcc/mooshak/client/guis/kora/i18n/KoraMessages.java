package pt.up.fc.dcc.mooshak.client.guis.kora.i18n;

import com.google.gwt.i18n.client.Messages;

public interface KoraMessages extends Messages {

	@DefaultMessage("It is missing <b>{0}</b> node(s) of type: <b>{1}</b>.")
	String insertNode(String quantity, String type);

	@DefaultMessage("It is missing <b>{0}</b> node(s) of type: <b>{1}</b>. "
			+ "Name suggestion(s): <b>{2}</b>.")
	String insertNode1(String quantity, String type, String names);

	@DefaultMessage("It is missing <b>1</b> node of type: <b>{0}</b>. "
			+ "Hint - label name <b>{1}</b>.")
	String insertNode2(String type, String name);

	@DefaultMessage("It is missing one node from type: <b>{0}</b>. "
			+ "Hint - label <b>{1}</b>, connected to other <b>{2}</b> node(s).")
	String insertNode3(String type, String name, String quantConnects);

	@DefaultMessage("It is missing one node from type: <b>{0}</b>. "
			+ "Hint - label <b>{1}</b>, connected to other <b>{2}</b> node(s). "
			+ "If you need more info check -> {3}.")
	String insertNode4(String type, String name, String quantConnects, String link);

	@DefaultMessage("It is missing <b>{0}</b> edge(s).")
	String insertEdge(String quantity);

	@DefaultMessage("It is missing <b>{0}</b> edge(s) of type: <b>{1}</b>. "
			+ "If you need more info check -> {2}.")
	String insertEdge1(String quantaty, String type, String link);

	@DefaultMessage("It is missing an edge of type: <b>{0}</b>. "
			+ "Hint - source node type: <b>{1}</b>, target node type: <b>{2}</b>.")
	String insertEdge2(String type, String sourceType, String targetType);

	@DefaultMessage("It is missing an edge of type: <b>{0}</b>. "
			+ "Hint - source [name: <b>{1}</b>, type: <b>{2}</b>], target [name: <b>{3}</b>, type: <b>{4}</b>].")
	String insertEdge3(String type, String sourceName, String sourceType, String targetName, String targetType);

	@DefaultMessage("It is missing an edge of type: <b>{0}</b>. "
			+ "Hint - source [name: <b>{1}</b>, type: <b>{2}</b>], target [name: <b>{3}</b>, type: <b>{4}</b>]. "
			+ "If you need more info check -> {5}.")
	String insertEdge4(String type, String sourceName, String sourceType, String targetName, String targetType,
			String link);

	@DefaultMessage("Type <b>{0}</b> has one more node of type <b>{1}</b> than it should.")
	String includeNode(String type, String type2);

	@DefaultMessage("Type <b>{0}</b> include a node more for type <b>{1}</b>.")
	String includeDeletion(String type, String type2);

	@DefaultMessage("<b>{0}</b> node(s) should be removed.")
	String deleteNode(String quantity);

	@DefaultMessage("<b>{0}</b> node(s) of type <b>{1}</b> should be removed. "
			+ "If you need more info check -> {2}.")
	String deleteNode1(String quantity, String type, String link);

	@DefaultMessage("A node of type: <b>{0}</b> should be removed. "
			+ "Hint - label name: <b>{1}</b>. "
			+ "If you need more info check -> {2}.")
	String deleteNode2(String type, String name, String link);

	@DefaultMessage("A node from type: <b>{0}</b> should be removed. "
			+ "Hint - label: <b>{1}</b>, connected to <b>{2}</b> node(s).")
	String deleteNode3(String type, String name, String numberConnection);

	@DefaultMessage("A node from type: <b>{0}</b> should be removed. "
			+ "Hint - label: <b>{1}</b>, connected to <b>{2}</b> node(s). "
			+ "If you need more info check -> {3}.")
	String deleteNode4(String type, String name, String numberConnection, String link);

	@DefaultMessage("<b>{0}</b> edge(s) should be removed.")
	String deleteEdge(String quantity);

	@DefaultMessage("<b>{0}</b> edge(s) from type: <b>{1}</b> should be removed.")
	String deleteEdge1(String quantity, String type);

	@DefaultMessage("An edge from type: <b>{0}</b> should be removed. "
			+ "Hint - source node type: <b>{1}</b>, target node type: <b>{2}</b>.")
	String deleteEdge2(String type, String sourceType, String targetType);

	@DefaultMessage("An edge from type: <b>{0}</b> should be removed. "
			+ "Hint - source: [name: <b>{1}</b>, type: <b>{2}</b>], target: [name: <b>{3}</b>, type: <b>{4}</b>]. "
			+ "If you need more info check -> {5}.")
	String deleteEdge3(String type, String sourceName, String sourceType, String targetName, String targetType,
			String link);

	@DefaultMessage("An edge from type: <b>{0}</b> should be removed.")
	String deleteEdge4(String quantity);

	@DefaultMessage("The diagram has <b>{0}</b> nodes with wrong type.")
	String differentType1(String quantity);

	@DefaultMessage("The diagram has <b>{0}</b> edges with wrong type.")
	String differentType2(String quantity);

	@DefaultMessage("The node <b>{0}</b> (type: <b>{1}</b>) has wrong type.")
	String differentNodeType(String name, String type);

	@DefaultMessage("The node <b>{0}</b> (type: <b>{1}</b>) has wrong type. "
			+ "Correct type is <b>{2}</b>. "
			+ "If you need more info check -> {3}.")
	String differentNodeType1(String name, String type, String correctType, String link);

	@DefaultMessage("The edge (type: <b>{0}</b>) between <b>{1}</b> and <b>{2}</b> has wrong type.")
	String differentEdgeType(String type, String source, String target);

	@DefaultMessage("The edge (type: <b>{0}</b>) between <b>{1}</b> and <b>{2}</b>  has wrong type. "
			+ "Correct type is <b>{3}</b>. "
			+ "If you need more info check -> {4}.")
	String differentEdgeType1(String type, String source, String target, String correctType, String link);

	@DefaultMessage("The diagram has <b>{0}</b> edges with wrong cardinality.")
	String wrongCardinality(String quantity);

	@DefaultMessage("The edge between <b>{0}</b> and <b>{1}</b> has the wrong type: <b>{2}</b>.")
	String wrongCardinality1(String source, String target, String wrongType);

	@DefaultMessage("The edge between <b>{0}</b> (name: <b>{1}</b>) and <b>{2}</b> (name: <b>{3}</b>) has the wrong property <b>{4}</b>.")
	String wrongCardinality2(String source, String sourceName, String target, String targetName, String wrongType);

	@DefaultMessage("The edge between <b>{0}</b> (name: <b>{1}</b>) and <b>{2}</b> (name: <b>{3}</b>) has its type (<b>{4}</b>) wrong. "
			+ "The current value is <b>{5}</b>, but the correct is <b>{6}</b>.")
	String wrongCardinality3(String source, String sourceName, String target, String targetName, String wrongtype,
			String wrongType1, String currectType);

	@DefaultMessage("The property <b>{0}</b> in edge <b>{1}</b>, between <b>{2}</b> and <b>{3}</b>, should be removed.")
	String deleteCardinality(String property, String edge, String source, String target);

	@DefaultMessage("The property <b>{0}</b> in edge between <b>{1}</b> (name: <b>{2}</b>) and <b>{3}</b> (name: <b>{4}</b>) should be removed.")
	String deleteCardinality1(String property, String source, String sourceName, String target, String targetName);

	@DefaultMessage("It is missing the property <b>{0}</b> in edge <b>{1}</b>, between <b>{2}</b> and <b>{3}</b>.")
	String insertCardinality(String property, String edge, String source, String target);

	@DefaultMessage("It is missing the property <b>{0}</b> in edge <b>{1}</b>, between <b>{2}</b> (<b>{3}</b>) and <b>{4}</b> (<b>{5}</b>).")
	String insertCardinality1(String property, String edge, String source, String sourceName, String target,
			String targetName);

	@DefaultMessage("<b>{0}</b> node(s) should have a property modified.")
	String modifyProperty(String quantity);

	// @DefaultMessage("<b>{0}</b> edge(s) from type: <b>{1}</b>, should be
	// modified property <b>{2}</b> ")
	@DefaultMessage("<b>{0}</b> edge(s) from type: <b>{1}</b> should have property <b>{2}</b> modified.")
	String modifyProperty1(String quantity, String type, String participation);

	@DefaultMessage("A node from type: <b>{0}</b> should have property <b>{1}</b> modified.")
	String modifyProperty2(String quantity, String type);

	@DefaultMessage("An edge of type <b>{0}</b> between node <b>{1}</b> and node <b>{2}</b> has the wrong type: <b>{3}</b>.")
	String modifyProperty3(String type, String source, String target, String wrongType);

	@DefaultMessage("A node of type <b>{0}</b> with name <b>{1}</b>, should have the property <b>{2}</b> modified. The current value is <b>{3}</b>, but the correct is <b>{4}</b>.")
	String modifyProperty4(String type, String name, String property, String wrongValue, String CorrectValue);

	@DefaultMessage("An edge of type <b>{0}</b> between nodes: <b>{1}</b> (name: <b>{2}</b>) and <b>{3}</b> (name: <b>{4}</b>) has property <b>{5}</b> wrong.")
	String modifyProperty5(String type, String source, String sourceName, String target, String targetName,
			String wrongType);

	@DefaultMessage("An edge of type <b>{0}</b> between nodes: <b>{1}</b> (name: <b>{2}</b>) and <b>{3}</b> (name: <b>{4}</b>) has property <b>{5}</b> wrong."
			+ " The current value is <b>{6}</b>, but the correct is <b>{7}</b>.")
	String modifyProperty6(String type, String source, String sourceName, String target, String targetName,
			String propertyName, String wrongType1, String currectType);

	@DefaultMessage("It is missing a property <b>{0}</b>, in node [name: <b>{1}</b>, type: <b>{2}</b>].")
	String insertProperty(String quantity, String name, String type);

	@DefaultMessage("The property <b>{0}</b> in element [type: <b>{1}</b>, <b>{2}</b>] should be removed.")
	String deleteProperty(String prop, String tipo, String name);

	@DefaultMessage("<b>{0}</b> node(s) is/are disconnected. Check node(s): <b>{1}</b>.")
	String nodeDisconnected(String quantity, String nodes);

	@DefaultMessage("A node (name: <b>{0}</b>, type: <b>{1}</b>) has wrong number of degree in/out: <b>{2}</b>.")
	String invalidDegree(String name, String type, String degree);

	@DefaultMessage("Invalid connection. An edge of type <b>{0}</b> can not connect the source [type: <b>{1}</b>, name: <b>{2}</b>] to the target [type: <b>{3}</b>, name: <b>{4}</b>].")
	String invalidConnection(String type, String Sname, String Stype, String Tname, String Ttype);

	@DefaultMessage("The node [type: <b>{0}</b>, name: <b>{1}</b>] has invalid number of links.")
	String invalidTypeConnection(String type, String name);

	@DefaultMessage("The node [type: <b>{0}</b>, name: <b>{1}</b>] has property <b>{2}</b>: <b>{3}</b> wrong.")
	String invalidContainer(String type, String name, String propTyprWrong, String Propname);

	@DefaultMessage("The type of the attempt diagram (<b>{0}</b>) is different from the type of the solution diagram (<b>{1}</b>).")
	String invalidExtension(String attempt, String soluntion);

	@DefaultMessage("Invalid number of components: {0}\n")
	String invalidNumberComponent(String componets);

	@DefaultMessage("It is missing <b>{0}</b> node(s): \n")
	String addNode(String quantity);

	@DefaultMessage("It is missing <b>{0}</b> edge(s): \n")
	String addEdge(String quantity);

	@DefaultMessage("<b>{0}</b> node(s) should be removed:\n")
	String removeNode(String quantity);

	@DefaultMessage("<b>{0}</b> edge(s) should be removed:\n")
	String removeEdge(String quantity);

	@DefaultMessage("<b>{0}</b> node(s) of type <b>{1}</b>. If you need more info check -> {2}.\n")
	String nodeType(String quantity, String type, String link);

	@DefaultMessage("<b>{0}</b> edge(s) of type: <b>{1}</b>. If you need more info check -> {2}.\n")
	String edgeType(String quantity, String type, String link);

	@DefaultMessage("Your attempt is {0}% close to the solution! \n")
	String marktext(String mark);

}
