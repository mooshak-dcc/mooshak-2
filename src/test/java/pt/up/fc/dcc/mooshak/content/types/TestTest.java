package pt.up.fc.dcc.mooshak.content.types;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class TestTest {
	
	private pt.up.fc.dcc.mooshak.content.types.Test loaded = null;
	private pt.up.fc.dcc.mooshak.content.types.Test created = null;
	Path base;
	
	static Path createdPath;

	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.PROBLEM_J_PATHNAME,"tests",
				"SOME_TEST");
	}
	
	@Before
	public void setUp() throws MooshakContentException {
		created = PersistentObject.create(createdPath,
				pt.up.fc.dcc.mooshak.content.types.Test.class);
		loaded = PersistentObject.openPath(CustomData.TEST_PATHNAME);
	}
	
	@After
	public void cleanUp()  throws Exception {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testFatal() throws MooshakContentException {

		assertEquals("",loaded.getFatal());
		assertEquals("",created.getFatal());

		for(String text: CustomData.TEXTS) {
			loaded.setFatal(text);
			assertEquals(text,loaded.getFatal());

			created.setFatal(text);
			created.save(); created.reopen();
			assertEquals(text,created.getFatal());
		}
	}
	
	@Test
	public void testWarning() throws MooshakContentException {

		assertTrue(loaded.getWarning().startsWith("Consider"));
		assertEquals("",created.getWarning());

		for(String text: CustomData.TEXTS) {
			loaded.setWarning(text);
			assertEquals(text,loaded.getWarning());

			created.setWarning(text);
			created.save(); created.reopen();
			assertEquals(text,created.getWarning());
		}
	}
	
	
	@Test
	public void testArgs() {
		assertEquals("",loaded.getArgs());
		assertEquals("",created.getArgs());
		
		for(String text: CustomData.TEXTS) {
			created.setArgs(text);
			assertEquals(text,created.getArgs());
		}
	}
	
	@Test
	public void testInput() throws MooshakContentException {
		assertEquals(loaded.getPath().resolve("input"),loaded.getInput());
		assertNull(created.getInput());
		
		for(String name: CustomData.FILENAMES) {
			Path file = loaded.getPath().resolve(name);
			created.setInput(file);
			created.save(); created.reopen();
			assertEquals(file.getFileName(),created.getInput().getFileName());
		}
	}
	
	@Test
	public void testOutput() throws MooshakContentException {
		assertEquals(loaded.getPath().resolve("output"),loaded.getOutput());
		assertNull(created.getOutput());
		
		for(String name: CustomData.FILENAMES) {
			Path file = loaded.getPath().resolve(name);
			created.setOutput(file);
			created.save(); created.reopen();
			assertEquals(file.getFileName(),created.getOutput().getFileName());
		}
	}

	@Test
	public void testContext() throws MooshakContentException {
		assertEquals(loaded.getPath().resolve("context"),loaded.getContext());
		assertNull(created.getContext());
		
		for(String name: CustomData.FILENAMES) {
			Path file = loaded.getPath().resolve(name);
			created.setContext(file);
			created.save(); created.reopen();
			assertEquals(file.getFileName(),created.getContext().getFileName());
		}
	}
	
	@Test
	public void testPoints() throws MooshakContentException {
		assertEquals(0,loaded.getPoints());
		assertEquals(0,created.getPoints());
		
		for(int value: CustomData.INTS) {
			created.setPoints(value);
			created.save(); created.reopen();
			assertEquals(value,created.getPoints());
		}
	}
}
