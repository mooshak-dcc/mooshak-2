package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.formatters.DateFormat;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class ContestProgressLineChart extends HTMLPanel implements HasInitializeHandlers {
	private static final int TIMED_UPDATE_MILLIS = 750;
	private static final int FULL_SECOND = 1000;
	private static final int FULL_MINUTE = 60 * FULL_SECOND;
	private static final int FULL_HOUR = 60 * FULL_MINUTE;
	private static final int FULL_DAY = 24 * FULL_HOUR;
	
	public enum EvolutionMode { SUBMISSIONS, ACCEPTED, REJECTED, QUESTIONS }

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	
	private List<InitializeHandler> initHandlers = 
			new ArrayList<InitializeHandler>();
	
	private LineChart chart = null;
	private Options options = null;
	private DataTable dataTable = null;

	private ListingDataProvider submissionsDataProvider;	
	private ListingDataProvider questionsDataProvider;	
	
	private JSONObject intervalsOption = new JSONObject();
	
	private long interval = 60 * 30 * 1000;
	private Map<String, Integer> occurrencesCount = new HashMap<String, Integer>();
	
	private long maxTime = 0;
	private EvolutionMode mode = EvolutionMode.SUBMISSIONS;
	
	SelectOneListBox<SelectableOption> modeSelector = new SelectOneListBox<>();
	SelectOneListBox<SelectableOption> intervalSelector = new SelectOneListBox<>();
	
	private OptionFormatter<SelectableOption> formatter = 
			new OptionFormatter<SelectableOption>() {
		 public String getLabel(SelectableOption option) { 
			 return option.getLabel(); 
			 };
         public String getValue(SelectableOption option)  { 
        	 return option.getId(); 
        	 };
	};
	
	private boolean initialized = false;

	public ContestProgressLineChart(SafeHtml safeHtml) {
		super(safeHtml);
		init();
	}

	public ContestProgressLineChart(String tag, String html) {
		super(tag, html);
		init();
	}
	
	public ContestProgressLineChart(String html) {
		super(html);
		init();
	}

	private void init() {
		
		submissionsDataProvider = ListingDataProvider.getDataProvider(Kind.SUBMISSIONS);
		questionsDataProvider = ListingDataProvider.getDataProvider(Kind.QUESTIONS);

		setHeight("100%");
		getElement().setPropertyString("position", "relative");
		intervalSelector.setFormatter(formatter);
		modeSelector.setFormatter(formatter);

		VisualizationUtils.loadVisualizationApi(new Runnable() {
			public void run() {
		
				initDataTable();
				initOptions();
		
				// Create a line chart visualization.
				chart = new LineChart(dataTable, options);
				chart.setHeight("100%");
				chart.setWidth("100%");
				add(chart);	
				
				final ContestProgressLineChartDisplay display = 
						new ContestProgressLineChartDisplay(chart.getElement(), Integer.MAX_VALUE,
								submissionsDataProvider.getKeyProvider());
				submissionsDataProvider.addDataDisplay(display);
				submissionsDataProvider.refresh();

				// Draw the chart
				chart.draw(dataTable, options);


				HorizontalPanel selectorPanel = new HorizontalPanel();
				
				modeSelector.setSelections(Arrays.asList(
						new SelectableOption("Submissions", "Submissions"), 
						new SelectableOption("Accepted", "Accepted"), 
						new SelectableOption("Rejected", "Rejected"),
						new SelectableOption("Questions","Questions")));
				modeSelector.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						mode = EvolutionMode.valueOf(modeSelector.getSelectedOption()
								.getLabel().toUpperCase());
						
						if (mode.equals(EvolutionMode.QUESTIONS)) {
							if (!questionsDataProvider.getDataDisplays().contains(display))
								questionsDataProvider.addDataDisplay(display);
							if (submissionsDataProvider.getDataDisplays().contains(display))
								submissionsDataProvider.removeDataDisplay(display);
							questionsDataProvider.refresh();
						} else {
							if (!submissionsDataProvider.getDataDisplays().contains(display))
								submissionsDataProvider.addDataDisplay(display);
							if (questionsDataProvider.getDataDisplays().contains(display))
								questionsDataProvider.removeDataDisplay(display);
							submissionsDataProvider.refresh();
						}
						display.redraw();
					}
				});
				
				selectorPanel.add(new Label("Mode: "));
				selectorPanel.add(modeSelector);
				
				intervalSelector.setSelections(Arrays.asList(
						new SelectableOption("00:15","00:15"), 
						new SelectableOption("00:30","00:30"), 
						new SelectableOption("01:00","01:00"),
						new SelectableOption("01:30","01:30"),
						new SelectableOption("02:00","02:00"),
						new SelectableOption("02:30","02:30"),
						new SelectableOption("03:00","03:00")));
				intervalSelector.setSelectedValue(new SelectableOption("00:30","00:30"));
				intervalSelector.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String[] parts = intervalSelector.getSelectedOption().getLabel().split(":");
						interval = Integer.parseInt(parts[0]) * FULL_HOUR 
								+ Integer.parseInt(parts[1]) * FULL_MINUTE;

						refreshXAxis();
						
						if (mode.equals(EvolutionMode.QUESTIONS)) {
							questionsDataProvider.refresh();
						} else {
							submissionsDataProvider.refresh();
						}
						
						display.redraw();
					}
				});
				
				selectorPanel.add(new Label("Interval: "));
				selectorPanel.add(intervalSelector);
				
				selectorPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
				selectorPanel.getElement().getStyle().setRight(5, Unit.PX);
				selectorPanel.getElement().getStyle().setTop(10, Unit.PX);
				add(selectorPanel);
			}
		},	CoreChart.PACKAGE, "controls");
	}

	private void initOptions() {
		options = Options.create();
		options.setCurveType("function");
		options.setLineWidth(4);
		options.setTitle(constants.contestProgressChartTitle());
		options.setLegend(LegendPosition.RIGHT);
		
		AxisOptions yAxisOptions = AxisOptions.create();
		yAxisOptions.set("format", "#");
		options.setVAxisOptions(yAxisOptions);

		refreshXAxis();
		
	    options.setInterpolateNulls(true);
	    options.setPointSize(5);
	    
	    JSONObject o = new JSONObject();
	    JSONArray actions = new JSONArray();
	    actions.set(0, new JSONString("dragToPan"));
	    actions.set(1, new JSONString("rightClickToReset"));
		o.put("actions", actions);
		o.put("axis", new JSONString("horizontal"));
		o.put("keepInBounds", new JSONString("true"));
		//o.put("maxZoomIn", new JSONString("4.0"));
		
		options.set("explorer", o.getJavaScriptObject());
	}

	private void refreshXAxis() {
		AxisOptions xAxisOptions = AxisOptions.create();
		xAxisOptions.set("format", "ddd HH:mm");
		xAxisOptions.set("step", new Date(interval));
		
		JSONObject gridlines = new JSONObject();
		gridlines.put("count", new JSONString((maxTime/interval) + ""));
		xAxisOptions.set("gridlines", gridlines.getJavaScriptObject());
		
		options.setHAxisOptions(xAxisOptions);
	}

	private DateFormat dateFormat = null;
	private void initDataTable() {
		dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.DATETIME, "Time");
		//dataTable.addColumn(ColumnType.STRING, "Problem");
		//dataTable.addColumn(ColumnType.NUMBER, "Submissions");
		
	    DateFormat.Options opt = DateFormat.Options.create();
	    opt.setPattern("d HH:mm");
	    dateFormat = DateFormat.create(opt);
	    dateFormat.format(dataTable, 0);
	}

	private void insertRow(Map<String, String> data) {

		if (dataTable == null || data == null)
			return;

		String time = data.get("time");
		String problem = data.get("problem");
		
		Date date = parseDate(time);
		if (date == null)
			return;
		
		String key = getKeyForOccurrence(problem, date);
		Date intervalDate = new Date(Long
				.parseLong(key.substring(key.indexOf(".") + 1)));
		
		if (occurrencesCount.get(key) != null) {
			occurrencesCount.put(key, occurrencesCount.get(key) + 1);

			int columnIndex = dataTable.getColumnIndex(problem);
			for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
				Date tmp = dataTable.getValueDate(i, 0);
				if (tmp.equals(intervalDate)) {
					dataTable.setValue(i, columnIndex, occurrencesCount.get(key));
					break;
				}
			}
		} else {
			
			int columnIndex = dataTable.getColumnIndex(problem);
			if (columnIndex < 0) {
				occurrencesCount.put(problem + ".0", 0);
				dataTable.addColumn(ColumnType.NUMBER, problem);
				columnIndex = dataTable.getColumnIndex(problem);
	
				JSONObject o2 = new JSONObject();
				o2.put("style", new JSONString("line"));
				o2.put("lineWidth", new JSONString("2"));
				
				intervalsOption.put(columnIndex - 1 + "", o2);
				options.set("intervals", intervalsOption.getJavaScriptObject());
			}

			int rowIndex = -1;
			for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
				Date tmp = dataTable.getValueDate(i, 0);
				if (tmp.equals(intervalDate)) {
					rowIndex = i;
					break;
				}
			}
			if (rowIndex < 0) {
				rowIndex = dataTable.getNumberOfRows();
				dataTable.addRow();
				
				dataTable.setValue(rowIndex, 0, intervalDate);
			}
		
			dataTable.setValue(rowIndex, columnIndex, 1);
			occurrencesCount.put(key, 1);
		}
		
	    dateFormat.format(dataTable, 0);

		chart.draw(dataTable, options);
	}
	
	public native void sortXAxis() /*-{
		var dt = this.@pt.up.fc.dcc.mooshak.client.guis.judge.view.ContestProgressLineChart::dataTable;
		dt.sort([{column: 0}]);
	}-*/;
	
	/**
	 * @param date
	 * @return
	 */
	private String showDate(Date date) {
		long time = date.getTime();
		
		DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm");
		String timeStr = dtf.format(date);
		
		int days = (int) (time / FULL_DAY);
		return days + " " + timeStr;
	}

	/**
	 * @param time
	 * @return
	 */
	private Date parseDate(String time) {
		if (time == null) 
			return null;
			
		int day = 0, hour = 0, min = 0, sec = 0;
		long timeSum = 0;
		
		String parts[] = null;
		if (time.indexOf(" ") != -1) {
			
			parts = time.split(" ");
			if (parts.length >= 2) {
				day = Integer.parseInt(parts[0]);
				
				timeSum += (long) day * 24 * 60 * 60 * 1000;
				
				time = parts[1];
			}
		}
		
		parts = time.split(":");

		hour = Integer.parseInt(parts[0]);
		min = Integer.parseInt(parts[1]);
		if (parts.length > 2)
			sec = Integer.parseInt(parts[2]);
		
		timeSum += hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000;
		
		return new Date(timeSum);
	}

	/**
	 * @param problem
	 * @param date
	 * @return
	 */
	private String getKeyForOccurrence(String problem, Date date) {
		String key = null;
		for (String k : occurrencesCount.keySet()) {
			String[] tmp = k.split("\\.");
			if (!tmp[0].equals(problem))
				continue;
			
			long t = Long.parseLong(tmp[tmp.length - 1]);
			if (t >= date.getTime() && t < date.getTime() + interval) {
				key = k;
				break;
			}
		}
		
		if (key == null) {
			long t = date.getTime() - (date.getTime() % interval);
			
			if (t > maxTime)
				maxTime = t;
			
			key = problem + "." + t;
		}
		return key;
	}

	class ContestProgressLineChartDisplay extends AbstractHasData<Row> {

		private Timer timedUpdate = null;

		public ContestProgressLineChartDisplay(Element elem, int pageSize, ProvidesKey<Row> keyProvider) {
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
						String classify = row.getValue("classification");
						if (classify == null && !mode.equals(EvolutionMode.QUESTIONS))
							continue;
						else if (classify != null) {
							if (classify.equalsIgnoreCase("accepted")) {
								if (mode.equals(EvolutionMode.REJECTED))
									continue;
							} else {
								if (mode.equals(EvolutionMode.ACCEPTED))
									continue;
							}
						}
						insertRow(row.getData());
					}
					
					refreshXAxis();
					
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

	private void clearChartData() {
		if (dataTable != null)
			dataTable.removeRows(0, dataTable.getNumberOfRows());
		
		intervalsOption = new JSONObject();
		occurrencesCount.clear();
		maxTime = 0;
	}

	@Override
	public HandlerRegistration addInitializeHandler(InitializeHandler handler) {
		initHandlers.add(handler);
		return null;
	}

}
