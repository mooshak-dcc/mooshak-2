package pt.up.fc.dcc.mooshak.client.guis.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationView.BaseStyle;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.HasSelectedValue;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.events.ContestUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

public class AuthenticationPresenter {

	private static final String MESSAGE_KEY = "message";

	private static final int REDIRECT_TIME = 5000;

	private static String preferredLocale = null;
	
	interface Display {
		HasSelectedValue<SelectableOption> getDomainHolder();

		HasSelectedValue<SelectableOption> getClientHolder();

		HasValue<String> getUserHolder();

		HasValue<String> getPasswordHolder();

		HasValue<String> getMessageHolder();
		 
		HasText getVersionHolder();

		HasClickHandlers getLogin();

		HasClickHandlers getClear();

		HasClickHandlers getGuest();

		HasClickHandlers getRegister();

		BaseStyle getStyle();

		
	}

	private static Logger LOGGER = Logger.getLogger("");
	private static Storage storage = Storage.getLocalStorageIfSupported();

	private EventManager eventManager = EventManager.getInstance();

	private BasicCommandServiceAsync rpc = null;
	private Display display;
	
	Map<String, ResultsContestInfo> contestsInfo;

	/**
	 * Create a singleton from this class with given arguments
	 * 
	 * @param disp
	 * @param rpc
	 * @return
	 */
	AuthenticationPresenter(Display disp, BasicCommandServiceAsync rpc) {

		initialize(disp, rpc);
		showStartupMessage();

		eventManager.addListener(ContestUpdateEvent.class,
				new MooshakEventListener<ContestUpdateEvent>() {

					@Override
					public void receiveEvent(ContestUpdateEvent event) {

						switch (event.getState()) {
						case READY:
						case RUNNING:
						case RUNNING_VIRTUALLY:
							Collection<SelectableOption> options = display
									.getDomainHolder().getSelections();
							if (!event.getFrozen()) {
								for (SelectableOption option : options) {
									if (option.getId().equals(
											event.getContestId())) {
										options.remove(option);
										break;
									}
								}

								options.add(new SelectableOption(event
										.getContestId(), event.getContest()));
								display.getDomainHolder()
										.setSelections(options);
								break;
							}
						default:
							options = display.getDomainHolder().getSelections();
							for (SelectableOption option : options) {
								if (option.getId().equals(event.getContestId())) {
									options.remove(option);
									display.getDomainHolder().setSelections(
											options);
									break;
								}
							}
							break;
						}
						LOGGER.log(Level.INFO, "received contest update");
					}
				});
	}
	
	/**
	 * Initialize fields and perform bindings on construction
	 * 
	 * @param display
	 * @param rpc
	 */
	private void initialize(Display display, BasicCommandServiceAsync rpc) {
		this.rpc = rpc;
		this.display = display;
		
		setVariableConfigurations();
		bindCallbacks();
	}
	
	/**
	 * Set variable configurations for this client from request to the server
	 * Get preferred locale if not already defined in the query string
	 * Get version and set it on the interface
	 */
	private void setVariableConfigurations() {
		
		String locale = Window.Location.getParameter("locale");
		
		if(locale == null)
			rpc.getPreferredLocale(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {
						preferredLocale = result;
					}
			});
		else
			preferredLocale = locale;
		
