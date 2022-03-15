package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import static pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.LINES;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider;
import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.IOPair;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.FeedbackPanel;
import pt.up.fc.dcc.mooshak.client.widgets.FileContent;
import pt.up.fc.dcc.mooshak.client.widgets.FileEditorWindowBox;
import pt.up.fc.dcc.mooshak.client.widgets.HasFileContent;

public class ProgramViewImpl extends Composite implements ProgramView, 
	HasFileContent {
	
	private static final int STORAGE_UPDATE_TIMER = 5 * 1000;

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;


	private static ProgramUiBinder uiBinder = GWT.create(ProgramUiBinder.class);

	@UiTemplate("ProgramView.ui.xml")
	interface ProgramUiBinder extends UiBinder<Widget, ProgramViewImpl> {}
	
	private ICPCConstants constants =
			GWT.create(ICPCConstants.class);
	
	private Presenter presenter = null;
	
	@UiField
	Label idNameLabel;
	
	@UiField
	Label titleLabel;
	
	@UiField
	FileContent editor;
	
	@UiField
	HTML observations;
	
	@UiField(provided = true)
	DataGrid<IOPair> iodata;
	
	private IODataProvider ioDataProvider= new IODataProvider();
	
	private Timer printTimer = null;
	private long printResetTime = -1;
	private int remainingPrints = -1;
	
	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;
	
	private Timer validateTimer = null;
	private long validateResetTime = -1;
	private int remainingValidations = -1;
	
	private int missingObs = 0;
	
	private Timer updateStorageTimer = null;

	public ProgramViewImpl() {
		configureIOdata();
		initWidget(uiBinder.createAndBindUi(this));
		
		submit.ensureDebugId("submitBtn");
		validate.ensureDebugId("validateBtn");
		print.ensureDebugId("printBtn");
		observations.ensureDebugId("observations");
		
		configurePrintTimer();		
		configureSubmitTimer();	
		configureValidateTimer();
		
		editor.setForm(this);
		
		updateStorageTimer = new Timer() {
			
			@Override
			public void run() {
				presenter.saveToLocalStorage(editor.getProgramName(), 
						editor.getProgramCode(), getInputs());
			}
		};
		updateStorageTimer.scheduleRepeating(STORAGE_UPDATE_TIMER);
	}
	
	public void setProgramIdentification(String id,String name) {
		idNameLabel.setText(id);
		titleLabel.setText(name);
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
					
				validate.setTitle(constants.validate() + " " + tooltip);
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
					
				submit.setTitle(constants.submit() + " " + tooltip);
			}
		};
		
		submitTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	/**
	 * Configures the timer to update the print tooltip
	 */
	private void configurePrintTimer() {
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
	}

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
	    
	    Column<IOPair, String> timeColumn = 
	    		new Column<IOPair, String>(new TextCell()) {
			@Override
			public String getValue(IOPair pair) {
				return pair.getExecTime() + "";
			}
			
			@Override
			public void render(Context context, IOPair object, SafeHtmlBuilder sb) {
				String toRender = object.getExecTime();
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
	    iodata.addColumn(timeColumn,"Exec Time");
	    iodata.setColumnWidth(timeColumn, 30, Unit.PCT);
	    
	    ioDataProvider.addDataDisplay(iodata);
	    
	}
	
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public boolean isLanguageEditable() {
		return editor.isLanguageEditable();
	}

	@Override
	public byte[] getProgramCode() {
		return editor.getProgramCode();
	}

	@Override
	public String getProgramName() {
		return editor.getProgramName();
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
			if(! "".equals(input)) {
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
	public void setOutputs(List<String> outputs) {
		List<IOPair> ioPairs = ioDataProvider.getList();
		
		int pos = 0;
		for(String output: outputs) {
			IOPair pair = ioPairs.get(nonNull.get(pos));
			pair.setOutput(output);
			ioPairs.set(nonNull.get(pos++), pair);
		}
		
		ioDataProvider.refresh();
	}

	@Override
	public void setExecutionTimes(List<String> times) {
		List<IOPair> ioPairs = ioDataProvider.getList();
		
		int pos = 0;
		for(String execTime: times) {
			IOPair pair = ioPairs.get(nonNull.get(pos));
			pair.setExecTime(execTime);
			ioPairs.set(nonNull.get(pos++), pair);
		}
		
		ioDataProvider.refresh();
	}
	
	private static RegExp errorPattern = RegExp.compile(":(\\d+):(.*)$","gm");
	private static RegExp filePattern = RegExp.compile("(.*/)(?:$|(.+?)(?:(\\.[a-zA-Z0-9]*)))","gm");

	public void setObservations(String text) {
		clearObservations();
		addObservations(text);
	}
	
	@Override
	public void addObservations(String text) {
		MatchResult result = errorPattern.exec(text);
		boolean info = true;
		
		editor.getEditor().clearAnnotations();
		
		while(result != null) {
			int row = Integer.parseInt(result.getGroup(1))-1;
			int column = 0;
			String error = result.getGroup(2);
			
			editor.getEditor().addAnnotation(row, column, error, AceAnnotationType.ERROR);
			
			result = errorPattern.exec(text);
			info = false;
		} 
		
		result = filePattern.exec(text);
		while(result != null) {
			String path = result.getGroup(1);
			text = text.replace(path, "");
			result = filePattern.exec(text);
		}
		
		String formattedText;
		if(info) 
			formattedText = "<br/>"+text;
		else { 
			editor.getEditor().setAnnotations();
			formattedText = "<pre>"+text+"</pre>";
		}
		
		observations.setHTML(observations.getHTML()+formattedText);
		
	}
	
	@Override
	public void addStatus(String status) {
		String color;
		
		if(status.indexOf("Accepted") != -1) 
			color="green";
		else
			color = "red";
		
		String formattedStatus = "<font color=\""+color+"\">"+status+"</font>";
		
		observations.setHTML(observations.getHTML() + formattedStatus);
	}
	
	@UiField
	Image feedback;
	
	@UiHandler({"feedback"})
	public void onFeedback(ClickEvent event) {
		
		feedbackPanel.center();
		feedbackPanel.show();
	}
	
	FeedbackPanel feedbackPanel = new FeedbackPanel();
	
	@Override
	public void addFeedback(String feedbackInHTML) {
		// observations.setHTML(observations.getHTML() + feedback);
		
		if(feedbackInHTML != null && ! "".equals(feedbackInHTML.trim())) {
			feedbackPanel.setContent(feedbackInHTML);
		
			feedback.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
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
	
	@UiField CustomImageButton print;
	
	@UiHandler({"print"})
	void print(ClickEvent event) {
		presenter.onProgramPrint();
	}
	
	
	@Override
	public void setPrintTooltip(int remaining, 
			long resetTime) {
		printResetTime = resetTime;
		remainingPrints = remaining;
	}
	
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

	String observationsHtml = ""; 
	
	@Override
	public void clearObservations() {
		observations.setText("");
		feedback.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

	@Override
	public void onChangeProgramExtension(final String extension,
			boolean askSkeletonUse) {
		if (askSkeletonUse)
			presenter.getProgramSkeleton(extension);
		presenter.updateEditable(extension);
	}

	@Override
	public void setEditable(boolean editable) {
		editor.setLanguageEditable(editable);
	}

	@Override
	public void setProgramCode(byte[] code) {
		editor.setFileContent(code);
	}
	
	@Override
	public void setProgramName(String name) {
		editor.setFilename(name);
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
	
	@Override
	public void setLanguages(Map<String, String> languages) {
		editor.setAvailableLanguages(languages);
		
		if (languages.size() > 1)
			return;
		
		if (editor.getProgramName().equals("") 
				|| languages.get(editor.getProgramExtension()) == null) {
			String lang = languages.values().iterator()
					.next().toUpperCase();
			editor.setMode(AceEditorMode.valueOf(lang));
			editor.updateFileName();
		}
	}
}
