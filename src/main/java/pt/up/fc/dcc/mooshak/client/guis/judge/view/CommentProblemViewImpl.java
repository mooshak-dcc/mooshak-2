package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CommentProblemViewImpl extends Composite implements
		CommentProblemView {

	private static final int MESSAGE_VIEWING_TIME = 5*1000;
	
	private static CommentProblemUiBinder uiBinder = GWT.create(CommentProblemUiBinder.class);

	@UiTemplate("CommentProblemView.ui.xml")
	interface CommentProblemUiBinder extends UiBinder<Widget, CommentProblemViewImpl> {}
	
	private Presenter presenter = null;
	
	// holds selected problem
	private String problemSelected;
	
	// radiobuttons of the problems
	private Map<String, RadioButton> rdProblems = new HashMap<>();
	
	@UiField
	TextBox subject;
	
	@UiField
	TextArea comment;
	
	@UiField
	HTMLPanel problems;
	
	@UiField
	Label message;
	
	
	public CommentProblemViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public String getSubject() {
		return subject.getText();
	}

	@Override
	public String getComment() {
		return comment.getText();
	}

	@Override
	public String getProblem() {
		if(problemSelected != null && problemSelected.equals(""))
			return null;
		return problemSelected;
	}

	@Override
	public void setProblems(List<SelectableOption> problemOptions) {
		
		ClickHandler handler = new ClickHandler() {
	
			@Override
			public void onClick(ClickEvent event) {
				RadioButton problem = (RadioButton) event.getSource();
				problemSelected = problem.getText();
			}
		};
		
		for(SelectableOption option: problemOptions) {
			RadioButton problem = new RadioButton("problems", option.getLabel());
			
			problem.setStyleName("problem");
			problem.addClickHandler(handler);
			problems.add(problem);
			rdProblems.put(option.getLabel(), problem);
		}
	}

	@Override
	public void setComment(String comment) {
		this.comment.setText(comment);
	}

	@Override
	public void setSubject(String subject) {
		this.subject.setText(subject);
	}

	@Override
	public void clearSelectedProblem() {
		if(problemSelected.equals(""))
			return;
		RadioButton problem = rdProblems.get(problemSelected);
		problem.setValue(false);
		problemSelected = "";
	}
	
	/* Command button */
	
	@UiField
	Button submit;
	
	@UiHandler(value = { "submit" })
	void submit(ClickEvent event) {
		presenter.onComment();
	}
	
	@UiField
	Button clear;
	
	@UiHandler(value = { "clear" })
	void clear(ClickEvent event) {
		presenter.onClear();
	}
	
	@Override
	public void setMessage(String text) {
		message.setText(text);
		
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

}
