package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CellResetEditorButton extends ClickableTextCell {
	private ResetEditorButton reset;

	public CellResetEditorButton() {
		super();

		reset = new ResetEditorButton();

	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		if (value == null || "".equals(value)) {
			sb.appendHtmlConstant("<div style=\"height: 16px\"></div>");
			return;
		}

		sb.appendHtmlConstant(reset.getElement().getString());

	}
	
	public String getElementString() {
		return reset.getElement().getString();
	}


}
