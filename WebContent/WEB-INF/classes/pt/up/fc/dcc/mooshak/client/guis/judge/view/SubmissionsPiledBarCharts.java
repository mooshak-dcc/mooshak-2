package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.BarChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;

public class SubmissionsPiledBarCharts extends HTMLPanel
		implements HasInitializeHandlers {
	private static final int TIMED_UPDATE_MILLIS = 1500;
	private static final String GREEN_HEX_CODE = "#109618";
	private static final String[] REDS_HEX_CODE = { "#CC3333", "#EE2A2A", "#BB2222", "#FF7F50", "#FF6347", "#FF0000",
			"#FF4500" };
	
	public enum Classification { 
		ACCEPTED(1),		      
		PRESENTATION_ERROR(2),		
		WRONG_ANSWER(3),
		EVALUATION_SKIPPED(4),
		OUTPUT_LIMIT_EXCEEDED(5),
		MEMORY_LIMIT_EXCEEDED(6),
		TIME_LIMIT_EXCEEDED(7),
		INVALID_FUNCTION(8),
		INVALID_EXIT_VALUE(9),
		RUNTIME_ERROR(10),
		COMPILE_TIME_ERROR(11),
		INVALID_SUBMISSION(12),
		PROGRAM_SIZE_EXCEEDED(13),
		REQUIRES_REEVALUATION(14),
		EVALUATING(15);
		
		private int order;

		private Classification(int order) {
			this.order = order;
		}

		public int getOrder() {
			return order;
		}
	}

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	
	private List<InitializeHandler> initHandlers = 
			new ArrayList<InitializeHandler>();

	private BarChart chart = null;
	private Options options = null;
	private DataTable dataTable = null;

	private ListingDataProvider dataProvider;

	private Map<String, Integer> classificationCount = new HashMap<String, Integer>();
	
	private JSONObject colorsOption = new JSONObject();
	
	private boolean initialized = false;

	public SubmissionsPiledBarCharts(SafeHtml safeHtml) {
		super(safeHtml);

		init();
	}

	public SubmissionsPiledBarCharts(String tag, String html) {
		super(tag, html);

		init();
	}

	public SubmissionsPiledBarCharts(String html) {
		super(html);

		init();
	}

	private void init() {
		dataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);

		setHeight("100%");

		VisualizationUtils.loadVisualizationApi(new Runnable() {
			public void run() {

				initDataTable();
				initOptions();

				// Create a bar chart visualization.
				chart = new BarChart(dataTable, options);
				chart.setHeight("100%");
				chart.setWidth("100%");
				add(chart);

				SubmissionsPiledBarChartDisplay display = new SubmissionsPiledBarChartDisplay(chart.getElement(),
						Integer.MAX_VALUE, dataProvider.getKeyProvider());
				dataProvider.addDataDisplay(display);

				// Draw the chart
				chart.draw(dataTable, options);
			}
		}, CoreChart.PACKAGE);

	}

	private void initOptions() {
		options = BarChart.createOptions();
		options.setTitle(constants.submissionsResultsChartTitle());
		options.setIsStacked(true);
		options.setLineWidth(4);
		options.setLegend(LegendPosition.RIGHT);
		options.set("bars", "horizontal");
	}

	private void initDataTable() {
		dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Problem");
	}

	public void insertSubmissionRow(Map<String, String> data) {

		if (dataTable == null || data == null)
			return;

		String classify = data.get("classification");
		String problem = data.get("problem");
		String key = problem + "." + classify;
		if (classificationCount.get(key) != null) {
			classificationCount.put(key, classificationCount.get(key) + 1);

			int columnIndex = dataTable.getColumnIndex(classify);
			for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
				String tmp = dataTable.getValueString(i, 0);
				if (tmp.equalsIgnoreCase(problem)) {
					dataTable.setValue(i, columnIndex, classificationCount.get(key));
					break;
				}
			}
		} else {

			int columnIndex = dataTable.getColumnIndex(classify);
			if (columnIndex < 0) {
				dataTable.addColumn(ColumnType.NUMBER, classify);
				columnIndex = dataTable.getColumnIndex(classify);

				JSONObject o2 = new JSONObject();
				if (classify.equalsIgnoreCase("accepted")) {
					o2.put("color", new JSONString(GREEN_HEX_CODE));
				} else {
					o2.put("color", new JSONString(REDS_HEX_CODE[(columnIndex - 1) % REDS_HEX_CODE.length]));
				}

				colorsOption.put(columnIndex - 1 + "", o2);
				options.set("series", colorsOption.getJavaScriptObject());
			}

			int rowIndex = -1;
			for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
				String tmp = dataTable.getValueString(i, 0);
				if (tmp.equalsIgnoreCase(problem)) {
					rowIndex = i;
					break;
				}
			}
			if (rowIndex < 0) {
				rowIndex = dataTable.getNumberOfRows();
				dataTable.addRow();
				dataTable.setValue(rowIndex, 0, problem);
				sortYAxis();
			}

			dataTable.setValue(rowIndex, columnIndex, 1);

			classificationCount.put(key, 1);
		}

		chart.draw(dataTable, options);
	}
	
	public native void sortYAxis() /*-{
		var dt = this.@pt.up.fc.dcc.mooshak.client.guis.judge.view.SubmissionsPiledBarCharts::dataTable;
		dt.sort([{column: 0}]);
	}-*/;

	class SubmissionsPiledBarChartDisplay extends AbstractHasData<Row> {

		private Timer timedUpdate = null;

		public SubmissionsPiledBarChartDisplay(Element elem, int pageSize, ProvidesKey<Row> keyProvider) {
			super(elem, pageSize, keyProvider);
		}

		@Override
		protected boolean dependsOnSelection() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected Element getChildContainer() {
			return getElement().getFirstChildElement();
		}

		@Override
		protected Element getKeyboardSelectedElement() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected boolean isKeyboardNavigationSuppressed() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected void renderRowValues(SafeHtmlBuilder sb, final List<Row> values, int start,
				SelectionModel<? super Row> selectionModel) throws UnsupportedOperationException {

			if (timedUpdate != null && timedUpdate.isRunning())
				timedUpdate.cancel();

			timedUpdate = new Timer() {

				@Override
				public void run() {
					clearChartData();

					for (Row row : values) {
						insertSubmissionRow(row.getData());
					}
					
					if (!initialized) {
						for (InitializeHandler h : initHandlers) {
							h.onInitialize(null);
						}
						initialized = true;
					}
				}
			};
			timedUpdate.schedule(TIMED_UPDATE_MILLIS);
		}

		@Override
		protected boolean resetFocusOnCell() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected void setKeyboardSelected(int index, boolean selected, boolean stealFocus) {
			// TODO Auto-generated method stub

		}

	}

	public void clearChartData() {
		if (dataTable != null)
			dataTable.removeRows(0, dataTable.getNumberOfRows());
		classificationCount.clear();
		colorsOption = new JSONObject();
	}

	@Override
	public HandlerRegistration addInitializeHandler(InitializeHandler handler) {
		initHandlers.add(handler);
		return null;
	}
}
