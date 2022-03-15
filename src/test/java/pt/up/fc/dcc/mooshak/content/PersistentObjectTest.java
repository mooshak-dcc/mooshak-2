package pt.up.fc.dcc.mooshak.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Foo;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.CustomData;

public class PersistentObjectTest {

	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome("home");
		
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	protected int count = 0;
	
	final static long DELAY = PersistentObject.PROTECTED_DELAY * 3 / 2;

	@Test
	public void testCopyContest() throws MooshakContentException {
		
		Contest original = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		Contest copy = original.copy();
		
		assertEquals(original,copy);
		
		assertEquals(original.getDesignation(),copy.getDesignation());
		assertEquals(original.getOrganizes(),copy.getOrganizes());
		assertEquals(original.getOpen(),copy.getOpen());
		
	}

	@Test
	public void testCopyGroup() throws MooshakContentException {
		
		Group original = PersistentObject.openPath(CustomData.GROUP_PATHNAME);
		Group copy = original.copy();
		
		assertEquals(original,copy);
		
	}
	
	@Test
	public void testCopySubmission() throws MooshakContentException {
		
		Submission original = PersistentObject.openPath(CustomData.SUBMISSION_PATHNAME);
		Submission copy = original.copy();
		
		assertEquals(original,copy);
		
	}
	
	
	@Test
	public void testExecuteProtected() throws Exception {
		Foo foo = PersistentObject.create(Paths.get("test"), Foo.class);
		Thread.sleep(DELAY); // wait for PO write time timer
		
		assertFalse(foo.isIgnoringFileSystemNotifications());

		foo.executeIgnoringFSNotifications(
				() -> assertTrue(foo.isIgnoringFileSystemNotifications()) 
		);
		
		assertTrue(foo.isIgnoringFileSystemNotifications());
		Thread.sleep(DELAY);
		assertFalse(foo.isIgnoringFileSystemNotifications());
	}
	
	
	@Test
	public void testLoadAllPO() throws Exception {
		
		Path data = PersistentObject.openPath("data").getAbsoluteFile();
		count = 0;
		
		Files.walkFileTree(data, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, 
					BasicFileAttributes attrs)
							throws IOException {

				if(Files.exists(dir.resolve(".data.tcl"))) {

					try {
						PersistentObject.open(dir);
						count++;
					} catch (MooshakContentException e) {
						System.err.println(e.getMessage());
					}

					return FileVisitResult.CONTINUE;
				} else
					return FileVisitResult.CONTINUE;
			}
		} );
		
		System.out.println("loaded POs:"+count);
	}
	
	@Test
	public void testIsPersistentObjectAbsolutePath() {
		Path path = Paths.get(CustomData.CONTEST);
		
		Path fullPath = PersistentCore.getAbsoluteFile(path);
		assertFalse(PersistentObject.isPersistentObjectAbsolutePath(path));

		assertTrue(PersistentObject.isPersistentObjectAbsolutePath(fullPath));
		
	}
	
}

