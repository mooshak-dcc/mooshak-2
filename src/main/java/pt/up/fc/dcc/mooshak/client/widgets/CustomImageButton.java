package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

/**
 * An Image that emulates a button
 * 
 * @author josepaiva
 */
public class CustomImageButton extends Image {

	private Set<ClickHandler> clickHandlers = new HashSet<>();

	private Set<HandlerRegistration> handlersRegist = new HashSet<>();

	private double overOpacity = 0.7;
	private double outOpacity = 1.0;
	private double clickOpacity = 0.4;
	private double disabledOpacity = 0.3;
	
	private boolean enabled = true;

	public CustomImageButton() {
		getElement().getStyle().setCursor(Cursor.POINTER);
		getElement().getStyle().setOpacity(outOpacity);

		// mouse is over
		addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(enabled)
					getElement().getStyle().setOpacity(overOpacity);
			}
		});

		// mouse is out
		addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(enabled)
					getElement().getStyle().setOpacity(outOpacity);
			}
		});

		// mouse is clicked
		addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if(enabled)
					getElement().getStyle().setOpacity(clickOpacity);
			}
			
		});
		
		// mouse is released
		addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				if(enabled)
					getElement().getStyle().setOpacity(overOpacity);
			}
			
		});

	}
	
	/**
	 * Emulate button enable/disable
	 * 
	 * @param value
	 */
	public void setEnabled(boolean value) {
		if (value) {
			if(!enabled) {
				getElement().getStyle().setOpacity(outOpacity);
	
				List<ClickHandler> list = new ArrayList<>(clickHandlers);
				for (ClickHandler clickHandler : list)
					addClickHandler(clickHandler);
				
				getElement().getStyle().setCursor(Cursor.POINTER);
			}
			
			enabled = true;
		} else {
			enabled = false;
			
			getElement().getStyle().setOpacity(disabledOpacity);
			
			for (HandlerRegistration handler : handlersRegist)
				handler.removeHandler();
			handlersRegist = new HashSet<>();
			
			getElement().getStyle().setCursor(Cursor.AUTO);
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		this.clickHandlers.add(handler);
		
		HandlerRegistration reg = super.addClickHandler(handler);
		handlersRegist.add(reg);
		
		return reg;
	}

	/**
	 * @return the overOpacity
	 */
	public double getOverOpacity() {
		return overOpacity;
	}

	/**
	 * @param overOpacity
	 *            the overOpacity to set
	 */
	public void setOverOpacity(double overOpacity) {
		this.overOpacity = overOpacity;
	}

	/**
	 * @return the outOpacity
	 */
	public double getOutOpacity() {
		return outOpacity;
	}

	/**
	 * @param outOpacity
	 *            the outOpacity to set
	 */
	public void setOutOpacity(double outOpacity) {
		this.outOpacity = outOpacity;
		
		if(enabled)
			getElement().getStyle().setOpacity(outOpacity);
	}

	/**
	 * @return the clickOpacity
	 */
	public double getClickOpacity() {
		return clickOpacity;
	}

	/**
	 * @param clickOpacity
	 *            the clickOpacity to set
	 */
	public void setClickOpacity(double clickOpacity) {
		this.clickOpacity = clickOpacity;
	}

	/**
	 * @return the disabledOpacity
	 */
	public double getDisabledOpacity() {
		return disabledOpacity;
	}

	/**
	 * @param disabledOpacity
	 *            the disabledOpacity to set
	 */
	public void setDisabledOpacity(double disabledOpacity) {
		this.disabledOpacity = disabledOpacity;
		
		if(!enabled)
			getElement().getStyle().setOpacity(disabledOpacity);
	}

}
