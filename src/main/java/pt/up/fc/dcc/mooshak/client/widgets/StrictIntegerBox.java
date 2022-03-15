package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Integer box that prevents the user from inserting non-numbers. 
 * 
 * @author josepaiva
 */
public class StrictIntegerBox extends IntegerBox {
	
	private boolean allowAbbrevs = false;

	public StrictIntegerBox() {
		super();
		
		initialize();
	}

	public void initialize() {
		
		setAlignment(TextBox.TextAlignment.RIGHT);

		addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();

				switch (keyCode) {
				case KeyCodes.KEY_LEFT:
				case KeyCodes.KEY_RIGHT:
				case KeyCodes.KEY_DELETE:
				case KeyCodes.KEY_BACKSPACE:
				case KeyCodes.KEY_ENTER:
				case KeyCodes.KEY_ESCAPE:
				case KeyCodes.KEY_TAB:
					return;
				}

				String parsedInput = parseNumberKey(keyCode);
				if (parsedInput.length() > 0) {
					return;
				} else {
					cancelKey();
				}

			}
		});
	}

	/**
	 * @return the allowAbbrevs
	 */
	public boolean isAllowAbbrevs() {
		return allowAbbrevs;
	}

	/**
	 * @param allowAbbrevs the allowAbbrevs to set
	 */
	public void setAllowAbbrevs(boolean allowAbbrevs) {
		this.allowAbbrevs = allowAbbrevs;
	}

	/**
	 * Check that the key pressed is either a number key or a numeric pad key.
	 * Converts numeric pad keys to the string representation of their number
	 * values. It can also be K, M or G if abbreviations are enabled.
	 * 
	 * @param keyCode The key code
	 * @return the key's representation as a string
	 */
	private String parseNumberKey(int keyCode) {
		String result = new String();

		switch (keyCode) {
		case KeyCodes.KEY_ZERO:
		case KeyCodes.KEY_ONE:
		case KeyCodes.KEY_TWO:
		case KeyCodes.KEY_THREE:
		case KeyCodes.KEY_FOUR:
		case KeyCodes.KEY_FIVE:
		case KeyCodes.KEY_SIX:
		case KeyCodes.KEY_SEVEN:
		case KeyCodes.KEY_EIGHT:
		case KeyCodes.KEY_NINE:
			return result = String.valueOf((char) keyCode);
		case KeyCodes.KEY_NUM_ZERO:
			return result = "0";
		case KeyCodes.KEY_NUM_ONE:
			return result = "1";
		case KeyCodes.KEY_NUM_TWO:
			return result = "2";
		case KeyCodes.KEY_NUM_THREE:
			return result = "3";
		case KeyCodes.KEY_NUM_FOUR:
			return result = "4";
		case KeyCodes.KEY_NUM_FIVE:
			return result = "5";
		case KeyCodes.KEY_NUM_SIX:
			return result = "6";
		case KeyCodes.KEY_NUM_SEVEN:
			return result = "7";
		case KeyCodes.KEY_NUM_EIGHT:
			return result = "8";
		case KeyCodes.KEY_NUM_NINE:
			return result = "9";
		case KeyCodes.KEY_G:
		case KeyCodes.KEY_K:
		case KeyCodes.KEY_M:
			if (allowAbbrevs)
				return result = String.valueOf((char) keyCode);
			else
				return result;
		}
		return result;
	}
}
