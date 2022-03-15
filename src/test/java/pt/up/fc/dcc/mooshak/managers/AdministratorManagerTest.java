package pt.up.fc.dcc.mooshak.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakField;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

public class AdministratorManagerTest {

	private static final String CREATED = "created";

	AdministratorManager administrator = AdministratorManager.getInstance();
	List<PersistentObject> trash = new ArrayList<PersistentObject>();

	@BeforeClass
	public static void setUpBeforeClass() {
		PersistentObject.setHome(CustomData.HOME);
		SafeExecution.setWebInf(CustomData.WEB_INF);
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		for(PersistentObject persistent: trash)
			persistent.delete();
	}

	@Test
	public <T extends PersistentObject> 
	void testCreateMooshakObjectProblemInvalid() throws MooshakContentException {

		testCreateMooshakObjectException(CustomData.PROBLEM_J_PATHNAME);
	}

	@Test
	public <T extends PersistentObject> 
	void testCreateMooshakObjectTestInvalid() throws MooshakContentException {

		testCreateMooshakObjectException(CustomData.TEST_PATHNAME);
	}

	@Test
	public <T extends PersistentObject> 
	void testCreateMooshakObjectPrintoutInvalid() throws MooshakContentException {

		testCreateMooshakObjectException(CustomData.PRINTOUT_PATHNAME);
	}

	@Test
	public <T extends PersistentObject> 
	void testCreateMooshakObjectInvalid() throws MooshakContentException {

		testCreateMooshakObjectException(CustomData.PROBLEM_J_PATHNAME);
	}

	@Test
	public void testCreateMooshakObjectSubmission() throws MooshakException {

		testCreateMooshakObject(CustomData.SUBMISSIONS_PATHNAME, p -> {});
	}

	@Test
	public void testCreateMooshakObjectPrintout() throws MooshakException {

		testCreateMooshakObject(CustomData.PRINTOUTS_PATHNAME, p -> {});
	}

	@Test
	public void testCreateMooshakObjectBallon() throws MooshakException {

		testCreateMooshakObject(CustomData.BALLOONS_PATHNAME, p -> {});
	}

	@Test
	public void testCreateMooshakObjectQuestion() throws MooshakException {

		testCreateMooshakObject(CustomData.QUESTIONS_PATHNAME, p -> {});
	}


	@Test
	public void testCreateMooshakObjectProblem() throws MooshakException {
		testCreateMooshakObject(CustomData.PROBLEMS_PATHNAME, p -> {

			try {
				p.open("images");
				p.open("tests");
			} catch (Exception cause) {
				cause.printStackTrace();
				fail("Unexpected exception");
			}
		});
	}

	@Test
	public void testCreateMooshakObjectContest() throws MooshakException {
		testCreateMooshakObject(CustomData.CONTESTS, p -> {

			try {
				p.open("submissions");
				p.open("printouts");
				p.open("questions");
				p.open("balloons");
				p.open("users");
			} catch (Exception cause) {
				cause.printStackTrace();
				fail("Unexpected exception");
			}
		});
	}


	@Test
	public void testDestroyMooshakObjectContest() throws MooshakException {

		testDestroyMooshakObject(CustomData.CONTESTS);
	}

	@Test
	public void testDestroyMooshakObjectProblem() throws MooshakException {

		testDestroyMooshakObject(CustomData.PROBLEMS_PATHNAME);
	}

	@Test
	public void testDestroyMooshakObjectLanguages() throws MooshakException {

		testDestroyMooshakObject(CustomData.LANGUAGES);
	}


	@Test
	public void testGetMooshakTypeTop() throws MooshakException {

		testMooshakType("Top", new Object[] { 
				"Fatal", AttributeType.LABEL,
				"Warning", AttributeType.LABEL, 
				"configs", AttributeType.DATA,
				"contests", AttributeType.DATA, 
				"trash", AttributeType.DATA },
				new Object[] {});
	}
	
	@Test
	public void testGetMooshakTypeImages() throws MooshakException {
		testMooshakType("Images", new Object[] {
				"Fatal", AttributeType.LABEL,
				"Warning", AttributeType.LABEL, 
				"Image", AttributeType.CONTENT,
		}, new Object[] {});
	}

