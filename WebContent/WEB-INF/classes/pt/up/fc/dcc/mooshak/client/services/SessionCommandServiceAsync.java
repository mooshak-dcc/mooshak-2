package pt.up.fc.dcc.mooshak.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SessionCommandServiceAsync {

	void getUserSessionTimeout(AsyncCallback<Integer> callback);

	void isSessionAlive(AsyncCallback<Boolean> callback);

	void ping(AsyncCallback<Void> callback);

}
