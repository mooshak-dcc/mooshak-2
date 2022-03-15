package pt.up.fc.dcc.mooshak.server.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class AdminCommandServiceImplTest {

	AdminCommandServiceImpl servlet;
	
	@Before
	public void setUp() throws Exception {
		servlet = new AdminCommandServiceImpl();
	}

	@Test
	public void testSanitizePathId() {

		for(String text: Arrays.asList("data","data/contests", 
				"data/contests/proto_icpc",
				"data/contests/../contests/proto_icpc",
				"data/contests/proto_icpc/problems",
				"data/contests/proto_icpc/problems/A",
				"data/contests/proto_icpc/problems/A/tests",
				"data/contests/proto_icpc/problems/A/tests/T1",
				"data/contests/proto_icpc/problems/A/tests/T1/in.txt"))
		assertEquals(text,CommandService.sanitizePathId(text));
		
		for(String text: Arrays.asList("/data","../data/contests", 
				"../../../data/contests/proto_icpc",
				"../data/contests/../proto_icpc/problems",
				"../data/contests/proto_icpc/problems/A",
				"../../../../../../../../root",
				"../../root"))
		assertFalse(text,text.equals(CommandService.sanitizePathId(text)));
		
	}


	@Test
	public void testSanitizePathSegment() {
		for(String text: Arrays.asList("data",
				"A.c","Prog.java","int.txt","A",".data.tcl"))
		assertEquals(text,CommandService.sanitizePathSegment(text));
		
		for(String text: Arrays.asList(
				"data/contests", 
				"data/contests/proto_icpc",
				"data/contests/../contests/proto_icpc",
				"data/contests/proto_icpc/problems",
				"data/contests/proto_icpc/problems/A",
				"data/contests/proto_icpc/problems/A/tests",
				"data/contests/proto_icpc/problems/A/tests/T1",
				"data/contests/proto_icpc/problems/A/tests/T1/in.txt",
				"/data","../data/contests", 
				"../../../data/contests/proto_icpc",
				"../data/contests/../proto_icpc/problems",
				"../data/contests/proto_icpc/problems/A",
				"../../../../../../../../root",
				"../../root"))
		assertFalse(text,text.equals(CommandService.sanitizePathSegment(text)));
	}
}
