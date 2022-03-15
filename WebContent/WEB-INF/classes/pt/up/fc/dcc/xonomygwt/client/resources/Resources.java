package pt.up.fc.dcc.xonomygwt.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);
	
	/* JQuery */
	@Source("jquery-3.2.1.min.js")
	TextResource jqueryJs();
	
	/* Xonomy resources */
	@Source("add.png")
	ImageResource addPng();
	
	@Source("bullet_arrow_down.png")
	ImageResource bulletArrowDownPng();
	
	@Source("bullet_arrow_up.png")
	ImageResource bulletArrowUpPng();
	
	@Source("callout.gif")
	ImageResource calloutGif();
	
	@Source("draghandle.gif")
	ImageResource draghandleGif();
	
	@Source("exclamation.png")
	ImageResource exclamationPng();
	
	@Source("loader.gif")
	ImageResource loaderGif();
	
	@Source("magnifier.png")
	ImageResource magnifierPng();
	
	@Source("minus.gif")
	ImageResource minusGif();
	
	@Source("plus.gif")
	ImageResource plusGif();
	
	@Source("bin_closed.png")
	ImageResource binClosedPng();
	
	@Source("sitemap.png")
	ImageResource sitemapPng();
	
	@Source("tag.png")
	ImageResource tagPng();
	
	@Source("xonomygwt.css")
	@NotStrict
	CssResource xonomyGwtCss();
	
	@Source("xonomy.js")
	TextResource xonomyJs();
	
	/* Other resources */
	@Source("helper.js")
	TextResource helperJs();
	
	/* Test resource */
	@Source("example-docspec.json")
	TextResource exampleDocspecJson();
	
	@Source("resources-docspec.json")
	TextResource resourcesDocspecJson();
	
	@Source("dl2-docspec.json")
	TextResource dl2DocspecJson();
	
	@Source("moo-docspec.json")
	TextResource mooDocspecJson();
}
