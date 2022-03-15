package pt.up.fc.dcc.quizEditor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);
	

	
//	@Source("plus.gif")
//	ImageResource plusGif();
//	
	@Source("style.css")
	@NotStrict
	CssResource styleCss();
	
	
	
	@Source("Editor.js")
	TextResource EditorJs();
	
	/* Other resources */
	@Source("importEditor.js")
	TextResource importEditorJs();
	
	@Source("XMLParser.js")
	TextResource XMLParserJs();
	
	
}
