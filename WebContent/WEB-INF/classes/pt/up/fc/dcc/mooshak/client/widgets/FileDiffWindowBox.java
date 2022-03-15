package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileDiffWindowBox extends WindowBox {

	private static FileDiffWindowBoxUiBinder uiBinder = GWT
			.create(FileDiffWindowBoxUiBinder.class);

	@UiTemplate("FileDiffWindowBox.ui.xml")
	interface FileDiffWindowBoxUiBinder extends
			UiBinder<Widget, FileDiffWindowBox> {
	}

	@UiField
	Button ok;

	@UiField
	HTMLPanel diffPanel;

	@UiField
	HTML expectedOutputPanel;

	@UiField
	HTML obtainedOutputPanel;

	@UiField
	HTML diffOutputPanel;

	@UiField
	ResizableHtmlPanel popupContainer;

	private String obtained;

	private String expected;
	
	public FileDiffWindowBox() {
		setWidget(uiBinder.createAndBindUi(this));
        setAutoHideEnabled(false);
        setGlassEnabled(false);
        setModal(true);
        setMinimizeIconVisible(false);
        setCloseIconVisible(true);
        setDraggable(true);
        setResizable(true);
        
        getElement().getStyle().setZIndex(1000);

		setMinWidth(600);
		setMinHeight(490);
		setWidth("600px");
		setHeight("490px");
		
		addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				diffPanel.setWidth(Math.max(popupContainer.getOffsetWidth() - 10, 0)+"px");
				diffPanel.setHeight(Math.max(popupContainer.getOffsetHeight() - 40, 0)+"px");
			}
		});

		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		obtainedOutputPanel.addDomHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				syncScrolling(obtainedOutputPanel.getElement(), 
						expectedOutputPanel.getElement(), 
						diffOutputPanel.getElement());
			}
		}, ScrollEvent.getType());
		
		expectedOutputPanel.addDomHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				syncScrolling(expectedOutputPanel.getElement(), 
						obtainedOutputPanel.getElement(), 
						diffOutputPanel.getElement());
			}
		}, ScrollEvent.getType());
		
		diffOutputPanel.addDomHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				syncScrolling(diffOutputPanel.getElement(), 
						expectedOutputPanel.getElement(), 
						obtainedOutputPanel.getElement());
			}
		}, ScrollEvent.getType());
		
		center();
		hide();
	}

	/**
	 * Set expected output
	 * @param expected
	 */
	public void setExpectedOutput(String expected) {
		this.expected = expected;
		
		String html = "";
		html = expected.replaceAll("\\n", "<br/>");
		expectedOutputPanel.setHTML(SafeHtmlUtils.fromTrustedString(html));
	}

	/**
	 * Set obtained output
	 * @param obtained
	 */
	public void setObtainedOutput(String obtained) {
		this.obtained = obtained;
		
		String html = "";
		html = obtained.replaceAll("\\n", "<br/>");
		
		obtainedOutputPanel.setHTML(SafeHtmlUtils.fromTrustedString(html));
	}

	public void setPresentationDiff(String expected, String obtained) {
		
		String highlighted = highlightFirstDiff(expected, obtained);
		
		setObtainedOutput(highlighted);
	}

	/**
	 * @param expected
	 * @param obtained
	 */
	private String highlightFirstDiff(String expected, String obtained) {
		
		String highlighted = obtained;
		
		int diffIndex = indexOfDifference(expected, obtained);

		if (obtained.charAt(diffIndex) == ' ') {
			highlighted = obtained.substring(0, diffIndex) 
					+ "<span style='color:red;'><del>&#9633;</del></span>" 
					+ obtained.substring(diffIndex + 1) ;
		} else if (obtained.charAt(diffIndex) == '\n') {
			highlighted = obtained.substring(0, diffIndex) 
					+ "<span style='color:red;'><del>&para;</del></span><br/>" 
					+ obtained.substring(diffIndex + 1) ;
		} else if (expected.charAt(diffIndex) == ' ') {
			highlighted = obtained.substring(0, diffIndex) 
					+ "<span style='color:red;'>&#9633;</span>" 
					+ obtained.substring(diffIndex + 1) ;
		} else if (expected.charAt(diffIndex) == '\n') {
			highlighted = obtained.substring(0, diffIndex) 
					+ "<span style='color:red;'>&para;</span><br/>" 
					+ obtained.substring(diffIndex + 1) ;
		} else {
			highlighted = obtained.substring(0, diffIndex) 
					+ "<span style='color:red;'>" + obtained.charAt(diffIndex)
					+ "</span>" 
					+ obtained.substring(diffIndex + 1) ;
		}
		
		return highlighted;
	}
	
	/**
	 * index of the first difference
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int indexOfDifference(String str1, String str2) {
	    if (str1 == str2) {
	        return -1;
	    }
	    if (str1 == null || str2 == null) {
	        return 0;
	    }
	    int i;
	    for (i = 0; i < str1.length() && i < str2.length(); ++i) {
	        if (str1.charAt(i) != str2.charAt(i)) {
	            break;
	        }
	    }
	    if (i < str2.length() || i < str1.length()) {
	        return i;
	    }
	    return -1;
	}

	
	private static final RegExp DIFF_HEADER_REGEX = 
			RegExp.compile("^\\@\\@\\s\\-(\\d+)\\,?(\\d+)?\\s\\+(\\d+)\\,?(\\d+)?\\s\\@\\@$", "gm");

	/**
	 * Set differences between expected and obtained
	 * @param diff
	 */
	public void setDifferences(String diff) {
		
		if (diff == null || diff.equals("")) {
			diffOutputPanel.setHTML(SafeHtmlUtils.fromTrustedString(""));
			return;
		}
		
		String[] lines = diff.split("\\n");
		String[] obtainedLines = obtained.split("\\n");

		String diffHtml = "";
		
		int lineCounter = 0;
		int diffLineCounter = 0;
		int addedLines = 0;
		
		MatchResult matchResult = DIFF_HEADER_REGEX.exec(diff);
		while (matchResult != null) {
			diffLineCounter++;
			
			int startLine = -1;
			int n = -1;
			if (matchResult.getGroup(4) != null) {
				startLine = Integer.parseInt(matchResult.getGroup(3));
				n = Integer.parseInt(matchResult.getGroup(4));
			} 
			else {
				startLine = Integer.parseInt(matchResult.getGroup(3));
				n = startLine;
			}
			
			for (int i = lineCounter + addedLines; i < startLine - 1; i++) {
				diffHtml += obtainedLines[lineCounter];
				diffHtml += "<br/>";
				lineCounter++;
			}
			
			int advanceAdd = 0;
			for (int i = 1; i <= n - startLine + 1; i++) {
				if (lines[diffLineCounter].startsWith("+")) {
					String tmp = lines[diffLineCounter].substring(1);
					
					//if (tmp.trim().equals(obtainedLines[lineCounter].trim())) {
					//	highlightFirstDiff(tmp, obtainedLines[lineCounter]);
					//}
					//else {
					if (advanceAdd > 0) {
						advanceAdd--;
						diffLineCounter++;
						continue;
					}
					diffHtml += "<i>" + (tmp.isEmpty() ? 
							"<span style='color:red;'>&para;</span>" : tmp) + "</i>";
					//}
					addedLines++;
				}
				else if (lines[diffLineCounter].startsWith("-")) {
					String tmp = lines[diffLineCounter].substring(1);
					boolean hasDual = findDualLine(tmp, lines, diffLineCounter + 1);
					if (hasDual) {
						diffHtml += highlightFirstDiff(tmp.trim(), obtainedLines[lineCounter]);
						advanceAdd++;
					} else {
						diffHtml += "<del>" + (tmp.isEmpty() ? "&para;" : tmp) + "</del>";
					}
					lineCounter++;
					i--;
				}
				else {
					diffHtml += lines[diffLineCounter];
					lineCounter++;
				}
				diffHtml += "<br/>";
				
				diffLineCounter++;
			}
			
			matchResult = DIFF_HEADER_REGEX.exec(diff);
		}
		
		
		for (int i = lineCounter; i < obtainedLines.length; i++) {
			diffHtml += obtainedLines[i];
			diffHtml += "<br/>";
		}
		
		diffOutputPanel.setHTML(SafeHtmlUtils.fromTrustedString(diffHtml));
	}

	private boolean findDualLine(String tmp, String[] lines, int i) {
		for (String line : lines) {
			if (!line.startsWith("+"))
				continue;
			
			line = line.substring(1);
			if (tmp.trim().equals(line.trim()))
				return true;
		}
		return false;
	}

	/**
	 * Synchronizes the scroll between two elements
	 * @param controlling
	 * @param controlled
	 */
	private native void syncScrolling(JavaScriptObject controlling, 
			JavaScriptObject controlled, JavaScriptObject controlled2) /*-{ 
		controlled.scrollTop = controlling.scrollTop;
		controlled.scrollLeft = controlling.scrollLeft;
		controlled2.scrollTop = controlling.scrollTop;
		controlled2.scrollLeft = controlling.scrollLeft;
	}-*/;
}
