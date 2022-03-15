package pt.up.fc.dcc.mooshak.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageResources extends ClientBundle {
	  public static final ImageResources INSTANCE =  GWT.create(ImageResources.class);

	
	@Source("add.png")
	ImageResource add();
	
	@Source("copy.png")
	ImageResource copy();
	
	@Source("create.png")
	ImageResource create();
	
	@Source("destroy.png")
	ImageResource destroy();
	
	@Source("menu.png")
	ImageResource menu();
	
	@Source("help.png")
	ImageResource help();
	
	@Source("logout.png")
	ImageResource logout();
	
	@Source("feedback.png")
	ImageResource feedback();
	
	@Source("helpICPC.png")
	ImageResource helpICPC();
	
	@Source("download.png")
	ImageResource download();
	
	@Source("upload.png")
	ImageResource upload();
	
	@Source("edit.png")
	ImageResource edit();
	
	@Source("freez.png")
	ImageResource freeze();
	
	@Source("list.png")
	ImageResource list();
	
	@Source("paste.png")
	ImageResource paste();
	
	@Source("rename.png")
	ImageResource rename();
	
	@Source("print.png")
	ImageResource print();
	
	@Source("question.png")
	ImageResource question();
	
	@Source("unfreeze.png")
	ImageResource unfreeze();
	
	@Source("undo.png")
	ImageResource undo();
	
	@Source("redo.png")
	ImageResource redo();
	
	@Source("view.png")
	ImageResource view();
	
	@Source("program.png")
	ImageResource program();
	
	@Source("ask.png")
	ImageResource ask();
	
	@Source("askButton.png")
	ImageResource askButton();
	
	@Source("clearButton.png")
	ImageResource clearButton();
	
	@Source("submitButton.png")
	ImageResource submitButton();
	
	@Source("validateButton.png")
	ImageResource validateButton();
	
	@Source("printButton.png")
	ImageResource printButton();
	
	@Source("refresh.png")
	ImageResource refresh();
	
	@Source("loadFile.png")
	ImageResource loadFile();
	
	@Source("export.png")
	ImageResource export();
	
	@Source("time.png")
	ImageResource time();
	
}
