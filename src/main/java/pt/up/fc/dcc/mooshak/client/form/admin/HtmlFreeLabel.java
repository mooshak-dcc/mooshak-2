package pt.up.fc.dcc.mooshak.client.form.admin;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Label;

/**
 * Label stripped of HTML tags
 */
public class HtmlFreeLabel extends Label implements MooshakWidget {
	private RegExp brRe =	RegExp.compile("<br>","gi");
	private RegExp tagRe =	RegExp.compile("<[^>]+>","g");
	
	private FormStyle style = FormResources.INSTANCE.style();
	
	private String field = null;
	private String foreground = "black";
	
	public HtmlFreeLabel() {
		super();
	}
	
	public HtmlFreeLabel(String foreground) {
		super();
		this.foreground = foreground;
	}
	
	@Override
	public void onLoad() {
		getElement().getStyle().setColor(foreground);
	}

	
	/**
	 * Remove HTML tags before setting text
	 * @see com.google.gwt.user.client.ui.Label#setText(java.lang.String)
	 */
	@Override
	public void setValue(MooshakValue value) {
		if (value == null)
			return;
		
		String text = "";
		
		try {
			text = brRe.replace(value.getSimple(), "\n");
			text = tagRe.replace(text," ");
		} catch (Exception e) {}
		
		super.setText(text);
		
		style.ensureInjected();
		getElement().setClassName(style.label());
		
		field = value.getSimple();
	}
	
	/**
	 * Silently ignoring events. This type of widget cannot be changed 
	 */
	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		setValue(value);
	}


	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field, getText());
	}


	/**
	 * Silently ignoring registration. This type of widget cannot be changed 
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<MooshakValue> handler) {

		return null;
	}

	@Override
	public boolean isEditing() {
		return false;
	}



	
}