	@Test
	public void testGetMooshakTypeLanguages() throws MooshakException {

		testMooshakType("Languages", new Object[] { "Fatal",
				AttributeType.LABEL, "Warning", AttributeType.LABEL,
				"MaxCompFork", AttributeType.INTEGER, "MaxExecFork",
				AttributeType.INTEGER, "MaxCore", AttributeType.INTEGER,
				"MaxData", AttributeType.INTEGER, "MaxOutput",
				AttributeType.INTEGER, "MaxStack", AttributeType.INTEGER,
				"MaxProg", AttributeType.INTEGER, "RealTimeout",
				AttributeType.INTEGER, "CompTimeout", AttributeType.INTEGER,
				"ExecTimeout", AttributeType.INTEGER, "MinUID",
				AttributeType.INTEGER, "MaxUID", AttributeType.INTEGER },
				new Object[] { "Defaults", "", false });

	}

	@Test
	public void testGetMooshakObjectRoot() throws MooshakException {
		
		testMooshakObject("data", "Top", new String[] { "Fatal", "", "Warning",
				"", "configs", "", "contests", "", "trash", "" }, new String[] {
				"data/contests", "data/configs" });
		
	}
	
	@Test
	public void testGetMooshakObjectExistingProblem() throws MooshakException {
		String id = "data/contests/proto_icpc/problems/J";
		
		// do basic testing
		testMooshakObject(id, "Problem", 
				new String[] { 
					"Fatal", 				"", 
					"Warning",	"Undefined variable <code>Color</code><br>", 
					"Name", 				"J", 
					"Color", 				"", 
					"Title",				"Financial Risk",
					"Type",					"",
					"Timeout", 				"2",
					"Static_corrector", 	"",
					"Dynamic_corrector",	""}, new String[] {
				id+"/images",id+"/tests" });
		
		MooshakObject tdo = administrator.getMooshakObject(id);
		
		MooshakValue description = tdo.getValues().get("Description");
		String content = new String(description.getContent());
		
		assertEquals("risk.html",description.getName());
		assertTrue(content.contains("ACME Corporation clients"));
		assertTrue(content.contains("Sample input"));
		
		MooshakValue pdf = tdo.getValues().get("PDF");
		assertEquals("PDF.pdf",pdf.getName());
		assertNull(pdf.getContent());
	}
	
	@Test
	public void testGetMooshakObjectNewProblem() throws MooshakException {
	
		String base = "data/contests/proto_icpc/problems/";
		String problemName = "Z";
		String id = base + problemName;
		
		administrator.createMooshakObject(base, problemName);
	
		trash.add(PersistentObject.openPath(id));
		
		MooshakObject tdo = administrator.getMooshakObject(id);
		
		MooshakValue pdf = tdo.getValues().get("PDF");
		assertNull(pdf.getName());
		assertNull(pdf.getContent());
	}
	
	
		
	@Test
	public void testGetMooshakObjectImages() throws MooshakException {
		String id = "data/contests/proto_icpc/problems/J/images";
		
		// do basic testing
		testMooshakObject(id, "Images", 
				new String[] { "Fatal", "", "Warning", ""}, 
				new String[] { });
		
		MooshakObject tdo = administrator.getMooshakObject(id);
		
		MooshakValue images = tdo.getValues().get("Image");
		
		Set<String> fileNames = new HashSet<>();
		
		for(int i=1; i<= 10 ; i++)
			fileNames.add("img"+i+".png");
		
		assertEquals(fileNames,images.getFileNames());
		
	}
	
	@Test
	public void testGetMooshakObjectLanguages() throws MooshakException {
		String base = "data/contests/proto_icpc/languages/";
		testMooshakObject(
				base,
				"Languages",
				new String[] {
						"Fatal",
						"Verifique <a target='select' "
								+ "href='1273514065553208?data+data/contests/proto_icpc/languages/Pascal'>"
								+ "<code>Pascal</code></a><br>", "Warning", "",
								"MaxCompFork", "10", "MaxExecFork", "0", "MaxCore",
								"0", "MaxData", "450K", "MaxOutput", "500K",
								"MaxStack", "1M", "MaxProg", "100K",
								"RealTimeout", "60", "CompTimeout", "60",
								"ExecTimeout", "5", "MinUID", "30000", "MaxUID",
								"60000", }, new String[] { base + "C", base + "CPP",
						base + "Java", base + "Python", base + "Pascal",
						base + "Perl" });

	}

