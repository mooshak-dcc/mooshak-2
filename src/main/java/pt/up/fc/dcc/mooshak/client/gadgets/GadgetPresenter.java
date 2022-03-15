package pt.up.fc.dcc.mooshak.client.gadgets;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiMessages;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.QuizCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

/**
 * Common interface to all presenters used in gadgets. Presenters are
 * participants of the MVP architectural pattern
 * 
 * @author josepaiva
 */
public abstract class GadgetPresenter<T extends View> implements Presenter {

	/* Logging */
	public static final Logger LOGGER = Logger.getLogger("");
	
	/* Constants and messages */
	public static final ICPCConstants ICPC_CONSTANTS = GWT.create(ICPCConstants.class);
	public static final ICPCMessages ICPC_MESSAGES = GWT.create(ICPCMessages.class);
	public static final EnkiConstants ENKI_CONSTANTS = GWT.create(EnkiConstants.class);
	public static final EnkiMessages ENKI_MESSAGES = GWT.create(EnkiMessages.class);

	/* Bus to send events */
	protected HandlerManager eventBus = null;

	/* Asynchronous Command Service */
	protected BasicCommandServiceAsync basicService = null;
	protected ParticipantCommandServiceAsync participantService = null;
	protected EnkiCommandServiceAsync enkiService = null;
	protected KoraCommandServiceAsync koraService = null;
	protected AsuraCommandServiceAsync asuraService = null;
	protected QuizCommandServiceAsync quizService = null;

	/* View associated */
	protected T view = null;

	/* Context fields */
	protected static ContextInfo contextInfo;
	protected String resourceId = null;
	protected String problemId = null;
	protected String name = null;
	protected String link = null;
	protected String language = null;
	protected ResourceType type = null;

	public GadgetPresenter(
			HandlerManager eventBus, 
			BasicCommandServiceAsync basicService,
			ParticipantCommandServiceAsync participantService, 
			EnkiCommandServiceAsync enkiService,
			KoraCommandServiceAsync koraService, 
			AsuraCommandServiceAsync asuraService,
			QuizCommandServiceAsync quizService,
			T view, Token token) {

		this.eventBus = eventBus;

		this.basicService = basicService;
		this.participantService = participantService;
		this.enkiService = enkiService;
		this.koraService = koraService;
		this.asuraService = asuraService;
		this.quizService = quizService;

		this.view = view;

		if (token != null) {
			this.resourceId = token.getId();
			this.problemId = token.getProblemId();
			this.name = token.getName();
			this.link = token.getLink();
			this.language = token.getLanguage();
			this.type = token.getType();
		}
	}

	/**
	 * @return the contextInfo
	 */
	public static ContextInfo getContextInfo() {
		return contextInfo;
	}

	/**
	 * @param contextInfo the contextInfo to set
	 */
	public static void setContextInfo(ContextInfo contextInfo) {
		GadgetPresenter.contextInfo = contextInfo;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId
	 *            the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId
	 *            the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the type
	 */
	public ResourceType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ResourceType type) {
		this.type = type;
	}

}
