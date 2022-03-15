package pt.up.fc.dcc.mooshak.evaluation.graph.parse;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class JSONHandlerTest {

	@Test
	public void test() throws FileNotFoundException, JSONException {
		FileInputStream fileStream = new FileInputStream("inputs/Eshu-simple-example.json");
		JSONHandler json = new JSONHandler(fileStream,null);
		Graph graph = json.parseReducible();
		assertEquals("Number of nodes", 2, graph.getNodes().size());
		assertEquals("Number of edges", 1, graph.getEdges().size());
		System.out.println(graph);
	}
	
	@Test
	public void testFcupParseReducible() throws FileNotFoundException, JSONException {
		FileInputStream fileStream = new FileInputStream("inputs/fcup.json");
		JSONHandler json = new JSONHandler(fileStream,null);
		Graph graph = json.parseReducible();
		//Simple Attributes = 16
		//28 nodes - 16 = 12 nodes
		//(28 edges - 16) * 2 = 24
		assertEquals("Number of nodes", 12, graph.getNodes().size());
		assertEquals("Number of edges", 24, graph.getEdges().size());
		System.out.println(graph);
	}
	
	@Test
	public void testFcup2ParseReducible() throws FileNotFoundException, JSONException {
		FileInputStream fileStream = new FileInputStream("inputs/fcup2.json");
		JSONHandler json = new JSONHandler(fileStream,null);
		Graph graph = json.parseReducible();
		assertEquals("Number of nodes", 14, graph.getNodes().size());
		assertEquals("Number of edges", 22, graph.getEdges().size());
		System.out.println(graph);
	}

	
	@Test
	public void testFcupParse() throws FileNotFoundException, JSONException {
		FileInputStream fileStream = new FileInputStream("inputs/fcup.json");
		JSONHandler json = new JSONHandler(fileStream,1,null);
		Graph graph = json.parse();
		assertEquals("Number of nodes", 28, graph.getNodes().size());
		assertEquals("Number of edges", 28, graph.getEdges().size());
	}
	
	
	@Test
	public void testClassParse() throws FileNotFoundException, JSONException, MooshakException {
		Configuration configuration= new Configuration("inputs/class/ClassConfig.xml");
//		System.out.println(configuration.getEshu().getDiagram().getContainerRules().get("Class"));
//		Containers rules =configuration.getEshu().getDiagram();
//		Containers rules =configuration.getEshu().getDiagram().getContainerRules().get("Class");
		FileInputStream fileStream = new FileInputStream("inputs/class2.json");
		JSONHandler json = new JSONHandler(fileStream,1,configuration.getDL2().getDiagram());
		Graph graph = json.parse();
		System.out.println(json.getEvaluationSyntax().getTextFeedback() );
		System.out.println(json.getEvaluationSyntax().getJsonFeedback() );

		assertEquals("Number of nodes", 3, graph.getNodes().size());
		assertEquals("Number of edges", 2, graph.getEdges().size());
		

//		List<Node> nodes = graph.getNodes();
//		for(Node n: nodes ){
//			System.out.println(n);
//			System.out.println();
//		}
	}
}
