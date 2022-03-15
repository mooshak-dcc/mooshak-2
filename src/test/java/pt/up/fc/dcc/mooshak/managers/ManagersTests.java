package pt.up.fc.dcc.mooshak.managers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	AdministratorManagerTest.class, 
	AuthManagerTest.class,
	ParticipantManagerTest.class })
public class ManagersTests {

}
