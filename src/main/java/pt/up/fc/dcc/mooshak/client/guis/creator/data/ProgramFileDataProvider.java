package pt.up.fc.dcc.mooshak.client.guis.creator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.view.client.ListDataProvider;

public class ProgramFileDataProvider extends
		ListDataProvider<ProgramFileDataLine> {

	public static final int LINES = 4;

	private static final Map<String, ProgramFileDataProvider> providers = 
			new HashMap<String, ProgramFileDataProvider>();

	/**
	 * Get a ProgramFileDataProvider for a given problem
	 * 
	 * @param problem
	 * @return
	 */
	public static ProgramFileDataProvider getDataProvider(String problem) {
		ProgramFileDataProvider provider = null;
		if (providers.containsKey(problem))
			provider = providers.get(problem);
		else {
			provider = new ProgramFileDataProvider();
			providers.put(problem, provider);
		}
		return provider;
	}

	public ProgramFileDataProvider() {

		List<ProgramFileDataLine> dataList = getList();
		for (int count = 0; count < LINES; count++) {
			dataList.add(new ProgramFileDataLine());
		}
	}

	/**
	 * Adds a line replacing an existing empty or not
	 * @param newLine
	 */
	public void addProgramFile(ProgramFileDataLine newLine) {
		List<ProgramFileDataLine> dataList = getList();

		ProgramFileDataLine line;
		if((line = findLineWithName(newLine.getId())) != null) {
			line.setValue(newLine.getValue());
		}
		else if((line = getEmptyLine()) == null)
			dataList.add(newLine);
		else {
			int pos = dataList.indexOf(line);
			dataList.remove(pos);
			dataList.add(pos, newLine);
		}
		flush();
		refresh();
	}

	private ProgramFileDataLine findLineWithName(String name) {
		for (ProgramFileDataLine line : getList()) {
			if(name.equals(line.getId()))
				return line;
		}
		return null;
	}

	/**
	 * Returns an empty line
	 * @return
	 */
	private ProgramFileDataLine getEmptyLine() {
		for (ProgramFileDataLine line : getList()) {
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
		for (ProgramFileDataLine file : getList()) {
			if(rowId.equals(file.getId())) {
				getList().remove(file);
				break;
			}
		}
		
		fillWithEmptyLines();
		flush();
	}

	private void fillWithEmptyLines() {
		for (int i = getList().size(); i < LINES; i++)
			getList().add(new ProgramFileDataLine());
	}

	public void removeAllLines() {
		getList().clear();
		fillWithEmptyLines();
		flush();
	}

	/**
	 * Removes all lines of the given type field
	 * @param field
	 */
	public void removeAllLines(String field) {
		List<ProgramFileDataLine> toRemove = new ArrayList<>();
		for (ProgramFileDataLine file : getList()) {
			if(file.getType() == null)
				continue;
			if(file.getType().equals(field))
				toRemove.add(file);
			
		}
		getList().removeAll(toRemove);
		fillWithEmptyLines();
		flush();
	}

}
