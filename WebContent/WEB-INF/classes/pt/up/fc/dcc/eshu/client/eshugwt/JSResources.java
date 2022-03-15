package pt.up.fc.dcc.eshu.client.eshugwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface JSResources extends ClientBundle {
	public static final JSResources INSTANCE = GWT.create(JSResources.class);
	
	@Source("scripts/Eshu/Language.js")
	TextResource languageJS();

	@Source("scripts/Graph/Box.js")
	TextResource boxJS();
	
	@Source("scripts/Graph/QuadTree.js")
	TextResource quadTreeJS();
	
	@Source("scripts/Graph/Graph.js")
	TextResource graphJS();
	
	@Source("scripts/Graph/Vertices.js")
	TextResource verticesJS();

	@Source("scripts/Graph/Edge.js")
	TextResource edgeJS();
	
	@Source("scripts/Graph/RectangleSelection.js")
	TextResource rectangleSelectionJS();
	
	@Source("scripts/Graph/ContainerTextbox.js")
	TextResource containerTextboxJS();
	
	@Source("scripts/Graph/Rectangle.js")
	TextResource rectangleJS();
	
	@Source("scripts/Graph/ComplexVertice.js")
	TextResource complexVerticeJS();
	
	@Source("scripts/Graph/TextBox.js")
	TextResource textBoxJS();
	
	@Source("scripts/Eshu/Init.js")
	TextResource initJS();
	
	@Source("scripts/Eshu/Eshu.js")
	TextResource eshu();
	
	@Source("scripts/Eshu/Configuration.js")
	TextResource Configuration();
	
	@Source("scripts/Eshu/Events.js")
	TextResource eventsJS();
	
	@Source("scripts/Eshu/FormatPanel.js")
	TextResource FormatPanelJS();
	
	@Source("scripts/Eshu/Layout.js")
	TextResource layoutJS();
	
	@Source("scripts/Eshu/NodeType.js")
	TextResource nodeTypeJS();
	
	@Source("scripts/Eshu/EdgeType.js")
	TextResource edgeTypeJS();
	
	@Source("scripts/Eshu/ContextMenu.js")
	TextResource contextMenuJS();

	@Source("scripts/Eshu/Map.js")
	TextResource mapJS();
	
	@Source("scripts/Eshu/XMLParser.js")
	TextResource xMLParserJS();

	@Source("scripts/Eshu/Tollbar.js")
	TextResource tollbarJS();
	
	@Source("scripts/Eshu/ImporExportJSON.js")
	TextResource imporExportJSONJS();

	@Source("scripts/Eshu/AlgorithmDispersal.js")
	TextResource algorithmDispersalJS();
	

	@Source("scripts/Commands/Command.js")
	TextResource commandJS();
	
	@Source("scripts/Commands/CommandStack.js")
	TextResource commandStackJS();
	
	@Source("scripts/Commands/Insert.js")
	TextResource insertJS();
	
	@Source("scripts/Commands/Remove.js")
	TextResource removeJS();

	@Source("scripts/Commands/Move.js")
	TextResource moveJS();
	
	@Source("scripts/Commands/Paste.js")
	TextResource pasteJS();
	
	@Source("scripts/Commands/Resize.js")
	TextResource resizeJS();
	
	@Source("scripts/Commands/ChangeType.js")
	TextResource changeTypeJS();
	
	
	
}