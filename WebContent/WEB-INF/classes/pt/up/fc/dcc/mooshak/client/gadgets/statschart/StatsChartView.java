package pt.up.fc.dcc.mooshak.client.gadgets.statschart;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;

public interface StatsChartView extends View {
	
	public interface Presenter {
		String getProblemId();
		void updateStatistics();
	}
	
	void setPresenter(Presenter presenter);
	
	void updateStatistics(ProblemStatistics stats);
	
}
