package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

/**
 * @author josepaiva
 */
public class CustomImageCell extends AbstractCell<String> {

	interface Template extends SafeHtmlTemplates {
		@Template("<img src=\"{0}\"/>")
		SafeHtml img(SafeUri url);
	}

	private static Template template;

	/**
	 * Construct a new ImageCell.
	 */
	public CustomImageCell() {
		if (template == null) {
			template = GWT.create(Template.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client
	 * .Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		if (value != null) {
			// The template will sanitize the URI.
			if(!"".equals(value))
				sb.append(template.img(UriUtils.fromTrustedString(value)));
		}
	}

}
