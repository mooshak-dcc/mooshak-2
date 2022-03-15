package pt.up.fc.dcc.mooshak.evaluation.special;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluator;


public class DiagramEvaluatorTest {

	private static final String PATH_TO_THIS_PACKADGE = "pt/up/fc/dcc/mooshak/evaluation/special";
	DiagramEvaluator evaluator;
	Set<Path> trash = new HashSet<>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() throws Exception {
		Evaluator.setTimeout(2);
		evaluator = new DiagramEvaluator();
	}
	
	@After
	public void cleanUp() throws IOException {
		
		for(Path path: trash)
			Files.deleteIfExists(path);
	}

	@Test
	public void testValidFCUP() {
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		Set<Path> solutions = new HashSet<>();
		
		solutions.add(fcupJsonFile);
		
		parameters.setProgram(fcupJsonFile);
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
		} catch (MooshakEvaluationException cause) {
			fail("Unexpected exception");
		}		
	}
	
	
	// /submissions/131131352_FCUP_up200908415/diagram.json
	
	@Test
	public void testFCUPBug() throws IOException, URISyntaxException {
		EvaluationParameters parameters = new EvaluationParameters();
		// Path fcupJsonFile = Paths.get(TestData.FCUP_DIAGRAM_JSON);
		String zipPathname = ClassLoader.getSystemResource(
				PATH_TO_THIS_PACKADGE+"/submissions.zip").toURI().toString();
		URI zipUri = new URI("jar:"+zipPathname);
		Map<String,?> env = new HashMap<>();
		// System.out.println(zipUri);
		
		FileSystem fileSystem = FileSystems.newFileSystem(zipUri,env);
		Path root = null;
		for(Path path:fileSystem.getRootDirectories()) {
			root = path;
			break;
		}
		
		Path program = root.resolve("submissions/131131352_FCUP_up200908415/diagram.json");
		
		Path temp = Files.createTempFile("diagram", ".json");
		trash.add(temp);
		
		Files.deleteIfExists(temp);
		System.out.println(temp);
		
		Files.copy(program,temp);
		
		parameters.setProgram(temp);
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {
			System.out.println(cause.getClassification()+"");
		}
		
	}
	
	@Test
	public void testFCUP20submissions() 
			throws JSONException, IOException, URISyntaxException, InterruptedException  {
		
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		String zipPathname = ClassLoader.getSystemResource(
				PATH_TO_THIS_PACKADGE+"/submissions.zip").toURI().toString();
		URI zipUri = new URI("jar:"+zipPathname);
		Map<String,?> env = new HashMap<>();
		// System.out.println(zipUri);
		
		FileSystem fileSystem = FileSystems.newFileSystem(zipUri,env);
		
		Set<Path> solutions = new HashSet<>();
		
		solutions.add(fcupJsonFile);
		parameters.setSolutions(solutions);
		
		// ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		for(Path root: fileSystem.getRootDirectories()) {
			try(DirectoryStream<Path> rootEntryStream = 
					Files.newDirectoryStream(root)) {
				for(Path rootEntry: rootEntryStream){
					
					try(DirectoryStream<Path> submissionsEntryStream = 
							Files.newDirectoryStream(rootEntry)) {
						for(Path submissionEntry: submissionsEntryStream){
							
							Path program = 
									submissionEntry.resolve("diagram.json").toAbsolutePath();
							
							System.out.println(program+":"+Files.exists(program));
							
							Path temp = Files.createTempFile("diagram", ".json");
							trash.add(temp);
							
							Files.deleteIfExists(temp);
							System.out.println(temp);
							
							Files.copy(program,temp);
							
							// pool.execute(() -> evaluate(parameters,temp));
							
							evaluate(parameters,temp);
									
							
						}
					}
					
				}
			}
		}
		
		// pool.shutdown();
		// pool.awaitTermination(10, TimeUnit.SECONDS);
			
	
	}

	private void evaluate(EvaluationParameters parameters,Path program) {
		parameters.setProgram(program);
		
		long start = System.currentTimeMillis();
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {
			System.out.println(cause.getClassification()+"");
			// System.out.println(cause.getMessage());
			// System.out.println(cause.getFeedback());
			
			// assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			// assertTrue(evaluator.isComplete());
		}
		/*
		catch(Exception cause) {
			System.out.println(cause);
		}
		*/
		
		long duration = System.currentTimeMillis()-start;
		System.out.println(duration);
	}
	
	
	@Test
	public void testFCUPwithJust1Entity1SecTimeout() throws JSONException {
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		String filename = ClassLoader.getSystemResource(
				PATH_TO_THIS_PACKADGE+"/just1entity.json").getPath();
		Set<Path> solutions = new HashSet<>();
				
		Evaluator.setTimeout(1); // give it more time
		
		solutions.add(fcupJsonFile);
		
		parameters.setProgram(Paths.get(filename));
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {
		// System.out.println(cause.getMessage());
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			assertTrue(! evaluator.isComplete());
			assertTrue(cause.getMessage().startsWith("WARNING: These hints might not be exactly correct!"));
		}		
	}
	
	
	
	@Test
	public void testFCUPwithJust1Entity5SecTimeout() throws JSONException {
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		String filename = ClassLoader.getSystemResource(
				PATH_TO_THIS_PACKADGE+"/just1entity.json").getPath();
		Set<Path> solutions = new HashSet<>();
		
		Evaluator.setTimeout(5); // give it more time
		
		solutions.add(fcupJsonFile);
		
		parameters.setProgram(Paths.get(filename));
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {
			// System.out.println(cause.getMessage());
			System.out.println(cause.getFeedback());
			
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			assertTrue(evaluator.isComplete());
			
		}		
	}

	
	@Test
	public void testFCUPMissingEdgeLabels() throws JSONException {
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		 
		String filename = "data/contests/proto_diagram/submissions/131107046_FCUP_team/diagram.json";
		
		Set<Path> solutions = new HashSet<>();
		
		solutions.add(fcupJsonFile);
		
		parameters.setProgram(PersistentCore.getAbsoluteFile(Paths.get(filename)));
		parameters.setSolutions(solutions);
		
		Evaluator.setTimeout(5); // give it more time
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {
			// System.out.println(cause.getMessage());
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			System.out.println(cause.getFeedback());
			
			JSONObject feedback = new JSONObject(cause.getFeedback());
			JSONArray edges = feedback.getJSONArray("edges");
			
			for(int n=0; n<edges.length(); n++) {
				JSONObject edge = edges.getJSONObject(n);
				String temporary = edge.getString("temporary");
				//System.out.println("\t"+node.toString());
				assertTrue(
						"Missing source in "+temporary+" edges",
						edge.has("source"));
				assertTrue(
						"Missing source in "+temporary+" edges",
						edge.has("target"));

			}
		}		
	}

	

	@Test
	public void testFCUPMissingNodeLabels() throws JSONException {
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		 
		String filename = "data/contests/proto_diagram/submissions/130703886_FCUP_team/diagram.json";
		
		Set<Path> solutions = new HashSet<>();
		
		solutions.add(fcupJsonFile);
		
		parameters.setProgram(PersistentCore.getAbsoluteFile(Paths.get(filename)));
		parameters.setSolutions(solutions);
		
		Evaluator.setTimeout(5); // give it more time
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {
			// System.out.println(cause.getMessage());
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			// System.out.println(cause.getFeedback());
			
			JSONObject feedback = new JSONObject(cause.getFeedback());
			JSONArray nodes = feedback.getJSONArray("nodes");
			
			for(int n=0; n<nodes.length(); n++) {
				JSONObject node = nodes.getJSONObject(n);
				String temporary = node.getString("temporary");
				//System.out.println("\t"+node.toString());
				assertTrue("Missing label in "+temporary+" node",node.has("label"));
			}
		}		
	}
	
	
	@Test
	public void testFCUPMissingAttribute() throws IOException, JSONException {
		EvaluationParameters parameters = new EvaluationParameters();
		Path fcupJsonFile = Paths.get(CustomData.FCUP_DIAGRAM_JSON);
		Path absolute = PersistentCore.getAbsoluteFile(fcupJsonFile);
		Path testDiagramFile = Files.createTempFile("testDiagram", ".json");
		Set<Path> solutions = new HashSet<>();
		String fcupJsonString = new String(Files.readAllBytes(absolute));
		JSONObject fcupJson = new JSONObject(fcupJsonString);
		
		
		JSONArray nodes = fcupJson.getJSONArray("nodes");
		JSONArray edges = fcupJson.getJSONArray("edges");
		
		nodes.remove(0);
		for(int  e=0; e < edges.length(); e++) {
			JSONObject edge = edges.getJSONObject(e);
			int source = edge.getInt("source");
			int target = edge.getInt("target");
			
			if(source == 0 || target == 0)
				edges.remove(e);
		}
		
		Files.write(testDiagramFile, fcupJson.toString(4).getBytes());
		trash.add(testDiagramFile);
		
		solutions.add(fcupJsonFile);
		
		parameters.setProgram(testDiagramFile);
		parameters.setSolutions(solutions);
		
		try {
			evaluator.evaluate(parameters);
			fail("Exception expected ");
		} catch (MooshakEvaluationException cause) {
			// System.out.println(cause.getMessage());
			
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			
			assertTrue(cause.getMessage().startsWith("Your attempt is 99% close to the solution."));
			
			JSONObject feedback = new JSONObject(cause.getFeedback());
			JSONArray  feedbackNodes = feedback.getJSONArray("nodes");
			
			assertEquals(0,feedback.getJSONArray("edges").length());
			assertEquals(1,feedbackNodes.length());
			
			JSONObject node = feedbackNodes.getJSONObject(0);
			
			assertEquals("insert",node.getString("temporary"));
			assertEquals("attribute",node.getString("type"));
			
			
			
		}
	}

}
