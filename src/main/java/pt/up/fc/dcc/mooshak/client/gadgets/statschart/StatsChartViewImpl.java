package pt.up.fc.dcc.mooshak.client.gadgets.statschart;

import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class StatsChartViewImpl extends ResizeComposite implements
		StatsChartView {
	
	EnkiConstants constants = GWT.create(EnkiConstants.class);

	private Presenter presenter = null;
	
	private PieChart pieChart = null;
	private PieOptions options = null;
	private DataTable dataTable = null;

	public StatsChartViewImpl() {

		final ResizableHtmlPanel panel = new ResizableHtmlPanel("");
		panel.setHeight("100%");
		panel.setWidth("100%");
		
		panel.addResizeHandler(new ResizeHandler() {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					
					if (pieChart == null)
						return;

					pieChart.draw(dataTable, options);
				}
			};
			
			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		});

		VisualizationUtils.loadVisualizationApi(new Runnable() {
			public void run() {
		
				dataTable = (DataTable) createDefaultTable();
				options = createOptions();
		
				// Create a pie chart visualization.
				pieChart = new PieChart(dataTable, options);
				pieChart.setHeight("100%");
				pieChart.setWidth("100%");
				panel.add(pieChart);

				presenter.updateStatistics();				
			}
		},	PieChart.PACKAGE);

		initWidget(panel);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * Creates the default options
	 * @return
	 */
	private PieOptions createOptions() {
		PieOptions options = PieOptions.create();
		options.set("width", "100%");
		options.set("height", "100%");
		options.set3D(true);
		options.setTitle(constants.problem() + " " + presenter.getProblemId() 
				+ ": " + constants.statistics());
		return options;
	}

	/**
	 * Creates the default data table
	 * @return
	 */
	private AbstractDataTable createDefaultTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Task");
		data.addColumn(ColumnType.NUMBER, "Tries");
		data.addRows(5);
		data.setValue(0, 0, constants.solvedAt1st());
		data.setValue(0, 1, 1);
		data.setValue(1, 0, constants.solvedAt2nd());
		data.setValue(1, 1, 1);
		data.setValue(2, 0, constants.solvedAt3rd());
		data.setValue(2, 1, 1);
		data.setValue(3, 0, constants.solvedAt4thMore());
		data.setValue(3, 1, 1);
		data.setValue(4, 0, constants.notSolved());
		data.setValue(4, 1, 0);
		return data;
	}
	
	/**
	 * Updates the data of the statistics
	 * @param stats
	 */
	@Override
	public void updateStatistics(ProblemStatistics stats) {
		if (dataTable == null || pieChart == null)
			return;
		dataTable.setValue(0, 1, stats.getSolvedAtFirst());
		dataTable.setValue(1, 1, stats.getSolvedAtSecond());
		dataTable.setValue(2, 1, stats.getSolvedAtThird());
		dataTable.setValue(3, 1, stats.getNumberOfStudentsWhoSolved() 
				- (stats.getSolvedAtFirst() + stats.getSolvedAtSecond()
				+ stats.getSolvedAtThird()));
		dataTable.setValue(4, 1, stats.getNumberOfStudentsWhoTried() -
				stats.getNumberOfStudentsWhoSolved());
		
		pieChart.draw(dataTable, options);
	}

}
