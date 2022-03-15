package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.statschart.StatsChartPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.statschart.StatsChartView;
import pt.up.fc.dcc.mooshak.client.gadgets.statschart.StatsChartViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

public class StatsChart extends Gadget {

	public StatsChart(EnkiCommandServiceAsync rpc, Token token, GadgetType type) {
		super(token, type);

		StatsChartView view = new StatsChartViewImpl();

		StatsChartPresenter presenter = new StatsChartPresenter(rpc, view, token);
		presenter.go(null);

		setView(view);
		setPresenter(presenter);
	}

	@Override
	public String getName() {
		return CONSTANTS.statistics();
	}
}
