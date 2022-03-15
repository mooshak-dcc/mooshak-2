package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ReplayEntryPoint implements EntryPoint {
	private static BasicCommandServiceAsync basicService = GWT.create(BasicCommandService.class);
	
	private TextArea logArea = new TextArea();
	
	
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		
		Style style = logArea.getElement().getStyle();
		style.setWidth(500, Unit.PX);
		style.setHeight(300, Unit.PX);
		style.setDisplay(Display.BLOCK);
		style.setProperty("margin", "50px auto");
		style.setProperty("resize", "none");
		logArea.setReadOnly(true);
		RootPanel.get().add(logArea);
		
		ReplayServiceCaller.setLogArea(logArea);

		String auditFile = Window.Location.getParameter("script");
		basicService.getScriptFile(auditFile, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				
				replayScript(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				logArea.setText(caught.getMessage());
			}
		});
		
	}
			
	/**
	 * Replay a script
	 * 
	 * @param script
	 * @throws IOException 
	 */
	private void replayScript(String script) {
		
		basicService.getTaskList(script, new AsyncCallback<List<List<String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				logArea.setText(caught.getMessage());
			}

			@Override
			public void onSuccess(List<List<String>> result) {
				try {
					replayScriptCont(result);
				} catch (Exception e) {
					logArea.setText(e.getMessage());
				}
			}
		});
		
	}

	private void replayScriptCont(List<List<String>> result) {
		long realStartDateMillis = new Date().getTime();
		long virtualStartDateMillis = -1;
		
		for (List<String> group : result) {
			final String dateStr = group.get(1);
			final String timeStr = group.get(2);
			final String profile = group.get(3);
			final String sessionId = group.get(4);
			final String serviceMethod = group.get(5);
			
			final List<String> args = new ArrayList<String>();
			for (int i = 6; i < group.size(); i++) {
				args.add(group.get(i));
			}

			DateTimeFormat df = DateTimeFormat.getFormat("yyyy-MM-dd hh:mm:ss");
			Date logDate = df.parse(dateStr + " " + timeStr);
				
			if (virtualStartDateMillis == -1)
				virtualStartDateMillis = logDate.getTime();
			
			Timer task = new Timer() {
				
				@Override
				public void run() {
					String[] argsArr = new String[args.size()];
					argsArr = args.toArray(argsArr);
					try {
						ReplayServiceCaller.callServiceMethod(serviceMethod, argsArr);
					} catch (Exception e) {
						ReplayServiceCaller.logMessage("Error invoking method: " +
								e.getMessage());
					}
				}
			};
			task.schedule((int) Math.max((logDate.getTime() - virtualStartDateMillis) - 
					(new Date().getTime() - realStartDateMillis), 0));
		}
		
	}
	
}
