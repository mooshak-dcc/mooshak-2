package pt.up.fc.dcc.mooshak.evaluation.kora.feedback;

import java.security.InvalidParameterException;

import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.FeedbackManager.Lang;


public enum SummaryFormat{
	
	INSERT_NODE(	
			"It's missing <b>%d</b> node(s) from type: <b>%s</b>.",
			"Falta(m) <b>%d</b> nó(s) do tipo: <b>%s</b>"),
	INSERT_NODE_1(	
			"It's missing <b>%d</b> node(s) from type: <b>%s</b>. Name suggestions: <b>%s</b>",
			"Falta(m) <b>%d</b> nó(s) do tipo: <b>%s</b>. Sugestões de nomes: <b>%s</b>"),
	INSERT_NODE_2(	
			"It's missing one node from type: <b>%s</b>. Hint -Label name %s",
			"Falta um nó do tipo: <b>%s</b>. Dica - Nome label: %s"),
	INSERT_NODE_3(	
			"It's missing one node from type:<b>%s</b>. Hint -Label %s, connected to other %s node(s)",
			"Falta um nó do tipo: <b>%s</b>. Dica - Label: <b>%s</b>, ligado a outro(s <b>%s</b> nó(s)"),
	
	DELETE_NODE(
			"Node(s)  <b>%d</b> should be removed.",
			"O(s) nó(s)<b>%d</b> precisa(m) de ser removido(s)."
			),
	DELETE_NODE_1(
			"%d node(s) from type: <b>%s</b> should be removed.",
			"%d nó(s) do tipo: <b>%s</b> precisam serem removidas."
			),
	DELETE_NODE_2(
			"A node from type: %s should be removed. Hint -Label name: %s",
			"Um nó do tipo: %s precisa ser removida. Dica- Nome label: %s"
			),
	DELETE_NODE_3(
			"A node from type:  <b>%s</b> should be removed. Hint -Label: <b>%s</b> ,connected to  <b>%d</b> node(s): ",
			"Um nó do tipo:  <b>%s</b> precisa ser removida. Dica- Label: <b>%s</b> ,ligado a <b>%d</b> nó(s)"
			),
	
	INSERT_EDGE(	
			"It's missing  <b>%d</b> edge(s).",
			"Falta(m)  <b>%d</b> aresta(s)."),
	INSERT_EDGE_1(	
			"It's missing <b>%d</b> edge(s) from type:  <b>%s</b>.",
			"Faltam  <b>%d</b> aresta(s) do tipo: <b>%s</b>. "),
	INSERT_EDGE_2(	
			"It's missing edge from type:  <b>%s</b>.Hint- source node type: %s, target node type: %s ",
			"Falta uma aresta do tipo:  <b>%s</b>. Dicas- tipo nó de origem: %s, Tipo nó de destino: %s"),
	INSERT_EDGE_3(	
			"It's missing edge from type:  <b>%s</b>. Hint- source: name %s, type %s, target: name %s type %s ",
			"Falta uma aresta do tipo:  <b>%s</b>. Dicas- No origem: nome na solução %s, tipo %s , nó de destino: nome na solução %s, tipo %s"),

	INCLUDE_NODE(	
			"Type <b>%s</b> has one more node of type %s than it should.",
			"Falta %s nó(s) do tipo: <b>%s</b>. "),
	

	INCLUDE_DELETION_NODE(	
			"Type <b>%s</b> include a node more for type %s .",
			"Tipo <b>%s</b> contém um nó do tipo %s a mais."),
	
	DELETE_EDGE(
			"%d edge(s) should be removed.",
			"%d aresta(s) precisa(m) ser(em) removida(s)."
			),
	DELETE_EDGE_1(
			"%d edge(s) from type:<b>%s</b> should be removed.",
			"%d aresta(s) do tipo: <b>%s</b> precisa ser removida."
			),
	DELETE_EDGE_2(
			"A edge from type: <b>%s</b> should be removed.Hint-source node type: <b>%s</b>, target node type: <b>%s</b>",
			"Uma aresta do tipo: <b>%s</b> precisa ser removida. Dicas- tipo nó de origem: <b>%s</b>, Tipo nó de destino:<b>%s</b>"
			),
	DELETE_EDGE_3(
			"A edge from type: <b>%s</b> should be removed. Hint- source: [name: <b>%s</b>, type: <b>%s</b>], target: [name: <b>%s</b>, type: <b>%s</b>]",
			"Uma aresta do tipo: <b>%s</b> precisa ser removida. No origem: nome <b>%s</b>, tipo <b>%s</b> , nó de destino: nome <b>%s</b>, tipo <b>%s</b>"
			),
	DELETE_EDGE_4(
			"A edge from type: <b>%s</b> should be removed.",
			"Uma aresta do tipo: <b>%s</b> precisa ser removida."
			),
	
