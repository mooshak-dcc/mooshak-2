package pt.up.fc.dcc.eshu.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import pt.up.fc.dcc.eshu.client.eshugwt.Eshu;

public class ESHU2_0 implements EntryPoint { 
	Panel root = RootPanel.get();
	final Eshu eshu = new Eshu();
	final HorizontalPanel settings = new HorizontalPanel();
	final HorizontalPanel settingsButton = new HorizontalPanel();
	final HorizontalPanel editors = new HorizontalPanel();
	final TextArea textArea = new TextArea();
	public void onModuleLoad() {
		
		
		createTextArea();
		createMenusettings();
		createEditors();
		
//		createButtonsettings();
		//eshu.setLanguage();		 
		//eshu.drawoption()
		
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(settings);
		verticalPanel.add(editors);
		verticalPanel.add(settingsButton);
	
		
		root.add(verticalPanel);
	}
	
	public void createCheckBoxGrid() {
		CheckBox grid = new CheckBox("Grid");
		grid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean checked = ((CheckBox) event.getSource()).getValue();
				//eshu.setGrid(checked);
			}
		});
		settings.add(grid); // Add it to settings panel.
	}
	
	public void createCheckBoxTools() {
		CheckBox tools = new CheckBox("Tools");
		tools.setValue(true);
		tools.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean checked = ((CheckBox) event.getSource()).getValue();

				//eshu.showOption(checked);
			}
		});
		settings.add(tools); // Add it to settings panel.
	}
	
	public void createCheckBoxKeepSelectedDraw() {
		CheckBox look = new CheckBox("Keep Sel. Draw");
		look.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean checked = ((CheckBox) event.getSource()).getValue();
				
				//eshu.keepSelectedDraw(checked);
			}
		});
		settings.add(look); // Add it to settings panel.
	}
	
	public void createCheckBoxKeepShowNodeInfow() {
		CheckBox showInfo = new CheckBox("Show Info");
		showInfo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean checked = ((CheckBox) event.getSource()).getValue();
				
			//	eshu.setShowNodeInfo(checked);
			}
		});
		settings.add(showInfo); // Add it to settings panel.
	}
	
	public void createRadioButtonPosition() {
		// Make some radio buttons, all in one group.
		RadioButton rbHorizontal = new RadioButton("position", "Horizontal");
		RadioButton rbVertical = new RadioButton("position", "Vertical");

		// Check 'horizontal' by default.
		rbHorizontal.setValue(true);

		rbHorizontal.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//eshu.setPositionHorizontal();
			}
		});

		rbVertical.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
			//	eshu.setPositionVertical();
			}
		});

		// Add them to the settings.
		settings.add(rbHorizontal);
		settings.add(rbVertical);
	}
	
	@SuppressWarnings("deprecation")
	public void createMenusettings(){
	DOM.setElementAttribute(settings.getElement(), "id", "settings");
	
	createCheckBoxGrid();
	createCheckBoxTools();
	createRadioButtonPosition();
	createCheckBoxKeepSelectedDraw();
	createCheckBoxKeepShowNodeInfow();
	createListBox();
	}
    
	public void createListBox() {

    ListBox lb = new ListBox();
    lb.addItem("EER");
    lb.addItem("UML");
    lb.addItem("AUT");
    lb.setVisibleItemCount(1);
    settings.add(lb);
	}
    
    
	public void createButtonsettings() {
	
	Button clear = new Button("Clear");
	clear.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			//eshu.clear();
			//textArea.setValue("");
		}
	});
	Button exportGraphToText = new Button("Export Graph To Text");
	exportGraphToText.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			exportGraphToTextArea();
		}
	});
	
	Button importGraphToText = new Button("Import Text To Graph");
	importGraphToText.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			importTextAreaToGraph();
		}
	});
	
	
	
	settingsButton.add(clear);
	settingsButton.add(exportGraphToText);
	
	settingsButton.add(importGraphToText);
	
	}
	
	public void createEditors() {
		this.eshu.resize(700,400 );
//		this.eshu.setGridVisible(true);
		this.eshu.setPosition("horizontal");
		editors.add(eshu);
		//Window.alert("22");
		editors.add(textArea);
	  
	}
	
	@SuppressWarnings("deprecation")
	public void createTextArea() {
		
	  // Let's make an 80x50 text area to go along with the other two.
			DOM.setElementAttribute(textArea.getElement(), "id", "editor");
		    textArea.setCharacterWidth(60);
		    textArea.setVisibleLines(26);
		    
		    
	}
	
	public void exportGraphToTextArea() {
		//textArea.setValue(eshu.getGraph());
	}
	
	public void importTextAreaToGraph() {
		//eshu.setGraph(textArea.getValue());
		
	}
	
	
}
