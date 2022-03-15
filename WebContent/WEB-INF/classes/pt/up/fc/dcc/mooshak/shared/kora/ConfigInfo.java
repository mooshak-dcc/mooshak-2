package pt.up.fc.dcc.mooshak.shared.kora;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Data transfer object configuration to Eshu
 * This object include files info to create Eshu
 * 
 * @author helder correia <hppc25@gmail.com>
 */

public class ConfigInfo implements IsSerializable{
	/**
	 * 
	 */
	private Map<String, String> editorStyle = new HashMap<String, String>();
	private Map<String, String> toolbarStyle = new HashMap<String, String>();
	private Map<String, String> vertice = new HashMap<String, String>();
	private Map<String, String> textBox = new HashMap<String, String>();
	private Map<String, String> imagesSVG = new HashMap<String, String>();
	private String nodeTypes="";
	private String EdgeTypes="";
//	private LinkedList<String> reducibles=new LinkedList<String>();  
	private String syntaxValidation="";
	
	

	public ConfigInfo() {
		super();
	}


	public Map<String, String> getEditorStyle() {
		return editorStyle;
	}

	public void setEditorStyle(Map<String, String> editorStyle) {
		this.editorStyle = editorStyle;
	}

	public Map<String, String> getToolbarStyle() {
		return toolbarStyle;
	}

	public void setToolbarStyle(Map<String, String> toolbarStyle) {
		this.toolbarStyle = toolbarStyle;
	}

	public Map<String, String> getVertice() {
		return vertice;
	}

	public void setVertice(Map<String, String> vertice) {
		this.vertice = vertice;
	}

	public Map<String, String> getTextBox() {
		return textBox;
	}

	public void setTextBox(Map<String, String> textBox) {
		this.textBox = textBox;
	}


	public String getNodeTypes() {
		return nodeTypes;
	}


	public void setNodeTypes(String nodeTypes) {
		this.nodeTypes = nodeTypes;
	}


	public String getEdgeTypes() {
		return EdgeTypes;
	}


	public void setEdgeTypes(String edgeTypes) {
		EdgeTypes = edgeTypes;
	}


	public Map<String, String> getImagesSVG() {
		return imagesSVG;
	}


	public void setImagesSVG(Map<String, String> imagesSVG) {
		this.imagesSVG = imagesSVG;
	}
	


	@Override
	public String toString() {
		return "ConfigInfo [editorStyle=" + editorStyle + ", toolbarStyle=" + toolbarStyle + ", vertice=" + vertice
				+ ", textBox=" + textBox + ", imagesSVG=" + imagesSVG + ", nodeTypes=" + nodeTypes + ", EdgeTypes="
				+ EdgeTypes + "]";
	}


//	public String getReducibles() {
//		return reducibles.toString();
//	}
//
//	public void setReducibles(LinkedList<String> reducibles) {
//		this.reducibles = reducibles;
//	}
//
//	public LinkedList<String> getReduciblesList() {
//		return reducibles;
//	}


	public String getSyntaxValidation() {
		return syntaxValidation;
	}


	public void setSyntaxValidation(String syntaxeValidation) {
		this.syntaxValidation = syntaxeValidation;
	}	
	

}
