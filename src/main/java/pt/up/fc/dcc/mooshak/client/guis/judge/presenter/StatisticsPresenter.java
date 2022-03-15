package pt.up.fc.dcc.mooshak.client.guis.judge.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.guis.judge.view.StatisticsView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.StatisticsView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;

public class StatisticsPresenter implements Presenter, 
	pt.up.fc.dcc.mooshak.client.Presenter {

	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private StatisticsView view;
	
	public StatisticsPresenter(BasicCommandServiceAsync rpcBasic, 
			StatisticsView view, HandlerManager eventBus) {
		this.rpcBasic = rpcBasic;
		this.view = view;
		this.eventBus = eventBus;

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {

		setDependentData();
	}

	private void setDependentData() {
		// TODO Auto-generated method stub
		
	}

}
