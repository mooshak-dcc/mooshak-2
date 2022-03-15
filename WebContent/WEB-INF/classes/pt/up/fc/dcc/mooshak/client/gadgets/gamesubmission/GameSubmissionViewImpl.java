package pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

public class GameSubmissionViewImpl extends ResizeComposite 
	implements GameSubmissionView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;

	private static GameSubmissionViewUiBinder uiBinder = GWT
			.create(GameSubmissionViewUiBinder.class);

	@UiTemplate("GameSubmissionView.ui.xml")
	interface GameSubmissionViewUiBinder extends
			UiBinder<Widget, GameSubmissionViewImpl> {}
	
	private ICPCConstants constants =
			GWT.create(ICPCConstants.class);
	
	private Presenter presenter = null;
	
	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;
	
	private Timer validateTimer = null;
	private long validateResetTime = -1;
	private int remainingValidations = -1;
	
	private int missingObs = 0;
	
	private Map<String, String> opponents;
	
	@UiField
	ResizableHtmlPanel panel;
	
	@UiField
	HTMLPanel commandsPanel;
	
	@UiField
	ListBox submissions;
	
	public GameSubmissionViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		configureSubmitTimer();	
		configureValidateTimer();

		panel.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				submissions.setHeight((event.getHeight() - commandsPanel.getOffsetHeight()) 
						+ "px");
			}
		});
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {				
				panel.onResize();
			}
		});
	}

	
	/**
	 * Configures the timer to update the validate tooltip
	 */
	private void configureValidateTimer() {
		validateTimer = new Timer() {
			
			@Override
			public void run() {
				String tooltip = "";
				if(remainingValidations >= 0) {
					tooltip += remainingValidations;
				}
				
				if(validateResetTime >= 0) {
					tooltip += "/";
					long diff = validateResetTime - new Date().getTime();
					
					if(diff <= 0) {
						tooltip += "00:00:00";
						validate.setTitle(tooltip);
						presenter.getValidationsTransactionsData();
						return;
					}
					
					tooltip += getCountdownLabel(diff);
				}
					
				if (tooltip.equals(""))
					validate.setTitle(validate.getTitle());
				else
					validate.setTitle(validate.getTitle() + "\n\n" +
							constants.submit() + " " + tooltip);
			}
		};
		
		validateTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	/**
	 * Configures the timer to update the submit tooltip
	 */
	private void configureSubmitTimer() {
		submitTimer = new Timer() {
			
			@Override
			public void run() {
				String tooltip = "";
				if(remainingSubmits >= 0) {
					tooltip += remainingSubmits;
				}
				
				if(submitResetTime >= 0) {
					tooltip += "/";
					long diff = submitResetTime - new Date().getTime();
					
					if(diff <= 0) {
						tooltip += "00:00:00";
						submit.setTitle(tooltip);
						presenter.getSubmissionsTransactionsData();
						return;
					}
					
					tooltip += getCountdownLabel(diff);
				}
					
				if (tooltip.equals(""))
					submit.setTitle(submit.getTitle());
				else
					submit.setTitle(submit.getTitle() + "\n\n" +
							constants.submit() + " " + tooltip);
			}
		};
		
		submitTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * Calculates a string in countdown timer form for a given
	 *  diff
	 * @param diff
	 * @return
	 */
	private String getCountdownLabel(long diff) {
		String label = "";
		
		int hours = (int) (diff / HOURS_IN_MILLIS);
		diff = diff % HOURS_IN_MILLIS;
		int minutes = (int) (diff / MINUTES_IN_MILLIS);
		diff = diff % MINUTES_IN_MILLIS;
		int seconds = (int) (diff / SECONDS_IN_MILLIS);
		
		if(hours < 10)
			label += "0" + hours;
		else
			label += hours;
		label += ":";
		if(minutes < 10)
			label += "0" + minutes;
		else
			label += minutes;
		label += ":";
		if(seconds < 10)
			label += "0" + seconds;
		else
			label += seconds;
		
		return label;
	}
	
	@Override
	public void setSubmitTooltip(int remaining, 
			long resetTime) {
		submitResetTime = resetTime;
		remainingSubmits = remaining;
	}
	
	@Override
	public void setValidateTooltip(int remaining, 
			long resetTime) {
		validateResetTime = resetTime;
		remainingValidations = remaining;
	}

	/* Command buttons */
	
	@UiField CustomImageButton submit;
	
	@UiHandler({"submit"})
	void submit(ClickEvent event) {
		presenter.onProgramEvaluate(true);
	}
	
	@UiField CustomImageButton validate;
	
	@UiHandler({"validate"})
	void validate(ClickEvent event) {
		presenter.onProgramEvaluate(false);
	}

	@Override
	public void increaseProgramWaitingEvaluation() {
		missingObs++;
		submit.setEnabled(false);
		validate.setEnabled(false);
	}

	@Override
	public void decreaseProgramWaitingEvaluation() {
		if (missingObs > 0) {
			missingObs--;
			submit.setEnabled(true);
			validate.setEnabled(true);
		}
		else
			Logger.getLogger("").log(Level.SEVERE, "An observation has been received "
					+ "and client was not waiting for it.");
	}

	@Override
	public List<String> getSelectedOpponents() {
		
		List<String> opponents = new ArrayList<>();
		for (int i = 0; i < submissions.getItemCount(); i++) {
			
			if (submissions.isItemSelected(i))
				opponents.add(submissions.getValue(i));
				
		}
		
		return opponents;
	}

	@Override
	public void setOpponents(Map<String, String> opponents) {
		this.opponents = opponents;
		updateList();
	}

	@Override
	public void addOpponent(String id, String teamId) {
		opponents.put(teamId, id);
		updateList();
	}
	
	private void updateList() {
		submissions.clear();
		for (String key : opponents.keySet()) {
			submissions.addItem(key, opponents.get(key));
		}
	}
}
