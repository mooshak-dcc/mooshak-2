package pt.up.fc.dcc.mooshak.client.form.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface FormResources extends ClientBundle {

	 public static final FormResources INSTANCE =  GWT.create(FormResources.class);

	  @Source("form.css")
	  public FormStyle style();

	  /*
	  @Source("config.xml")
	  public TextResource initialConfiguration();

	  @Source("manual.pdf")
	  public DataResource ownersManual();
	  */
}
