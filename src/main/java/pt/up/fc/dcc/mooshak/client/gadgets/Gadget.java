package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.core.client.GWT;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;

/**
 * Type that represents a Gadget
 * 
 * @author josepaiva
 */
public class Gadget {
	public static final EnkiConstants CONSTANTS = GWT.create(EnkiConstants.class);

	private Presenter presenter;
	private View view;
	
	private Token token;
	private GadgetType type;

	public Gadget(Token token, GadgetType type) {
		this.token = token;
		this.type = type;
	}
	
	public Gadget(View view, Presenter presenter, Token token, 
			GadgetType type) {
		this.view = view;
		this.presenter = presenter;
		this.token = token;
		this.type = type;
	}
	
	/**
	 * @return the presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	/**
	 * @param presenter the presenter to set
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(Token token) {
		this.token = token;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return token.getId();
	}
	
	/**
	 * @return the type
	 */
	public GadgetType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(GadgetType type) {
		this.type = type;
	}
	
	/**
	 * Name of the gadget (each type of gadget has a name)
	 * @return the name of the gadget
	 */
	public String getName() {
		return getType().toString();
	}
}
