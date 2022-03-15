package pt.up.fc.dcc.eshu.client.eshugwt;

import static pt.up.fc.dcc.eshu.client.eshugwt.JSResources.INSTANCE;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Eshu extends Composite {
	private static final int DEFAULT_WIDTH = 700;
	private static final int DEFAULT_HEIGHT = 400;
	
	private static EshuUiBinder uiBinder = GWT
			.create(EshuUiBinder.class);

	@UiTemplate("Eshu.ui.xml")
	interface EshuUiBinder extends UiBinder<Widget, Eshu> {
	}

	private static int nextId = 0;

	private final String elementId;
	
	@UiField
	FlowPanel panel;
	
	JavaScriptObject eshu;
	
	private Element divElement;
	
	private int width;
	private int height;

//	public Eshu() {
//		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
//	}
	
	public Eshu() {
		//Window.alert("21");
		initWidget(uiBinder.createAndBindUi(this));
		elementId = "_Eshu" + nextId++;
		divElement = panel.getElement();
		divElement.setId(elementId);
		injectJavaScriptFiles();
		//Window.alert("21");

		startEshu();
	//	Window.alert("22");
		
		Event.addNativePreviewHandler(new NativePreviewHandler() {
		    @Override
		    public void onPreviewNativeEvent(NativePreviewEvent event) {
		        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE) {
		            if (event.getNativeEvent().getEventTarget() != null) {
		                Element as = Element.as(event.getNativeEvent().getEventTarget());
		                if (as == RootPanel.getBodyElement()) {
		                    event.getNativeEvent().preventDefault();
		                }
		            }
		 
		        }
		    }
		});
	}

	/**
	 * Inject JS files stored as resources in script elements of top window
	 */
	private void injectJavaScriptFiles() {
		for(TextResource textResource: Arrays.asList(
				INSTANCE.languageJS(),
				INSTANCE.eshu(),
				INSTANCE.Configuration(),
				INSTANCE.quadTreeJS(),
				INSTANCE.graphJS(),
				INSTANCE.boxJS(),
				INSTANCE.imporExportJSONJS(),
				INSTANCE.algorithmDispersalJS(),
				INSTANCE.contextMenuJS(),
				INSTANCE.rectangleSelectionJS(),
				INSTANCE.textBoxJS(), 
				INSTANCE.initJS(),
				INSTANCE.tollbarJS(),
				INSTANCE.mapJS(),
				INSTANCE.layoutJS(),
				INSTANCE.eventsJS(),
				INSTANCE.FormatPanelJS(),
				INSTANCE.edgeJS(),
				INSTANCE.layoutJS(),
				INSTANCE.nodeTypeJS(),
				INSTANCE.edgeTypeJS(),
				INSTANCE.verticesJS(),
				INSTANCE.rectangleJS(),
				INSTANCE.containerTextboxJS(),
				INSTANCE.complexVerticeJS(),
				INSTANCE.xMLParserJS(),
				INSTANCE.commandJS(),
				INSTANCE.commandStackJS(),
				INSTANCE.insertJS(),
				INSTANCE.removeJS(),
				INSTANCE.moveJS(),
				INSTANCE.pasteJS(),
				INSTANCE.resizeJS(),
				INSTANCE.changeTypeJS()
				
				 )) {
			// log(textResource.getName());
			ScriptInjector
				.fromString(textResource.getText())
				.setWindow(ScriptInjector.TOP_WINDOW)
				.inject();
		}
	}
	
	public native void log(String text) /*-{
	
		$wnd.console.log(text);
	}-*/;
	
	private native void startEshu() /*-{
		var div = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::divElement;
		var eshu = new $wnd.Eshu(div);
	
	
		this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu = eshu;
	}-*/;
	
	
	/**
	 * Resize the widget to the width and height specified
	 * 
	 * @param width The desired width for the graph
	 * @param height The desired height for the graph
	 */
	public native void resize(int width, int height) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.resize(width, height);
	}-*/;
	
	/**
	 * Resize the widget to the width and height specified via setters
	 */
	public void resize() {
		resize(this.width, this.height);
	}
	
	public native void setGridVisible(boolean value) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setGridVisible(value);
	}-*/;
	
	
	public native void setGridLineColor(String color) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setGridLineColor(color);
	}-*/;	
	
	
	public native void setPosition(String position) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setPosition(position);
	}-*/;	
	
	
	public native void setToolbarBorderWidth(String borderWidth) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setToolbarBorderWidth(borderWidth);
	}-*/;
	
	
	public native void setToolbarBorderColor(String color) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setToolbarBorderColor(color);
	}-*/;
	
	
	public native void setToolbarBackgroundColor(String color) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setToolbarBackgroundColor(color);
	}-*/;
	
	
	public native void createNodeTypes(String listNodeTypes) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.createNodeTypes(listNodeTypes);
	}-*/;
	
	public native void createEdgeType(String listEdgeTypes) /*-{
	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
	eshu.createEdgeTypes(listEdgeTypes);
}-*/;
	
	
	public native void delete() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		
		eshu.deleteElement();
	}-*/;
	
	public native void redraw() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		
		eshu.draw();
	}-*/;
	
	
	
	
	/**
	 * 
	 */
	public native void clear() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.clear();
	}-*/;
	
	/**
	 * 
	 */
	public native void copy() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.copy();
	}-*/;
	
	/**
	 * 
	 */
	public native void paste() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.paste();
	}-*/;
	
//	public native void chooseTypeEdge(int type) /*-{
//		var eshu = this.@pt.up.fc.dcc.eshu.client.Eshu::eshu;
//	
//		eshu.chooseTypeEdge(type);
//	}-*/;
	
	

	public native String exportGraphEshu() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
	
		return eshu.exportGraph();
	}-*/;

	public native Void importGraphEshu(String value) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		//eshu.creatObjectGraph(value);
		eshu.importGraph(value);
	}-*/;
	
	public native Void importDiff(String value) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
	//eshu.creatObjectGraph(value);
		eshu.importGraphDiff(value);
	}-*/;
//	
//
//	
//	
//	public native void setGrid(boolean value) /*-{
//	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
//	eshu.setGrid(value);
//	
//}-*/;
	
	public native void showToolbar(boolean value) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.showToolbar(value);
		
	}-*/;
	
	public native void setSyntaxValidation(String value) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setSyntaxValidation(value);
	}-*/;
//	
//	public native void keepSelectedDraw(boolean value) /*-{
//	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
//	eshu.setTypeNodedrawLook(value);
//	
//}-*/;
//	
//	
	public native void setPositionHorizontal() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setPositionHorizontal();
	}-*/;
	
	public native void setPositionVertical() /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setPositionVertical();
	}-*/;
	
	public native void setImageSVG(String key, String image) /*-{
		var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
		eshu.setImageSVG(key,image);
	}-*/;
	
	
	public native void createNodeTypeArrowToolbar() /*-{
	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
	eshu.createNodeTypeArrow();
	}-*/;
	
	public native void resetEshu() /*-{
	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
	eshu.resetEshu();
	}-*/;
	
	
	public native void initEshu() /*-{
	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
	eshu.init();
	}-*/;
	
	
	
	
	
//	
//	
//	public native void setShowNodeInfo(boolean value) /*-{
//	var eshu = this.@pt.up.fc.dcc.eshu.client.eshugwt.Eshu::eshu;
//	eshu.setShowNodeInfo(value);
//}-*/;

	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
}
