package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.PersistentObject;


/**
 * This class contains static fields with data recurrently used in test cases
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since July 2013
 * @version 2.0
 *
 */
public class CustomData {

	public static final Path WEB_INF = Paths.get("WebContent/WEB-INF");

	public static final String HOME = "home";
	
	public static final String CONFIGS = "data/configs/";
	public static final String CONTESTS = "data/contests/";
	public static final String CONTEST = "data/contests/proto_icpc/";
	public static final String ENKI_CONTEST = "data/contests/proto_enki/";
	public static final String GAME_CONTEST = "data/contests/proto_game/";
	
	public static final String FLAGS_PATHNAME = CONFIGS+"flags";
	public static final String FLAG_PATHNAME = CONFIGS+"flags/pt";
	
	public static final String LDAP_PATHNAME = "data/configs/ldap";
	public static final String PROFS_LDAP_PATHNAME = LDAP_PATHNAME+"/profs";
	public static final String STUDENTS_LDAP_PATHNAME = LDAP_PATHNAME+"/alunos";
	
	public static final String CONTEST_PATHNAME = CONTEST;
	public static final String GROUPS_PATHNAME = CONTEST+"groups";
	public static final String GROUP_PATHNAME = CONTEST+"groups/myGroup";
	public static final String TEAM_PATHNAME = CONTEST+"groups/myGroup/team";
	public static final String PRINTOUTS_PATHNAME = CONTEST+"printouts/";
	public static final String PRINTOUT_PATHNAME = CONTEST + "printouts/115051333_C_team/";
	public static final String BALLOONS_PATHNAME = CONTEST + "balloons";
	public static final String BALLOON_PATHNAME = CONTEST + "balloons/72466529_C_team";
	public static final String PROBLEM_PATHNAME = CONTEST + "problems/J";
	public static final String QUESTIONS_PATHNAME = CONTEST+"questions/";
	public static final String LANGUAGES = CONTEST + "languages/";
	public static final String JAVA_PATHNAME =  LANGUAGES + "Java/";
	public static final String PROBLEMS_PATHNAME = CONTEST + "problems/";
	public static final String PROBLEM_J_PATHNAME = CONTEST + "problems/J";
	public static final String PROBLEM_C_PATHNAME = CONTEST + "problems/C";
	public static final String PROBLEM_M_PATHNAME = CONTEST + "problems/M";
	public static final String SUBMISSIONS_PATHNAME = CONTEST + "submissions/";
	public static final String SUBMISSION_PATHNAME = CONTEST + "submissions/05075034_P_team/";
	public static final String QUESTION_PATHNAME =	CONTEST + "questions/78593732_C_team/";
	public static final String TEST_PATHNAME = CONTEST + "problems/J/tests/T1";
	public static final String USERS_PATHNAME = CONTEST + "users";
	public static final String GAME_SUBMISSIONS_PATHNAME = GAME_CONTEST + "submissions/";
	public static final String PROBLEM_TICTACTOE_PATHNAME = GAME_CONTEST + "problems/A";
	
	public static final String COMPILATION_ERROR_SUBMISSION_PATHNAME =	CONTEST + "submissions/54474140_J_team";	
	
	public static final String FCUP_DIAGRAM_JSON = 
			"data/contests/proto_diagram/problems/FCUP/FCUP.json";
	
	static final String[] TEXTS = {
		"", "OK", "two letters", "three letter text", "123", "2 words", 
		"olá", "text with <b>tags</b> and <b>entities</b>: ol&aacute;"
	};
	
	static final String[] EMAILS = {
		"Some.Guy@some.domain.org", "Some Guy <Some.Guy@some.domain.org>"
	};
	
	static final int[] INTS = { Integer.MIN_VALUE, -10, 9, -22,  -1, 0,
			1, 10, 100, Integer.MAX_VALUE};
	
