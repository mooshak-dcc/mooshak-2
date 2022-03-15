package pt.up.fc.dcc.mooshak.client.guis.runner.view;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TabButton;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.client.widgets.ContestSelector;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.TimeBar;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopLevelViewImpl extends Composite implements TopLevelView {

	private static RunnerUiBinder uiBinder = GWT.create(RunnerUiBinder.class);

	@UiTemplate("TopLevelView.ui.xml")
	interface RunnerUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}
	
	@UiField ContestSelector contestsel;
	
	@UiField HTMLPanel actions;

	@UiField CardPanel content;
	
	@UiField
	TimeBar timeBar;
	
	private Presenter presenter = null;

	public TopLevelViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		pending.select();
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
	public CardPanel getContent() {
		return content;
	}
	
	public void setContest(String name) {
		contestsel.setSelectedId(name);
	}
	
	public void setContestSelector(List<SelectableOption> contests) {
		contestsel.setSelections(contests);
	}
	
	@UiField TabButton printouts;
	
	@UiHandler("printouts")
	void printouts(ClickEvent e) {
		TabButton.deselect("problems","actions");
		if(presenter != null)
			presenter.onListingClicked(Kind.PRINTOUTS);
	}
	
	@UiField TabButton balloons;
	
	@UiHandler("balloons")
	void balloons(ClickEvent e) {
		TabButton.deselect("problems","actions");
		if(presenter != null)
			presenter.onListingClicked(Kind.BALLOONS);
	}
	
	@UiField TabButton rankings;
	
	@UiHandler("rankings")
	void rankings(ClickEvent e) {
		TabButton.deselect("problems","actions");
		if(presenter != null)
			presenter.onListingClicked(Kind.RANKINGS);
	}
	
	@UiField TabButton pending;
	
	@UiHandler("pending")
	void pending(ClickEvent e) {
		TabButton.deselect("problems","actions");
		if(presenter != null)
			presenter.onListingClicked(Kind.PENDING);
	}
	
	@UiHandler("contestsel")
	void contestsel(ChangeEvent e) {
		TabButton.deselect("problems","actions","listings");
		if(presenter != null)
			presenter.onContestSelectedChanged(contestsel.getSelectedOption().getId());
	}
	
	@UiField
	CustomImageButton logout;
	
	@UiHandler("logout")
	void logout(ClickEvent e) {
		if (presenter != null)
			presenter.onLogoutClicked();
	}

	@Override
	public void setDates(Date start, Date end, Date current) {
		timeBar.initValues(start, end, current);
		
	}
}
