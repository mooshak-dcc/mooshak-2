package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StatisticsViewImpl extends Composite 
	implements StatisticsView {

	private static final int MESSAGE_VIEWING_TIME = 15*1000;

	private static StatisticsUiBinder uiBinder = GWT.create(StatisticsUiBinder.class);

	@UiTemplate("StatisticsView.ui.xml")
	interface StatisticsUiBinder extends UiBinder<Widget, StatisticsViewImpl> {
	}

	private Presenter presenter;
	
	@UiField
	ContestProgressLineChart progressChart;
	
	@UiField
	SubmissionsPiledBarCharts submissionsChart;
	
	@UiField
	Label message;
	
	private int waitingFor = 2;

	public StatisticsViewImpl() {

		initWidget(uiBinder.createAndBindUi(this));
		progressChart.addInitializeHandler(new InitializeHandler() {
			
			@Override
			public void onInitialize(InitializeEvent event) {
				waitingFor--;
				if (waitingFor <= 0)
					clearMessage();
			}
		});
		submissionsChart.addInitializeHandler(new InitializeHandler() {
			
			@Override
			public void onInitialize(InitializeEvent event) {
				waitingFor--;
				if (waitingFor <= 0)
					clearMessage();
			}
		});
		
		setMessage("Loading ...");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public void setMessage(String text) {
		message.setText(text);
		message.getElement().getStyle().setDisplay(Display.BLOCK);
		resetMessage();
		
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		cleanupTimer = new Timer() {

			@Override
			public void run() {
				clearMessage();
				
				if (waitingFor > 0)
					setMessage("There was a problem loading charts.\n"
							+ "Please check your connection!");
			}
			
		};
		cleanupTimer.schedule(MESSAGE_VIEWING_TIME);
	}

		
	private void clearMessage() {
		message.setText("");
		message.getElement().getStyle().setDisplay(Display.NONE);
		cleanupTimer = null;
	}
}
