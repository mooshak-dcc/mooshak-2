package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.Date;

import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.events.ChangeDateEvent;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

public class CustomDateTimePicker extends DateTimePicker {
	private static final String PREFIX_ID = "date-time-picker-";
	
	static int index = 1;

	public CustomDateTimePicker() {
		super();
		setId(PREFIX_ID + (index++));
		hide();
	}
	
	@Override
	public void onShow(Event e) {
		super.onShow(e);
	}
	
	@Override
	protected void configure() {
		super.configure();
		showTodayNowButtons();
	}

	/**
	 * Show now button
	 */
	public void showTodayNowButtons() {
		showTodayNowButtons(this, getElement());
	}

	private static native void showTodayNowButtons(CustomDateTimePicker t, Element e) /*-{
		
		$wnd.setVal = @pt.up.fc.dcc.mooshak.client.widgets.CustomDateTimePicker::setValueFromBtn(Lpt/up/fc/dcc/mooshak/client/widgets/CustomDateTimePicker;Lcom/google/gwt/core/client/JsDate;Z);
		
		var dp = $wnd.jQuery(e).datetimepicker();
		var els = $doc.querySelectorAll(".datetimepicker tfoot");
		
		els.forEach(function(el) {
			
			if (el.children.length > 2)
				return;
			
			var tr = document.createElement("tr");
			var now = document.createElement("th");
			now.innerHTML = "Now";
			now.setAttribute("colspan", 10);
			now.addEventListener("click", function(){
				$wnd.setVal(t, new Date(), true);
			});
			tr.appendChild(now);
			el.appendChild(tr);
			
			var tr = document.createElement("tr");
			var today = document.createElement("th");
			today.innerHTML = "Today";
			today.setAttribute("colspan", 10);
			today.addEventListener("click", function(){
				var date = new Date();
				$wnd.setVal(t, new Date(date.getFullYear(), date.getMonth(), date.getDate(), 
					0, 0, 0, 0), true);
			});
			tr.appendChild(today);
			el.appendChild(tr);
		});
	}-*/;
	
	public static void setValueFromBtn(CustomDateTimePicker t, JsDate value, boolean fireEvents) {
		t.setValue(new Date((long) value.getTime()), fireEvents);
		t.fireEvent(new ChangeDateEvent(null));
	}

}
