package pt.up.fc.dcc.mooshak.client.gadgets.videoviewer;

import pt.up.fc.dcc.mooshak.client.guis.enki.resources.EnkiResources;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.media.client.Video;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class VideoViewImpl extends ResizeComposite implements VideoView {
	private static final RegExp REGEX_YOUTUBE_VIDEO = RegExp.compile(
			"^(https?:\\/\\/www\\.youtube\\.com\\/watch\\/v\\/|"
			+ "https?:\\/\\/www\\.youtube\\.com\\/watch\\?v\\=)(.+)$");
	private static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed/";
	
	private static VideoUiBinder uiBinder = 
			GWT.create(VideoUiBinder.class);

	@UiTemplate("VideoView.ui.xml")
	interface VideoUiBinder extends UiBinder<Widget, VideoViewImpl> {
	}

	@UiField
	BaseStyle style;

	interface BaseStyle extends CssResource {

		String playBtn();

		String bgImage();
	}

	private Presenter presenter;

	@UiField
	ResizableHtmlPanel panel;
	
	@UiField
	ResizableHtmlPanel videoPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	/*@UiField
	Button btnShare;*/
	
	private Object player = null;
	
	public VideoViewImpl(String name, String src) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if (REGEX_YOUTUBE_VIDEO.test(src)) {
			MatchResult result = REGEX_YOUTUBE_VIDEO.exec(src);
			final String id = result.getGroup(2);
			
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					String img = null;
					int width = videoPanel.getOffsetWidth();
					if (width > 640) {
						img = "maxresdefault.jpg";
					} else if (width > 480) {
						img = "sddefault.jpg";
					} else if (width > 320) {
						img = "hqdefault.jpg";
					} else if (width > 120) {
						img = "mqdefault.jpg";
					} else if (width == 0) {
						img = "hqdefault.jpg";
					} else {
						img = "default.jpg";
					}
					
					final String thumbUrl = "https://img.youtube.com/vi/" + id + "/" + img;
					
					final HTMLPanel imgDiv = new HTMLPanel("");
					
					imgDiv.addDomHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							String frameStyleText = "width: 100%; height: 100%; ";
							
							SafeHtmlBuilder builder = new SafeHtmlBuilder()
								.append(fromTrustedString("<iframe id=\"video-" + id + "\" style=\""))
								.append(fromTrustedString(frameStyleText))
								.append(fromTrustedString("\" src=\""))
								.append(fromTrustedString(YOUTUBE_EMBED_URL + id + "?autoplay=1"))
								.append(fromTrustedString("\" frameborder=\"0\" allowfullscreen></iframe>"));
							
							HTML youtubePlayer = new HTML();
							youtubePlayer.setHTML(builder.toSafeHtml());
							videoPanel.add(youtubePlayer);
							videoPanel.remove(imgDiv);

							youtubePlayer.setWidth("100%");
							youtubePlayer.setHeight("100%");
							
							player = youtubePlayer;
						}
					}, ClickEvent.getType());

					Image imgTag = new Image(thumbUrl);
					imgDiv.setStyleName(style.bgImage());
					
					HTMLPanel playBtn = new HTMLPanel("");
					playBtn.setStyleName(style.playBtn());
					playBtn.getElement().getStyle().setBackgroundImage("url(\"" + EnkiResources.INSTANCE.youtubeVideoPlay()
							.getSafeUri().asString() + "\"");
					
					imgDiv.add(imgTag);
					imgDiv.add(playBtn);
					
					videoPanel.add(imgDiv);
				}
			});	
		}
		else {
			Video video = Video.createIfSupported();
			videoPanel.add(video);
			video.setWidth("100%");
			video.setHeight("100%");
			
			video.addSource(src);
			video.setAutoplay(true);
			video.setControls(true);
			
			this.player = video;
		}	
		
		setVideoSrc(name, src);
		
		panel.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				videoPanel.setHeight((event.getHeight() - buttonsPanel.getOffsetHeight()) 
						+ "px");
			}
		});
		new Timer() {
			
			@Override
			public void run() {
				videoPanel.setHeight((panel.getOffsetHeight() - buttonsPanel.getOffsetHeight()) 
						+ "px");
				panel.onResize();
			}
		}.schedule(1500);

		setDraggableParentProperties();
	}
	
	/**
	 * Resolving issues when clicking inside draggable parent
	 */
	public void setDraggableParentProperties() {
		panel.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());
		
		panel.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * @return the presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public void setVideoSrc(String name, String src) {
		
		updateSharingButtons(name, src);
	}

	private void updateSharingButtons(String name, String src) {
	    
		Element shareDiv = buttonsPanel.getElementById("share_box_video");
		
		if (shareDiv != null) {
		    shareDiv.setAttribute("data-url", src);
		    shareDiv.setAttribute("data-title", name);
		}
	    
		Element body = Document.get().getElementsByTagName("body").getItem(0);
	    ScriptElement sce = Document.get().createScriptElement();
	    sce.setType("text/javascript");
	    sce.setSrc("https://s7.addthis.com/js/300/addthis_widget.js#pubid=ra-55b8ec3a5bd800cc");
	    sce.setPropertyString("async", "async");
	    body.appendChild(sce);
	}
	
}
