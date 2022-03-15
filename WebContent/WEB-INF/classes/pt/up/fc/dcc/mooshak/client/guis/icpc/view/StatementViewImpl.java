package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StatementViewImpl extends Composite implements StatementView {

	private Presenter presenter;

	private static final String PDF_URL_PREFIX = "pdf";
	private static final String IMG_URL_PREFIX = "image";

	static RegExp imgRegExp = RegExp.compile("<img([^>]*)src=(\"|')([^\"']*)(\"|')([^>]*)>", "gi");

	static RegExp scriptMathJaxRegExp = RegExp
			.compile("<script([^>]*)src=(\"|')(\\.\\./mathjax/[^\"']*)(\"|')([^>]*)>([^<]*)</script>");

	private static StatementUiBinder uiBinder = GWT.create(StatementUiBinder.class);

	@UiTemplate("StatementView.ui.xml")
	interface StatementUiBinder extends UiBinder<Widget, StatementViewImpl> {
	}

	@UiField
	Label idNameLabel;

	@UiField
	Label titleLabel;

	@UiField
	HTML htmlContent;

	public StatementViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		htmlContent.ensureDebugId("statement");
	}

	@Override
	public void onLoad() {

		htmlContent.ensureDebugId("statement");
	}

	public void setProgramIdentification(String id, String name) {
		idNameLabel.setText(id);
		titleLabel.setText(name);
	}

	@Override
	public void setPDFStatement(String problemId) {
		String frameStyleText = "width: 100%; height: 600px; ";

		SafeHtmlBuilder builder = new SafeHtmlBuilder().append(fromTrustedString("<iframe style=\""))
				.append(fromTrustedString(frameStyleText)).append(fromTrustedString("\" src=\""))
				.appendEscaped(PDF_URL_PREFIX).append(fromTrustedString("/")).appendEscaped(problemId)
				.append(fromTrustedString("\"></iframe>"));

		htmlContent.setHTML(builder.toSafeHtml());
		htmlContent.setStyleName("pdfContent");
	}

	@Override
	public void setHTMLStatement(String html, final String problemId) {
		Style style = htmlContent.getElement().getStyle();

		MatchResult matcher = scriptMathJaxRegExp.exec(html);

		if (matcher == null) {
			htmlContent
					.setHTML(imgRegExp.replace(html, "<img$1src=$2" + IMG_URL_PREFIX + "/" + problemId + "/$3$4$5>"));
		} else {
			String src = matcher.getGroup(3);
			src = src.replace("../", GWT.getHostPageBaseURL());
			final String noSceHtml = scriptMathJaxRegExp.replace(html, "");

			if (!hasMathJax()) {
				ScriptInjector.fromUrl(src).setWindow(ScriptInjector.TOP_WINDOW)
						.setCallback(new Callback<Void, Exception>() {
	
							@Override
							public void onSuccess(Void result) {
								htmlContent.setHTML(imgRegExp.replace(noSceHtml,
										"<img$1src=$2" + IMG_URL_PREFIX + "/" + problemId + "/$3$4$5>"));
							}
	
							@Override
							public void onFailure(Exception reason) {
								Window.alert("Could not load MathJax");
							}
						}).inject();
			} else {
				htmlContent.setHTML(imgRegExp.replace(noSceHtml,
						"<img$1src=$2" + IMG_URL_PREFIX + "/" + problemId + "/$3$4$5>"));
				reloadMathJax(htmlContent.getElement());
			}
		}

		style.setPadding(5, Unit.MM);
	}

	public static final native void reloadMathJax(JavaScriptObject el)/*-{
		if($wnd.MathJax) {
			var m = $wnd.MathJax;
			m.Hub.Typeset(el);
		}
	}-*/;

	public static final native boolean hasMathJax()/*-{
		return $wnd.MathJax !== undefined;
	}-*/;

	@Override
	public void setStatement(String html) {

		htmlContent.setHTML("<p>" + html + "</p>");

		Style style = htmlContent.getElement().getFirstChildElement().getStyle();
		style.setTextAlign(TextAlign.CENTER);
		style.setColor("orange");
		style.setFontSize(32, Unit.PX);
		style.setFontWeight(FontWeight.BOLD);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * @return the presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	public Widget getContentAsWidget() {
		return htmlContent.asWidget();
	}
}
