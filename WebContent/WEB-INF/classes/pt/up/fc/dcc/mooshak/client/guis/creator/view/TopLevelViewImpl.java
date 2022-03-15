package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TabButton;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.client.widgets.ContestSelector;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.DropFileSupportHandler;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class TopLevelViewImpl extends DropFileSupportHandler implements TopLevelView {
	
	private static final int MESSAGE_VIEWING_TIME = 5*1000;
	
	private static CreatorUiBinder uiBinder = GWT.create(CreatorUiBinder.class);

	@UiTemplate("TopLevelView.ui.xml")
	interface CreatorUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}

	interface BaseStyle extends CssResource {

		String globalMoveCursor();
	}
	
	@UiField
	BaseStyle style;
	
	private String problemsObjectId;

	@UiField
	ContestSelector contestsel;

	@UiField
	ProblemSelector problems;

	@UiField
	CardPanel content;
	
	@UiField
	Label message;

	private Presenter presenter = null;

	private String problemId = "";

	public TopLevelViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		new Timer() {
			
			@Override
			public void run() {
				contestsel.setWidth((contestsel.getElement().getParentElement().getOffsetWidth() - 
						contestsel.getElement().getParentElement().getFirstChildElement()
						.getOffsetWidth() - 25) + "px");
			}
		}.schedule(1000);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Presenter getPresenter() {
		return this.presenter;
	}

	@Override
	public String getProblemsObjectId() {
		return problemsObjectId;
	}

	@Override
	public void setProblemsObjectId(String problemsObjectId) {
		this.problemsObjectId = problemsObjectId;
	}

	@Override
	public CardPanel getContent() {
		return content;
	}

	public void setContest(String name) {
		contestsel.setSelectedId(name);
	}

	public void setContestSelector(List<SelectableOption> contests) {
		contests.add(0, new SelectableOption("", ""));
		contestsel.setSelections(contests);
	}
	
	@Override
	public void setProblems(List<SelectableOption> problemOptions) {
		
		problems.setSelections(problemOptions);
		problems.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> event) {
				if (event.getValue() != null)
					view(event.getValue().getId(), event.getValue().getLabel());
			}
		});
		
		if(!problemId.equals(""))
			problems.selectProblem(problemId.replace(problemsObjectId, ""));
	}

	@Override
	public void addProblemId(String problemId) {
		problems.addProblemId(problemId);
	}

	/**
	 * Updates the label of the tab
	 * @param id
	 * @param tabName
	 */
	public void updateTabName(String id, String tabName) {
		problems.updateTabName(id, tabName);
	}
	
	@Override
	public void selectProblem(String problem) {
		problems.selectProblem(problem);
	}

	@Override
	public String getProblemId() {
		return problemId;
	}

	@Override
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * Fires a view problem event
	 * 
	 * @param id
	 * @param text
	 */
	private void view(String id, String text) {
		if (presenter != null)
			presenter.onViewProblemClicked(id, text);
	}
	
	@Override
	public void setMessage(String text, boolean expires) {
		message.setText(text);
		
		if (expires)
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

	/* Top Events */

	@UiField
	TabButton add;

	@UiHandler("add")
	void add(ClickEvent e) {
		setProblemId("");
		if (presenter != null)
			presenter.onAddProblemClicked(problemsObjectId);
	}

	@UiField
	TabButton delete;

	@UiHandler("delete")
	void delete(ClickEvent e) {
		
		if ("".equals(problemId)) 
			return;

		final SelectableOption last = problems.getSelectedOption();
		problems.setSelectedValue(last);
		
		new OkCancelDialog("Do you really want to delete problem " 
				+ problemId + "?") {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (presenter != null) 
					presenter.onDeleteProblemClicked(problemId);
				
				setProblemId("");
			}
		});
		
	}

	@UiHandler("contestsel")
	void contestsel(ChangeEvent e) {
		TabButton.deselect("problems");
		if (presenter != null)
			presenter.onContestSelectedChanged(contestsel.getSelectedOption()
					.getId());
	}
	
	@UiField
	CustomImageButton logout;
	
	@UiHandler("logout")
	void logout(ClickEvent e) {
		if (presenter != null)
			presenter.onLogoutClicked();
	}
	
	@Override
	public void setWaitingCursor() {
		RootPanel.getBodyElement().addClassName(style.globalMoveCursor());
	}
	
	@Override
	public void unsetWaitingCursor() {
		RootPanel.getBodyElement().removeClassName(style.globalMoveCursor());
	}

	@Override
	public void onFileDropped(String content, String name) {
		if (name.endsWith(".zip") || name.endsWith(".rar")
				|| name.endsWith(".tar.gz"))
			presenter.uploadFile(name, Base64Coder.decode(content));
	}
	
	/**
	 * Receive load notification to set JS event handlers
	 */
	protected void onLoad() {
		int id;

		synchronized (pool) {
			pool.add(this);
			id = pool.size() - 1;
		}

		newFileContentJS(id, null, null, RootPanel.getBodyElement(),
				content.getElement());

		final int idf = id;
		RootPanel.get().addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				// stop default behaviour
				event.preventDefault();
				event.stopPropagation();

				// starts the fetching, reading and callbacks
				dropHandlerSupport(event.getDataTransfer(), idf);
			}
		}, DropEvent.getType());
	}
}
