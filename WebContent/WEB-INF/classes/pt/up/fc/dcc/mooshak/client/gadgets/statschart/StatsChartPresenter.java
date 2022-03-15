package pt.up.fc.dcc.mooshak.client.gadgets.statschart;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.statschart.StatsChartView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;

public class StatsChartPresenter extends GadgetPresenter<StatsChartView> implements Presenter {
	
	public StatsChartPresenter(EnkiCommandServiceAsync enkiService,
			StatsChartView view, Token token) {
		
		super(null, null, null, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}
	
	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		//updateStatistics();
	}

	@Override
	public void updateStatistics() {
		enkiService.getProblemStatistics(problemId, 
				new AsyncCallback<ProblemStatistics>() {
			
			@Override
			public void onSuccess(ProblemStatistics result) {
				view.updateStatistics(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

}
