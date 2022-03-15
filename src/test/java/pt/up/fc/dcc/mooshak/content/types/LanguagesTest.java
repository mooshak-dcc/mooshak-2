package pt.up.fc.dcc.mooshak.content.types;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class LanguagesTest {

	Languages loaded;
	Languages created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.CONTEST_PATHNAME,	"SOME_LANGUAGES");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Languages.class);
		loaded = PersistentObject.openPath(CustomData.LANGUAGES);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testCompFork() throws MooshakContentException {
		assertEquals(10, created.getMaxCompFork());
		assertEquals(10, loaded.getMaxCompFork());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxCompFork(number);
			assertEquals(number,loaded.getMaxCompFork());
			
			created.setMaxCompFork(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxCompFork());
		}
	}
	
	@Test
	public void testMaxExecFork() throws MooshakContentException {
		assertEquals(0, created.getMaxExecFork());
		assertEquals(0, loaded.getMaxExecFork());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxExecFork(number);
			assertEquals(number,loaded.getMaxExecFork());
			
			created.setMaxExecFork(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxExecFork());
		}
	}

	
	@Test
	public void testMaxCore() throws MooshakContentException {
		assertEquals(0, created.getMaxCore());
		assertEquals(0, loaded.getMaxCore());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxCore(number);
			assertEquals(number,loaded.getMaxCore());
			
			created.setMaxCore(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxCore());
		}
	}


	@Test
	public void testMaxData() throws MooshakContentException {
		assertEquals(33554432, created.getMaxData());
		assertEquals(460800, loaded.getMaxData());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxData(number);
			assertEquals(number,loaded.getMaxData());
			
			created.setMaxData(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxData());
		}
	}

	@Test
	public void testMaxOutput() throws MooshakContentException {
		assertEquals(512000, created.getMaxOutput());
		assertEquals(512000, loaded.getMaxOutput());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxOutput(number);
			assertEquals(number,loaded.getMaxOutput());
			
			created.setMaxOutput(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxOutput());
		}
	}
	
	@Test
	public void testMaxStack() throws MooshakContentException {
		assertEquals(8388608, created.getMaxStack());
		assertEquals(1048576, loaded.getMaxStack());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxStack(number);
			assertEquals(number,loaded.getMaxStack());
			
			created.setMaxStack(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxStack());
		}
	}
	
	@Test
	public void testMaxProg() throws MooshakContentException {
		assertEquals(102400, created.getMaxProg());
		assertEquals(102400, loaded.getMaxProg());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxProg(number);
			assertEquals(number,loaded.getMaxProg());
			
			created.setMaxProg(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxProg());
		}
	}
	
	@Test
	public void testRealTimeout() throws MooshakContentException {
		assertEquals(60, created.getRealTimeout());
		assertEquals(60, loaded.getRealTimeout());
		
		for(int number: CustomData.INTS) {
			loaded.setRealTimeout(number);
			assertEquals(number,loaded.getRealTimeout());
			
			created.setRealTimeout(number);
			created.save(); created.reopen();
			assertEquals(number,created.getRealTimeout());
		}
	}
	
	@Test
	public void testCompTimeout() throws MooshakContentException {
		assertEquals(60, created.getCompTimeout());
		assertEquals(60, loaded.getCompTimeout());
		
		for(int number: CustomData.INTS) {
			loaded.setCompTimeout(number);
			assertEquals(number,loaded.getCompTimeout());
			
			created.setCompTimeout(number);
			created.save(); created.reopen();
			assertEquals(number,created.getCompTimeout());
		}
	}
	
	@Test
	public void testExecTimeout() throws MooshakContentException {
		assertEquals(5, created.getExecTimeout());
		assertEquals(5, loaded.getExecTimeout());
		
		for(int number: CustomData.INTS) {
			loaded.setExecTimeout(number);
			assertEquals(number,loaded.getExecTimeout());
			
			created.setExecTimeout(number);
			created.save(); created.reopen();
			assertEquals(number,created.getExecTimeout());
		}
	}
	
	@Test
	public void testMaxUID() throws MooshakContentException {
		assertEquals(60000, created.getMaxUID());
		assertEquals(60000, loaded.getMaxUID());
		
		for(int number: CustomData.INTS) {
			loaded.setMaxUID(number);
			assertEquals(number,loaded.getMaxUID());
			
			created.setMaxUID(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMaxUID());
		}
	}

	@Test
	public void testMinUID() throws MooshakContentException {
		assertEquals(30000, created.getMinUID());
		assertEquals(30000, loaded.getMinUID());
		
		for(int number: CustomData.INTS) {
			loaded.setMinUID(number);
			assertEquals(number,loaded.getMinUID());
			
			created.setMinUID(number);
			created.save(); created.reopen();
			assertEquals(number,created.getMinUID());
		}
	}
	
	@Test
	public void testFind() throws MooshakContentException {
		
		for(String name: CustomData.LANGUAGE_NAMES)
		assertEquals(name,loaded.find(name).getIdName());
	}
	
}
