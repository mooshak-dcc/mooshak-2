package pt.up.fc.dcc.mooshak.client.widgets;

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
import com.google.gwt.user.client.ui.Widget;

/**
 * PDF or HTML Statement Viewer
 * 
 * @author josepaiva
 */
public class StatementViewer extends Composite {
	
	private static final String PDF_URL_PREFIX = "pdf";
	private static final String IMG_URL_PREFIX = "image";

	static RegExp imgRegExp = RegExp.compile("<img([^>]*)src=(\"|')([^\"']*)(\"|')([^>]*)>", "gi");

	static RegExp scriptMathJaxRegExp = RegExp
			.compile("<script([^>]*)src=(\"|')(\\.\\./mathjax/[^\"']*)(\"|')([^>]*)>([^<]*)</script>");

	private static StatementViewerUiBinder uiBinder = GWT.create(StatementViewerUiBinder.class);

	@UiTemplate("StatementViewer.ui.xml")
	interface StatementViewerUiBinder extends UiBinder<Widget, StatementViewer> {
	}

	@UiField
	HTML htmlContent;
	
	private int height = 600;

	public StatementViewer() {
		initWidget(uiBinder.createAndBindUi(this));
		htmlContent.ensureDebugId("statement");
	}

	@Override
	public void onLoad() {

		htmlContent.ensureDebugId("statement");
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	public void setPDFStatement(String problemId) {
		String frameStyleText = "width: 100%; height: " + height + "px; ";

		SafeHtmlBuilder builder = new SafeHtmlBuilder().append(fromTrustedString("<iframe style=\""))
				.append(fromTrustedString(frameStyleText)).append(fromTrustedString("\" src=\""))
				.appendEscaped(PDF_URL_PREFIX).append(fromTrustedString("/")).appendEscaped(problemId)
				.append(fromTrustedString("\"></iframe>"));

		htmlContent.setHTML(builder.toSafeHtml());
		htmlContent.setStyleName("pdfContent");
	}

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

	public void setStatement(String html) {
		Style style = htmlContent.getElement().getStyle();

		htmlContent.setHTML(html);

		style.setTextAlign(TextAlign.CENTER);
		style.setColor("orange");
		style.setFontSize(32, Unit.PX);
		style.setFontWeight(FontWeight.BOLD);
	}

	public Widget getContentAsWidget() {
		return htmlContent.asWidget();
	}
}