	DIFFERENT_TYPE(
			"The diagram has <b>%d</b>  <b>%s</b> with wrong type.",
			"O diagrama tem <b>%d</b> <b>%s</b> com tipos errados."
			),
	DIFFERENT_TYPE_NODE(
			"The node <b>%s</b> (type-<b>%s</b>) has  wrong type",
			"O nó %s (tipo-<b>%s</b>) tem tipo errado."
			),
	DIFFERENT_TYPE_NODE1(
			"The node <b>%s</b> (type-<b>%s</b>) has wrong type. Correct type is <b>%s</b>. ",
			"O nó <b>%s</b> (tipo-<b>%s</b>) tem tipo errado. Tipo correcto é <b>%s</b>. "
			),
	DIFFERENT_TYPE_EDGE(
			"The edge <b>%s</b> between <b>%s</b> and <b>%s</b>  has wrong type.",
			"A aresta<b>%s</b> entre <b>%s</b> e<b>%s</b> tem tipo errado."
			),
	DIFFERENT_TYPE_EDGE1(
			"The edge type <b>%s</b>  between <b>%s</b> and <b>%s</b>  has wrong type. Correct type is %s ",
			"A aresta tipo<b>%s</b> entre <b>%s</b> e<b>%s</b> tem tipo errado. Tipo Correcto é <b>%s</b>. "
			),
	
	WRONG_CARDINALITY(
			"The diagram has <b>%d</b>  edges with errors  in cardinalities",
			"O diagrama tem <b>%d</b> aresta(s) com propriedades (cardinalidade(s) + participação)  errada(s)."
			),
	WRONG_CARDINALITY1(
			"The edge between <b>%s</b> and <b>%s</b> has the wrong <b>%s</b>",
			"A aresta entre <b>%s</b> e <b>%s</b> tem a propriedade <b>%s</b> errada."
			),
	WRONG_CARDINALITY2(
			"The edge between <b>%s</b> (name-<b>%s</b>) and <b>%s</b> (name-<b>%s</b>) has the wrong property <b>%s</b> ",
			"A aresta entre <b>%s</b>(nome-<b>%s</b>) e <b>%s</b>(nome-<b>%s</b>) tem a propriedade <b>%s</b> errada."
			),
	WRONG_CARDINALITY3(
			"The edge between <b>%s</b> (name-<b>%s</b>) and <b>%s</b> (name-<b>%s</b>) has the wrong  <b>%s</b>. Current%s, the correct (<b>%s</b>).",
			"A aresta entre <b>%s</b>(nome-<b>%s</b>) e <b>%s</b>(nome-<b>%s</b>) tem a propriedade <b>%s</b> errada(<b>%s</b>). Valor correto é (<b>%s</b>)."
			),
	
	DELETE_CARDINALITY1(
			"The property (<b>%s</b>) in edge ((<b>%s</b>)) between (<b>%s</b>) and (<b>%s</b>) should be removed",
			"A propriedade (<b>%s</b>) na aresta (<b>%s</b>) entre <b>%s</b> e <b>%s</b> precisa ser removida."
			),
	DELETE_CARDINALITY2(
			"The property <b>%s</b> in edge between <b>%s</b> (name-<b>%s</b>) and <b>%s</b> (name-<b>%s</b>) should be removed",
			"A propriedade <b>%s</b> na aresta entre <b>%s</b> (nome-<b>%s</b>) e <b>%s</b> (nome-<b>%s</b>) precisa ser removida."
			),
	
	INSERT_CARDINALITY1(
			"It's missing the property <b>%s</b> in edge (<b>%s</b>) between <b>%s</b> and<b>%s</b>  ",
			"Falta a propriedade <b>%s</b> na aresta <b>%s</b> entre <b>%s</b> e <b>%s</b> ."
			),
	INSERT_CARDINALITY2(
			"It's missing the property <b>%s</b> in edge (<b>%s</b>) between <b>%s</b> (<b>%s</b>) and <b>%s</b> (<b>%s</b>)  ",
			"Falta a propriedade <b>%s</b> na aresta (<b>%s</b>) entre <b>%s</b> e <b>%s</b> (<b>%s</b>) ."
			),
	
//	TOTAL_ERROR(
//			"The Diagram has <b> %d </b> errors.<b> %d </b> errors in  Nodes, <b> %d </b> errors in Edges erros and <b> %d </b> erros in cardinalities",
//			"O diagrama contém <b> %d </b> erros. <b> %d </b> erros nos nós, <b> %d </b> erros nas arestas e <b> %d </b> erros nas cardinalidades"
//			),
	TOTAL_ERROR(
			"The Diagram has <b> %d </b> errors.",
			"O diagrama contém <b> %d </b> erros."
			),
	
