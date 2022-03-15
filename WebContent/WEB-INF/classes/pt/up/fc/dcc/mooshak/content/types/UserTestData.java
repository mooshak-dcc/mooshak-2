package pt.up.fc.dcc.mooshak.content.types;



import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;


public class UserTestData extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(
			name="Input",
			type=AttributeType.CONTENT)
	private Void input;
	@MooshakAttribute(
			name="Output",
			type=AttributeType.CONTENT)
	private Void output;

	@MooshakAttribute(name="ExecutionTimes")
	String executionTimes = "";
	
	/**
	 * Get input files (staring with "in" and with ".txt" extension) 
	 * in this directory
	 * @return
	 */
	public List<Path> getInputFiles() 
			throws MooshakException{
		List<Path> list = new ArrayList<>();

		try(DirectoryStream<Path> stream = 
				Files.newDirectoryStream(getAbsoluteFile(), "in*.txt")) {

			for(Path path: stream)
				list.add(path);
			return list;
		} catch (IOException cause) {
			throw new MooshakException("listing input files",cause);
		}
	}



	/**
	 * Get input files (staring with "out" and with ".txt" extension) 
	 * in this directory
	 * @return 
	 * @return
	 */
	public List<Path> getOutputFiles() 
			throws MooshakException{
		List<Path> list = new ArrayList<>();
		
		try(DirectoryStream<Path> stream = 
				Files.newDirectoryStream(getAbsoluteFile(), "out*.txt")) {
			
			for(Path path: stream)
				list.add(path);
			return list;
		} catch (IOException cause) {
			throw new MooshakException("listing output files",cause);
		}
	}	
	
	/**
	 * Runs will be randomly ordered. Get their order from their number
	 * @param file
	 * @return
	 */
	 public int orderOfOutputFile(Path file) {
		String fileName = Filenames.getSafeFileName(file);
		return Integer.parseInt(fileName.substring(3,fileName.indexOf('.')));
	}
	 
	 public synchronized void addExecutionTimes(int order, double cpuTime) {
		 if(! "".equals(executionTimes))
			 executionTimes += ",";
		 
		 executionTimes += order + ":" +cpuTime;
	 }
	 
	 
	 private static Pattern COLON = Pattern.compile(",");
	 private static Pattern FIELD = Pattern.compile("(\\d+):(.*)");
	 
	 /**
	  * Get map with times of execution of each user supplied test 
	  * @return
	  */
	 Map<Integer,String> getExecutionTimes() {
		 Map<Integer,String> map = new HashMap<>();
		 Matcher matcher;
		 
		 if (executionTimes == null)
			 return map;
		 
		for(String field: COLON.split(executionTimes))
			if((matcher=FIELD.matcher(field)).matches()) {
				Integer order = new Integer(matcher.group(1));
				String times = matcher.group(2);
				map.put(order, times);
			} 
		 
		 return map;
	 }


	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}
