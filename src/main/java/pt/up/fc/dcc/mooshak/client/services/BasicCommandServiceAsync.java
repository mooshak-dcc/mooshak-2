package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BasicCommandServiceAsync {

	void changeContest(String contest, AsyncCallback<Void> callback);

	void context(AsyncCallback<ContextInfo> callback);

	void getColumns(String Kind, AsyncCallback<List<ColumnInfo>> callback);

	void getContestId(AsyncCallback<String> callback);

	void getDomains(boolean listCreated, boolean listConcluded,
			AsyncCallback<Map<String, ResultsContestInfo>> callback);

	void initSession(AsyncCallback<Void> callback);

	void login(String domain, String user, String password,
			AsyncCallback<String> callback);

	void register(String domain, String user, String password,
			AsyncCallback<String> callback);

	void logout(AsyncCallback<Void> callback);

	void refreshRows(AsyncCallback<Void> callback);

	void validateCaptcha(String challenge, AsyncCallback<Boolean> callback);

	void isSessionAlive(AsyncCallback<Boolean> callback);

	void isLoggedInAsAdmin(AsyncCallback<Boolean> callback);

	void switchProfileBackToAdmin(AsyncCallback<Void> callback);

	void getPreferredLocale(AsyncCallback<String> callback);
	
	void getPreferredLocale(List<String> asList,AsyncCallback<String> callback);

	void getVersion(AsyncCallback<String> callback);

	void getProblemNameById(String problemId, AsyncCallback<String> callback);

	void diffStrings(String expected, String obtained, AsyncCallback<String> callback);

	void getScriptFile(String path, AsyncCallback<String> callback);

	void getTaskList(String script, AsyncCallback<List<List<String>>> callback);

}
