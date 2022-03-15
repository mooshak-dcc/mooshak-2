package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.types.Problem.ProblemDifficulty;
import pt.up.fc.dcc.mooshak.content.types.Problem.ProblemType;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;

public class Problems extends PersistentContainer<Problem> {
	private static final long serialVersionUID = 1L;

	public enum Presents {
		RADIO, MENU, LIST, TEXT;
		
		public String toString() {
			return super.toString().toLowerCase();
		}
	};

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(name = "Presents", type = AttributeType.MENU, tip = "Type of selector to present problems")
	private Presents presents = null;

	@MooshakAttribute(name = "Problem", type = AttributeType.CONTENT, tip = "Problem folder")
	private Void problem = null;

	/*-------------------------------------------------------------------*\
	 * 		            Operations                                    *
	\*-------------------------------------------------------------------*/

	@MooshakOperation(name = "Update Problems", inputable = false, 
			tip = "Sends an Event to all listeners to update problems")
	private CommandOutcome sendProblemsUpdate() {
		CommandOutcome outcome = new CommandOutcome();

		Contest contest = null;
		try {
			contest = getParent();
		} catch (MooshakContentException e1) {
			outcome.setMessage("Could not send problems");
			return outcome;
		}

		if (contest.getContestStatus().equals(ContestStatus.RUNNING)) {
			for (Problem problem : getContent()) {
				try {
					problem.broadcastProblemChanges();
				} catch (MooshakContentException e) {
					e.printStackTrace();
				}
			}
		}

		outcome.setMessage("Problems sent");

		return outcome;
	}

	@MooshakOperation(
			name = "Import Remote Problem", 
			inputable = true, 
			tip = "Import a problem from remote repository")
	private CommandOutcome importRemote(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();

		String url = context.getValue("url");
		if (url != null) {
			boolean result = createProblemFromRemoteURL(url);

			if (result)
				outcome.setMessage("Problem successfully imported");
			else
				outcome.setMessage("Error importing problem");
		} else {
			outcome.setMessage("No url provided!");
		}

		return outcome;
	}

	// -------------------- Helper Functions ----------------------//

	/**
	 * Creates a problem from a remote zip file
	 * 
	 * @param url
	 * @return
	 */
	public boolean createProblemFromRemoteURL(String url) {

		try {
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			Problem problem = null;
			zipinputstream = new ZipInputStream(new FileInputStream(url));

			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null) {
				String entryName = zipentry.getName();

				String extension = "";
				int i = entryName.lastIndexOf('.');
				if (i > 0)
					extension = entryName.substring(i + 1);

				if (extension.equals("xml")) {
					StringBuilder s = new StringBuilder();
					byte[] buffer = new byte[1024];
					int read = 0;

					while ((read = zipinputstream.read(buffer, 0, 1024)) >= 0)
						s.append(new String(buffer, 0, read));

					try {
						problem = setupProblemFromXML(s.toString());
					} catch (Exception e) {
						zipinputstream.closeEntry();
						zipinputstream.close();
						e.printStackTrace();
						return false;
					}

					break;
				}
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();
			}

			zipinputstream.close();

			extractProblemFiles(problem, url);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void extractProblemFiles(Problem problem, String url) {
		try {
			String destinationname = getAbsoluteFile().toString() +  
					File.separator + problem.getId().toString();
			byte[] buf = new byte[1024];
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new FileInputStream(url));

			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null) {
				String entryName = zipentry.getName();
				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(destinationname + File.separator 
						+ entryName);

				if (newFile.isDirectory())
					break;

				new File(newFile.getParent()).mkdirs();

				fileoutputstream = new FileOutputStream(
						newFile);

				while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}

			zipinputstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a problem from a XML Mooshak-like problem file
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Problem setupProblemFromXML(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));

		String id = null, name = null;

		Node problemNode = doc.getElementsByTagName("Problem").item(0);
		Node node = null;
		if ((node = problemNode.getAttributes().getNamedItem("xml:id")) == null)
			throw new Exception("Id not found!");
		id = node.getNodeValue();
		if ((node = problemNode.getAttributes().getNamedItem("Name")) == null)
			throw new Exception("Name not found!");
		name = node.getNodeValue();

