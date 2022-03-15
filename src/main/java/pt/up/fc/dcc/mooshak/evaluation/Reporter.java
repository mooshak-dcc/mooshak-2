package pt.up.fc.dcc.mooshak.evaluation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.EnvironmentValueType;
import pt.up.fc.dcc.mooshak.content.erl.EnvironmentValuesType;
import pt.up.fc.dcc.mooshak.content.erl.ExerciseType;
import pt.up.fc.dcc.mooshak.content.erl.MarkType;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.erl.SummaryType;
import pt.up.fc.dcc.mooshak.content.erl.SummaryType.Feedback;
import pt.up.fc.dcc.mooshak.content.erl.SummaryType.Feedback.Item;
import pt.up.fc.dcc.mooshak.content.erl.TestType;
import pt.up.fc.dcc.mooshak.content.erl.TestsType;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.MooshakTypeException;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Report;
import pt.up.fc.dcc.mooshak.content.types.Reports;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.util.StringEscapeUtils;
import pt.up.fc.dcc.mooshak.evaluation.ExecutionResourceUsage.UsageVars;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer.TestRun;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * A reporter creates creates a report using shared types built from 
 * the XML Schema definition (XSD) of the Evaluation Report Language (ERL) 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Reporter {
	
	private static final String TEST_ORDER = "TestOrder";

	static final String ERL_PACKAGE = "pt.up.fc.dcc.mooshak.content.erl";

	static final int SMALL_OUTPUT = 500;

	static final String LARGE_OUTPUT_MESSAGE = 
			"...\n\nOutput too long: truncating";
	
	private ReportType report = null;
	private Submission submission;
	private Map<Integer,TestType> synchronizedTestMap = null;
	private Feedback feedback = null;
	
	public Reporter(Submission submission) {
		this.submission = submission;
	}
	
	public void start() {
		report = new ReportType();
		synchronizedTestMap = 
				Collections.synchronizedMap(new HashMap<Integer,TestType>());
	}

	/**
	 * Report on programming language
	 * @param language
	 */
	public void setProgrammingLanguage(Language language) {
		String name;
		
		if(language == null)
			name = "unknown language";
		else
			name = language.getName();
		
		report.setProgrammingLanguage(StringEscapeUtils.removeInvalidChars(name));
	}
	
	/**
	 * Add feedback lines to report
	 * @param lines
	 */
	public void setFeedback(List<String> lines) {
		if(lines != null && lines.size() > 0) {
			feedback = new Feedback();
		
			List<Item> items = feedback.getItem();
			for(String line: lines) {
				Item item = new Item();
			
				item.setContent(StringEscapeUtils.removeInvalidChars(line));
				items.add(item);
			}
		}
	}
	
	/**
	 * Report exercise 
	 * @param problem
	 */
	public void setExercise(Problem problem) {
		String title;
		String url = "TODO"; // TODO provide exercise URLs
		
		if(problem == null) 
			title = " ah-hoc exercise or problem";
		else
			title = problem.getTitle();
		
		
		ExerciseType exercise = new ExerciseType();
		exercise.setValue(StringEscapeUtils.removeInvalidChars(title));
		exercise.setHref(StringEscapeUtils.removeInvalidChars(url));

		report.setExercise(exercise);
	}
	
	public void setCompilationErrors(String message) {
		report.setCompilationErrors(StringEscapeUtils
				.removeInvalidChars(message));
	}
	
	/**
	 * Add data on test run execution to the evaluation report
	 * @param parameters
	 * @param testRun
	 * @param usage
	 * @throws MooshakTypeException
	 * @throws MooshakSafeExecutionException
	 */
	 void addTest(EvaluationParameters parameters, TestRun testRun,
			ExecutionResourceUsage usage) throws MooshakTypeException,
			MooshakSafeExecutionException {
		
		TestType testType = new TestType();
		testType.setId(parameters.getTestId());
		testType.setInput(StringEscapeUtils.removeInvalidChars(
				getFileContent(parameters.getInput())));

		testType.setClassify(StringEscapeUtils.removeInvalidChars(
				testRun.classification.toString()));
		
		if(testRun.classification == Classification.EVALUATION_SKIPPED)
			return;
		
		testType.setExpectedOutput(StringEscapeUtils.removeInvalidChars(
				getFileContent(parameters.getExpected())));
		testType.setObtainedOutput(StringEscapeUtils.removeInvalidChars(
				getFileContent(parameters.getObtained())));


		EnvironmentValuesType environmentValues = new EnvironmentValuesType();
		List<EnvironmentValueType> environmentValuesList = 
				environmentValues.getEnvironmentValue();		
		for(UsageVars var: UsageVars.values()) {
			EnvironmentValueType value = new EnvironmentValueType();

			if(usage != null) {
				value.setName(StringEscapeUtils.removeInvalidChars(var.toString()));
				value.setValue(StringEscapeUtils.removeInvalidChars(usage
						.getUsage(var).toString()));
				environmentValuesList.add(value); 		
			}
		}
		
		EnvironmentValueType testOrder = new EnvironmentValueType();
		testOrder.setName(StringEscapeUtils.removeInvalidChars(TEST_ORDER));
		testOrder.setValue(StringEscapeUtils.removeInvalidChars(
				Integer.toString(parameters.getTestOrder())));
		environmentValuesList.add(testOrder);
		
		testType.setEnvironmentValues(environmentValues);
		
		// TODO Change mark to integer and improve marks with total value and content 
		MarkType markType = new MarkType();
		markType.setObtainedValue(StringEscapeUtils.removeInvalidChars(""+submission.getMark()));
		markType.setTotalValue(StringEscapeUtils.removeInvalidChars("TotalValue?"));
		markType.setContent(StringEscapeUtils.removeInvalidChars("content?"));
		testType.setMark(markType);
		
		synchronizedTestMap.put(parameters.getTestOrder(),testType);
	}
	
	/**
	 * Reads content of file and returns it as a string.
	 * The empty string is returned if path is null.
	 * large files are truncated and only their first lines returned,
	 * with a message at the end explaining it.
	 * 
	 * @param path
	 * @return
	 * @throws MooshakTypeException
	 */
	String getFileContent(Path path) throws MooshakTypeException {
		String text = "";
		
		try {
			if (path == null)
				text = "";
			else if(Files.size(path) < SMALL_OUTPUT)
				text = PersistentCore
						.getAbsoluteFileContentGuessingCharset(path);
			else {
				String line;
				int size = 0;
				
				try(BufferedReader reader = Files.newBufferedReader(path)) {
					while(size < SMALL_OUTPUT && 
							(line = reader.readLine()) != null) {
						text += line + "\n";
						size += line.length();
						size++;
					}
				}
				text += LARGE_OUTPUT_MESSAGE;
			}
		} catch (IOException | MooshakContentException cause) {
			throw new MooshakTypeException("reading content of " + path,cause);
		}
		
		return text;
	}

	/**
	 * Conclude the report, adding all pending tests
	 */
	void conclude() {
		SummaryType summary = new SummaryType();
		summary.setClassify(StringEscapeUtils.removeInvalidChars(submission
				.getClassify().toString()));
		
		if(feedback != null)
			summary.setFeedback(feedback);
		
		MarkType markType = new MarkType();
		markType.setObtainedValue(StringEscapeUtils.removeInvalidChars(
				""+submission.getMark()));

		summary.setMark(markType);
		report.setSummary(summary);

		TestsType testsElement = new TestsType();
		List<TestType> testList = testsElement.getTest();
		// Copy synchronized map content in synchronized block
		synchronized (synchronizedTestMap) {
			List<Integer> orders = 
					new ArrayList<Integer>(synchronizedTestMap.keySet());
			Collections.sort(orders);
			
			for (Integer order: orders)
				testList.add(synchronizedTestMap.get(order));
		}
		
		report.setTests(testsElement);
	}
	
	/**
	 * Returns all reports as a Java object of a shared type.
	 * If object is unavailable then it is parsed from an XML file
	 * 
	 * @return
	 * @throws MooshakSafeExecutionException 
	 */
	public List<ReportType> getAllReports() throws MooshakException {
		
		Reports reports = submission.open("reports");
		
		List<ReportType> reportsList = new ArrayList<>();
		
		if (reports.find("1") == null)
			reportsList.add(getReport(1));
		for (Report report : reports.getContent()) {
			reportsList.add(getReport(Integer.parseInt(report.getIdName())));
		}
		return reportsList;
	}
	
	/**
	 * Return a report as a Java object of a shared type.
	 * If object is unavailable then it is parsed from an XML file
	 * 
	 * @return
	 * @throws MooshakSafeExecutionException 
	 */
	public ReportType getReport(int reportNumber) throws MooshakException {
		if(report == null) {
			
			Path reportFileName = Submission.REPORT_FILE_NAME;
			Path absPath = null;
			if (reportNumber == 1 && Files.exists(PersistentObject.getAbsoluteFile(
					submission.getReportPath()))) 
				absPath = PersistentCore.getAbsoluteFile(submission.getReportPath());
			else
				absPath = PersistentCore.getAbsoluteFile(Paths.get(reportNumber + "",
						reportFileName.toString()));
			
			JAXBElement<ReportType> document;
			try(InputStream stream = Files.newInputStream(absPath)) {
				
				JAXBContext context = JAXBContext.newInstance( ERL_PACKAGE );
			    Unmarshaller unmarshaller = context.createUnmarshaller();
				
				@SuppressWarnings("unchecked")
				JAXBElement<ReportType> unmarshal = (JAXBElement<ReportType>) 
					unmarshaller.unmarshal(stream);
				document = unmarshal;
			} catch (IOException | JAXBException cause) {
				throw new MooshakException("parsing report document",cause);
			} 
			report = document.getValue();
		}
		return report;
	}

	/**
	 * Save report in XML file
	 * @throws MooshakSafeExecutionException
	 */
	public void save() throws Exception {
		
		int reportNumber = 1;
		if (submission.getReportPath() != null && 
				Files.exists(PersistentObject.getAbsoluteFile(submission.getReportPath())))
			reportNumber++;

		Reports reports = null;
		try {
			reports = submission.open("reports");
		} catch (Exception e) {
			reports = submission.create("reports", Reports.class);
		}
		
		reportNumber += reports.getContent().size();
		String moName = reportNumber + "";
		AdministratorManager.getInstance().createMooshakObject(reports.getPath().toString(),
				reportNumber + "");
		
		Path reportFileName = Submission.REPORT_FILE_NAME;
		
		Path file = PersistentCore.getAbsoluteFile(Paths.get(reports.getPath().toString(),
				moName, reportFileName.toString()));
		
		try(OutputStream stream = Files.newOutputStream(file)) {
			JAXBContext context = JAXBContext.newInstance( ERL_PACKAGE );
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty(
					Marshaller.JAXB_FORMATTED_OUTPUT, 
					Boolean.TRUE );
			marshaller.marshal(report, stream);
				
		} catch (IOException | JAXBException cause) {
				String message = "Error serializing report document";
				throw new MooshakContentException(message,cause);
		}

		Report report = reports.open(moName);
		report.setReportPath(reportFileName);
		report.save();
	}

}
