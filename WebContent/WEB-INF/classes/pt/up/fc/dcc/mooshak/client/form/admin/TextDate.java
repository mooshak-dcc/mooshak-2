package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

class TextDate extends Button {
	  DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
	  DatePicker datePicker = new DatePicker();
	  
	  TextDate() {
		  popup.setAnimationEnabled(true);
		  popup.setWidget(datePicker); 
	  }

	  @Override
	  protected void onLoad() {
		  
		   addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Button source = (Button) event.getSource();
					String text;
					
					if(popup.isShowing()) {
						Date date = datePicker.getValue();
						text=(date==null ? "" : Long.toString(date.getTime()));
						source.setText(text);
						
						popup.hide();		
					} else {
						int left = source.getAbsoluteLeft() ;
						int top = source.getAbsoluteTop() + source.getOffsetHeight();
						popup.setPopupPosition(left, top);
						
						text = getText();
						if(! "".equals(text)) {
							try {
								long value = Long.parseLong(text);
								datePicker.setValue(new Date(value));
							} catch(NumberFormatException cause) {
								 final Logger logger = Logger.getLogger("");
								 logger.log(Level.INFO,"Parsing date as long",cause);
							}
						}				
						
						popup.show();
					}
				}
		  });
	  }
	
}