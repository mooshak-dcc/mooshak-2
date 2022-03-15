package pt.up.fc.dcc.mooshak.client.widgets;

import pt.up.fc.dcc.mooshak.client.views.Listing;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;

public class CategorizedHeaderCell extends ClickableTextCell {
	DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
	CategorizedColumnFilter filter;

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div style=\"color:blue;\">{0}</div>")
		SafeHtml text(String value);
	}

	private static Templates templates = GWT.create(Templates.class);

	private String column;

	public CategorizedHeaderCell(String column, Listing view, int x, int y) {
		SafeHtml s = templates.text(column);
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.append(s);

		filter = new CategorizedColumnFilter(column, view, this);

		popup.setPopupPosition(x, y);
		popup.getElement().getStyle().setZIndex(20);
		popup.add(filter);

		this.column = column;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		if (value == null) {
			return;
		}

		SafeHtml renderedText = templates.text(value);

		sb.append(renderedText);
	}

	public String getValue() {
		return column;
	}

	public void showFilter() {

		if (popup.isShowing())
			popup.hide();
		else
			popup.show();

	}

	/**
	 * @return the filter
	 */
	public CategorizedColumnFilter getFilter() {
		return filter;
	}

}
