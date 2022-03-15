package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import java.util.Arrays;
import java.util.Date;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.views.ListingImpl;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class AskViewImpl extends ListingImpl implements AskView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;

	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static AskUiBinder uiBinder = GWT.create(AskUiBinder.class);

	@UiTemplate("AskView.ui.xml")
	interface AskUiBinder extends UiBinder<Widget, AskViewImpl> {
	}
	
	private ICPCConstants constants =
			GWT.create(ICPCConstants.class);

	private Presenter presenter = null;

	private String problemId;
	private String problemLabel = null;

	@UiField
	Label idNameLabel;
	
	@UiField
	Label titleLabel;
	
	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	DataGrid<Row> grid;

	@UiField
	TextBox subject;

	@UiField
	TextArea question;

	@UiField
	TextArea answer;

	ListingDataProvider dataProvider;

	@UiField
	Label message;
	
	private Timer askTimer = null;
	private long askResetTime = -1;
	private int remainingAsks = -1;
	
	private int rows;

	public AskViewImpl(String problemId, int rows) {
		this.setProblemId(problemId);
		this.rows = rows;

		dataProvider = ListingDataProvider.getDataProvider(Kind.QUESTIONS);
		dataProvider.setDisplayRows(rows);
		
		configureGrid();
		initWidget(uiBinder.createAndBindUi(this));
		
		answer.setReadOnly(true);

		final SingleSelectionModel<Row> selectionModel = new SingleSelectionModel<Row>();
		grid.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						presenter.onSelectedItemChanged();
						clearSelectedItem();
					}
				});
		

		configureAskTimer();
	}

	public void setProgramIdentification(String id,String name) {
		idNameLabel.setText(id);
		titleLabel.setText(name);
	}
	
	/**
	 * Configures the timer to update the print tooltip
	 */
	private void configureAskTimer() {
		askTimer = new Timer() {
			
			@Override
			public void run() {
				String tooltip = "";
				if(remainingAsks >= 0) {
					tooltip += remainingAsks;
				}
				
				if(askResetTime >= 0) {
					tooltip += "/";
					long diff = askResetTime - new Date().getTime();
					
					if(diff <= 0) {
						tooltip += "00:00:00";
						ask.setTitle(tooltip);
						presenter.getQuestionsTransactionsData();
						return;
					}
					
					tooltip += getCountdownLabel(diff);
				}
					
				ask.setTitle(constants.ask() + " " + tooltip);
			}
		};
		
		askTimer.scheduleRepeating(SECONDS_IN_MILLIS);
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
	public void setMessage(String message, boolean result) {
		if(result)
			this.message.getElement().getStyle().setColor("orange");
		else 
			this.message.getElement().getStyle().setColor("red");
		
		this.message.setText(message);
		
		resetMessage();
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		new Timer() {

			@Override
			public void run() {
				message.setText("");
				cleanupTimer = null;
			}
			
		}.schedule(MESSAGE_VIEWING_TIME);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public String getQuestion() {
		return SimpleHtmlSanitizer.sanitizeHtml(question.getText())
				.asString();
	}

	@Override
	public void clearQuestion() {
		question.setText("");
	}

	@Override
	public String getSubject() {
		return SimpleHtmlSanitizer.sanitizeHtml(subject.getText())
				.asString();
	}

	@Override
	public void clearSubject() {
		subject.setText("");
	}

	@Override
	public void clearAnswer() {
		answer.setText("");
	}

	/* Command buttons */

	@UiField
	CustomImageButton ask;

	@UiHandler({ "ask" })
	void ask(ClickEvent event) {
		presenter.onAsk();
	}

	@UiField
	CustomImageButton clear;

	@UiHandler({ "clear" })
	void clear(ClickEvent event) {
		presenter.onClear();
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId
	 *            the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @return the problemLabel
	 */
	public String getProblemLabel() {
		return problemLabel;
	}

	/**
	 * @param problemLabel
	 *            the problemLabel to set
	 */
	public void setProblemLabel(String problemLabel) {
		this.problemLabel = problemLabel;
	}

	private void configureGrid() {

		grid = new DataGrid<Row>(rows, 
				Row.KEY_PROVIDER);
		grid.setWidth("100%");

		grid.setAutoHeaderRefreshDisabled(true);

		dataProvider.addDataDisplay(grid);

		pager = dataProvider.getPager();

		pager.setDisplay(grid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SingleSelectionModel<Row> getSelectionModel() {
		return (SingleSelectionModel<Row>) grid.getSelectionModel();
	}

	@Override
	public void clearSelectedItem() {
		Row selected = getSelectionModel().getSelectedObject();
		if (selected != null) {
			grid.getSelectionModel().setSelected(selected, false);
			grid.redraw();
		}
	}

	@Override
	public void setSubject(String subject) {
		this.subject.setText(new HTML(subject).getHTML().toString());
	}

	@Override
	public void setQuestion(String question) {
		this.question.setText(new HTML(question).getHTML().toString());
	}

	@Override
	public void setAnswer(String answer) {
		this.answer.setText(new HTML(answer).getHTML().toString());
	}
	
	@Override
	public void setAskTooltip(int remaining, 
			long resetTime) {
		askResetTime = resetTime;
		remainingAsks = remaining;
	}

	@Override
	public void setFiltering() {
		dataProvider.resetFilter();
		dataProvider.addFilter("problem", Arrays.asList(problemLabel, 
				"??", ListingDataProvider.NON_BREAKING_SPACE));
		//grid.setPageSize(dataProvider.getDisplayRows());
		pager.setDisplay(grid);
	}

	@Override
	public boolean getSortable() {
		return true;
	}

	@Override
	public ListingDataProvider getDataProvider() {
		return dataProvider;
	}

	@Override
	public DataGrid<Row> getDataGrid() {
		return grid;
	}

	@Override
	public SimplePager getPager() {
		return pager;
	}

}
