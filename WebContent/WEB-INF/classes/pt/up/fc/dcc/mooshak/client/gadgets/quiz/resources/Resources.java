package pt.up.fc.dcc.mooshak.client.gadgets.quiz.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("quiz.js")
	TextResource quizJS();
	
	@Source("quiz.css")
	TextResource quizCSS();
	
	@Source("correct.png")
	@ImageOptions(width=16, height=16)
	ImageResource correct();
	
	@Source("wrong.png")
	@ImageOptions(width=16, height=16)
	ImageResource wrong();
	
	@Source("assets.css")
	CssResource assetsCss();
}
