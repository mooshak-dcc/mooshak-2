package pt.up.fc.dcc.mooshak.client.events;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {
	
	void getEvents(int serial, AsyncCallback<List<MooshakEvent>> callback );
	
	void getSerial(Date date,AsyncCallback<Integer> callback);
	
}
