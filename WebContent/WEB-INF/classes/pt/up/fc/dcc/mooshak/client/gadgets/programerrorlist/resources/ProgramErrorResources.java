package pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ProgramErrorResources extends ClientBundle {

	public static final ProgramErrorResources INSTANCE = GWT
			.create(ProgramErrorResources.class);

	@Source("warning.png")
	ImageResource warning();

	@Source("error.png")
	ImageResource error();
}
