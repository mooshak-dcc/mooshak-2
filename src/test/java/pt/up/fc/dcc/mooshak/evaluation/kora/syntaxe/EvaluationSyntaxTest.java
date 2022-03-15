package pt.up.fc.dcc.mooshak.evaluation.kora.syntaxe;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.JSONHandler;
import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class EvaluationSyntaxTest {
	
	
	
	
	
	@Test
	public void test() {
		//EvaluationSyntax es=new  EvaluationSyntax();
		
//		Node n1 =es.new Node(1);
//		Node n2 =es.new Node(2);
//		Node n3 =es.new Node(3);
//		Node n4 =es.new Node(4);
//		es.nodes.add(n1);
//		es.nodes.add(n2);
//		es.nodes.add(n3);
//		es.nodes.add(n4);
//		
//		es.edges.add(es.new Edge(n1, n2));
//		//es.edges.add(es.new Edge(n2, n3));
//		es.edges.add(es.new Edge(n1, n4));
//		
//		es.bfs(null);
		
		
		
	}
	
	@Test
	public void testFcupParse() throws FileNotFoundException, JSONException, MooshakException {
		Configuration fileConfig= new Configuration("inputs/casouso/CasoUsoConfig.xml");
		FileInputStream fileStream = new FileInputStream("inputs/fcupSyntaxe.json");
		//*diagram esta null depois eliminar *******************************************************************************
		JSONHandler json = new JSONHandler(fileStream,1,null);
		Graph graph = json.parse();
		//assertEquals("Number of nodes", 28, graph.getNodes().size());
		//assertEquals("Number of edges", 28, graph.getEdges().size());
		
		EvaluationSyntax evaluationSyntax=new  EvaluationSyntax(graph,fileConfig.getDL2().getDiagram());
		//graph.bfs();;
//	//	ArrayList <List<Node>> componentList=evaluationSyntax.getComponentGraph();
//		for(List<Node> n :componentList )
//			System.out.println("n nodes: "+n.size());
//		
//		assertEquals("Number of Components", 1, componentList.size());
		
	}
	
	
	@Test
	public void getDisconnectedNodesTest() throws FileNotFoundException, JSONException, MooshakException {
		Configuration fileConfig= new Configuration("inputs/casouso/CasoUsoConfig.xml");
		FileInputStream fileStream = new FileInputStream("inputs/fcupSyntaxe.json");
		//*diagram esta null depois eliminar *******************************************************************************
		JSONHandler json = new JSONHandler(fileStream,1,null);
		Graph graph = json.parse();
		
		//EvaluationSyntax evaluationSyntax=new  EvaluationSyntax(graph,fileConfig.getEshu().getDiagram());
//		List <Node> nodes=evaluationSyntax.getDisconnectedNodes();
//		System.out.println(nodes.size());
//		assertEquals("Number of nodes Disconnecteds", 2, nodes.size());
		
	}
	
	

	@Test
	public void sytaxeValidationTest() throws FileNotFoundException, JSONException, MooshakException {
		Configuration fileConfig= new Configuration("inputs/casouso/CasoUsoConfig.xml");
		
		
		FileInputStream fileStream = new FileInputStream("inputs/casouso/attemptCasoUso.json");
		//*diagram esta null depois eliminar *******************************************************************************
		JSONHandler json = new JSONHandler(fileStream,1,null);
		Graph graph = json.parse();
		
		EvaluationSyntax evaluationSyntax=new  EvaluationSyntax(graph,fileConfig.getDL2().getDiagram());
			
		if(evaluationSyntax.isEvaluationError("umluc")){
			System.out.println(evaluationSyntax.getTextFeedback());
			System.out.println(evaluationSyntax.getJsonFeedback());
		}
		else 
			System.out.println("Don't have erro syntax");
		
		//assertEquals("Number of nodes Disconnecteds", 2, nodes.size());
		
		//evaluationSyntax.sytaxeValidationEdges();
			
	}
	
	
	
}