	TOTAL_ERROR_NODE(
			" <b> %d </b> errors in  Nodes,",
			" <b> %d </b> erros nos nós, "
			),
	
	TOTAL_ERROR_EDGE(
			" <b> %d </b> errors in Edges erros",
			" <b> %d </b> erros nas arestas"
			),
	TOTAL_ERROR_CARDINALITY(
			" and <b> %d </b> erros in cardinalities",
			" e <b> %d </b> erros nas cardinalidades"
			),
	
	MODIFY_NODE(
			"A node from type: <b>%s</b> should be modified",
			"Um nó do tipo: <b>%s</b> precisa ser modificado"
			),
	MODIFY_EDGE(
			"A edge from type: <b>%s</b> should be modified",
			"Uma aresta do tipo: <b>%s</b> precisa ser modificada"
			),
	
	MODIFY_PROPERTY(
			"<b>%d</b> node(s) should be modified property",
			"<b>%d</b> nó(s) precisa(m) ser(em) modificada(s)"
			),
	
	MODIFY_PROPERTY1(
			"<b>%d</b> node(s) from type: <b>%s</b>, should be modified property",
			"<b>%d</b> aresta(s) do tipo: <b>%s</b> precisa ser modificada"
			),
	MODIFY_PROPERTY2(
			"A node from type: <b>%s</b>, should be modified the property <b>%s</b>",
			"Uma aresta do tipo: <b>%s</b> precisa ser modificada a propriedade <b>%s</b>"
			),
	MODIFY_PROPERTY3(
			"In some node from type: <b>%s</b>, should be modified the property <b>%s</b>, wrong <b>%s</b> : current value <b>%s</b>, correct value %s.",
			"Num nó do tipo: <b>%s</b> precisa ser modificada a proriedade <b>%s</b>, valor atual <b>%s</b>, valor correcto <b>%s</b>"
			),
	
	INVALID_DEGREE(
			"A node (name: <b>%s</b>, type:<b>%s</b> ) has wrong number of degree in/out (<b>%d</b>) for type <b>%s</b>. The correct degree is <b>%d</b>.",
			"um nó (nome: <b>%s</b>, tipo: <b>%s</b> ) tem número de ligações inválida (<b>%d</b>) para o  tipo <b>%s</b>. O valor de ligação esperado é <b>%d</b>."
			),
	
	INSERT_PROPERTY(
			"It's missing a property <b>%s</b>,  from node type: <b>%s</b>",
			"Falta uma propriedade <b>%s</b>, para um nó do tipo <b>%s</b>"
			),
	DELETE_PROPERTY(
			"Should be removed a property: <b>%s</b>, in a node type %s. ",
			"Precisa remover uma propriedade <b>%s</b> em um nó do tipo <b>%s</b>, "
			),
	
	
	NODE_DISCONNECTED(
			"<b>%d</b> node(s) is/are disconnected: <b>%s</b>",
			"<b>%d</b> nós está(ão) desconectados: <b>%s</b>"
			),	
	INVALID_DEGREE_IN_OUT(
			"A node (name: <b>%s</b>, type: <b>%s</b> ) has wrong number of degree in/out: %d",
			"um nó (nome: <b>%s</b>, tipo: <b>%s</b> ) tem número de ligações invalido: %d"
			),
	INVALID_CONNECTION(
			"Connection invalid. Edge type: <b>%s</b> can not connect to source: name - <b>%s</b> type - <b>%s</b>, target: name- <b>%s</b>, type: <b>%s</b>",
			"Ligação inválida. Aresta do tipo: <b>%s</b>, não pode conectar nó origem: nome - <b>%s</b>, tipo <b>%s</b>, nó destino: nome - <b>%s</b>, tipo <b>%s</b>"
			),
	INVALID_TYPE_CONNECTED(
			"A Node from type: <b>%s</b>, name: <b>%s</b> has invalid number of links ",
			"um nó do tipo: <b>%s</b>, nome: <b>%s</b> tem o numero de ligação invalido"
			);
	
	
	String en,pt;
	
	SummaryFormat(String en,String pt){
		this.en = en;
		this.pt = pt;
	}
	
	public String format(Lang lang, Object...values) {
		String format;
		
		switch(lang) {
		case EN:
			format = en;
			break;
		case PT:
			format = pt;
			break;
		default:
			throw new InvalidParameterException("Unknown language:"+lang);
		}
		
		return String.format(format,values);
	}

	
}


