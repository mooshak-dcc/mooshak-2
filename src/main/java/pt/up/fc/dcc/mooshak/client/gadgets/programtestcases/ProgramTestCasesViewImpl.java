package pt.up.fc.dcc.mooshak.client.gadgets.programtestcases;

import static pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.LINES;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider;
import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.IOPair;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.FileEditorWindowBox;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class ProgramTestCasesViewImpl extends ResizeComposite
	implements ProgramTestCasesView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;

	private static ProgramTestCasesViewUiBinder uiBinder = GWT
			.create(ProgramTestCasesViewUiBinder.class);

	@UiTemplate("ProgramTestCasesView.ui.xml")
	interface ProgramTestCasesViewUiBinder extends
			UiBinder<Widget, ProgramTestCasesViewImpl> {}
	
	private ICPCConstants constants =
			GWT.create(ICPCConstants.class);
	
	private Presenter presenter = null;

	interface BaseStyle extends CssResource {
		String testPassed();
		String testWrong();
	}
	
	@UiField
	static BaseStyle style; 
	
	@UiField
	ResizableHtmlPanel panel;
	
	@UiField(provided = true)
	DataGrid<IOPair> iodata;
	
	@UiField
	HTMLPanel commandsPanel;
	
	private IODataProvider ioDataProvider= new IODataProvider();
	
	/*private Timer printTimer = null;
	private long printResetTime = -1;
	private int remainingPrints = -1;*/
	
	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;
	
	private Timer validateTimer = null;
	private long validateResetTime = -1;
	private int remainingValidations = -1;
	
	private int missingObs = 0;

	public ProgramTestCasesViewImpl() {
		configureIOdata();
		initWidget(uiBinder.createAndBindUi(this));
		
		submit.ensureDebugId("submitBtn");
		validate.ensureDebugId("validateBtn");
		/*print.ensureDebugId("printBtn");*/
		
		/*configurePrintTimer();*/		
		configureSubmitTimer();	
		configureValidateTimer();

		panel.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				iodata.setHeight((event.getHeight() - commandsPanel.getOffsetHeight()) 
						+ "px");
			}
		});
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {				
				panel.onResize();
			}
		});
		setDraggableParentProperties();
	}

	
	/**
	 * Resolving issues when clicking inside draggable parent
	 */
	public void setDraggableParentProperties() {
		iodata.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());
		
		iodata.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
	}
	
	/**
	 * Configures the timer to update the validate tooltip
	 */
	private void configureValidateTimer() {
		validateTimer = new Timer() {
			
			@Override
			public void run() {
				String tooltip = "";
				if(remainingValidations >= 0) {
					tooltip += remainingValidations;
				}
				
				if(validateResetTime >= 0) {
					tooltip += "/";
					long diff = validateResetTime - new Date().getTime();
					
					if(diff <= 0) {
						tooltip += "00:00:00";
						validate.setTitle(tooltip);
						presenter.getValidationsTransactionsData();
						return;
					}
					
					tooltip += getCountdownLabel(diff);
				}
					
				if (tooltip.equals(""))
					validate.setTitle(validate.getTitle());
				else
					validate.setTitle(validate.getTitle() + "\n\n" +
							constants.submit() + " " + tooltip);
			}
		};
		
		validateTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	/**
	 * Configures the timer to update the submit tooltip
	 */
	private void configureSubmitTimer() {
		submitTimer = new Timer() {
			
			@Override
			public void run() {
				String tooltip = "";
				if(remainingSubmits >= 0) {
					tooltip += remainingSubmits;
				}
				
				if(submitResetTime >= 0) {
					tooltip += "/";
					long diff = submitResetTime - new Date().getTime();
					
					if(diff <= 0) {
						tooltip += "00:00:00";
						submit.setTitle(tooltip);
						presenter.getSubmissionsTransactionsData();
						return;
					}
					
					tooltip += getCountdownLabel(diff);
				}
					
				if (tooltip.equals(""))
					submit.setTitle(submit.getTitle());
				else
					submit.setTitle(submit.getTitle() + "\n\n" +
							constants.submit() + " " + tooltip);
			}
		};
		
		submitTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	/**
	 * Configures the timer to update the print tooltip
	 */
	/*private void configurePrintTimer() {
		printTimer = new Timer() {
			
			@Override
			public void run() {
				String tooltip = "";
				if(remainingPrints >= 0) {
					tooltip += remainingPrints;
				}
				
				if(printResetTime >= 0) {
					tooltip += "/";
					long diff = printResetTime - new Date().getTime();
					
					if(diff <= 0) {
						tooltip += "00:00:00";
						print.setTitle(tooltip);
						presenter.getPrintoutsTransactionsData();
						return;
					}
					
					tooltip += getCountdownLabel(diff);
				}
					
				print.setTitle(constants.print() + " " + tooltip);
			}
		};
		
		printTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}*/

	/**
	 * Calculates a string in countdown timer form for a given
	 *  diff
	 * @param diff
	 * @return
	 */
	private String getCountdownLabel(long diff) {
		String label = "";
		
		int hours = (int) (diff / HOURS_IN_MILLIS);
		diff = diff % HOURS_IN_MILLIS;
		int minutes = (int) (diff / MINUTES_IN_MILLIS);
		diff = diff % MINUTES_IN_MILLIS;
		int seconds = (int) (diff / SECONDS_IN_MILLIS);
		
		if(hours < 10)
			label += "0" + hours;
		else
			label += hours;
		label += ":";
		if(minutes < 10)
			label += "0" + minutes;
		else
			label += minutes;
		label += ":";
		if(seconds < 10)
			label += "0" + seconds;
		else
			label += seconds;
		
		return label;
	}
	
	private  void configureIOdata() {
				
		iodata = new DataGrid<IOPair>(LINES,IOPair.KEY_PROVIDER);
	    iodata.setWidth("100%");
	    
	    iodata.setAutoHeaderRefreshDisabled(true);
	    
	    Column<IOPair, String> inputColumn = 
	    		new Column<IOPair, String>(new ClickableTextCell()) {

			@Override
			public String getValue(IOPair pair) {
				return pair.getInput();
			}
			
			@Override
			public void render(Context context, IOPair object, SafeHtmlBuilder sb) {
				String toRender = object.getInput();
				if (toRender != null && toRender.trim().length() > 0) {
					if(toRender.length() > 10)
						toRender = toRender.substring(0, 9) + "...";
					
					if(toRender.indexOf("\n") != -1)
						toRender = toRender.substring(0, toRender.indexOf("\n")) + "...";
					
					sb.appendEscapedLines(toRender);
				} else {
					/*
					 * Render a blank space to force the rendered element to have a
					 * height. Otherwise it is not clickable.
					 */
					sb.appendHtmlConstant("\u00A0");
				}
			}
	    };
	    inputColumn.setFieldUpdater(new FieldUpdater<IODataProvider.IOPair, String>() {
			
			@Override
			public void update(int index, final IOPair object, String value) {
				FileEditorWindowBox fileEditorWindowBox = new FileEditorWindowBox() {
					
					@Override
					public void save() {
						object.setInput(getEditorContent());
						iodata.redraw();
					}
					
					public void hide() {
						super.hide();
						removeFromParent();
					};
					
					@Override
					public String getFileName() {
						return "input.txt";
					}
				};
				fileEditorWindowBox.setEditorContent(object.getInput());
				fileEditorWindowBox.show();
			}
		});
	    
	    iodata.addColumn(inputColumn,"Input");
	    iodata.setColumnWidth(inputColumn, 30, Unit.PCT);
	    
	    Column<IOPair, String> outputColumn = 
	    		new Column<IOPair, String>(new ClickableTextCell()) {
			@Override
			public String getValue(IOPair pair) {
				return pair.getOutput();
			}
			
			@Override
			public void render(Context context, IOPair object, SafeHtmlBuilder sb) {
				String toRender = object.getOutput();
				if (toRender != null && toRender.trim().length() > 0) {
					if(toRender.length() > 10)
						toRender = toRender.substring(0, 9) + "...";
					
					if(toRender.indexOf("\n") != -1)
						toRender = toRender.substring(0, toRender.indexOf("\n")) + "...";
					
					sb.appendEscapedLines(toRender);
				} else {
					/*
					 * Render a blank space to force the rendered element to have a
					 * height. Otherwise it is not clickable.
					 */
					sb.appendHtmlConstant("\u00A0");
				}
			}
	    	
	    };
	    outputColumn.setFieldUpdater(new FieldUpdater<IODataProvider.IOPair, String>() {
			
			@Override
			public void update(int index, final IOPair object, String value) {
				FileEditorWindowBox fileEditorWindowBox = new FileEditorWindowBox() {
					
					@Override
					public void save() {
						iodata.redraw();
					}
					
					public void hide() {
						super.hide();
						removeFromParent();
					};
					
					@Override
					public String getFileName() {
						return "output.txt";
					}
				};
				fileEditorWindowBox.setEditorContent(object.getOutput());
				fileEditorWindowBox.setReadOnly(true);
				fileEditorWindowBox.show();
			}
		});
	    iodata.addColumn(outputColumn,"Output");
	    iodata.setColumnWidth(outputColumn, 30, Unit.PCT);
	    
	    ioDataProvider.addDataDisplay(iodata);
	    
	}
	
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	// order of non empty input/output rows
	List<Integer> nonNull = new ArrayList<Integer>();
	
	@Override
	public List<String> getInputs() {
		List<IOPair> ioPairs = ioDataProvider.getList();
		List<String> inputs = new ArrayList<String>();
		
		nonNull.clear();
		int pos = 0;
		for(IOPair pair: ioPairs) {
			String input = pair.getInput();
			String output = pair.getOutput();
			if(! "".equals(input) || ! "".equals(output)) {
				inputs.add(input);
				nonNull.add(pos);
			}
			pos++;
		}
		return inputs;
	}

	@Override
	public void setInputs(List<String> inputs) {
		List<IOPair> ioPairs = ioDataProvider.getList();

		nonNull.clear();
		int pos = 0;
		for(String input: inputs) {
			nonNull.add(pos);
			IOPair pair = ioPairs.get(nonNull.get(pos));
			pair.setInput(input);
			ioPairs.set(nonNull.get(pos), pair);
			pos++;
		}
		
		ioDataProvider.refresh();
	}

	@Override
	public void setOutputs(final List<String> outputs) {
		List<IOPair> ioPairs = ioDataProvider.getList();
		int pos = 0;
		for(String output: outputs) {
			IOPair pair = ioPairs.get(nonNull.get(pos));
			
			if (pair.isPublicTest())
				presenter.hasTestPassed(pos, pair.getOutput(), output);
			else
				pair.setOutput(output);
			ioPairs.set(nonNull.get(pos++), pair);
		}
	    
	    iodata.setRowStyles(new RowStyles<IODataProvider.IOPair>() {
			
			@Override
			public String getStyleNames(IOPair row, int rowIndex) {
				if (row.isPublicTest() && outputs != null && !outputs.isEmpty()) {
					if (row.isPassed())
						return style.testPassed();
					else
						return style.testWrong();
				}
				return "";
			}
		});
		
		ioDataProvider.refresh();
	}
	
	

	/* Command buttons */
	
	@UiField CustomImageButton submit;
	
	@UiHandler({"submit"})
	void submit(ClickEvent event) {
		presenter.onProgramEvaluate(true);
	}
	
	@UiField CustomImageButton validate;
	
	@UiHandler({"validate"})
	void validate(ClickEvent event) {
		presenter.onProgramEvaluate(false);
	}
	
	/*@UiField CustomImageButton print;
	
	@UiHandler({"print"})
	void print(ClickEvent event) {
		presenter.onProgramPrint();
	}
	
	
	@Override
	public void setPrintTooltip(int remaining, 
			long resetTime) {
		printResetTime = resetTime;
		remainingPrints = remaining;
	}*/
	
	@Override
	public void setSubmitTooltip(int remaining, 
			long resetTime) {
		submitResetTime = resetTime;
		remainingSubmits = remaining;
	}
	
	@Override
	public void setValidateTooltip(int remaining, 
			long resetTime) {
		validateResetTime = resetTime;
		remainingValidations = remaining;
	}

	@Override
	public void insertPublicTestCases(Map<String, String> ios) {
		List<IOPair> ioPairs = ioDataProvider.getList();
		
		int pos = 0;
		for(IOPair pair: ioPairs) {
			String input = pair.getInput();
			if("".equals(input)) {
				break;
			}
			pos++;
		}
		
		for (String input : ios.keySet()) {
			String output = ios.get(input);
			IOPair pair = ioPairs.get(pos);
			pair.setInput(input);
			pair.setOutput(output);
			pair.setPublicTest(true);
			nonNull.add(pos);
			pos++;
		}
		
		ioDataProvider.refresh();
		iodata.redraw();
	}

	@Override
	public void setTestPassed(int pos, boolean passed) {
		List<IOPair> ioPairs = ioDataProvider.getList();
		int i = 0;
		for (IOPair ioPair : ioPairs) {
			if (i == pos) {
				ioPair.setPassed(passed);
				break;
			}
			i++;
		}
		
		ioDataProvider.refresh();
		iodata.redraw();
	}

	@Override
	public void increaseProgramWaitingEvaluation() {
		missingObs++;
		submit.setEnabled(false);
		validate.setEnabled(false);
	}

	@Override
	public void decreaseProgramWaitingEvaluation() {
		if (missingObs > 0) {
			missingObs--;
			submit.setEnabled(true);
			validate.setEnabled(true);
		}
		else
			Logger.getLogger("").log(Level.SEVERE, "An observation has been received "
					+ "and client was not waiting for it.");
	}


}
