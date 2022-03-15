package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;

public class CellDeleteButton extends ClickableTextCell {
	private Button delete;

	public CellDeleteButton() {
		super();

		delete = new Button("X");
		
		Style style = delete.getElement().getStyle();
		style.setColor("red");
		style.setBorderWidth(0, Unit.PX);
		style.setFloat(Float.LEFT);
		style.setPadding(2, Unit.PX);

	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		if (value == null || "".equals(value)) {
			sb.appendHtmlConstant("<div style=\"height: 20px\"></div>");
			return;
		}

		sb.appendHtmlConstant(delete.getElement().getString());

	}

}
