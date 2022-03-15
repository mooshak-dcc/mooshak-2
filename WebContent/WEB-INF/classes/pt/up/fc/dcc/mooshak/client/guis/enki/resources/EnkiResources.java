package pt.up.fc.dcc.mooshak.client.guis.enki.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface EnkiResources extends ClientBundle {

	public static final EnkiResources INSTANCE = GWT
			.create(EnkiResources.class);

	@Source("close.png")
	ImageResource close();

	@Source("blank.png")
	ImageResource blank();
	
	@Source("backward.png")
    ImageResource back();

    @Source("forward.png")
    ImageResource next();

    @Source("menu.png")
    ImageResource menu();

    @Source("multimedia_resource.png")
    ImageResource multimedia();

    @Source("book_resource.png")
    ImageResource book();

    @Source("exercise_resource.png")
    ImageResource exercise();

    @Source("media-skipforward.png")
    ImageResource mediaSkipForward();

    @Source("media-skiptoend.png")
    ImageResource mediaSkipToEnd();

    @Source("media-skipback.png")
    ImageResource mediaSkipBack();

    @Source("media-skiptostart.png")
    ImageResource mediaSkipToStart();

    @Source("media-stop.png")
    ImageResource mediaStop();

    @Source("media-play.png")
    ImageResource mediaPlay();

    @Source("media-pause.png")
    ImageResource mediaPause();

    @Source("tick.png")
    ImageResource tick();

    @Source("star.png")
    ImageResource star();

    @Source("auxiliary.png")
    ImageResource auxiliary();

    @Source("full_screen.png")
    ImageResource fullScreen();

    @Source("exit_full_screen.png")
    ImageResource exitFullScreen();

    @Source("eshu_properties.png")
    ImageResource eshuProperties();

    @Source("copy.png")
    ImageResource copy();

    @Source("paste.png")
    ImageResource paste();

    @Source("youtube-video-play.png")
    ImageResource youtubeVideoPlay();
}
