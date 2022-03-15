package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.DiagramHandler;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.ERDiagramHandler;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.JSONHandler;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.ClassDiagramHandler;

public class Parser {
	Graph graph;

	public Graph parse(String fileName) throws GraphEvalException{
		return parse(fileName, "Class");
	}
	
	public Graph parse(String fileName, String handlerType) throws GraphEvalException {
		File file = new File(fileName);

		if (fileName.matches("(.*).dia"))
			return parseGzip(file, handlerType);
		else if (fileName.matches("(.*).xml"))
			return parseXml(file, handlerType);
		else if (fileName.matches("(.*).json"))
			return parseJSON(file, handlerType);
		else
			throw new GraphEvalException("Unknown graph file type");
	}

	Graph parseGzip(File gZipFile, String handlerType) throws GraphEvalException {

		try (FileInputStream fileStream = new FileInputStream(gZipFile);
				GZIPInputStream stream = new GZIPInputStream(fileStream)) {
			return parseFile(stream, handlerType);
		} catch (IOException cause) {
			throw new GraphEvalException("Error parsing GZip file " + gZipFile,
					cause);
		}
	}

	Graph parseXml(File xmlFile, String handlerType) throws GraphEvalException {

		try (FileInputStream fileStream = new FileInputStream(xmlFile)) {
			return parseFile(fileStream, handlerType);
		} catch (IOException cause) {
			throw new GraphEvalException("Error parsing XML file " + xmlFile,
					cause);
		}
	}
	
	Graph parseJSON(File jsonFile, String handlerType) throws GraphEvalException {
		try (FileInputStream fileStream = new FileInputStream(jsonFile)) {
			//TODO handler type = EER
			handlerType = "EER";
			//*diagram esta null depois eliminar *******************************************************************************
			JSONHandler jsonHandler = new JSONHandler(fileStream, 1,null);
			return jsonHandler.parseReducible();
		} catch (IOException cause) {
			throw new GraphEvalException("Error parsing JSON file " + jsonFile,
					cause);
		} catch (JSONException cause) {
			throw new GraphEvalException("Error parsing JSON file " + jsonFile,
					cause);
		}
	}

	Graph getGraph() {
		return this.graph;
	}

	Graph parseFile(InputStream stream, String handlerType) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			DiagramHandler handler;
			if(handlerType.equals("EER"))
				handler = new ERDiagramHandler();
			else
				handler = new ClassDiagramHandler();
			
			saxParser.parse(stream, handler);

			return this.graph = handler.graph;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

