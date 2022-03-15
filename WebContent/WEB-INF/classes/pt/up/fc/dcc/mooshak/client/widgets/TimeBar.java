package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TimeBar  extends Composite  {
	
	DateTimeFormat FORMATTER = DateTimeFormat.getFormat(
			PredefinedFormat.HOUR24_MINUTE);

	@UiField
    SimplePanel elapsed;
	
	@UiField
	Label startLabel;
	
	@UiField
	Label endLabel; 

	private long startTime;
	private long endTime;
	
	private long difference = 0;
	private long duration;
	
	private int timerScheduleRepeating=30;
	
	
	private Timer timer = null;
	
	private static TimeBarUiBinder uiBinder = GWT
			.create(TimeBarUiBinder.class);
	
	interface TimeBarUiBinder extends
		UiBinder<Widget, TimeBar> {
	}

	public TimeBar() {
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
	public void initValues(Date start,Date end, Date current) {
		
		if(timer != null)
			timer.cancel();
		
		if(start != null)
			startLabel.setText(FORMATTER.format(start));
		
		if(end != null)
			endLabel.setText(FORMATTER.format(end));
		
		if(start == null || end == null) {
			Style style = elapsed.getElement().getStyle();
			
			style.setWidth(0.0D, Unit.PCT);
		} else {
			
			startTime = start.getTime();
			endTime = end.getTime();
			
			difference = new Date().getTime() - current.getTime();
			duration  = endTime - startTime;
			
			adjust();
		
			timer = new Timer() {

				@Override
				public void run() {
					adjust();
				}
			};
		
			timer.scheduleRepeating(timerScheduleRepeating*1000);
		}
	}

	Label waitLabel = null;

	private void adjust() {
		long serverTime = new Date().getTime() - difference;
		Style style = elapsed.getElement().getStyle();
		double percentage;
		
		if(waitLabel != null)
			waitLabel.removeFromParent();
		
		if(serverTime < startTime) {
			percentage = 0.0D;
			String wait = FORMATTER.format(new Date(startTime - serverTime));
			
			elapsed.add(waitLabel = new Label(" -"+wait));
			
		} else if(serverTime > endTime) { 
			
			percentage = 100.0D;
			if(timer != null)
				timer.cancel();
		} else
			percentage =  (serverTime - startTime)*100 / duration;
		
		style.setWidth(percentage, Unit.PCT);
	}


	public int getTimerScheduleRepeating() {
		return timerScheduleRepeating;
	}


	public void setTimerScheduleRepeating(int seconds) {
		this.timerScheduleRepeating = seconds;
	}
	
}
