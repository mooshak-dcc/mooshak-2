package pt.up.fc.dcc.mooshak.client.guis.icpc.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.IOPair;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Data provider to back the table with input/output tests 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since 2.0
 */
public class IODataProvider extends  ListDataProvider<IOPair> {

	public static final int LINES = 22;
	
	private static final Map<String,IODataProvider> providers =
			new HashMap<String,IODataProvider>();

	/**
	 * Get a IODataProvider for a given problem
	 * @param problem
	 * @return
	 */
 	public static IODataProvider getDataProvider(String problem) {
		IODataProvider provider = null;
		if(providers.containsKey(problem))
			provider = providers.get(problem);
		else {
			provider = new IODataProvider();
			providers.put(problem, provider);
		}
		return provider;
	}

	
	/**
	 * A line in the input output grid
	 */
	public static class IOPair {
		int id;
		String input = "";
		String output = "";
		String execTime;
		
		boolean publicTest = false;
		boolean passed = false;
		
		static int lines = 0;
		
		public static final ProvidesKey<IOPair> KEY_PROVIDER = 
				new ProvidesKey<IOPair>() {
		      @Override
		      public Object getKey(IOPair item) {
		        return item == null ? null : item.getId();
		      }
		    };
		
		public IOPair() {
			id=lines;
			lines++;
		}
		
		
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * @return the input
		 */
		public String getInput() {
			return input;
		}
		/**
		 * @param input the input to set
		 */
		public void setInput(String input) {
			this.input = input;
		}
		/**
		 * @return the output
		 */
		public String getOutput() {
			return output;
		}
		/**
		 * @param output the output to set
		 */
		public void setOutput(String output) {
			this.output = output;
		}


		/**
		 * @return the publicTest
		 */
		public boolean isPublicTest() {
			return publicTest;
		}

		/**
		 * @param publicTest the publicTest to set
		 */
		public void setPublicTest(boolean publicTest) {
			this.publicTest = publicTest;
		}

		/**
		 * @return the passed
		 */
		public boolean isPassed() {
			return passed;
		}

		/**
		 * @param passed the passed to set
		 */
		public void setPassed(boolean passed) {
			this.passed = passed;
		}


		/**
		 * @return the execTime
		 */
		public String getExecTime() {
			return execTime;
		}


		/**
		 * @param execTime the execTime to set
		 */
		public void setExecTime(String execTime) {
			this.execTime = execTime;
		}
	}
	
	public IODataProvider() {
		
		List<IOPair> ioDataList = getList();
		for(int count=0; count< LINES; count++) {
			ioDataList.add(new IOPair());
		}
	}
	
}
