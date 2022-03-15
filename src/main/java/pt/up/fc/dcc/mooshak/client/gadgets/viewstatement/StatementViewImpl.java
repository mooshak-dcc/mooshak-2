package pt.up.fc.dcc.mooshak.client.gadgets.viewstatement;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
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
	
	interface BaseStyle extends CssResource {
	    String pdfContent();
	}
	
	@UiField BaseStyle style; 

	@UiField
	Label idNameLabel;

	@UiField
	Label titleLabel;

	@UiField
	HTML htmlContent;
	
	@UiField
	HTMLPanel buttonsPanel;

	public StatementViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		htmlContent.ensureDebugId("statement");
		
		buttonsPanel.getElement().getStyle().setDisplay(Display.NONE);
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
	public void setExternalResource(String url) {
		String frameStyleText = "width: 100%; height: 100%; ";
		
		SafeHtmlBuilder builder = new SafeHtmlBuilder()
			.append(fromTrustedString("<iframe style=\""))
			.append(fromTrustedString(frameStyleText))
			.append(fromTrustedString("\" src=\""))
			.append(fromTrustedString(url))
			.append(fromTrustedString("\"></iframe>"));
		
		htmlContent.setHTML(builder.toSafeHtml());
		htmlContent.setStyleName(style.pdfContent());
		
		setVideoSrc(titleLabel.getText(), url);
		buttonsPanel.getElement().getStyle().setDisplay(Display.BLOCK);
	}
	
	@Override
	public void setPDFStatement(String problemId) {
		String frameStyleText = "width: 100%; height: 100%; ";
		
		SafeHtmlBuilder builder = new SafeHtmlBuilder()
			.append(fromTrustedString("<iframe style=\""))
			.append(fromTrustedString(frameStyleText))
			.append(fromTrustedString("\" src=\""))
			.appendEscaped(PDF_URL_PREFIX)
			.append(fromTrustedString("/"))
			.appendEscaped(problemId)
			.append(fromTrustedString("\"></iframe>"));
		
		htmlContent.setHTML(builder.toSafeHtml());
		htmlContent.setStyleName(style.pdfContent());

		buttonsPanel.getElement().getStyle().setDisplay(Display.NONE);
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

		buttonsPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		Style style = htmlContent.getElement().getStyle();

		htmlContent.setHTML(html);

		style.setTextAlign(TextAlign.CENTER);
		style.setColor("orange");
		style.setFontSize(32, Unit.PX);
		style.setFontWeight(FontWeight.BOLD);

		buttonsPanel.getElement().getStyle().setDisplay(Display.NONE);
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

	@Override
	public void setVideoSrc(String name, String src) {
		
		updateSharingButtons(name, src);
	}

	private void updateSharingButtons(String name, String src) {
	    
		Element shareDiv = buttonsPanel.getElementById("share_box_video");
		
		if (shareDiv != null) {
		    shareDiv.setAttribute("data-url", src);
		    shareDiv.setAttribute("data-title", name);
		}
	    
		Element body = Document.get().getElementsByTagName("body").getItem(0);
	    ScriptElement sce = Document.get().createScriptElement();
	    sce.setType("text/javascript");
	    sce.setSrc("https://s7.addthis.com/js/300/addthis_widget.js#pubid=ra-55b8ec3a5bd800cc");
	    sce.setPropertyString("async", "async");
	    body.appendChild(sce);
	}
}
