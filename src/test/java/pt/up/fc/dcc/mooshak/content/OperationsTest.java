package pt.up.fc.dcc.mooshak.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.Operations.Operation;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Foo;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Tests on Operations, static methods for handling 
 * persistent objects' operations
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class OperationsTest {

	Operation defaults, prepare, type;
	
	@Before
	public void setUp() throws Exception {
		
		prepare = Operations.getOperation(Contest.class,"Prepare");
		type = Operations.getOperation(Contest.class,"Type");
		defaults = Operations.getOperation(Languages.class,"Defaults");
		
	}
	
	@Test
	public void testEcho() throws Exception {
		Operation echo = Operations.getOperation(Foo.class,"echo");
		Foo foo = new Foo();
		MethodContext context = new MethodContext(); 
		CommandOutcome outcome;
		
		context.addPair("a", "1");
	
		outcome = echo.execute(foo,context);
		
		assertEquals("a:1",outcome.getMessage());
		
		try {
			outcome = echo.execute(foo,null);
			fail("Exception expected");
		} catch(MooshakException cause) {
			assertNotNull(cause);
		}
	}
	
	
	@Test
	public void testWrongReturn() {
		try {
			Operations.getOperation(Foo.class,"wrongReturn");
			fail("Exception expected");
		} catch(MooshakContentException cause) {
			assertNotNull(cause);
		}
		
		try {
			Operations.getOperation(Foo.class,"wrongReturn1");
			fail("Exception expected");
		} catch(MooshakContentException cause) {
			assertNotNull(cause);
		}
		
		try {
			Operations.getOperation(Foo.class,"wrongReturn2");
			fail("Exception expected");
		} catch(MooshakContentException cause) {
			assertNotNull(cause);
		}
	}
	
	@Test
	public void testWho() throws Exception {
		Operation who = Operations.getOperation(Foo.class,"who");
		Foo foo = new Foo();
		MethodContext context = new MethodContext(); 
		CommandOutcome outcome;
						
		for(String name: CustomData.LANGUAGE_NAMES) {
			foo.setName(name);
			outcome = who.execute(foo,null);
			assertEquals(name,outcome.getMessage());
		}
		
		try {
			outcome = who.execute(foo,context);
			fail("Exception expected");
		} catch(MooshakException cause) {
			assertNotNull(cause);
		}
	}

	@Test
	public void testLanguagesDesfaults() {
		assertEquals("Defaults",defaults.getName());
		assertEquals("",defaults.getCategory());
		assertFalse(defaults.getInputable());
		assertEquals("",defaults.getTip());
		assertEquals("",defaults.getHelp());
	}
	
	@Test
	public void testContestPrepare() {
		assertEquals("Prepare",prepare.getName());
		assertEquals("",prepare.getCategory());
		assertTrue(prepare.getInputable());
		assertEquals("Prepare contest to start",prepare.getTip());
		assertEquals("",prepare.getHelp());
	}
	
	@Test
	public void testContestType() {
		assertEquals("Type",type.getName());
		assertEquals("",type.getCategory());
		assertFalse(type.getInputable());
		assertTrue(type.getTip().startsWith("Configures several"));
		assertEquals("",type.getHelp());
	}

	
	public void testContestOperations() {
		Collection<Operation> operations = new HashSet<>();
		operations.add(prepare);
		operations.add(type);
				
		assertEquals(operations,Operations.getOperations(Contest.class));
	}
	
	public void testLanguagesOperations() {
		Collection<Operation> operations = new HashSet<>();
		operations.add(defaults);
				
		assertEquals(operations,Operations.getOperations(Contest.class));
	}
}
