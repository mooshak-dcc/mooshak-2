package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.view.client.ListDataProvider;

public class SkeletonsDataProvider extends ListDataProvider<ProblemSkeletonsDataLine> {

	public static final int LINES = 2;

	private static final Map<String, SkeletonsDataProvider> providers = new HashMap<String, SkeletonsDataProvider>();

	/**
	 * Get a DataProvider for a given problem
	 * 
	 * @param problem
	 * @return
	 */
	public static SkeletonsDataProvider getDataProvider(String problem) {
		SkeletonsDataProvider provider = null;
		if (providers.containsKey(problem))
			provider = providers.get(problem);
		else {
			provider = new SkeletonsDataProvider();
			providers.put(problem, provider);
		}
		return provider;
	}

	public SkeletonsDataProvider() {

		List<ProblemSkeletonsDataLine> ioDataList = getList();
		for (int count = 0; count < LINES; count++) {
			ioDataList.add(new ProblemSkeletonsDataLine());
		}
	}

	/**
	 * Adds a line replacing an existing empty or not
	 * @param newLine
	 */
	public void addPair(ProblemSkeletonsDataLine newLine) {
		List<ProblemSkeletonsDataLine> ioDataList = getList();

		ProblemSkeletonsDataLine line;
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

	private ProblemSkeletonsDataLine findLineWithName(String name) {
		for (ProblemSkeletonsDataLine line : getList()) {
			if(name.equals(line.getName()))
				return line;
		}
		return null;
	}

	/**
	 * Returns an empty line
	 * @return
	 */
	private ProblemSkeletonsDataLine getEmptyLine() {
		for (ProblemSkeletonsDataLine line : getList()) {
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
		for (ProblemSkeletonsDataLine ioPair : getList()) {
			if(rowId.equals(ioPair.getId())) {
				getList().remove(ioPair);
				break;
			}
		}
		
		fillWithEmptyLines();
	}

	private void fillWithEmptyLines() {
		for (int i = getList().size(); i < LINES; i++)
			getList().add(new ProblemSkeletonsDataLine());
	}

	public void removeAllLines() {
		getList().clear();
		fillWithEmptyLines();
	}
	
	

}