	@Test
	public void testSetMooshakObjectWithFile() throws MooshakException {
		String base = "data/contests/proto_icpc/submissions/";
		
		String id = base + "testSubmission";
		administrator.createMooshakObject(base, "testSubmission");
		
		MooshakObject submission = administrator.getMooshakObject(id); 
		
		trash.add(PersistentObject.openPath(id));
		
		submission.setFieldValue("Program", new MooshakValue("Program", 
				"program.java", ("import java.text.NumberFormat;"
						+ "\n class Risk {"
						+ "\n public static void main(String args[]) {}"
						+ "\n static void process_each_client(){}"
						+ "\n }").getBytes()));
		
		submission.setFieldValue("Consider", new MooshakValue("Consider", 
				"YES"));
		
		submission.setFieldValue("Classify", new MooshakValue("Classify", 
				"WRONG_ANSWER"));
		
		administrator.setMooshakObject(submission);
		
		submission = administrator.getMooshakObject(id); 
		String program = new String(submission.getValues().get("Program")
				.getContent());
		
		assertTrue(program.indexOf("import java.text.NumberFormat;")>= 0);
		assertTrue(program.indexOf("public static void main(String ")>= 0);
		assertTrue(program.indexOf("static void process_each_client") >= 0);
		assertTrue(program.indexOf("class Risk {") >= 0);
		
		
		testMooshakObject(id, "Submission", new String[] {
				"Classify", "WRONG_ANSWER", "Consider", 
				"YES"
		}, new String[] {});
	}
	
	@Test
	public void testSetMooshakObjectWithMultipleFiles() 
			throws MooshakException, IOException {
		
		String base = "data/contests/proto_icpc/problems/";
		String problemName = "Z";
		String fieldName = "Image";
		String testFilePrefix = "testFile";
		int testFileCount = 10;
		
		AdministratorManager.getInstance().createMooshakObject(base,problemName);
		
		Problem problem = PersistentObject.openPath(base).open(problemName);
		
		Path imagesPath = problem.getPath().resolve("images");
		Path imagesFull = PersistentCore.getAbsoluteFile(imagesPath);
		String id = imagesPath.toString();
		
		trash.add(problem);
		
		MooshakObject images = administrator.getMooshakObject(id); 
		MooshakValue value = new MooshakValue("Image");
		
		for(int i=0; i<testFileCount;i++) {
			String fileName = testFilePrefix+i;
			Path path = imagesFull.resolve(testFilePrefix+i);
			byte[] content = new byte[i];
			
			assertFalse(Files.exists(path));
			
			for(byte b=0; b<i; b++)
				content[b] =  b;
			
			value.addFileValue(new MooshakValue(fieldName, fileName, content));
		}
			
		images.setFieldValue(fieldName, value);
		
		administrator.setMooshakObject(images);
		
		
		for(int i=0; i<testFileCount;i++) {
			Path path = imagesFull.resolve(testFilePrefix+i);
			
			assertTrue(Files.exists(path));
			assertEquals((long) i,Files.size(path));
		}
	}
		
	@Test
	public void testEditSubmissionProgram() throws MooshakException {
		String testSubmission = "testSubmission";
		String id = CustomData.SUBMISSIONS_PATHNAME+"/" +testSubmission;
		
		administrator.createMooshakObject(CustomData.SUBMISSIONS_PATHNAME, 
				testSubmission);
		
		MooshakObject submission = administrator.getMooshakObject(id); 
		
		trash.add(PersistentObject.openPath(id));
		
		submission.setFieldValue("Program", new MooshakValue("Program", 
				"Risc.java", // submission.getFieldValue("Program").getName() 
				("import java.text.NumberFormat;"
						+ "\n class Risk {"
						+ "\n public static void main(String args[]) {}"
						+ "\n static void process_each_client(){}"
						+ "\n }").getBytes()));
		
		administrator.setMooshakObject(submission);
		
		submission = administrator.getMooshakObject(id); 
		String program = new String(submission.getValues().get("Program")
				.getContent());
		
		assertTrue(program.indexOf("import java.text.NumberFormat;")>= 0);
		assertTrue(program.indexOf("public static void main(String ")>= 0);
		assertTrue(program.indexOf("static void process_each_client") >= 0);
		assertTrue(program.indexOf("class Risk {") >= 0);
		
	}
	
	
	@Test
	public void testGetMooshakObjectWithFile() throws MooshakException {
		MooshakObject tdo = administrator.getMooshakObject(CustomData.PROBLEM_J_PATHNAME);
		
		Map<String,MooshakValue> values = tdo.getValues();
		
		String program = new String(values.get("Program").getContent());
		
		assertTrue(program.indexOf("import java.text.NumberFormat;")>= 0);
		assertTrue(program.indexOf("public static void main(String ")>= 0);
		assertTrue(program.indexOf("static void process_each_client") >= 0);
		assertTrue(program.indexOf("class risk {") >= 0);
	}


