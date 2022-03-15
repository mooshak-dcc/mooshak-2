package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.MultiLanguageMenu;
import pt.up.fc.dcc.mooshak.client.widgets.NotificationWidget;
import pt.up.fc.dcc.mooshak.client.widgets.TimeBar;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.hydro4ge.raphaelgwt.client.Raphael;

public class TopLevelViewImpl extends Composite implements TopLevelView,
	HasTutorial {

	private static final int ALL_PROBLEMS_AREA = 520;
	private static final int MAX_PROBLEM_AREA = 60;
	private static final int PROBLEM_WIDTH_RATIO = 4;
	private static final int PROBLEM_MARGIN_RATIO = 1;
	private static final int PROBLEM_AREA_RATIO = 
			PROBLEM_WIDTH_RATIO + 2 * PROBLEM_MARGIN_RATIO;
	
	private static final int CONTEST_FONT_SIZE = 32;
	private static final int TEAM_FONT_SIZE = 24;
	
	private static ICPCUiBinder uiBinder = GWT.create(ICPCUiBinder.class);

	@UiTemplate("TopLevelView.ui.xml")
	interface ICPCUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}


	@UiField
	HTMLPanel basePanel;
	
	@UiField
	MultiLanguageMenu langMenu;

	@UiField
	SpanElement contest;
	@UiField
	SpanElement team;

	@UiField
	HTMLPanel problems;
	@UiField
	CardPanel content;
	
	@UiField
	TimeBar timeBar;

	@UiField
	BaseStyle style;
	
	@UiField
	Label version;

	interface BaseStyle extends CssResource {

		String film();
	}

	private Presenter presenter = null;

	private Raphael film;

	public TopLevelViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		film = new Raphael(1200, 900);
		film.getElement().setClassName(style.film());
		
		program.ensureDebugId("programTab");
		submissions.ensureDebugId("submissionsTab");
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public CardPanel getContent() {
		return content;
	}
	
	@Override
	public void showTutorial() {
		basePanel.add(film);
	}

	private Map<String,TabButton> tabButtonOfProblem = new HashMap<String,TabButton>();
	
	private static final RegExp REGEX_ID_NUMBER = RegExp.compile("([^\\d]*)(\\d+)([^\\d]*)$");
	@Override
	public void setProblems(List<SelectableOption> problemOptions) {
		problems.clear();

		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				TabButton button = (TabButton) event.getSource();
				String action = TabImageButton.getSelection("actions").getKind();

				TabButton.deselect("listings");
				if ("view".equals(action))
					view(button.getId(), button.getText());
				else if ("program".equals(action))
					program(button.getId(), button.getText());
				else if ("ask".equals(action))
					ask(button.getId(), button.getText());
			}
		};
		
		Collections.sort(problemOptions, new Comparator<SelectableOption>() {

			@Override
			public int compare(SelectableOption o1, SelectableOption o2) {
				String s1 = o1.getLabel();
				String s2 = o2.getLabel();
				
				if (!(REGEX_ID_NUMBER.test(s1) && REGEX_ID_NUMBER.test(s2)))
					return s1.compareTo(s2);
				
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
				return s1.compareTo(s2);
			}
		});

		int area =  (problemOptions.size() == 0 ? MAX_PROBLEM_AREA : Math.min(
				ALL_PROBLEMS_AREA / problemOptions.size(),
				MAX_PROBLEM_AREA));
		int width  = area * PROBLEM_WIDTH_RATIO / PROBLEM_AREA_RATIO;
		int margin = area * PROBLEM_MARGIN_RATIO / PROBLEM_AREA_RATIO;
		
		for (SelectableOption option : problemOptions) {
			TabButton problem = new TabButton();
			Style style = problem.getElement().getStyle();
			
			problem.setId(option.getId());
			problem.ensureDebugId("problem" + option.getId());
			problem.setText(option.getLabel());
			problem.setGroup("problems");
			problem.styleAs("problem");

			style.setWidth(width, Unit.PX);
			style.setMarginLeft(margin, Unit.PX);
			style.setMarginRight(margin, Unit.PX);
			
			problem.addClickHandler(handler);
			problems.add(problem);
			
			tabButtonOfProblem.put(option.getLabel(), problem);
		}
	}

	public void setContest(String name) {
		contest.setInnerHTML(name);
		
		stretchFontSizeToParentElement(contest, contest.getParentElement(),
				CONTEST_FONT_SIZE);
	}

	public void setTeam(String name) {
		team.setInnerHTML(name);
		
		stretchFontSizeToParentElement(team, team.getParentElement(),
				TEAM_FONT_SIZE);
	}
	
	private void stretchFontSizeToParentElement(Element element, 
			Element parent, int maxSize) {
		
		Style style = element.getStyle();
		
		int size = 8;
		style.setDisplay(Display.INLINE);
		style.setFontSize(size, Unit.PX);
		
		while (element.getOffsetWidth() < 
				element.getParentElement().getOffsetWidth() - 20
				&& size < maxSize) {
			style.setFontSize(size, Unit.PX);
			size++;
		}
		
		style.setFontSize(size-1, Unit.PX);
	}

	@UiField
	TabImageButton view;

	@UiHandler("view")
	void view(ClickEvent e) {
		TabButton problem = TabButton.getSelection("problems");
		TabButton.deselect("listings");
		view(problem.getId(), problem.getText());
	}

	private void view(String id, String label) {
		if (presenter != null)
			presenter.onViewStatementClicked(id, label);
	}

	@UiField
	TabImageButton program;

	@UiHandler("program")
	void program(ClickEvent e) {
		TabButton problem = TabButton.getSelection("problems");
		TabButton.deselect("listings");
		program(problem.getId(), problem.getText());
	}

	private void program(String id, String label) {
		if (presenter != null)
			presenter.onEditProgramClicked(id, label);
	}
	
	@Override
	public void selectProgramEditor(String problemLabel) {
		TabButton.deselect("listings");
		program.select();
		
		if (tabButtonOfProblem.get(problemLabel) != null)
			tabButtonOfProblem.get(problemLabel).select();
	}

	@UiField
	TabImageButton ask;

	@UiHandler("ask")
	void ask(ClickEvent e) {
		TabButton problem = TabButton.getSelection("problems");
		TabButton.deselect("listings");
		ask(problem.getId(), problem.getText());
	}

	private void ask(String id, String label) {
		if (presenter != null)
			presenter.onAskQuestionClicked(id, label);
	}

	@UiField
	TabButton submissions;

	@UiHandler("submissions")
	void submissions(ClickEvent e) {
		TabButton.deselect("problems");
		TabImageButton.deselect("actions");
		if (presenter != null)
			presenter.onListingClicked(Kind.SUBMISSIONS);
	}

	@UiField
	TabButton questions;

	@UiHandler("questions")
	void questions(ClickEvent e) {
		TabButton.deselect("problems");
		TabImageButton.deselect("actions");
		if (presenter != null)
			presenter.onListingClicked(Kind.QUESTIONS);
	}

	@UiField
	TabButton printouts;

	@UiHandler("printouts")
	void printouts(ClickEvent e) {
		TabButton.deselect("problems");
		TabImageButton.deselect("actions");
		if (presenter != null)
			presenter.onListingClicked(Kind.PRINTOUTS);
	}

	@UiField
	TabButton balloons;

	@UiHandler("balloons")
	void balloons(ClickEvent e) {
		TabButton.deselect("problems");
		TabImageButton.deselect("actions");
		if (presenter != null)
			presenter.onListingClicked(Kind.BALLOONS);
	}

	@UiField
	TabButton rankings;

	@UiHandler("rankings")
	void rankings(ClickEvent e) {
		TabButton.deselect("problems");
		TabImageButton.deselect("actions");
		if (presenter != null)
			presenter.onListingClicked(Kind.RANKINGS);
	}
	
	@UiField
	CustomImageButton helpButton;
	
	@UiHandler("helpButton")
	void help(ClickEvent e) {
		if (presenter != null)
			presenter.onHelpClicked();
	}
	
	@UiField
	CustomImageButton logout;
	
	@UiHandler("logout")
	void logout(ClickEvent e) {
		if (presenter != null)
			presenter.onLogoutClicked();
	}

	@Override
	public Raphael showTutorialPanel() {
		if(basePanel.getWidgetIndex(film) == -1)
			basePanel.add(film);
		return film;
	}

	@Override
	public void hideTutorialPanel() {
		basePanel.remove(film);
	}

	@Override
	public void showNotification(String message) {
		NotificationWidget notification = new NotificationWidget();
		notification.setAnimationDuration(1000);
		notification.setShowDuration(3000);
		notification.setHeight("200px");
		RootPanel.get().add(notification);
		notification.show(message);
	}

	@Override
	public void setDates(Date start, Date end, Date current) {
		timeBar.initValues(start, end, current);
		
	}
	
	@Override
	public void setVersion(String versionText) {
		version.setText(versionText);
	}
}
