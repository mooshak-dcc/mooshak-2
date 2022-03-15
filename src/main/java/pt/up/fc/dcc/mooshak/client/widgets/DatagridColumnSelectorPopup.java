package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.views.Listing;

public class DatagridColumnSelectorPopup extends DecoratedPopupPanel {
	private static DatagridColumnSelectorPopupUiBinder uiBinder = GWT
			.create(DatagridColumnSelectorPopupUiBinder.class);

	interface DatagridColumnSelectorPopupUiBinder extends
			UiBinder<Widget, DatagridColumnSelectorPopup> {
	}

	private Listing view;

	@UiField
	HTMLPanel columnList;

	@UiField
	CheckBox columnCheck;
	
	private List<CheckBox> columnCheckboxes = new ArrayList<>();

	public DatagridColumnSelectorPopup(Listing view) {
		add(uiBinder.createAndBindUi(this));
		this.view = view;
		
		setAutoHideEnabled(true);
		hide();
	}

	@UiHandler(value = { "columnCheck" })
	void columnCheck(ClickEvent e) {
		if (columnCheck.getValue()) {
			for (CheckBox checkBox : columnCheckboxes) {
				checkBox.setValue(true);
				view.showColumn(checkBox.getFormValue());
			}
		}
	}

	public void setColumnList(Map<String, String> columnsLabel) {
		columnList.clear();
		
		for (String key : columnsLabel.keySet()) {
			final CheckBox cb = new CheckBox(columnsLabel.get(key));
			cb.setFormValue(key);
			cb.setValue(true);
			
			columnCheckboxes.add(cb);
			
			cb.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (cb.getValue().booleanValue())
						view.showColumn(cb.getFormValue());
					else {
						view.hideColumn(cb.getFormValue());
						columnCheck.setValue(false);
					}
				}
			});
			
		}
		
		/*Collections.sort(columnCheckboxes, new Comparator<CheckBox>() {

			@Override
			public int compare(CheckBox o1, CheckBox o2) {
				return o1.getText().compareTo(o2.getText());
			}
		});*/
		
		for (CheckBox cb : columnCheckboxes) {
			columnList.add(cb);
		}
	}

	public void setVisibleColumns(List<String> cbList) {
		for (CheckBox cb : columnCheckboxes) {
			if (cbList.contains(cb.getFormValue())) {
				view.showColumn(cb.getFormValue());
				cb.setValue(true);
			} else {
				view.hideColumn(cb.getFormValue());
				cb.setValue(false);
				columnCheck.setValue(false);
			}
		}
	}

	@UiField
	Button close;

	@UiHandler(value = { "close" })
	void close(ClickEvent e) {
		hide();
	}

}
