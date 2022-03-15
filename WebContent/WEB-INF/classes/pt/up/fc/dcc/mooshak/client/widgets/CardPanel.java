package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extension of DeckPanel that uses names rather than indexes to refer to panels
 * 
 * @author José Paulo Leal <zp@dcc.fc.up,pt>
 */
public class CardPanel extends DeckPanel {

	
	Map<String,Integer> indexOfName = new HashMap<String,Integer>();
		
	CardPanel() {
		super();
	}
	
	public boolean hasCard(String name) {
		return indexOfName.containsKey(name);
	}
	
	public void addCard(String name,Widget widget) {
		int index =  indexOfName.size();
		
		add(widget);
		indexOfName.put(name, index);	
	}
	
	public void showCard(String name) {
		if(hasCard(name))
			showWidget(indexOfName.get(name)); 
	}
}
