package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.events.EventService;
import pt.up.fc.dcc.mooshak.client.events.EventServiceAsync;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;

public class ReplayEventServiceCaller extends ReplayServiceCaller {
	private static EventServiceAsync eventService = GWT.create(EventService.class);
	
	private static int serial;

	public static void getSerial(String date) {

		logMessage("Executing getSerial: " + date);
		eventService.getSerial(new Date(), new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getSerial failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						logMessage("getSerial succeeded: " + result.intValue());
						
						serial = result.intValue();
					}
				});
	}

	public static void getEvents(String serialStr) {

		logMessage("Executing getEvents: " + serialStr);
		eventService.getEvents(serial, new AsyncCallback<List<MooshakEvent>>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getEvents failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<MooshakEvent> result) {
						logMessage("getEvents succeeded");
					}
				});
	}
}
