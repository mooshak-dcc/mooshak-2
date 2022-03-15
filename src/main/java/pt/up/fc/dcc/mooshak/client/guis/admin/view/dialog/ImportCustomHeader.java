package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.List;

import com.google.gwt.user.cellview.client.Header;

public class ImportCustomHeader extends Header<String> {

	public ImportCustomHeader(String name, 
			List<String> options) {
		super(new ImportCustomHeaderCell(options, name));
		
	}

	@Override
	public String getValue() {
		return ((ImportCustomHeaderCell) getCell()).getValue();
	}
	
	public void setColumn(String column) {
		((ImportCustomHeaderCell) getCell()).setColumn(column);
	}

}
