package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.Date;

import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.constants.DateTimePickerFormatViewType;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.constants.DateTimePickerView;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.DialogContent;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Dialog with selector for time
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TimeSelectionDialog extends OkCancelDialog {

	public TimeSelectionDialog(DialogContent content) {
		super(content);
	}

	public TimeSelectionDialog(DialogContent content, String text) {
		super(content, text);
	}

	public TimeSelectionDialog(String text) {
		super(new TimeSelectorDialogContent(), text);
	}
	
	/**
	 * Dialog content with selector for time
	 * 
	 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
	 */
	static class TimeSelectorDialogContent extends Composite implements DialogContent {
		
		private HTMLPanel base;
		
		private DateTimePicker dateTimePicker;
		
		public TimeSelectorDialogContent() {
			base = new HTMLPanel("");
			initWidget(base);
			
			dateTimePicker = new DateTimePicker();
			dateTimePicker.setStartView(DateTimePickerView.DAY);
			dateTimePicker.setMaxView(DateTimePickerView.DAY);
			dateTimePicker.setMinView(DateTimePickerView.HOUR);
			dateTimePicker.setFormatViewType(DateTimePickerFormatViewType.TIME);
			dateTimePicker.setFormat("hh:ii");
			dateTimePicker.setAutoClose(true);
			
			dateTimePicker.getElement().getStyle().setWidth(150, Unit.PX);
			dateTimePicker.getElement().getStyle().setMarginLeft(75, Unit.PX);
			dateTimePicker.getElement().getStyle().setMarginRight(75, Unit.PX);
			
			base.add(dateTimePicker);
		}

		@Override
		public MethodContext getContext() {
			MethodContext ctx = new MethodContext();
			
			ctx.addPair("time", Long.toString(dateTimePicker.getValue().getTime()));
			
			return ctx;
		}

		@Override
		public void setContext(MethodContext ctx) {
			
			String maxTimeStr = ctx.getValue("max_time");
			if (maxTimeStr != null) {
				
				long maxTime = Long.parseLong(maxTimeStr);
				dateTimePicker.setEndDate(new Date(maxTime));
			}
		}

		@Override
		public String getWidth() {
			return "400px";
		}

		@Override
		public String getHeight() {
			return "200px";
		}
		
	}

}
