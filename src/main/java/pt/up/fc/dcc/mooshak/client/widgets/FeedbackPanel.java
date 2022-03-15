package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FeedbackPanel extends PopupPanel {
	
	public FeedbackPanel() {
		super(true,true);
		setGlassEnabled(false);			
		
		Style style = getElement().getStyle();
		
		// style.setBackgroundColor("#BBC");
		style.setColor("#669");
		style.setBorderWidth(3, Unit.MM);
		style.setZIndex(20);
	}
	
	public void setContent(String feedbackInHTML) {
		VerticalPanel panel = new VerticalPanel();
		Style panelStyle = panel.getElement().getStyle();
		HTML html = new HTML(feedbackInHTML);
		Style htmlStyle = html.getElement().getStyle();
		Button ok = new Button("OK");
		Style okStyle = ok.getElement().getStyle();
		ok.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		

		
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSpacing(2);
		panelStyle.setBorderWidth(3, Unit.MM);
		panelStyle.setBorderStyle(BorderStyle.SOLID);
		panelStyle.setBorderColor("#BBC");
		
		htmlStyle.setMargin(5,Unit.MM);
		htmlStyle.setWidth(20, Unit.CM);
		htmlStyle.setHeight(10, Unit.CM);
		htmlStyle.setBackgroundColor("white");
		htmlStyle.setOverflow(Overflow.AUTO);
		
		okStyle.setMargin(5,Unit.MM);
		okStyle.setWidth(2, Unit.CM);
		
		panel.add(new HTML("<h2>Feedback</h2>"));
		panel.add(html);
		panel.add(ok);
		
		setWidget(panel);
	}
}
