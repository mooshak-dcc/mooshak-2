package pt.up.fc.dcc.mooshak.client.gadgets.resourcerating;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcerating.ResourceRatingView.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;

public class ResourceRatingPresenter extends GadgetPresenter<ResourceRatingView> implements Presenter {
	private EnkiConstants constants = GWT.create(EnkiConstants.class);

	public ResourceRatingPresenter(EnkiCommandServiceAsync enkiService, HandlerManager eventBus,
			ResourceRatingView view, Token token) {
		super(eventBus, null, null, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		updateResourcesFeedback();
	}

	@Override
	public void onSubmit(int rating, String comment) {
		enkiService.sendFeedbackToResource(contextInfo.getactivityId(), resourceId, rating, comment,
				new AsyncCallback<Feedback>() {

					@Override
					public void onSuccess(Feedback f) {
						view.setRating(f.getRating(), f.getComment());
						view.setMessage(constants.resourceRated());
					}

					@Override
					public void onFailure(Throwable e) {
						view.setMessage(e.getMessage());
					}
				});
	}

	@Override
	public void updateResourcesFeedback() {
		enkiService.getFeedbackToResource(contextInfo.getactivityId(), resourceId, new AsyncCallback<Feedback>() {

			@Override
			public void onSuccess(Feedback f) {
				view.setRating(f.getRating(), f.getComment());
			}

			@Override
			public void onFailure(Throwable e) {

			}
		});
	}

}