	static final long[] LONGS = { Long.MIN_VALUE, -10L, 9L, -22L,  -1L, 0L,
		1L, 10L, 100L, Long.MAX_VALUE};
	
	static final double DOUBLES[] = { Double.MIN_NORMAL, -10D, -2D, 0D, 
		1D, 2D, 10D, 100D, 1000D, Double.MAX_VALUE};

	static final Color[] COLORS = { Color.black, Color.orange, Color.pink, 
		Color.red, Color.green, Color.blue, Color.yellow, Color.magenta,
		Color.cyan, Color.white };
	
	static final Date[] DATES = { new Date(0), new Date(), 
		new Date(new Date().getTime()-1<<5),
		new Date(new Date().getTime()+1<<5),
		new Date(Long.MAX_VALUE)};
	
	static final String FILENAMES[] = { "xpto.txt", "some stuf.png"};

	public static final String[] LANGUAGE_NAMES =  
		{  "Java", "Python", "C", "CPP", "Perl", "Pascal" };

	
	public static final String CONTEST_ID = "proto_icpc";
	public static final String TEAM_ID = "team";
	public static final String PROBLEM_ID = "A";
	public static final String HELLO_NAME = "Hello.java";
	public static final String HELLO_CODE = "public class Hello {" +
			"    public static void main(String[] args) {" +
			"          System.out.println(\"Hello Mooshak\");"+
			"    } " +
			"}";
	public static final String NO_PROBLEM_ID = null;
	public static final String NO_SESSION = null;
	public static final List<String> NO_INPUT = null;
	public static final List<String> SINGLE_EMPTY_INPUT = new ArrayList<>();
	static { 	SINGLE_EMPTY_INPUT.add(""); }
	
	public static final String ADD_NAME = "Add.java";
	public static final String ADD_CODE = "import java.util.Scanner;\n"
			+ "public class Add {\n"
			+ "  public static void main(String args[]) {\n"
			+ "		Scanner in = new Scanner(System.in);\n"
			+ "     int a = in.nextInt();\n"
			+ "     int b = in.nextInt();\n"
			+ "		System.out.println(a+b);\n"
			+ "     in.close();\n"
			+ "  }\n"
			+ "}\n";
	public static final List<String> NUMBER_PAIRS_INPUT = new ArrayList<>();
	public static final boolean CONSIDER = true;
	public static final boolean DONT_CONSIDER = false;


	static { for(String data: new String[] { "1 1", "1 -1", "2 2"})
		NUMBER_PAIRS_INPUT.add(data);
	}


	/**
	 * Cleans up a tree from a pathname
	 * @param path
	 * @throws IOException
	 */
	public static void cleanup(String pathname) throws IOException {
		cleanup(Paths.get(pathname).toAbsolutePath());
		
	}
	
	/**
	 * Cleansup a tree, removing everything
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static void cleanup(Path path) throws IOException {

		if(!Files.exists(path))
			return;
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {

				Files.delete(file);

				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {

				Files.delete(dir);

				return super.postVisitDirectory(dir, exc);
			}

		});
	}

	/**
	 * Check the content of a persistent object data file
	 */
	private static final Pattern BLANCS = Pattern.compile("\\s+");
	public static boolean checkContent(
			PersistentObject persistent,
			String field,
			String value) 
			
			throws IOException {
		Path path = persistent.getAbsoluteFile(".data.tcl");
		
		for(String line : Files.readAllLines(path)) {
			String[] parts = BLANCS.split(line);
			
			if(parts.length >= 3 && parts[1].equals(field)) {
				String content = "";
				boolean first = true;
				
				for(int i=2; i<parts.length; i++) {
					if(first)
						first = false;
					else
						content += " ";
					content += parts[i];
				}
				
				if(parts.length > 3 ) // remove brackets
					content = content.substring(1,content.length()-1);
				if("{}".equals(content))
					content = "";
					
				return content.equals(value);
			}
				
		}
		return false;	
	}


}
