package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TabButton;
import pt.up.fc.dcc.mooshak.client.utils.HasSelectedValue;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class ProblemSelector extends HTMLPanel implements HasSelectedValue<SelectableOption> {
	private static final RegExp REGEX_ID_NUMBER = RegExp.compile("([^\\d]*)(\\d+)([^\\d]*)$");

	private static final int ALL_PROBLEMS_AREA = 500;
	private static final int MAX_PROBLEM_AREA = 60;
	private static final int PROBLEM_WIDTH_RATIO = 4;
	private static final int PROBLEM_MARGIN_RATIO = 1;
	private static final int PROBLEM_AREA_RATIO = PROBLEM_WIDTH_RATIO + 2 * PROBLEM_MARGIN_RATIO;
	private static final int MAX_TAB_BUTTONS = 15;

	private OptionFormatter<SelectableOption> formatter = new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};
	
	private List<ValueChangeHandler<SelectableOption>> handlers = new ArrayList<>();
	
	private Map<String, SelectableOption> tabs = new HashMap<>();
	
	private SelectableOption value = null;
	
	private Label label = new Label("Problems:");
	private SelectOneListBox<SelectableOption> selector = new SelectOneListBox<>();

	public ProblemSelector(SafeHtml safeHtml) {
		this(safeHtml.asString());
	}
	
	public ProblemSelector(String html) {
		this("div", html);
	}
	

	public ProblemSelector(String tag, String html) {
		super(tag, html);
		
		selector.setFormatter(formatter);
		
		selector.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {

			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> event) {
				
				if (tabs.size() > MAX_TAB_BUTTONS)
					setValue(event.getValue(), true);
			}
		});

		Style lblStyle = label.getElement().getStyle();
		Style selStyle = selector.getElement().getStyle();
		lblStyle.setDisplay(Display.NONE);
		lblStyle.setFloat(Float.LEFT);
		lblStyle.setColor("teal");
		lblStyle.setMarginTop(10, Unit.PX);
		selStyle.setDisplay(Display.NONE);
		selStyle.setFloat(Float.LEFT);
		selStyle.setMarginTop(10, Unit.PX);
		
		add(label);
		add(selector);
	}

	@Override
	public SelectableOption getValue() {
		return value;
	}

	@Override
	public void setValue(SelectableOption value) {
		this.value = value;
		
		selector.setSelectedValue(value);
		
		if (tabs.size() <= MAX_TAB_BUTTONS) {
			
			for (int i = 0; i < getWidgetCount(); i++) {
				TabButton tabButton = (TabButton) getWidget(i);
				
				if (value.getId().equals(tabButton.getId()))
					tabButton.select();
			}
		}
	}

	@Override
	public void setValue(SelectableOption value, boolean fireEvents) {
		SelectableOption prevValue = getValue();
		setValue(value);
		if (fireEvents && (prevValue == null || value == null || !prevValue.equals(value)))
			fireChangeHandler();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SelectableOption> handler) {
		
		final int size = handlers.size();
		handlers.add(handler);
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				handlers.remove(size);
			}
		};
	}

	@Override
	public void setSelections(Collection<SelectableOption> selections) {
		
		clear();
		tabs.clear();
		
		List<SelectableOption> selectionsList = new ArrayList<>(selections);

		Collections.sort(selectionsList, new Comparator<SelectableOption>() {

			@Override
			public int compare(SelectableOption o1, SelectableOption o2) {
				String s1 = o1.getLabel();
				String s2 = o2.getLabel();

				if (REGEX_ID_NUMBER.test(s1) && REGEX_ID_NUMBER.test(s2)) {

					MatchResult r1 = REGEX_ID_NUMBER.exec(s1);
					MatchResult r2 = REGEX_ID_NUMBER.exec(s2);
	
					if (r1.getGroupCount() == r2.getGroupCount()) {
						for (int i = 1; i < r1.getGroupCount(); i++) {
							if (i == 2) {
								Integer i1 = Integer.parseInt(r1.getGroup(i));
								Integer i2 = Integer.parseInt(r2.getGroup(i));
	
								if (i1.compareTo(i2) != 0)
									return i1.compareTo(i2);
							}
	
							if (!r1.getGroup(i).equals(r2.getGroup(i)))
								return r1.getGroup(i).compareTo(r2.getGroup(i));
						}
					}
				}

				char[] s1Arr = s1.toCharArray();
				char[] s2Arr = s2.toCharArray();
				for (int i = 0; i < Math.min(s1Arr.length, s2Arr.length); i++) {
					
					if (s1Arr[i] > s2Arr[i])
						return 1;
					else if (s1Arr[i] < s2Arr[i])
						return -1;
				}
				
				if (s1Arr.length < s2Arr.length)
					return -1;
				if (s1Arr.length > s2Arr.length)
					return 1;
				
				return 0;
			}
		});

		selector.setSelections(selectionsList);
		
		if (selectionsList.size() > MAX_TAB_BUTTONS) {
			add(label);
			label.getElement().getStyle().setDisplay(Display.BLOCK);
			add(selector);
			selector.getElement().getStyle().setDisplay(Display.BLOCK);

			tabs.put("", new SelectableOption("", ""));
			for (SelectableOption option : selectionsList) 
				tabs.put(option.getId(), option);
			selector.setSelections(tabs.values());
			
			return;
		}
		
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				TabButton button = (TabButton) event.getSource();
				setValue(new SelectableOption(button.getId(), button.getText()), true);
			}
		};

		int area = (selectionsList.size() == 0 ? MAX_PROBLEM_AREA
				: Math.min(ALL_PROBLEMS_AREA / selectionsList.size(), MAX_PROBLEM_AREA)) - 1;
		int width = area * PROBLEM_WIDTH_RATIO / PROBLEM_AREA_RATIO;
		int margin = area * PROBLEM_MARGIN_RATIO / PROBLEM_AREA_RATIO;

		for (SelectableOption option : selectionsList) {
			TabButton problem = new TabButton();
			Style style = problem.getElement().getStyle();

			problem.setId(option.getId());
			problem.setText(option.getLabel());
			problem.setGroup("problems");
			problem.styleAs("problem");

			style.setWidth(width, Unit.PX);
			style.setMarginLeft(margin, Unit.PX);
			style.setMarginRight(margin, Unit.PX);

			problem.addClickHandler(handler);
			add(problem);

			tabs.put(option.getId(), option);
		}
	}

	@Override
	public Collection<SelectableOption> getSelections() {
		return tabs.values();
	}

	@Override
	public void setSelectedValue(SelectableOption selected) {
		setValue(selected);
	}

	@Override
	public SelectableOption getSelectedOption() {
		return value;
	}
	
	/**
	 * Fire value change handlers
	 */
	public void fireChangeHandler() {
		ValueChangeEvent<SelectableOption> event = new ValueChangeEvent<SelectableOption>(value) {};
		for (ValueChangeHandler<SelectableOption> handler : handlers) {
			handler.onValueChange(event);
		}
	}

	/**
	 * Updates the label
	 * @param id
	 * @param tabName
	 */
	public void updateTabName(String id, String tabName) {
		SelectableOption tb = tabs.get(id.substring(id.lastIndexOf("/") >= 0 ?
				id.lastIndexOf("/") + 1 : 0));
		
		if (tb != null)
			tb.setLabel(tabName);
		
		SelectableOption option = selector.getSelectedOption();
		setSelections(new ArrayList<>(tabs.values()));
		
		setSelectedValue(option);
	}
	
	/**
	 * Add problem to selections
	 */
	public void addProblemId(String problemId) {
		
		if (tabs.get(problemId) != null)
			return;
		
		SelectableOption selectedOption = getSelectedOption();
		
		SelectableOption option = new SelectableOption(problemId, problemId);
		tabs.put(problemId, option);
		
		setSelections(new ArrayList<>(tabs.values()));
		
		if (selectedOption != null)
			setSelectedValue(selectedOption);
	}
	
	/**
	 * Select problem
	 * @param problem
	 */
	public void selectProblem(String problem) {
		
		SelectableOption selected = tabs.get(problem);
		
		if (tabs.size() <= MAX_TAB_BUTTONS) {
			
			for (int i = 0; i < getWidgetCount(); i++) {
				TabButton tab = ((TabButton) getWidget(i));
				if(tab.getId().equalsIgnoreCase(problem))
					tab.select();
			}
		}
		
		if (selected != null)
			setSelectedValue(selected);
	}
}
