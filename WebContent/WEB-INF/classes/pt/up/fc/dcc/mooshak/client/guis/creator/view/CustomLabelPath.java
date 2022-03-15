/**
 * 
 */
package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.ProblemView.BaseStyle;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;

/**
 * Label to hold paths
 * @author josepaiva
 */
public class CustomLabelPath extends Label implements MooshakWidget {
	
	private String field = null;
	
	private BaseStyle style;
	private String value = "";
	private String valuePath = "";
	private byte[] content;
	private String tip = "";
	
	public CustomLabelPath() {
		
	}
	
	/**
	 * @param style the style to set
	 */
	public void setStyle(BaseStyle style) {
		this.style = style;
	}

	@Override
	public void setText(String text) {
		if("".equals(tip))
			tip = text;
		setValue(text);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	@Override
	public MooshakValue getValue() {
		if(tip.equals(value))
			return new MooshakValue(field, null,content);
		return new MooshakValue(field, value,content);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		field = value.getField();
		
		String text = "";
		if(value.isSimpleValue())
			text = value.getSimple();
		else {
			text = value.getName();
			if(value.getContent() == null)
				content = "".getBytes();
			else
				content = value.getContent();
		}
			
		if(text == null || text.equals("")) {
			this.value = tip;
			if(style != null)
				addStyleName(style.tip());
			text = tip;
		}
		else if(style != null)
			removeStyleName(style.tip());
		
		setValue(text);
			
	}
	
	public void setValue(String text) {
		valuePath = text;
		
		int index = -1;
		if((index = text.lastIndexOf("/")) != -1)
			this.value = text.substring(index+1);
		else if((index = text.lastIndexOf("\\")) != -1)
			this.value = text.substring(index+1);
		else 
			this.value = text;

		super.setText(this.value);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
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

	public String getValuePath() {
		return valuePath;
	}

}
