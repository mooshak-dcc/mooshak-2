package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.mydata.MyDataPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.mydata.MyDataView;
import pt.up.fc.dcc.mooshak.client.gadgets.mydata.MyDataViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

public class MyData extends Gadget {

	private static MyData myData = null;

	private MyData(EnkiCommandServiceAsync rpcEnki, Token token, GadgetType type) {
		super(token, type);

		MyDataView view = new MyDataViewImpl();

		MyDataPresenter presenter = new MyDataPresenter(rpcEnki, view, token);

		presenter.go(null);

		setPresenter(presenter);
		setView(view);
	}

	public static MyData getInstance(EnkiCommandServiceAsync rpcEnki, Token token, GadgetType type) {
		if (myData == null)
			myData = new MyData(rpcEnki, token, type);
		return myData;
	}

	@Override
	public String getName() {
		return CONSTANTS.myData();
	}
}
