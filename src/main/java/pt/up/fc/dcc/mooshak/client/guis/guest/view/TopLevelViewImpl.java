package pt.up.fc.dcc.mooshak.client.guis.guest.view;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TabButton;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.client.widgets.ContestSelector;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopLevelViewImpl extends Composite 
	implements TopLevelView {

	private static ScoreBoardUiBinder uiBinder = 
			GWT.create(ScoreBoardUiBinder.class);

	@UiTemplate("TopLevelView.ui.xml")
	interface ScoreBoardUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}


	@UiField
	HTMLPanel basePanel;

	/*@UiField
	SpanElement contest;*/
	
	@UiField
	CardPanel ranking;
	
	@UiField
	CardPanel submission;

	@UiField
	ContestSelector contestsel;

	private Presenter presenter = null;

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
	public CardPanel getRankingPanel() {
		return ranking;
	}

	@Override
	public CardPanel getSubmissionPanel() {
		return submission;
	}

	public void setContest(String name) {
		contestsel.setSelectedId(name);
	}

	@Override
	public void setContestSelector(List<SelectableOption> contests) {
		contestsel.setSelections(contests);
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

}
