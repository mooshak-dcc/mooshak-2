package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

public class ReplayBasicServiceCaller extends ReplayServiceCaller {
	private static BasicCommandServiceAsync basicService = 
			GWT.create(BasicCommandService.class);
	
	public static void login(String domain, String user, String password) {
		
		logMessage("Executing login " + domain + " " + user + " " + password);
		basicService.login(domain, user, password, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				logMessage("login succeeded: " + result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("login failed: " + caught.getMessage());
			}
		});
	}
	
	public static void changeContest(String contest) {
		
		logMessage("Executing changeContest " + contest);
		basicService.changeContest(contest, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("changeContest failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("changeContest succeeded");
			}
		});
	}
	
	public static void context() {
		
		logMessage("Executing context");
		basicService.context(new AsyncCallback<ContextInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("context failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ContextInfo result) {
				logMessage("context succeeded");
			}
		});
	}
	
	public static void getColumns(String kind) {
		
		logMessage("Executing getColumns " + kind);
		basicService.getColumns(kind, new AsyncCallback<List<ColumnInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getColumns failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<ColumnInfo> result) {
				logMessage("getColumns succeeded");
			}
		});
	}
	
	public static void getContestId() {
		
		logMessage("Executing getContestId");
		basicService.getContestId(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				logMessage("getContestId succeeded: " + result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("getContestId failed: " + caught.getMessage());
			}
		});
	}
	
	public static void getDomains(String listCreated, String listConcluded) {
		
		logMessage("Executing getDomains " + listCreated + " " + listConcluded);
		basicService.getDomains(Boolean.parseBoolean(listCreated), 
				Boolean.parseBoolean(listConcluded), 
				new AsyncCallback<Map<String,ResultsContestInfo>>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getDomains failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Map<String, ResultsContestInfo> result) {
						logMessage("getDomains succeeded");
					}
				});
	}
	
	public static void initSession() {
		
		logMessage("Executing initSession");
		basicService.initSession(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				logMessage("initSession succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("initSession failed: " + caught.getMessage());
			}
		});
	}
	
	public static void register(String domain, String user, String password) {

		logMessage("Executing register " + domain + " " + user + " " + password);
		basicService.register(domain, user, password, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				logMessage("register succeeded: " + result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("register failed: " + caught.getMessage());
			}
		});
	}
	
	public static void logout() {

		logMessage("Executing logout");
		basicService.logout(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				logMessage("logout succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("logout failed: " + caught.getMessage());
			}
		});
	}
	
	public static void refreshRows() {

		logMessage("Executing refreshRows");
		basicService.refreshRows(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("refreshRows failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("refreshRows succeeded");
			}
		});
	}
	
	public static void validateCaptcha(String challenge) {

		logMessage("Executing validateCaptcha");
		basicService.validateCaptcha(challenge, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("validateCaptcha failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("validateCaptcha succeeded: " + Boolean.toString(result));
			}
		});
	}
	
	public static void isSessionAlive() {

		logMessage("Executing isSessionAlive");
		basicService.isSessionAlive(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("isSessionAlive failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("isSessionAlive succeeded: " + Boolean.toString(result));
			}
		});
	}
	
	public static void isLoggedInAsAdmin() {

		logMessage("Executing isLoggedInAsAdmin");
		basicService.isLoggedInAsAdmin(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("isLoggedInAsAdmin failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("isLoggedInAsAdmin succeeded: " + Boolean.toString(result));
			}
		});
	}
	
	public static void switchProfileBackToAdmin() {

		logMessage("Executing switchProfileBackToAdmin");
		basicService.switchProfileBackToAdmin(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("switchProfileBackToAdmin failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("switchProfileBackToAdmin succeeded");
			}
		});
	}
	
	public static void getPreferredLocale() {

		logMessage("Executing getPreferredLocale");
		basicService.getPreferredLocale(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getPreferredLocale failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getPreferredLocale succeeded: " + result);
			}
		});
	}
	
	public static void getPreferredLocale(String asList) {
// TODO:
		logMessage("Executing getPreferredLocale");
		basicService.getPreferredLocale(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getPreferredLocale failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getPreferredLocale succeeded: " + result);
			}
		});
	}
	
	public static void getVersion() {

		logMessage("Executing getVersion");
		basicService.getVersion(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				logMessage("getVersion succeeded: " + result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("getVersion failed: " + caught.getMessage());
			}
		});
	}
	
	public static void getProblemNameById(String problemId) {

		logMessage("Executing getProblemNameById");
		basicService.getProblemNameById(problemId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProblemNameById failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getProblemNameById succeeded: " + result);
			}
		});
	}
	
	public static void diffStrings(String expected, String obtained) {

		logMessage("Executing diffStrings");
		basicService.diffStrings(expected, obtained, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				logMessage("diffStrings succeeded: " + result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("diffStrings failed: " + caught.getMessage());
			}
		});
	}
	
}
