package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A widget to select balloon colors from a list of 
 * typically used colors. 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ColorPicker extends Button implements MooshakWidget {
	private DecoratedPopupPanel popup = new DecoratedPopupPanel(true);

	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers =
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	private String field = null;
	
	/**
	 * List of colors displayed in the color picker
	 */
	enum StandardColor {
		
		WHITE("#FFFFFF"), SILVER("#C0C0C0"), GRAY("#808080"),  BLACK("#000000"),
		AQUA("#00FFFF"),  TEAL("#008080"),   NAVY("#000080"),  BLUE("#0000FF"),
		FUCHSIA("#FF00FF"),PURPLE("#800080"),MAROON("#800000"),RED("#FF0000"),
		LIME("#00FF00"),   ORANGE("#FFA500"),OLIVE("#808000"), GREEN("#008000"),  
		YELLOW("#FFFF00");
		
		String rgb;
		StandardColor(String rgb) {
			this.rgb=rgb;
		}
	}
	
	private static final RegExp VALID_HEX_VOLUE = 
			RegExp.compile("^#[0-9A-F]{6}$","i");
	
	private static final int NCOLORS = StandardColor.values().length;
	private static final int COLUMNS = 4;
	private static final int ROWS = NCOLORS/COLUMNS + (NCOLORS%COLUMNS==0?0:1);
	
	// private static Logger LOGGER = Logger.getLogger("");
	
	public ColorPicker() {

		final FlexTable grid = new FlexTable();
		final TextBox text = new TextBox();
		final ColorPicker button = this;
		
		int row =0;
		int column=0;
		for(final StandardColor color: StandardColor.values()) {
			Label label = new Label(color.toString());
			label.setTitle(color.toString());
			setBackground(label.getElement(), color.rgb);
			setColor(label.getElement(),getContrast(color.rgb));
			setColorStyles(label.getElement());
			
			label.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					MooshakValue value=new MooshakValue(field,color.toString());
					button.setValue(value,true);
					text.setText(color.rgb);
				}
			});
			
			grid.setWidget(row, column++, label);
			if(column == COLUMNS) {
				row++;
				column = 0;
			}
		}
		grid.setWidget(row, column, text);
		grid.getFlexCellFormatter().setColSpan(row, column, COLUMNS-column);

		text.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String value = text.getValue();
				if(VALID_HEX_VOLUE.test(value)) {
					button.setValue(new MooshakValue(field,value));		
					setColor(text.getElement(),"black");
				} else 
					setColor(text.getElement(),"red");
			}
		});
	
		popup.getElement().getStyle().setZIndex(100);
		popup.setAnimationEnabled(true);
		popup.setWidget(grid);
	}

	/**
	 * Set styles of default a color
	 * @param element
	 */
	private static native void setColorStyles(
			JavaScriptObject element) /*-{
		element.style.padding = "5px";
		element.style.textAlign = "center";
		element.style.minWidth = "60px";
		element.style.cursor = "pointer";
	}-*/;

	@Override
	protected void onLoad() {

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button source = (Button) event.getSource();

				if (popup.isShowing()) 
					popup.hide();
				else {
					int left = source.getAbsoluteLeft();
					int top = source.getAbsoluteTop()
							+ source.getOffsetHeight();
					popup.setPopupPosition(left, top);

					popup.show();

				} 
				
			}
		});
	}
	
	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field,getText());
	}
	
	@Override
	public void setValue(MooshakValue value) {
		
		setValue(value,false);
	}
	
	class ColorPickerChangedEvent extends ValueChangeEvent<MooshakValue> {
		
		ColorPickerChangedEvent(MooshakValue value,ColorPicker source) {
			super(value);
			setSource(source);
		}
	}
	
	
	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		String hexColor;
		String text = value.getSimple();
		
		field = value.getField();
		
		if(VALID_HEX_VOLUE.test(text))
			hexColor = text;
		else
			hexColor = getHexColor(text);
		
		text = getNameIfAvailable(text);
			
		setColor(getElement(), getContrast(hexColor));
		
		setBackground(getElement(), text);
		
		super.setText(text);
		
		if(fireEvents) {
			ValueChangeEvent<MooshakValue> event =
					new ColorPickerChangedEvent(new MooshakValue(field,hexColor),this);
			
			for(ValueChangeHandler<MooshakValue> handler: valueChangeHandlers)
				handler.onValueChange(event);
		}
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		valueChangeHandlers.add(handler);
		
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				valueChangeHandlers.remove(handler);
			}};
	}
	
	
	/*****************************************************************\
	 * 			Static methods for handling colors                   *
	\*****************************************************************/
	
	
	/**
	 * If parameter is name of a standard color then return its RGB
	 * otherwise return the RGB of white
	 * @param text
	 * @return
	 */
	private static String getHexColor(String text) {
		String hexColor = "#FFFFFF";
		
		for(StandardColor color: StandardColor.values())
			if(color.toString().equals(text))
				return color.rgb;

		return hexColor;
	}
	
	/**
	 * A human readable name of a standard color corresponding to given rgb 
	 * @param rgb
	 * @return
	 */
	static String getNameIfAvailable(String rgb) {
		String name = rgb;
		
		for(StandardColor color: StandardColor.values())
			if(color.rgb.equals(rgb))
				name = color.toString();
				
		return name;
	}
	
	/**
	 * Get the hex of white or black 
	 * depending of what contrasts with given color
	 * @param color 	hex of (background) color
	 * @return			black or white in hex
	 */
	private static String getContrast(String color) {
		int light = 0;
		
		color = color.toUpperCase();
		for(int i=1; i<color.length(); i++) {
			int code = Character.codePointAt(color, i);
			if(Character.isDigit(color.charAt(i)))
				light += (code - Character.codePointAt("0", 0));
			else 
				light += 10 + (code - Character.codePointAt("A", 0));

		}
		if(light > 32)
			return "#000000";
		else
			return "#FFFFFF";
	}


	/**
	 * Set background color on given HTML element
	 * @param element
	 * @param color
	 */
	public static native void setBackground(
			JavaScriptObject element, 
			String color) /*-{
	  element.style.background = color;
	}-*/;

	/**
	 * Set foreground color on given HTML element
	 * @param element
	 * @param color
	 */
	public static native void setColor(
			JavaScriptObject element, 
			String color) /*-{
	  element.style.color = color;
	}-*/;

	@Override
	public boolean isEditing() {
		return popup.isShowing();
	}

}
