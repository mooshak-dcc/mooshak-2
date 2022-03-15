package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;

public class ImportCustomHeaderCell extends AbstractInputCell<String, String> {

	interface OptionTemplate extends SafeHtmlTemplates {
		@Template("<option value=\"{0}\">{0}</option>")
		SafeHtml deselected(String option);

		@Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
		SafeHtml selected(String option);
	}

	interface ColumnTemplate extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div style=\"display: inline-"
				+ "block\">{0}</div>")
		SafeHtml text(String value);
	}

	private static ColumnTemplate columnTemplate = GWT
			.create(ColumnTemplate.class);

	private static OptionTemplate optionTemplate = GWT
			.create(OptionTemplate.class);

	private String column;
	
	private int selectedIndex = -1;

	private HashMap<String, Integer> indexForOption = new HashMap<String, Integer>();
	private final List<String> options;

	public ImportCustomHeaderCell(List<String> options, String value) {
		super(BrowserEvents.CHANGE);

		this.options = new ArrayList<String>(options);
		int index = 0;
		for (String option : options) {
			if(option.equalsIgnoreCase(value))
				selectedIndex = index;
			indexForOption.put(option, index++);
		}

		this.column = value;

	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		String viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}
		
		sb.append(columnTemplate.text(column));
		
		int selectedIndex = getSelectedIndex(viewData == null ? value
				: viewData);
		this.selectedIndex = selectedIndex;
		
		sb.appendHtmlConstant("<select tabindex=\"-1\">");
		int index = 0;
		for (String option : options) {
			if (index++ == selectedIndex) {
				sb.append(optionTemplate.selected(option));
			} else {
				sb.append(optionTemplate.deselected(option));
			}
		}
		sb.appendHtmlConstant("</select>");
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value,
			NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		String type = event.getType();
		if (BrowserEvents.CHANGE.equals(type)) {
			Object key = context.getKey();
			SelectElement select = parent.getLastChild().cast();
			selectedIndex = select.getSelectedIndex();
			String newValue = options.get(select.getSelectedIndex());
			setViewData(key, newValue);
			finishEditing(parent, newValue, key, valueUpdater);
			if (valueUpdater != null) {
				valueUpdater.update(newValue);
			}
		}
	}

	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	private int getSelectedIndex(String value) {
		Integer index = indexForOption.get(value);
		if (index == null) {
			return -1;
		}
		return index.intValue();
	}
	
	public String getValue() {
		if(selectedIndex == -1)
			return null;
		return options.get(selectedIndex);
	}

}
