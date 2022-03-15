package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.xonomygwt.client.wrapper.XonomyGWTWrapper;

/**
 * Xml editor widget that implements MooshakWidget
 * 
 * @author josepaiva
 */
public class CustomXmlEditor extends Composite implements MooshakWidget {

	private static final Logger LOGGER = Logger.getLogger("");

	/*private static CustomXmlEditor instance = null;*/

	private HandlerRegistration initAttachHandlerRegistration = null;
	private HandlerRegistration valueAttachHandlerRegistration = null;
	private HandlerRegistration docSpecAttachHandlerRegistration = null;

	private boolean isEditing = false;
	private String field = null;

	private XonomyGWTWrapper editor;

	public CustomXmlEditor() {
		
		this.editor = new XonomyGWTWrapper();
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(editor);

		panel.setCellHeight(editor, "100%");
		panel.getElement().getStyle().setPosition(Position.RELATIVE);
		initWidget(panel);

		init();
	}

	/*public static CustomXmlEditor getInstance() {
		if (instance == null)
			instance = new CustomXmlEditor();
		return instance;
	}*/

	/**
	 * Initialize XML editor
	 */
	private void init() {

		initAttachHandlerRegistration = editor.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {

				editor.render("<root></root>", null);

				initAttachHandlerRegistration.removeHandler();
			}
		});
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
							editor.setData(new String(value.getContent(), "UTF-8"));
						else
							editor.setData("<" + XonomyGWTWrapper.getDefaultRootElement() + "/>");
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Setting value on XML editor.");
					}

					if (fireEvents)
						editor.fireEvent(new ValueChangeEvent<MooshakValue>(value) {
						});

					valueAttachHandlerRegistration.removeHandler();
					valueAttachHandlerRegistration = null;
				}
			});
		} else {
			
			try {
				if (value.getContent() != null)
					editor.setData(new String(value.getContent(), "UTF-8"));
				else
					editor.setData("<" + XonomyGWTWrapper.getDefaultRootElement() + "/>");
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Setting value on XML editor.");
			}
		}

		if (fireEvents)
			editor.fireEvent(new ValueChangeEvent<MooshakValue>(value) {});
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
		return new MooshakValue(field, "toBeSetAfter", getXML().getBytes());
	}

	public void setMode(String mode) {
		XonomyGWTWrapper.setMode(mode);
	}

	/**
	 * Set document specification
	 * 
	 * @param docSpec
	 *            document specification
	 */
	public void setDocSpec(final String docSpec) {

		if (editor.isAttached())
			editor.setDocSpec(docSpec);
		else {
			if (docSpecAttachHandlerRegistration != null)
				docSpecAttachHandlerRegistration.removeHandler();
			docSpecAttachHandlerRegistration = editor.addAttachHandler(new Handler() {

				@Override
				public void onAttachOrDetach(AttachEvent event) {

					editor.setDocSpec(docSpec);

					docSpecAttachHandlerRegistration.removeHandler();
					docSpecAttachHandlerRegistration = null;
				}
			});
		}
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
	public String getXML() {
		return XonomyGWTWrapper.harvest();
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