		rpc.getVersion(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				display.getVersionHolder().setText(result);
			}});
	}

	/**
	 * Bind callbacks
	 */
	private void bindCallbacks() {

		populateDomainSelector();

		/*display.getPasswordHolder().addValueChangeHandler(
				new ValueChangeHandler<String>() {

					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						login();
					}
				});*/

		display.getLogin().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				login();
			}
		});

		display.getClear().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				display.getUserHolder().setValue("");
				display.getPasswordHolder().setValue("");
			}
		});

		display.getGuest().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (getContestInfoPublicScoreboard(display.getDomainHolder().getSelectedOption().getId()))
					loginToScoreboard();
			}
		});

		display.getRegister().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				register();
			}
		});

	}

	/**
	 * Set domain selector with list of available domains returned by RPC call
	 */
	private void populateDomainSelector() {
		rpc.getDomains(false, false,
				new AsyncCallback<Map<String, ResultsContestInfo>>() {

					@Override
					public void onSuccess(Map<String, ResultsContestInfo> result) {
						contestsInfo = result;

						List<SelectableOption> options = new ArrayList<SelectableOption>();

						for (String key : getKeysOrdedByContestName(result)) {
							options.add(new SelectableOption(result.get(key)
									.getContestId(), result.get(key)
									.getContestName()));
						}
						display.getDomainHolder().setSelections(options);

						if (options.size() > 0)
							display.getDomainHolder().fireEvent(
									new ValueChangeEvent<SelectableOption>(
											options.get(0)) {
									});
					}

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.log(Level.SEVERE, caught.getMessage());
					}
				});
	}

	/**
	 * Return a list of keys of the result map ordered by contest name
	 * 
	 * @param result
	 * @return
	 */
	private List<String> getKeysOrdedByContestName(
			final Map<String, ResultsContestInfo> result) {

		ArrayList<String> keys = new ArrayList<String>(result.keySet());
		Collections.sort(keys, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String n1 = result.get(o1).getContestName();
				String n2 = result.get(o2).getContestName();
				return n1.compareTo(n2);
			}

		});

		return keys;
	}

	/**
	 * Move to another entry point if user is authenticated in given domain
	 */
	public void login() {
		final Map<Style, Cursor> cursors = 
				AbstractAppController.setCursorsToWait();
		String domain = null;
		if (!display.getDomainHolder().getSelections().isEmpty())
			 domain = display.getDomainHolder().getValue().getId();
		String user = display.getUserHolder().getValue();
		String password = display.getPasswordHolder().getValue();

		rpc.login(domain, user, password, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String module) {

				if (module == null) {
					display.getMessageHolder().setValue("Undefined profile");
					AbstractAppController.resetCursors(cursors);
				}
				else 
					redirectTo(module  + ".html");
				
				
			}

			@Override
			public void onFailure(Throwable caught) {
				AbstractAppController.resetCursors(cursors);
				display.getMessageHolder().setValue(caught.getMessage());
			}
		});
	}

	/**
	 * Move to scoreboard entry point if user is authenticated in given domain
	 */
	public void loginToScoreboard() {
		final Map<Style, Cursor> cursors = 
				AbstractAppController.setCursorsToWait();
		
		String domain = display.getDomainHolder().getValue().getId();

		rpc.login(domain, "public", "", new AsyncCallback<String>() {

			@Override
			public void onSuccess(String module) {
				redirectTo(module + ".html");
			}

			@Override
			public void onFailure(Throwable caught) {
				AbstractAppController.resetCursors(cursors);
				display.getMessageHolder().setValue(caught.getMessage());
			}
		});
	}

	/**
	 * Register and move to another entry point if user is authenticated in
	 * given domain
	 */
	private void register() {
		final String domain = display.getDomainHolder().getValue().getId();

		display.getUserHolder().setValue("");
		display.getPasswordHolder().setValue("");

		final RegisterFormDialog registerForm = new RegisterFormDialog(domain);

		new OkCancelDialog(registerForm, "Register") {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				rpc.validateCaptcha(registerForm.getResponse(),
						new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								if (result.booleanValue())
									registerValidCaptcha(domain,
											registerForm.getUser(),
											registerForm.getPassword(),
											registerForm.getConfirmPassword());
								else
									display.getMessageHolder().setValue(
											"Wrong captcha!");
							}

							@Override
							public void onFailure(Throwable caught) {
								display.getMessageHolder().setValue(
										caught.getMessage());
								LOGGER.log(Level.SEVERE, caught.getMessage());
							}
						});
			}
		});
	}

	private void registerValidCaptcha(String domain, String user,
			String password, String confirmPassword) {

		if (!password.equals(confirmPassword)) {
			display.getMessageHolder().setValue("Passwords don't match");
			return;
		}

		rpc.register(domain, user, password, new AsyncCallback<String>() {

			@Override
			public void onSuccess(final String url) {

				if (url == null)
					display.getMessageHolder().setValue("Undefined profile");
				else {
					display.getMessageHolder()
							.setValue(
									"Your registration "
											+ "succeeded. You will be redirected in 5 seconds ...");

					Timer redirectTimer = new Timer() {

						@Override
						public void run() {
							redirectTo(url + ".html");
						}
					};
					redirectTimer.schedule(REDIRECT_TIME);

				}
			}

			@Override
			public void onFailure(Throwable caught) {
				display.getMessageHolder().setValue(caught.getMessage());
			}
		});
	}

	/**
	 * Convenience method to logout with error message
	 * 
	 * @param line
	 */
	public static void logout(Throwable throwable) {
		logout(throwable.getMessage());
	}

	/**
	 * * Event handling will be stopped before a new authentication
	 */
	public static void logout(String... messages) {
		BasicCommandServiceAsync rpcService = GWT
				.create(BasicCommandService.class);

		EventManager.getInstance().stop();

		for (String message : messages)
			addStartupMessage(message);

		rpcService.logout(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				addStartupMessage(caught.getMessage());
				redirectToAuthentication();
			}

			@Override
			public void onSuccess(Void result) {
				redirectToAuthentication();
			}

		});
	}

	/**
	 * * Method to check session state
	 */
	public static void isSessionAlive() {
		BasicCommandServiceAsync rpcService = GWT
				.create(BasicCommandService.class);

		rpcService.isSessionAlive(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				redirectToAuthentication();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (!result.booleanValue())
					redirectToAuthentication();
			}
		});
	}

	/**
	 * Add a message to display on message box when dialogue is redisplayed
	 * 
	 * @param line
	 */
	private static void addStartupMessage(String line) {
		String message = storage.getItem(MESSAGE_KEY);

		if (line == null)
			return;

		line = stripHtmlTags(line);

		if ("".equals(line) || "0".equals(line.trim()))
			return;

		if (message == null)
			message = line;
		else if (message.contains(line))
			return;
		else
			message += "\n" + line;

		LOGGER.log(Level.SEVERE, line);
		storage.setItem(MESSAGE_KEY, message);
	}

	/**
	 * Strip HTML tags from given line
	 * 
	 * @param line
	 * @return
	 */
	private static String stripHtmlTags(String line) {

		return line.replaceAll("(<([^>]+)>)", "");
	}

	/**
	 * Display on message box stored messages
	 */
	private void showStartupMessage() {
		String message = storage.getItem(MESSAGE_KEY);

		if (message != null) {
			display.getMessageHolder().setValue(message);
			storage.removeItem(MESSAGE_KEY);
		}
	}

	/**
	 * Redirect to authentication, typically on error.
	 */
	public static void redirectToAuthentication() {

		redirectTo("Authentication.html");
	}

	/**
	 * Redirect application to given URL
	 * 
	 * @param url
	 *            to redirect
	 */
	public static void redirectTo(String url) {
		String expandedURL = getUrlInApp(url);
		
		if(! expandedURL.contains("locale=") && preferredLocale != null)
			expandedURL += "?locale=" + preferredLocale;
		
		Window.Location.replace(expandedURL);
	}

	/**
	 * Return given URL with context and state for this webapp
	 * 
	 * @param url
	 * @return
	 */
	public static String getUrlInApp(String url) {
		StringBuilder builder = new StringBuilder();
		String path = Window.Location.getPath();

		builder.append(path.substring(0, path.lastIndexOf('/') + 1)); // context
		builder.append(url);
		builder.append(Window.Location.getQueryString()); // query string

		return builder.toString();
	}

	public boolean getContestInfoRegister(String contest) {
		return contestsInfo.get(contest).getRegister();
	}

	public boolean getContestInfoPublicScoreboard(String contest) {
		return contestsInfo.get(contest).isPublicScoreboard();
	}

}
