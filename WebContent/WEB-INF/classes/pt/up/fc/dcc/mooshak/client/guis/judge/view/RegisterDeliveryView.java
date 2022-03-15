package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

public interface RegisterDeliveryView extends View {

	public interface Presenter {
		void onChange(String objectId, MooshakValue value);
	}
	
	
	void setPresenter(Presenter presenter);

	void setObjectId(String objectId);
	void setFormDataProvider(FormDataProvider dataProvider);
	void setMessage(String message);
	void refreshProviders();
}
