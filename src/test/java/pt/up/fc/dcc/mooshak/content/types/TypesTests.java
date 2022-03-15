package pt.up.fc.dcc.mooshak.content.types;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pt.up.fc.dcc.mooshak.content.util.UtilTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	// Foo tests core module
	FooTest.class,

	// sub packages
	UtilTests.class, 
	
	// Content classes
	ContestTest.class,
	BalloonsTest.class,
	BalloonTest.class,
	ContestsTest.class,
	FlagTest.class,
	FlagsTest.class,
	GroupTest.class,
	GroupsTest.class,
	LDAPTest.class,
	ProblemTest.class,
	ProblemsTest.class,
	// ProfileTest.class,
	PrintoutTest.class,
	PrintoutsTest.class,
	QuestionTest.class,
	QuestionsTest.class,
	LanguageTest.class,
	LanguagesTest.class,
	SubmissionTest.class,
	SubmissionTest.class,
	TeamTest.class,
	TestTest.class,
	//TestsTest.class,
	UserTest.class,
	UsersTest.class,
	UserTestData.class
	})
public class TypesTests {}