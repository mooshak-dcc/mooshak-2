package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;

/**
 * Abstract class extended by views with multi-language mode
 * 
 * @author josepaiva
 */
public class MultiLanguageMenu extends Composite {

	private static MultiLanguageUiBinder uiBinder = GWT
			.create(MultiLanguageUiBinder.class);

	@UiTemplate("MultiLanguageMenu.ui.xml")
	interface MultiLanguageUiBinder extends UiBinder<Widget, MultiLanguageMenu> {
	}
	
	private ICPCMessages messages = GWT.create(ICPCMessages.class);

	/*private static final Map<String, String> specialCases = 
			new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;

		{
			put("en", "gb");
		}
	};*/

	private Set<String> languages = new HashSet<>(Arrays.asList("en", "pt","es", "arb"));

	@UiField
	HTMLPanel pane;

	public MultiLanguageMenu() {
		initWidget(uiBinder.createAndBindUi(this));

		for (final String lang : languages) {
			
			final Label btn = new Label(lang.toUpperCase());
			btn.getElement().getStyle().setMarginBottom(10, Unit.PX);
			btn.getElement().getStyle().setCursor(Cursor.POINTER);
			/*final CustomImageButton btn = new CustomImageButton();

			String nativeName = LocaleInfo.getLocaleNativeDisplayName(lang);
			btn.setTitle(nativeName == null ? lang.toUpperCase() : nativeName
					.toUpperCase());
			btn.setAltText(lang);*/

			/*String imgName = lang;
			if (lang.indexOf("_") != -1)
				imgName = lang.substring(0, lang.indexOf("_"));
			if (specialCases.get(lang) != null)
				imgName = specialCases.get(lang);*/
			
			//String uri = "flag/" + imgName.toLowerCase();
			//btn.setUrl(UriUtils.fromTrustedString(uri).asString());

			//btn.setPixelSize(21, 14);

			btn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					new OkCancelDialog(messages
							.changeLanguageConfirmation(btn.getText())) {
					}.addDialogHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							changeLanguageTo(lang);
						}
					});
				}
			});
			pane.add(btn);
		}
	}

	/**
	 * Changes the current language
	 * 
	 * @param lang
	 */
	protected void changeLanguageTo(String lang) {
		UrlBuilder builder = Location.createUrlBuilder().setParameter("locale",
				lang);
		Window.Location
				.replace(builder.buildString().replaceAll("%3A9997", ""));
	}

	/**
	 * Adds an available language to the menu list
	 * 
	 * @param abbrev
	 *            A language abbreviation (ex: en, pt)
	 */
	public void addLanguage(String abbrev) {
		languages.add(abbrev);
	}

}
