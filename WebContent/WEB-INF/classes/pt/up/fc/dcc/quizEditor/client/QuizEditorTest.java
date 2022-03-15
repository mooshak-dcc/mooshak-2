package pt.up.fc.dcc.quizEditor.client;

import pt.up.fc.dcc.quizEditor.client.wrapper.QuizEditor;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QuizEditorTest implements EntryPoint { 
	
	// isto é o que vais no Mooshak

	
	public void onModuleLoad() {
		
		Panel root = RootPanel.get();
		final QuizEditor quizEditor = QuizEditor.getInstance();
		
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(quizEditor);
		root.add(horizontalPanel);
		
		quizEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				// aqui chamavas o transformador
				//Window.alert(event.getValue());
				
			}
		});
		
	}
	
	
	
	
}