		try {
			AdministratorManager.getInstance().destroyMooshakObject(getPath()
					.toString() + File.separator + id);
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "File does not yet exists");
		}
		
		AdministratorManager.getInstance().createMooshakObject(
				getPath().toString(), id);

		Problem problem = find(name);
		problem.setName(name);

		if ((node = problemNode.getAttributes().getNamedItem("Fatal")) != null)
			problem.setFatal(node.getNodeValue());
		if ((node = problemNode.getAttributes().getNamedItem("Warning")) != null)
			problem.setWarning(node.getNodeValue());
		if ((node = problemNode.getAttributes().getNamedItem("Color")) != null)
			problem.setColor(Color.getColor(node.getNodeValue()));
		if ((node = problemNode.getAttributes().getNamedItem("Title")) != null)
			problem.setTitle(node.getNodeValue());
		if ((node = problemNode.getAttributes().getNamedItem("Difficulty")) != null)
			problem.setDifficulty(ProblemDifficulty.valueOf(node.getNodeValue()
					.toUpperCase()));
		if ((node = problemNode.getAttributes().getNamedItem("Type")) != null)
			problem.setType(ProblemType.valueOf(node.getNodeValue()
					.toUpperCase()));
		if ((node = problemNode.getAttributes().getNamedItem("Description")) != null)
			problem.setHtmlDescription(Paths.get(getAbsoluteFile().toString(),
					node.getNodeValue()));
		if ((node = problemNode.getAttributes().getNamedItem("PDF")) != null)
			problem.setPdfDescription(Paths.get(getAbsoluteFile().toString(),
					node.getNodeValue()));
		if ((node = problemNode.getAttributes().getNamedItem("Environment")) != null)
			problem.setEnvironment(Paths.get(getAbsoluteFile().toString(),
					node.getNodeValue()));
		if ((node = problemNode.getAttributes().getNamedItem("Timeout")) != null)
			problem.setTimeout(Integer.parseInt(node.getNodeValue()));
		if ((node = problemNode.getAttributes()
				.getNamedItem("Static_corrector")) != null)
			problem.setStaticCorrector(node.getNodeValue());
		if ((node = problemNode.getAttributes().getNamedItem(
				"Dynamic_corrector")) != null)
			problem.setDynamicCorrector(node.getNodeValue());
		if ((node = problemNode.getAttributes().getNamedItem(
				"Dynamic_corrector")) != null)
			problem.setDynamicCorrector(node.getNodeValue());
		if ((node = problemNode.getAttributes().getNamedItem(
				"Original_location")) != null)
			problem.setOriginalLocation(node.getNodeValue());
		
		try {
			if ((node = problemNode.getAttributes().getNamedItem("Start")) != null)
				problem.setStart(DateFormat.getDateInstance().parse(
						node.getNodeValue()));
		} catch (ParseException e) {
			problem.setStart(new Date());
		}
		
		try {
			if ((node = problemNode.getAttributes().getNamedItem("Stop")) != null)
				problem.setStop(DateFormat.getDateInstance().parse(
						node.getNodeValue()));
		} catch (ParseException e) {
			problem.setStop(new Date());
		}
		problem.save();

		NodeList tests = doc.getElementsByTagName("Test");
		Node testNode = null;
		for (int i = 0; i < tests.getLength(); i++) {
			testNode = tests.item(i);
			setupTestFromXMLNode(testNode, problem);
		}

		return problem;
	}

	/**
	 * Creates a test from a XML Mooshak-like problem file test node
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Test setupTestFromXMLNode(Node testNode, Problem problem)
			throws Exception {

		String id = null;

		Node node = null;
		if ((node = testNode.getAttributes().getNamedItem("xml:id")) == null)
			throw new Exception("Test id not found!");
		id = node.getNodeValue().substring(
				node.getNodeValue().lastIndexOf(".") + 1);

		Tests tests = problem.open("tests");

		AdministratorManager.getInstance().createMooshakObject(
				tests.getPath().toString(), id);

		Test test = tests.find(id);

		if ((node = testNode.getAttributes().getNamedItem("Fatal")) != null)
			test.setFatal(node.getNodeValue());
		if ((node = testNode.getAttributes().getNamedItem("Warning")) != null)
			test.setWarning(node.getNodeValue());
		if ((node = testNode.getAttributes().getNamedItem("args")) != null)
			test.setArgs(node.getNodeValue());
		if ((node = testNode.getAttributes().getNamedItem("input")) != null)
			test.setInput(Paths.get(test.getAbsoluteFile().toString(),
					node.getNodeValue()));
		if ((node = testNode.getAttributes().getNamedItem("output")) != null)
			test.setOutput(Paths.get(test.getAbsoluteFile().toString(),
					node.getNodeValue()));
		if ((node = testNode.getAttributes().getNamedItem("context")) != null)
			test.setContext(Paths.get(test.getAbsoluteFile().toString(),
					node.getNodeValue()));
		if ((node = testNode.getAttributes().getNamedItem("Points")) != null)
			if (!node.getNodeValue().equals(""))
				test.setPoints(Integer.parseInt(node.getNodeValue()));
		if ((node = testNode.getAttributes().getNamedItem("Feedback")) != null)
			test.setFeedback(node.getNodeValue());
		if ((node = testNode.getAttributes().getNamedItem("Show")) != null)
			if (!node.getNodeValue().equals(""))
				test.setShow("YES".equals(node.getNodeValue().toUpperCase()));
		test.save();
		return test;
	}

	// -------------------- Setters and getters ----------------------//

	/**
	 * get fatal errors messages
	 * 
	 * @return
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;

	}

	/**
	 * Set fatal error messages
	 * 
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * get warning errors messages
	 * 
	 * @return
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;

	}

	/**
	 * Set warning error messages
	 * 
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Type of widget to present problem selector
	 * 
	 * @return
	 */
	public Presents getPresents() {
		if (presents == null)
			return Presents.RADIO;
		else
			return presents;
	}

	/**
	 * Set type of widget to present problem selector. Use {@code null} to set
	 * the default
	 * 
	 * @return
	 */
	public void setPresents(Presents presents) {
		this.presents = presents;
	}

}
