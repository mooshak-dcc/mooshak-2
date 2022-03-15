package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import pt.up.fc.dcc.mooshak.client.form.admin.ColorPicker;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * Widget to change colors of a GUI 
 * @author josepaiva
 */
public class ColorTweaker extends DialogBox {
	
	private Map<String, ColorPicker> colorSelectors = new HashMap<>();
	private Map<String, Map.Entry<ColorPicker, ColorPicker>> gradientSelectors = new HashMap<>();
	private Map<String, List<ValueChangeHandler<String>>> handlers = new HashMap<>();
	
	private VerticalPanel content = null;

	public ColorTweaker() {
		super(true, true);
		
		content = new VerticalPanel();
		
		setText("Theme Color Customization");
		center();
		hide();
		
		add(content);
	}
	
	public void addColorSelector(String name, String label, String defaultColor) {
		ColorPicker cp = createColorPicker(name, defaultColor);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new Label(label));
		hp.add(cp);

		hp.getElement().getStyle().setProperty("margin", "10px 20px");
		content.add(hp);

		colorSelectors.put(name, cp);
	}
	
	public void addGradientColorSelector(String name, String label, String defaultColor1, 
			String defaultColor2) {
		ColorPicker cp1 = createColorPicker(name, defaultColor1);
		ColorPicker cp2 = createColorPicker(name, defaultColor2);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new Label(label));
		hp.add(cp1);
		hp.add(cp2);
		
		hp.getElement().getStyle().setProperty("margin", "10px 20px");
		content.add(hp);
		
		gradientSelectors.put(name, new AbstractMap.SimpleEntry<>(cp1, cp2));
	}

	/**
	 * @param name
	 * @param defaultColor
	 * @return
	 */
	private ColorPicker createColorPicker(String name, String defaultColor) {
		ColorPicker cp = new ColorPicker();
		cp.setValue(new MooshakValue(name, defaultColor));
		
		cp.addValueChangeHandler(new ValueChangeHandler<MooshakValue>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<MooshakValue> event) {
				
				List<ValueChangeHandler<String>> handlersList = handlers.get(event.getValue().getField());
				
				if (handlersList == null)
					return;
				
				ValueChangeEvent<String> newEvent = null;
				if (gradientSelectors.containsKey(name)) {
					Entry<ColorPicker, ColorPicker> cps = gradientSelectors.get(event.getValue().getField());
					newEvent = new ValueChangeEvent<String>(cps.getKey().getValue().getSimple() + "," + 
							cps.getValue().getValue().getSimple()) {};
				} else {
					newEvent = new ValueChangeEvent<String>(event.getValue().getSimple()) {};
				}
				
				for (ValueChangeHandler<String> handler : handlersList) {
					handler.onValueChange(newEvent);
				}
			}
		});
		
		return cp;
	}
	
	public void addColorChangeHandler(String name, ValueChangeHandler<String> handler) {
		
		if (handlers.get(name) == null)
			handlers.put(name, new ArrayList<ValueChangeHandler<String>>());
		handlers.get(name).add(handler);
	}
}
