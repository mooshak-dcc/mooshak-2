package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class MainTest {

	@Test
	public void test() throws IOException {
		String attemptFile = "/home/mooshak/data/contests/2015SQL4/submissions/00243177_P03_up201205709/p1.dia";
		Pattern pattern = Pattern.compile("/[^/_]+_[^/_]+(\\d)+_[^/_]+(\\d)+/[^/]+$");
		Matcher matcher = pattern.matcher(attemptFile);
		matcher.find();
		System.out.println(matcher.groupCount());
		String studentID = matcher.group(2);
		String problemID = matcher.group(1);
		
		int studentLastDigit = Integer.parseInt(studentID);
		int problemLastDigit = Integer.parseInt(problemID);
		
		System.out.println((studentLastDigit + problemLastDigit) % 2 == 0);	
	}

}
