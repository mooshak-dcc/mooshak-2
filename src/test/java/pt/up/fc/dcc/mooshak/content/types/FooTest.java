package pt.up.fc.dcc.mooshak.content.types;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class FooTest {

	private static final String HOME_DIR_NAME = "coreTest";

	private static final Charset CHARSET = Charset.defaultCharset();
	
	private static final long MAX_LOAD_TIME = 5L;

	static PersistentObject root;
	final String name = "foo";
	final String pathname = "data/foo";
	
	String[] subFooNames = {"a", "b", "c"};
	String[] subBarNames = {"d", "e"};
	
	Foo foo;
	
	
	@BeforeClass 
	public static void initialSetUp() throws MooshakContentException {
		
	}
	
	@Before
	public void setUp() throws IOException, MooshakContentException   {		
		CustomData.cleanup(HOME_DIR_NAME);
		PersistentObject.setHome(HOME_DIR_NAME);
		root = PersistentObject.getRoot();
		foo = root.create(name, Foo.class);
	}
	
	@After
	public void cleanUp() throws IOException {
		CustomData.cleanup(HOME_DIR_NAME);
		
	}
	
	@AfterClass
	public static void cleanUpFinal() throws IOException, MooshakContentException  {
		CustomData.cleanup(HOME_DIR_NAME);
		PersistentObject.setHome("");
	}
	
	@Test
	public void testLoadType() throws MooshakContentException  {
		
		Class<PersistentObject> type;
		
		type = PersistentObject.loadType("Foo");
		
		assertEquals(Foo.class, type);
		
		try {
			// this type is undefined in this package 
			type = PersistentObject.loadType("NonExisting");
			fail("Exception expected");
		} catch(MooshakContentException cause) {
			assertNotNull(cause);
		}
		
		try {
			// this type exists but isn't Persistent Object
			type = PersistentObject.loadType("Dummy");
			fail("Exception expected");
		} catch(MooshakContentException cause) {
			assertNotNull(cause);
		}
	}
	
	@Test
	public void testCreateFoo() throws MooshakException {
		
		assertNotNull(foo);
		assertEquals(pathname,foo.getPath().toString());

		try {
			// object in existing path
			foo = (Foo) root.create(pathname, Foo.class);
		} catch(MooshakException e) {
			assertNotNull(e);
		}

	}
	
	@Test
	public void testOpenFoo() throws MooshakException {
		
		assertNotNull(foo);
		assertEquals(pathname,foo.getPath().toString());
		
		Foo foo2 = root.open("foo");
		
		// 2 object from the same path are the same 
		assertEquals(foo,foo2);
	}

	@Test
	public void testOpenFooAbsolute() throws MooshakException {
		
		assertNotNull(foo);
		assertEquals(pathname,foo.getPath().toString());
		
		Foo foo2 = root.open("foo");
		
		// 2 object from the same path are the same 
		assertEquals(foo,foo2);
	}

	
	@Test
	public void testOpenFooStatic() throws MooshakException {
		
		assertNotNull(foo);
		
		Foo foo2 = PersistentObject.openPath("data/foo");
		
		// 2 object from the same path are the same 
		assertEquals(foo,foo2);
	}
	

	@Test
	public void testProperties() throws MooshakException {
		String name = "name";
		String other = "other";
		
		foo.setName(name);
		foo.setOther(other);
		
		assertEquals(name, foo.getName());
		assertEquals(other, foo.getOther());
		
		assertEquals(name, foo.getName());
		assertEquals(other, foo.getOther());
		
		// another objects with the same path is the same object 
		Foo bar = root.open("foo");
		
		assertEquals(name, bar.getName());
		assertEquals(other, bar.getOther());
	}

	@Test
	public void testClose() throws MooshakException {
		String name = "name";
		String other = "other";
		
		foo.setName(name);
		foo.setOther(other);
		
		foo.close();
		
		long start = System.nanoTime();
		Foo bar = root.open("foo");
		long loadTimeMilis = System.currentTimeMillis()- start;
		
		if(loadTimeMilis >= MAX_LOAD_TIME)
			System.err.println("load time: "+loadTimeMilis+"ms");
		assertTrue(loadTimeMilis < MAX_LOAD_TIME);
		
		assertEquals(null, bar.getName());
		assertEquals(null, bar.getOther());
	}
	
	
	@Test
	public void testSave() throws MooshakException {
		String name = "name";
		String other = "other";
		
		foo.setName(name);
		foo.setOther(other);
		
		foo.save();
		
		// forget about this path. force use of file 
		foo.close();
		
		Foo bar = PersistentObject.openPath(pathname);
	
		assertEquals(name, bar.getName());
		assertEquals(null, bar.getOther());
	}
	
	
	@Test
	public void testCreateNamed() throws MooshakException {
		
		createNamedDescendats(foo);
		
	}

	private void createNamedDescendats(Foo foo) throws MooshakException {
		for(String name: subFooNames) {
			testCreatedNamedSub(foo, name,Foo.class);			
		}
		
		for(String name: subBarNames) {
			testCreatedNamedSub(foo, name,Bar.class);			
		}
	}

	private <T extends PersistentObject> void testCreatedNamedSub(Foo foo, String name,Class<T> type)
			throws MooshakException {
		//PersistentObject subPath = foo.open(name);
		
		//subPath.delete();

		T sub =  foo.create(name, type);
		
		assertNotNull(sub);
		// assertEquals(sub.getClass().getName(), PersistentObject.loadType(subPath));
		//assertEquals(subPath,sub.getPath());
	}
	
	@Test
	public void testOpenNamed() throws MooshakException {
		
		createNamedDescendats(foo);
		
		for(String name: subFooNames) {
			testOpenNamedSub(foo, name);			
		}
		
		
	}

	private void testOpenNamedSub(Foo foo,  String name) throws MooshakException {

		Foo sub = foo.open(name);		
		
		String value = "name";
		String other = "other";
		
		
		sub.setName(value);
		sub.setOther(other);
		
		assertEquals(value, sub.getName());
		assertEquals(other, sub.getOther());
		
		assertEquals(value, sub.getName());
		assertEquals(other, sub.getOther());
		
		
		// another objects with the same path is the same object 
		Foo sub2 = (Foo) foo.open(name);
		
		assertEquals(sub,sub2);
		
		assertEquals(value, sub2.getName());
		assertEquals(other, sub2.getOther());
		
	}
	
	
	@Test
	public void testGetChildren() throws MooshakException {
		
		createNamedDescendats(foo);
		
		assertEquals(5, foo.getChildren(true).size());
		
		assertEquals(3, foo.getChildren(Foo.class,true).size());
		assertEquals(2, foo.getChildren(Bar.class,true).size());
	}
	
	@Test
	public void testGetParent() throws MooshakException {
		
		for(PersistentObject descendat: foo.getChildren(true))
			assertEquals(foo, descendat.getParent());
	}
	
	@Test
	public void testCheckFileSystem() throws Exception {
		String name = "name";
		String label = "Name";
		String other = "other";
		
		Foo foo = PersistentObject.openPath(pathname);
		
		foo.setName(name);
		foo.setOther(other);
		
		foo.save();
		
		Path file  = foo.getAbsoluteFile(".data.tcl");
		//System.out.println(file);				
		FileTime fileTime = FileTime.fromMillis(System.currentTimeMillis());
		Files.setLastModifiedTime(file,fileTime);
		
		// wait for notifications ignore delay
		Thread.sleep(PersistentObject.PROTECTED_DELAY); 
		
		try(Writer writer = Files.newBufferedWriter(file, CHARSET)) {
			writer.write("set "+label+" {"+other+"}\n");
		}
		 
		long time;
		long start = System.currentTimeMillis();
		
		do {
			time = System.currentTimeMillis() - start;
			if(other.equals(foo.getName()))
				break;
			else
				Thread.sleep(10);
			
		} while(time < 1000);

		assertEquals(other, foo.getName());
	}
	
	
}