	private <T extends PersistentObject> 
	void testDestroyMooshakObject(String id) throws MooshakException {

		Path containerPath = Paths.get(id);
		Path createdAbsolutePath = PersistentObject.getAbsoluteFile(
				containerPath.resolve(CREATED));

		assertFalse("Unexpected file:"+createdAbsolutePath.toString(),
				Files.exists(createdAbsolutePath));

		administrator.createMooshakObject(id,CREATED);		
		
		PersistentObject persistent = 
				PersistentObject.open(containerPath).open(CREATED);
		trash.add(persistent);
		
		assertTrue(Files.exists(createdAbsolutePath));

		administrator.destroyMooshakObject(persistent.getPath().toString());

		assertFalse(Files.exists(createdAbsolutePath));
	}


	private <T extends PersistentObject> 
	void testCreateMooshakObjectException(String id) 
			throws MooshakContentException {

		Path submissionsPath = Paths.get(id);
		Path createdPath = PersistentObject.getAbsoluteFile(
				submissionsPath.resolve(CREATED));

		assertFalse(Files.exists(createdPath));

		try {
			administrator.createMooshakObject(id,CREATED);
			fail("Exception expected");
		} catch (MooshakException e) {

			assertNotNull(e);
		}
		
		assertFalse(Files.exists(createdPath));
	}

	private <T extends PersistentObject> 
	void testCreateMooshakObject(String id,Consumer<T> moreTests) 
			throws MooshakException {

		PersistentContainer<T> container = PersistentObject.openPath(id);
		Path submissionsPath = Paths.get(id);
		Path createdPath = PersistentObject.getAbsoluteFile(
				submissionsPath.resolve(CREATED));

		assertFalse("Unexpected "+createdPath.toString(),
				Files.exists(createdPath));

		administrator.createMooshakObject(id, CREATED);
		
		assertTrue(Files.exists(createdPath));

		T contained = container.open(CREATED);

		moreTests.accept(contained);

		contained.delete();

	}

	private void testMooshakType(String type, Object[] fieldsData,
			Object[] methodsData) throws MooshakException {

		MooshakType tdo = administrator.getMooshakType(type);

		assertEquals(type, tdo.getType());

		List<MooshakField> fields = tdo.getFields();
		for (int i = 0; i < fieldsData.length; i += 2) {
			String fieldName = (String) fieldsData[i];
			AttributeType fieldType = (AttributeType) fieldsData[i + 1];

			MooshakField field = new MooshakField(fieldName, fieldType);

			assertTrue(fieldName + ":" + fieldType, fields.contains(field));
		}

		List<MooshakMethod> methods = tdo.getMethods();
		for (int i = 0; i < methodsData.length; i += 3) {
			String name = (String) methodsData[i];
			String category = (String) methodsData[i + 1];
			boolean inputable = (Boolean) methodsData[i + 2];

			MooshakMethod method = new MooshakMethod();

			method.setName(name);
			method.setCategory(category);
			method.setInputable(inputable);

			assertTrue(name + ":" + category, methods.contains(method));
		}

	}

	private void testMooshakObject(String id, String type,
			String[] nameValuePairs, String[] childrenNames)
					throws MooshakException {

		MooshakObject tdo = administrator.getMooshakObject(id);

		assertEquals(id, tdo.getId());
		assertEquals(type, tdo.getType());

		Map<String, MooshakValue> values = tdo.getValues();

		for (int i = 0; i < nameValuePairs.length; i += 2) {
			String name = nameValuePairs[i];
			String value = nameValuePairs[i + 1];

			assertEquals("in field '"+name+"'", value, 
					values.get(name).getSimple());
		}

		List<String> children = tdo.getChildren();
		for (String child : childrenNames)
			assertTrue(child, children.contains(child));
	}
	
	@Test
	public void testDiffStrings() {
		String diff = administrator.diffStrings("ALTO 0 km 450 m \n"
				+ "ALTO 9 km 750 m \nPATAMAR\nBAIXO 19 km 100 m \n", 
				"ALTO 0 km 450 m\nALTO 9 km 750 m\n"
				+ "PATAMAR\nBAIXO 19 km 100 m\n");
		assertEquals("@@ -1,4 +1,4 @@\n"
				+ "-ALTO 0 km 450 m \n"
				+ "-ALTO 9 km 750 m \n"
				+ "+ALTO 0 km 450 m\n"
				+ "+ALTO 9 km 750 m\n"
				+ " PATAMAR\n"
				+ "-BAIXO 19 km 100 m \n"
				+ "+BAIXO 19 km 100 m\n", diff);
	}

}
