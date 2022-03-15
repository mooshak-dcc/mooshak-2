package pt.up.fc.dcc.mooshak.client.form.admin;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.quizEditor.client.wrapper.QuizEditor;

/**
 * Quiz Editor editor widget that implements MooshakWidget
 * 
 * @author helder correia
 */
public class CustomQuizEditor extends Composite implements MooshakWidget {

	private static final Logger LOGGER = Logger.getLogger("");

	private static CustomQuizEditor instance = null;

	private HandlerRegistration valueAttachHandlerRegistration = null;

	private boolean isEditing = false;
	private String field = null;

	private QuizEditor editor = QuizEditor.getInstance();

	private CustomQuizEditor() {
		ResizableHtmlPanel panel = new ResizableHtmlPanel("");
		panel.add(editor);

		/*
		 * HorizontalPanel panel = new HorizontalPanel(); panel.add(editor);
		 * 
		 * panel.setCellHeight(editor, "100%");
		 * panel.getElement().getStyle().setPosition(Position.RELATIVE);
		 */
		initWidget(panel);

		init();
	}

	public static CustomQuizEditor getInstance() {
		if (instance == null)
			instance = new CustomQuizEditor();
		return instance;
	}

	/**
	 * Initialize Quiz editor
	 */
	private void init() {

	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	@Override
	public void setValue(final MooshakValue value, final boolean fireEvents) {
		field = value.getField();

		if (!editor.isAttached()) {

			if (valueAttachHandlerRegistration != null)
				valueAttachHandlerRegistration.removeHandler();

			valueAttachHandlerRegistration = editor.addAttachHandler(new Handler() {

				@Override
				public void onAttachOrDetach(AttachEvent event) {

					if (!event.isAttached())
						return;

					try {
						if (value.getContent() != null)
							editor.importXML(new String(value.getContent(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						LOGGER.log(Level.SEVERE, "Unsupported encoding while setting value on Quiz editor.");
					}

					if (fireEvents)
						editor.fireEvent(new ValueChangeEvent<MooshakValue>(value) {
						});

					valueAttachHandlerRegistration.removeHandler();
				}
			});
		} else {
			try {
				if (value.getContent() != null)
					editor.importXML(new String(value.getContent(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.log(Level.SEVERE, "Unsupported encoding while setting value on Quiz editor.");
			}
		}

		if (fireEvents)
			editor.fireEvent(new ValueChangeEvent<MooshakValue>(value) {
			});
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<MooshakValue> handler) {

		return editor.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> originalEvent) {
				if (field == null)
					return;

				MooshakValue value = new MooshakValue(field, "tobeSetAfter", originalEvent.getValue().getBytes());
				ValueChangeEvent<MooshakValue> event = new ValueChangeEvent<MooshakValue>(value) {
				};

				handler.onValueChange(event);
			}
		});
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field, "toBeSetAfter", editor.exportJson().getBytes());
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		editor.setHeight(height + "px");
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		editor.setHeight(height);
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		editor.setWidth(width + "px");
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		editor.setWidth(width);
	}

	/**
	 * Get XML content
	 * 
	 * @return XML content
	 */
	public String getJson() {
		return editor.exportJson();
	}

	/**
	 * Set field
	 * 
	 * @param field
	 *            the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

}
