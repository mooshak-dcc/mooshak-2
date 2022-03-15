package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.views.Listing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class CategorizedColumnFilter extends Composite {
	private static CategorizedColumnFilterUiBinder uiBinder = GWT
			.create(CategorizedColumnFilterUiBinder.class);

	interface CategorizedColumnFilterUiBinder extends
			UiBinder<Widget, CategorizedColumnFilter> {
	}

	private Listing view;

	private CategorizedHeaderCell cell;

	@UiField
	Label filterName;

	@UiField
	ListBox filterList;

	@UiField
	CheckBox filterCheck;

	public CategorizedColumnFilter(String name, Listing view,
			CategorizedHeaderCell cell) {
		initWidget(uiBinder.createAndBindUi(this));
		filterList.setMultipleSelect(true);
		this.view = view;
		filterName.setText("Filter: " + name);
		this.cell = cell;
	}

	@UiHandler({ "filterList" })
	void filterList(ChangeEvent e) {
		view.updateDataGrid();
		filterCheck.setValue(false);
	}

	@UiHandler(value = { "filterCheck" })
	void filterCheck(ClickEvent e) {
		if (filterCheck.getValue()) {
			for (int i = 0; i < filterList.getItemCount(); i++)
				filterList.setItemSelected(i, false);
			view.updateDataGrid();
		}
	}

	public List<String> getSelectionListbox() {
		List<String> selections = new ArrayList<String>();
		for (int i = 0; i < filterList.getItemCount(); i++)
			if (filterList.isItemSelected(i))
				selections.add(filterList.getItemText(i));

		return selections;
	}

	public void setDataList(Collection<String> data) {
		
		List<String> selections = getSelectionListbox();
		filterList.clear();
		
		List<String> dataList = new ArrayList<>(data);
		Collections.sort(dataList);
		for (String string : dataList) {
			filterList.addItem(string);
		}
		
		for (int i = 0; i < filterList.getItemCount(); i++)
			if (selections.contains(filterList.getItemText(i)))
				filterList.setItemSelected(i, true);
			else
				filterList.setItemSelected(i, false);
	}

	@UiField
	Button close;

	@UiHandler(value = { "close" })
	void close(ClickEvent e) {
		cell.showFilter();
	}

}
