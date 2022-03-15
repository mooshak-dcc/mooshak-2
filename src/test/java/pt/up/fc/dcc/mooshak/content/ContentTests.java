package pt.up.fc.dcc.mooshak.content;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pt.up.fc.dcc.mooshak.content.types.TypesTests;
import pt.up.fc.dcc.mooshak.content.util.UtilTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// test classes
	AttributesTest.class,
	OperationsTest.class,
	PathManagerTest.class,
	PersistentContainerTest.class,
	PersistentObjectTest.class,
	// sub packages
	UtilTests.class, 
	TypesTests.class,
	
	})
public class ContentTests {}