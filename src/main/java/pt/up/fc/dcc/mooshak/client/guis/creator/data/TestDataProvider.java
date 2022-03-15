package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.view.client.ListDataProvider;

/**
 * Data provider to back the table of tests
 * 
 * @author josepaiva
 */
public class TestDataProvider extends ListDataProvider<ProblemTestsDataLine> {

	public static final int LINES = 5;

	private static final Map<String, TestDataProvider> providers = new HashMap<String, TestDataProvider>();

	/**
	 * Get a IODataProvider for a given problem
	 * 
	 * @param problem
	 * @return
	 */
	public static TestDataProvider getDataProvider(String problem) {
		TestDataProvider provider = null;
		if (providers.containsKey(problem))
			provider = providers.get(problem);
		else {
			provider = new TestDataProvider();
			providers.put(problem, provider);
		}
		return provider;
	}

	public TestDataProvider() {

		List<ProblemTestsDataLine> ioDataList = getList();
		for (int count = 0; count < LINES; count++) {
			ioDataList.add(new ProblemTestsDataLine());
		}
	}

	/**
	 * Adds a line replacing an existing empty or not
	 * @param newLine
	 */
	public void addIOPair(ProblemTestsDataLine newLine) {
		List<ProblemTestsDataLine> ioDataList = getList();

		ProblemTestsDataLine line;
		if((line = findLineWithName(newLine.getName())) != null) {
			line.setFieldValues(newLine.getFieldValues());
		}
		else if((line = getEmptyLine()) == null)
			ioDataList.add(newLine);
		else {
			int pos = ioDataList.indexOf(line);
			ioDataList.remove(pos);
			ioDataList.add(pos, newLine);
		}
	}

	private ProblemTestsDataLine findLineWithName(String name) {
		for (ProblemTestsDataLine line : getList()) {
			if(name.equals(line.getName()))
				return line;
		}
		return null;
	}

	/**
	 * Returns an empty line
	 * @return
	 */
	private ProblemTestsDataLine getEmptyLine() {
		for (ProblemTestsDataLine line : getList()) {
			if("".equals(line.getName()) || line.getName() == null)
				return line;
		}
		
		return null;
	}

	/**
	 * Deletes the row with given id
	 * @param rowId
	 */
	public void deleteRow(String rowId) {
		for (ProblemTestsDataLine ioPair : getList()) {
			if(rowId.equals(ioPair.getId())) {
				getList().remove(ioPair);
				break;
			}
		}
		
		fillWithEmptyLines();
	}

	private void fillWithEmptyLines() {
		for (int i = getList().size(); i < LINES; i++)
			getList().add(new ProblemTestsDataLine());
	}

	public void removeAllLines() {
		getList().clear();
		fillWithEmptyLines();
	}
	
	

}
