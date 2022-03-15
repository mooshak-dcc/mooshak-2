package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Simple widget for providing notification feedback.
 */
public class NotificationWidget extends Composite {
	/**
	 * Default CSS styles for this widget.
	 */
	public interface Style extends CssResource {
		String container();

		String notificationText();
	}

	interface Binder extends UiBinder<HTMLPanel, NotificationWidget> {
	}

	private class NotificationAnimation extends Animation {
		private int endSize;
		private int startSize;

		@Override
		protected void onComplete() {
			if (endSize == 0) {
				borderElement.getStyle().setDisplay(Display.NONE);
				return;
			}
			borderElement.getStyle().setHeight(endSize, Unit.PX);
		}

		@Override
		protected void onUpdate(double progress) {
			double delta = (endSize - startSize) * progress;
			double newSize = startSize + delta;
			borderElement.getStyle().setHeight(newSize, Unit.PX);
		}

		void animateMole(int startSize, int endSize, int duration) {
			this.startSize = startSize;
			this.endSize = endSize;
			if (duration == 0) {
				onComplete();
				return;
			}
			run(duration);
		}
	}

	private static final Binder BINDER = GWT.create(Binder.class);
	
	static int showing = 0;

	@UiField()
	DivElement borderElement;

	@UiField
	DivElement heightMeasure;

	@UiField()
	SpanElement notificationText;

	@UiField
	Label closeNotification;

	int showAttempts = 0;

	Timer showTimer = new Timer() {
		@Override
		public void run() {
			if (showAttempts > 0) {
				showImpl();
			}
		}
	};

	private final NotificationAnimation animation = new NotificationAnimation();

	private int animationDuration;
	private int showDuration;

	public NotificationWidget() {
		initWidget(BINDER.createAndBindUi(this));
	}

	/**
	 * Hides the notification.
	 */
	public void hide() {
		if (showAttempts > 0) {
			--showAttempts;
		}
		if (showAttempts == 0) {
			hideNow();
		}
	}

	/**
	 * Force mole to hide and discard outstanding show attempts.
	 */
	public void hideNow() {
		showAttempts = 0;
		animation.animateMole(heightMeasure.getOffsetHeight(), 0,
				animationDuration);
		RootPanel.get().remove(this);
		--showing;
	}

	/**
	 * Sets the animation duration in milliseconds. The animation duration
	 * defaults to 0 if this method is never called.
	 * 
	 * @param duration
	 *            the animation duration in milliseconds.
	 */
	public void setAnimationDuration(int duration) {
		this.animationDuration = duration;
	}

	/**
	 * Sets the notification duration in milliseconds.
	 * 
	 * @param duration
	 *            the notification duration in milliseconds.
	 */
	public void setShowDuration(int duration) {
		this.showDuration = duration;
	}

	/**
	 * Sets the message text to be displayed.
	 * 
	 * @param message
	 *            the text to be displayed.
	 */
	public void setMessage(String message) {
		notificationText.setInnerText(message);
	}

	/**
	 * Display the notification with the existing message.
	 */
	public void show() {
		++showAttempts;
		++showing;
		showImpl();
		
		closeNotification.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	/**
	 * Set the message text and then display the notification.
	 */
	public void show(String message) {
		setMessage(message);
		show();
		
		closeNotification.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	/**
	 * Display the notification, but after a delay.
	 * 
	 * @param delay
	 *            delay in milliseconds.
	 */
	public void showDelayed(int delay) {
		if (showAttempts == 0) {
			if (delay == 0) {
				show();
			} else {
				++showAttempts;
				showTimer.schedule(delay);
			}
		}
		
		closeNotification.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	private void showImpl() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				getElement().getStyle().setTop(10 + (Math.max(0, (showing - 1)) * 205), Unit.PX);
				borderElement.getStyle().setDisplay(Display.BLOCK);
				animation.animateMole(0, heightMeasure.getOffsetHeight(),
						animationDuration);
			}
		});
	}
}