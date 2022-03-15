package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

/**
 * Widget for changing contest without logging out and in again
 * 
 * @author josepaiva
 */
public class ContestSelector extends SelectOneListBox<SelectableOption> {

	private OptionFormatter<SelectableOption> formatter = new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	private String selectedId = null;

	public ContestSelector() {
		setFormatter(formatter);
	}

	public void setSelectedId(String selectedId) {
		this.selectedId = selectedId;
		
		select();
	}
	
	@Override
	public void setSelections(Collection<SelectableOption> selections) {
		
		List<SelectableOption> options = new ArrayList<SelectableOption>(selections);
		Collections.sort(options, new Comparator<SelectableOption>() {

			@Override
			public int compare(SelectableOption o1, SelectableOption o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
			
		});
		
		super.setSelections(options);
		
		select();
	}

	private void select() {
		
		if (selectedId == null)
			return;
		
		for (SelectableOption so : getSelections()) {
			if (so.getId().equals(selectedId)) {
				setSelectedValue(so);
			}
		}
	}
}
