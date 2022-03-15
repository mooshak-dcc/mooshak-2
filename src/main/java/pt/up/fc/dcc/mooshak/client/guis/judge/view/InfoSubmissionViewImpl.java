package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import static pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.LINES;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellBrowser.Builder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomRadioButton;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomTextArea;
import pt.up.fc.dcc.mooshak.client.form.admin.HtmlFreeLabel;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.form.admin.TextListBox;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider;
import pt.up.fc.dcc.mooshak.client.guis.icpc.data.IODataProvider.IOPair;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.data.Test;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.FeedbackPanel;
import pt.up.fc.dcc.mooshak.client.widgets.FileDiffWindowBox;
import pt.up.fc.dcc.mooshak.client.widgets.FileEditorWindowBox;
import pt.up.fc.dcc.mooshak.client.widgets.TabbedMultipleEditor;
import pt.up.fc.dcc.mooshak.client.widgets.TabbedMultipleEditor.EditorMode;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class InfoSubmissionViewImpl extends Composite implements
		InfoSubmissionView, HasFormData,
		HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue> {

	private static final int SECONDS_TO_MILLIS = 1000;

	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static InfoSubmissionUiBinder uiBinder = GWT
			.create(InfoSubmissionUiBinder.class);

	@UiTemplate("InfoSubmissionView.ui.xml")
	interface InfoSubmissionUiBinder extends
			UiBinder<Widget, InfoSubmissionViewImpl> {
	}

	private Presenter presenter = null;
	
	private FormDataProvider formDataProvider;
	
	private String id;
	
	@UiField
	StackPanel submissionDetails;
	
	@UiField
	Label message;

	@UiField
	Label submission;

	@UiField
	CustomLabelPath file;

	@UiField
	HtmlFreeLabel received;

	@UiField
	HtmlFreeLabel analyzed;

	@UiField
	CustomLabelPath team;

	@UiField
	CustomLabelPath language;

	@UiField
	CustomLabelPath problemId;

	@UiField
	Label compilation;

	@UiField
	HtmlFreeLabel cpu;

	@UiField
	HtmlFreeLabel memory;

	@UiField
	Label classification;

	@UiField
	HtmlFreeLabel mark;

	@UiField
	HtmlFreeLabel observations;

	@UiField
	HtmlFreeLabel feedback;
	
	@UiField
	CustomRadioButton pending;

	@UiField
	CustomRadioButton finalized;

	@UiField
	TabbedMultipleEditor editor;
	
	@UiField(provided = true)
	CellTable<TestLine> testsTable;
	
	private ListDataProvider<TestLine> testTableDataProvider;

	// CellBrowser for test display
	@UiField(provided = true)
	CellBrowser tests;

	// CellBrowser model
	private TestTreeViewModel testTree;

	// CellBrowser data provider
	private AbstractDataProvider<Test> dataProvider;

	@UiField
	HTMLPanel reportDiv;

	@UiField
	TextListBox result;
	
	@UiField
	SelectOneListBox<SelectableOption> lbReports;
	
	@UiField 
	CustomTextArea reviewerObservations;
	
	@UiField 
	CustomTextArea reviewerFeedback;
	
	@UiField(provided = true)
	DataGrid<IOPair> iodata;
	
	private IODataProvider ioDataProvider= new IODataProvider();
	
	private OptionFormatter<SelectableOption> formatter = 
			new OptionFormatter<SelectableOption>() {
		 public String getLabel(SelectableOption option) { 
			 return option.getLabel(); 
			 };
         public String getValue(SelectableOption option)  { 
        	 return option.getId(); 
        	 };
	};
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	
	private PopupStatementViewer popupStatement = new PopupStatementViewer();
	
	private FileDiffWindowBox fileDiffWindowBox = new FileDiffWindowBox();
	
	private List<String> expectedOutputs = null;
	private List<String> obtainedOutputs = null;
	
	public InfoSubmissionViewImpl() {
		testTableDataProvider = new ListDataProvider<TestLine>(new ArrayList<TestLine>(10));
		dataProvider = new ListDataProvider<Test>(new ArrayList<Test>());
		configureIOdata();
		configureCellBrowser();		
		initTestsTable();
		initWidget(uiBinder.createAndBindUi(this));
		lbReports.setFormatter(formatter);
		setClassificationOptions();
		linkFieldsToHandlers();
		
		submissionDetails.addHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent clickEvent) {
		        if (submissionDetails.getSelectedIndex() == 2) {
		        	editor.redisplay();
		        	ioDataProvider.refresh();
		        }
		    }
		}, ClickEvent.getType());
		
		//editor.disableEditing();

		editor.setMode(EditorMode.CODE);
	}

	private void linkFieldsToHandlers() {
		fields.put("Program", file);
		file.addValueChangeHandler(this);
		
		fields.put("Classify", result);
		result.addValueChangeHandler(this);

		fields.put("Date", received);
		received.addValueChangeHandler(this);

		fields.put("Elapsed", analyzed);
		analyzed.addValueChangeHandler(this);

		fields.put("Team", team);
		team.addValueChangeHandler(this);

		fields.put("Language", language);
		language.addValueChangeHandler(this);
		
		fields.put("Problem", problemId);
		problemId.addValueChangeHandler(this);

		fields.put("CPU", cpu);
		cpu.addValueChangeHandler(this);

		fields.put("Memory", memory);
		memory.addValueChangeHandler(this);

		fields.put("Mark", mark);
		mark.addValueChangeHandler(this);

		fields.put("Observations", observations);
		observations.addValueChangeHandler(this);

		fields.put("Feedback", feedback);
		feedback.addValueChangeHandler(this);

		fields.put("ReviewerObservations", reviewerObservations);
		reviewerObservations.addValueChangeHandler(this);

		fields.put("ReviewerFeedback", reviewerFeedback);
		reviewerFeedback.addValueChangeHandler(this);

		pending.addValueChangeHandler(this);
		finalized.addValueChangeHandler(this);

	}
	
	private  void configureIOdata() {
				
		iodata = new DataGrid<IOPair>(LINES,IOPair.KEY_PROVIDER);
	    iodata.setWidth("95%");
	    
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
					if(toRender.length() > 8)
						toRender = toRender.substring(0, 7) + "...";
					
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
					if(toRender.length() > 8)
						toRender = toRender.substring(0, 7) + "...";
					
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
					if(toRender.length() > 8)
						toRender = toRender.substring(0, 7) + "...";
					
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
	    ioDataProvider.refresh();
	}

	@Override
	public void setEditorKind(EditorKind kind) {
		editor.setMode(EditorMode.valueOf(kind.toString()));
		
		if (kind.equals(EditorKind.CODE)) {
			iodata.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		} else {
			iodata.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}

	private void initTestsTable() {

	    // Create a CellTable.

	    // Set a key provider that provides a unique key for each test. If key is
	    // used to identify contacts when fields (such as the name and address)
	    // change.
	    testsTable = new CellTable<TestLine>(
	        TestLine.KEY_PROVIDER);
	    testsTable.setWidth("100%", true);

	    // Do not refresh the headers and footers every time the data is updated.
	    testsTable.setAutoHeaderRefreshDisabled(true);
	    testsTable.setAutoFooterRefreshDisabled(true);

	    // Attach a column sort handler to the ListDataProvider to sort the list.
	    ListHandler<TestLine> sortHandler = new ListHandler<TestLine>(
	        testTableDataProvider.getList());
	    testsTable.addColumnSortHandler(sortHandler);

	    // Initialize the columns.
	    initTestsTableColumns(sortHandler);
	    
	    testTableDataProvider.addDataDisplay(testsTable);
	}

	private void initTestsTableColumns(ListHandler<TestLine> sortHandler) {

	    // test
	    Column<TestLine, String> testColumn = new Column<TestLine, String>(
	        new ClickableTextCell()) {
	      @Override
	      public String getValue(TestLine object) {
	        return object.getName();
	      }
	    };
	    testColumn.setSortable(true);
	    sortHandler.setComparator(testColumn, new Comparator<TestLine>() {
	      @Override
	      public int compare(TestLine o1, TestLine o2) {
	        return o1.getName().compareTo(o2.getName());
	      }
	    });
	    testsTable.addColumn(testColumn, "Test");
	    testsTable.setColumnWidth(testColumn, 10, Unit.PCT);
	    testColumn.setFieldUpdater(new FieldUpdater<TestLine, String>() {

			@Override
			public void update(int index, TestLine object, String value) {
				if (object.getResult() == null ||
						!object.getResult().toUpperCase().equals("OUTPUT LIMIT EXCEEDED"))
					presenter.getDiffTest(expectedOutputs.get(object.getId()), 
							obtainedOutputs.get(object.getId()));
				else
					showDiffWindowBox(expectedOutputs.get(object.getId()), 
							"... output too long ...", "");
			}
		});

	    // result
	    Column<TestLine, String> resultColumn = new Column<TestLine, String>(
	        new ClickableTextCell()) {
		      @Override
		      public String getValue(TestLine object) {
		        return object.getResult();
		      }
		      
		      @Override
			public void render(Context context, TestLine object, SafeHtmlBuilder sb) {
				if (object.getResult().equalsIgnoreCase("accepted"))
					sb.append(SafeHtmlUtils.fromTrustedString("<span style='color: green'>" + 
							object.getResult() + "</span>"));
				else 
					sb.append(SafeHtmlUtils.fromTrustedString("<span style='color: red'>" + 
							object.getResult() + "</span>"));
					
			}
	    };
	    resultColumn.setSortable(true);
	    sortHandler.setComparator(resultColumn, new Comparator<TestLine>() {
	      @Override
	      public int compare(TestLine o1, TestLine o2) {
	        return o1.getResult().compareTo(o2.getResult());
	      }
	    });
	    testsTable.addColumn(resultColumn, "Result");
	    testsTable.setColumnWidth(resultColumn, 15, Unit.PCT);
	    resultColumn.setFieldUpdater(new FieldUpdater<TestLine, String>() {

			@Override
			public void update(int index, TestLine object, String value) {
				if (object.getResult() == null ||
						!object.getResult().toUpperCase().equals("OUTPUT LIMIT EXCEEDED"))
					presenter.getDiffTest(expectedOutputs.get(object.getId()), 
							obtainedOutputs.get(object.getId()));
				else
					showDiffWindowBox(expectedOutputs.get(object.getId()), 
							"... output too long ...", "");
			}
		});

	    // expected output
	    Column<TestLine, String> expectedColumn = new Column<TestLine, String>(
	        new ClickableTextCell()) {
	      @Override
	      public String getValue(TestLine object) {
	        return object.getExpected();
	      }
	      
	      @Override
	      public void render(Context context, TestLine object, SafeHtmlBuilder sb) {
				String toRender = object.getExpected();
				if (toRender != null && toRender.trim().length() > 0) {
					if(toRender.length() > 150)
						toRender = toRender.substring(0, 149) + "...";
					
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
	    expectedColumn.setSortable(true);
	    sortHandler.setComparator(expectedColumn, new Comparator<TestLine>() {
	      @Override
	      public int compare(TestLine o1, TestLine o2) {
	        return o1.getExpected().compareTo(o2.getExpected());
	      }
	    });
	    expectedColumn.setFieldUpdater(new FieldUpdater<TestLine, String>() {

			@Override
			public void update(int index, TestLine object, String value) {
				if (object.getResult() == null ||
						!object.getResult().toUpperCase().equals("OUTPUT LIMIT EXCEEDED"))
					presenter.getDiffTest(expectedOutputs.get(object.getId()), 
							obtainedOutputs.get(object.getId()));
				else
					showDiffWindowBox(expectedOutputs.get(object.getId()), 
							"... output too long ...", "");
			}
		});
	    testsTable.addColumn(expectedColumn, "Expected Output");
	    testsTable.setColumnWidth(expectedColumn, 30, Unit.PCT);

	    // obtained output
	    Column<TestLine, String> obtainedColumn = new Column<TestLine, String>(
	        new ClickableTextCell()) {
	      @Override
	      public String getValue(TestLine object) {
	        return object.getObtained();
	      }
	      
	      @Override
	      public void render(Context context, TestLine object, SafeHtmlBuilder sb) {
				String toRender = object.getObtained();
				if (toRender != null && toRender.trim().length() > 0) {
					if(toRender.length() > 150)
						toRender = toRender.substring(0, 149) + "...";
					
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
	    obtainedColumn.setSortable(true);
	    sortHandler.setComparator(obtainedColumn, new Comparator<TestLine>() {
	      @Override
	      public int compare(TestLine o1, TestLine o2) {
	        return o1.getObtained().compareTo(o2.getObtained());
	      }
	    });
	    obtainedColumn.setFieldUpdater(new FieldUpdater<InfoSubmissionViewImpl.TestLine, String>() {
			
			@Override
			public void update(int index, TestLine object, String value) {
				if (object.getResult() == null ||
						!object.getResult().toUpperCase().equals("OUTPUT LIMIT EXCEEDED"))
					presenter.getDiffTest(expectedOutputs.get(object.getId()), 
							obtainedOutputs.get(object.getId()));
				else
					showDiffWindowBox(expectedOutputs.get(object.getId()), 
							"... output too long ...", "");
			}
		});
	    testsTable.addColumn(obtainedColumn, "Obtained Output");
	    testsTable.setColumnWidth(obtainedColumn, 30, Unit.PCT);
	}

	private void configureCellBrowser() {
		testTree = new TestTreeViewModel(dataProvider);
		Builder<String> builder = new CellBrowser.Builder<String>(testTree,
				null);

		tests = builder.build();
		tests.setAnimationEnabled(true);
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

	@Override
	public void setData(final Map<String, String> data) {
		
		String lastId = null;
		
		List<SelectableOption> options = new ArrayList<>();
		for (String option : data.keySet()) {
			options.add(new SelectableOption(option, option));
			lastId = option;
		}
		lbReports.setSelections(options);

		lbReports.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {

			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> event) {
				parseMessage(data.get(event.getValue().getId()));
			}
		});
		
		if (lastId != null) {
			parseMessage(data.get(lastId));
			lbReports.setSelectedValue(options.get(options.size() - 1));
		}	
		
		if (options.size() <= 1)
			reportDiv.getElement().getStyle().setDisplay(Display.NONE);
		else
			reportDiv.getElement().getStyle().setDisplay(Display.BLOCK);
			
	}

	/**
	 * Parse xml message
	 * 
	 * @param messageXml
	 */
	private void parseMessage(String messageXml) {
		
		// parse the XML document into a DOM
		Document messageDom = null;
		try {
			messageDom = XMLParser.parse(messageXml);
		} catch (Exception e) {
			return;
		}

		Text text;
		Element element;
		
		try {
			text = (Text) messageDom
					.getElementsByTagName("programmingLanguage").item(0)
					.getFirstChild();
			if (text != null)
				language.setText(text.getData());
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not find programming "
					+ "language in DOM Tree");
		}
		
		try {
			text = (Text) messageDom.getElementsByTagName("exercise").item(0)
					.getFirstChild();
			if (text != null)
				problemId.setText(text.getData());
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not find exercise"
					+ " in DOM Tree");
		}
		
		try {
			text = (Text) messageDom.getElementsByTagName("compilationErrors")
					.item(0).getFirstChild();
			if (text != null)
				compilation.setText(text.getData());
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not find compilation"
					+ " errors in DOM Tree");
		}
		
		try {
			element = ((Element) messageDom.getElementsByTagName("summary")
					.item(0));
			mark.setText(((Element) element.getElementsByTagName("mark")
					.item(0)).getAttribute("obtainedValue"));

			classification.setText(((Text) element
					.getElementsByTagName("classify").item(0).getFirstChild())
					.getData());
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not find summary"
					+ " in DOM Tree");
		}
		
		try {
			fillTestsCellBrowser(messageDom.getElementsByTagName("test"));
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not find tests "
					+ " in DOM Tree");
		}
	}

	/**
	 * Fills the tests given in the NodeList test in a cellbrowser
	 * 
	 * @param tests
	 */
	private void fillTestsCellBrowser(NodeList tests) {
		Text text;
		Element element;
		testTableDataProvider.getList().clear();
		TestLine.lines = 0;
		List<Test> newList = new ArrayList<>();
		for (int i = 0; i < tests.getLength(); i++) {

			element = (Element) tests.item(i);
			
			String id = element.getAttribute("id");
			
			if (id == null)
				id = "TEST " + (i + 1);
			
			TestLine t = new TestLine();
			Test test = new Test(id);
			t.setName(id);
			
			text = (Text) element.getElementsByTagName("input").item(0)
					.getFirstChild();
			if (text != null)
				test.addField("Input", text.getData());
			else {
				test.addField("Input", "<span style=\"color:red\">NO INPUT</span>");
			}

			text = (Text) element.getElementsByTagName("expectedOutput")
					.item(0).getFirstChild();
			if (text != null) {
				test.addField("Expected Output", text.getData());
				t.setExpected(text.getData());
			} else {
				test.addField("Expected Output", "<span style=\"color:red\">NO OUTPUT</span>");
			}

			text = (Text) element.getElementsByTagName("obtainedOutput")
					.item(0).getFirstChild();
			if (text != null) {
				test.addField("Obtained Output", text.getData());
				t.setObtained(text.getData());
				
				if (t.getExpected().equals(""))
					t.setExpected(t.getObtained());
			} else {
				test.addField("Obtained Output", "<span style=\"color:red\">NO OUTPUT</span>");
			}

			text = (Text) element.getElementsByTagName("classify").item(0)
					.getFirstChild();
			if (text != null) {
				test.addField("Classify", text.getData());
				t.setResult(text.getData());
			}

			Element e = (Element) element.getElementsByTagName("mark").item(0);
			text = (Text) e.getFirstChild();
			if (text != null)
				test.addField("Mark Content", text.getData());
			test.addField("Obtained Value", e.getAttribute("obtainedValue"));
			test.addField("Total Value", e.getAttribute("totalValue"));

			int nEnvValues = element.getElementsByTagName("environmentValue").getLength();
			if (nEnvValues >= 2) {
				e = (Element) element.getElementsByTagName("environmentValue")
						.item(0);
				test.addField("Elapsed", e.getAttribute("label"));
			} 
			if (nEnvValues >= 3) {
				e = (Element) element.getElementsByTagName("environmentValue")
						.item(1);
				test.addField("CPU", e.getAttribute("label"));
			}
			if (nEnvValues >= 4) {
				e = (Element) element.getElementsByTagName("environmentValue")
						.item(2);
				test.addField("Memory", e.getAttribute("label"));
			}

			newList.add(test);

			if (t.getResult() == null || !t.getResult().equalsIgnoreCase("ACCEPTED"))
				testTableDataProvider.getList().add(t);
		}
		((ListDataProvider<Test>) dataProvider).setList(newList);
		
	    testsTable.setRowCount(testTableDataProvider.getList().size());
	    testsTable.setPageSize(testTableDataProvider.getList().size());
	    testsTable.redraw();
	}

	@Override
	public String getProgramName() {
		return file.getText();
	}

	@Override
	public byte[] getProgramCode() {
		return editor.getValue().getContent();
	}

	@Override
	public String getProblemId() {
		return problemId.getText();
	}

	private void setClassificationOptions() {
		result.insertItem("EVALUATING", 13);
		result.insertItem("REQUIRES_REEVALUATION", 12);
		result.insertItem("PROGRAM_SIZE_EXCEEDED", 11);
		result.insertItem("OUTPUT_LIMIT_EXCEEDED", 10);
		result.insertItem("MEMORY_LIMIT_EXCEEDED", 9);
		result.insertItem("TIME_LIMIT_EXCEEDED", 8);
		result.insertItem("INVALID_FUNCTION", 7);
		result.insertItem("RUNTIME_ERROR", 6);
		result.insertItem("COMPILE_TIME_ERROR", 5);
		result.insertItem("INVALID_SUBMISSION", 4);
		result.insertItem("PRESENTATION_ERROR", 3);
		result.insertItem("WRONG_ANSWER", 2);
		result.insertItem("ACCEPTED", 1);
	}

	@Override
	public void setProgramCode(byte[] code) {
		//new HTML(SimpleHtmlSanitizer.sanitizeHtml(code).asString()).getText().getBytes()
		editor.setValue(new MooshakValue(null, null, code));
		editor.redisplay();
	}

	@Override
	public void setFormDataProvider(FormDataProvider provider) {
		if (this.formDataProvider != null)
			this.formDataProvider.removeFormDataProvider(this);
		provider.removeFormDataProvider(this);
		provider.addFormDataProvider(this);
		this.formDataProvider = provider;
	}

	@Override
	public void setObjectId(String objectId) {
		id = objectId;
		submission.setText(id);
	}
	
	@Override
	public void refreshProviders() {
		if(formDataProvider != null)
			formDataProvider.refresh();
	}
	
	@Override
	public void setMessage(String text) {
		message.setText(text);
		
		resetMessage();
		
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		new Timer() {

			@Override
			public void run() {
				message.setText("");
				cleanupTimer = null;
			}
			
		}.schedule(MESSAGE_VIEWING_TIME);
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		MooshakValue pair = event.getValue();
		
		presenter.onChange(id,pair);
	}

	/* Buttons */

	@UiField
	Button reevaluate;

	@UiHandler(value = { "reevaluate" })
	void reevaluate(ClickEvent e) {
		if (presenter != null)
			presenter.onProgramReEvaluate();
	}

	@UiField
	Button viewStatement;

	@UiHandler(value = { "viewStatement" })
	void viewStatement(ClickEvent e) {
		popupStatement.show();
		StatementViewImpl.reloadMathJax(popupStatement.getElement());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<MooshakValue> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Populate to form's field values with a map indexed by field names
	 * 
	 * @param data a map with the field values indexed by field names
	 */
	@Override
	public void setFieldValues(Map<String,MooshakValue> data) {
		result.setValue(new MooshakValue("Classify", classification.getText().toUpperCase()));
		
		for(String fieldName: data.keySet()) {
			if(fieldName.equals("State")) {
				if(data.get(fieldName).getSimple()
						.equalsIgnoreCase("pending")) {
					finalized.setValue(new MooshakValue(fieldName, "false"));
					pending.setValue(new MooshakValue(fieldName, "true"));
				}
				else {
					pending.setValue(new MooshakValue(fieldName, "false"));
					finalized.setValue(new MooshakValue(fieldName, "true"));
				}
					
			}
			else if (fieldName.equalsIgnoreCase("Classify")) {
				if (data.get(fieldName).getSimple() != null)
					result.setValue(new MooshakValue(fieldName, data.get(fieldName)
							.getSimple().replace(" ", "_").toUpperCase()));
			}
			else if(fields.containsKey(fieldName)) {
				if(fieldName.equalsIgnoreCase("Date")) {
					try {
						fields.get(fieldName).setValue(new MooshakValue(fieldName, 
								(new Date(Long.parseLong(data.get(fieldName)
										.getSimple()) * SECONDS_TO_MILLIS
										).toString())));
					} catch (Exception e) {
						fields.get(fieldName).setValue(new MooshakValue(fieldName,
								(new Date(0)).toString()));
					}
				}
				else if(fieldName.equalsIgnoreCase("Elapsed")) {
					long elapsed = 0;
					
					try {
						if(!data.get(fieldName).getSimple().equals(""))
							elapsed = (long) (Long.parseLong(data.get(fieldName)
									.getSimple()) * SECONDS_TO_MILLIS); 

						fields.get(fieldName).setValue(new MooshakValue(fieldName, 
								(new Date(Long.parseLong(data.get("Date")
										.getSimple()) * SECONDS_TO_MILLIS
										+ elapsed)).toString()));
					} catch (Exception e) {
						fields.get(fieldName).setValue(new MooshakValue(fieldName,
								(new Date(0)).toString()));
					}
				}
				else if(!fields.get(fieldName).isEditing())
					fields.get(fieldName).setValue(data.get(fieldName));
			}
		}
	}
	
	/**
	 * Get field values as map indexed by field names
	 * @param data
	 */
	@Override
	public Map<String,MooshakValue> getFieldValues() {
		Map<String,MooshakValue> data = new HashMap<String,MooshakValue>();
		
		for(String fieldName: data.keySet()) {
			data.put(fieldName,fields.get(fieldName).getValue());
		}
		
		return data;
	}

	@Override
	public void setStatement(StatementView statementView) {
		popupStatement.setStatement(statementView);
	}

	@Override
	public String getObjectId() {
		return id;
	}
	
	/**
	 * A line in the test tabel
	 */
	public static class TestLine {
		int id;
		String name;
		String time;
		String execTime;
		String memory;
		String result = "";
		String expected = "";
		String obtained = "";

		boolean passed = false;
		
		static int lines = 0;
		
		public static final ProvidesKey<TestLine> KEY_PROVIDER = 
				new ProvidesKey<TestLine>() {
		      @Override
		      public Object getKey(TestLine item) {
		        return item == null ? null : item.getId();
		      }
		    };
		
		public TestLine() {
			id=lines;
			lines++;
		}
		
		
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}


		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}


		/**
		 * @return the time
		 */
		public String getTime() {
			return time;
		}


		/**
		 * @param time the time to set
		 */
		public void setTime(String time) {
			this.time = time;
		}


		/**
		 * @return the execTime
		 */
		public String getExecTime() {
			return execTime;
		}


		/**
		 * @param execTime the execTime to set
		 */
		public void setExecTime(String execTime) {
			this.execTime = execTime;
		}


		/**
		 * @return the memory
		 */
		public String getMemory() {
			return memory;
		}


		/**
		 * @param memory the memory to set
		 */
		public void setMemory(String memory) {
			this.memory = memory;
		}


		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}


		/**
		 * @param result the result to set
		 */
		public void setResult(String result) {
			this.result = result;
		}


		/**
		 * @return the expected
		 */
		public String getExpected() {
			return expected;
		}


		/**
		 * @param expected the expected to set
		 */
		public void setExpected(String expected) {
			this.expected = expected;
		}


		/**
		 * @return the obtained
		 */
		public String getObtained() {
			return obtained;
		}


		/**
		 * @param obtained the obtained to set
		 */
		public void setObtained(String obtained) {
			this.obtained = obtained;
		}


		/**
		 * @return the passed
		 */
		public boolean isPassed() {
			return passed;
		}


		/**
		 * @param passed the passed to set
		 */
		public void setPassed(boolean passed) {
			this.passed = passed;
		}

	}

	@Override
	public void showDiffWindowBox(String expected, String obtained, String diff) {
		
		fileDiffWindowBox.setExpectedOutput(expected);
		fileDiffWindowBox.setObtainedOutput(obtained);

		if (!diff.isEmpty())
			fileDiffWindowBox.setDifferences(diff);
		else if (result.getSelectedValue() != null &&
				result.getSelectedValue().equals("PRESENTATION_ERROR")) {
			fileDiffWindowBox.setPresentationDiff(expected, obtained);
		} else {
			fileDiffWindowBox.setDifferences("");
		}
		fileDiffWindowBox.show();
	}

	@Override
	public void setExpectedOutputs(List<String> result) {
		expectedOutputs = result;
	}

	@Override
	public void setObtainedOutputs(List<String> result) {
		obtainedOutputs = result;
	}

	@Override
	public void setProblemTestCases(Map<String, String> testCases) {
		List<IOPair> ioPairs = ioDataProvider.getList();
		
		int pos = 0;
		for(IOPair pair: ioPairs) {
			String input = pair.getInput();
			if("".equals(input)) {
				break;
			}
			pos++;
		}
		
		for (String input : testCases.keySet()) {
			String output = testCases.get(input);
			IOPair pair = ioPairs.get(pos);
			pair.setInput(input);
			//pair.setOutput(output);
			pair.setPublicTest(true);
			nonNull.add(pos);
			pos++;
		}
		
		ioDataProvider.refresh();
		iodata.redraw();
	}
	
	@UiField
	HTML validationObservations;
	
	private static RegExp errorPattern = RegExp.compile(":(\\d+):(.*)$","gm");
	private static RegExp filePattern = RegExp.compile("(.*/)(?:$|(.+?)(?:(\\.[a-zA-Z0-9]*)))","gm");

	
	@Override
	public void setObservations(String text) {
		clearObservations();
		addObservations(text);
	}
	
	@Override
	public void addObservations(String text) {
		MatchResult result = errorPattern.exec(text);
		boolean info = true;
		
		if (editor.getMode().equals(EditorMode.CODE))
			editor.getCodeEditor().clearAnnotations();
		
		while(result != null) {
			int row = Integer.parseInt(result.getGroup(1))-1;
			int column = 0;
			String error = result.getGroup(2);
			
			if (editor.getMode().equals(EditorMode.CODE))
				editor.getCodeEditor().addAnnotation(row, column, error, AceAnnotationType.ERROR);
			
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
			formattedText = text;
		else { 
			if (editor.getMode().equals(EditorMode.CODE))
				editor.getCodeEditor().setAnnotations();
			formattedText = "<pre>"+text+"</pre>";
		}
		
		validationObservations.setHTML(validationObservations.getHTML()+formattedText);
		
	}
	
	@Override
	public void addStatus(String status) {
		String color;
		
		if(status.indexOf("Accepted") != -1) 
			color="green";
		else
			color = "red";
		
		String formattedStatus = "<font color=\""+color+"\">"+status+"</font>";
		
		validationObservations.setHTML(validationObservations.getHTML() + formattedStatus);
	}
	
	@UiField
	Image feedbackBtn;
	
	@UiHandler({"feedbackBtn"})
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
		
			feedbackBtn.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
	}
	
	
	/* Command buttons */
	
	@UiField CustomImageButton validate;
	
	@UiHandler({"validate"})
	void validate(ClickEvent event) {
		presenter.validateSubmission();
	}

	String observationsHtml = ""; 
	
	@Override
	public void clearObservations() {
		validationObservations.setText("");
		feedbackBtn.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

}
