package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Team;

public class KeyGeneratorTest {

	@Test
	public void test() {
		Team team = new Team() ;
		team.setName("team");
		team.setPassword("plain");
		team.setEmail("email@fc.up.pt");
		
		Problem probleam = new Problem();
		probleam.setName("graph");
		probleam.setDifficulty(null);
		probleam.setTitle("title");	
		
		Team team2 = new Team() ;
		team2.setName("team");
		team2.setPassword("plain");
		team2.setEmail("email@fc.up.pt");
		
		Problem probleam2 = new Problem();
		probleam2.setName("graph");
		probleam2.setDifficulty(null);
		probleam2.setTitle("title");
		
		
		Team team3 = new Team() ;
		team3.setName("team3");
		team3.setPassword("plain3");
		team3.setEmail("email3@fc.up.pt");
		
		Problem probleam3 = new Problem();
		probleam3.setName("graph3");
		
		
		KeyGenerator key = new KeyGenerator(team,probleam);
		KeyGenerator key2 = new KeyGenerator(team2,probleam2);
		KeyGenerator key3 = new KeyGenerator(team3,probleam3);
		
		assertEquals("key generator", key.hashCode(), key2.hashCode());
		assertEquals("key generator",key.equals(key3),false);
		
		
	}

}
