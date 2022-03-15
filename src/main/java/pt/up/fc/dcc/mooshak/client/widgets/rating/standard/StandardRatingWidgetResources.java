package pt.up.fc.dcc.mooshak.client.widgets.rating.standard;


import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StandardRatingWidgetResources extends ClientBundle {

	@Source("starSelected.png")
	ImageResource starSelected();

	@Source("starSelectedLeft.png")
	ImageResource starSelectedLeft();

	@Source("starSelectedRight.png")
	ImageResource starSelectedRight();

	@Source("starUnselected.png")
	ImageResource starUnselected();

	@Source("starUnselectedLeft.png")
	ImageResource starUnselectedLeft();

	@Source("starUnselectedRight.png")
	ImageResource starUnselectedRight();

	@Source("starHover.png")
	ImageResource starHover();

	@Source("starHoverLeft.png")
	ImageResource starHoverLeft();

	@Source("starHoverRight.png")
	ImageResource starHoverRight();

	@Source("clear.png")
	ImageResource clear();
}
