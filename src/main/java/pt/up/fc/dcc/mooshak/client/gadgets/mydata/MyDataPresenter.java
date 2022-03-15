package pt.up.fc.dcc.mooshak.client.gadgets.mydata;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.mydata.MyDataView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class MyDataPresenter extends GadgetPresenter<MyDataView> implements Presenter {

	private String studentId;
	
	public MyDataPresenter(EnkiCommandServiceAsync enkiService,
			MyDataView view, Token token) {
		super(null, null, null, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}
	
	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		updateProfile();
	}

	/**
	 * @return the studentId
	 */
	@Override
	public String getStudentId() {
		return studentId;
	}

	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
		view.setStudentId(studentId);
	}

	@Override
	public void updateProfile() {

		enkiService.getProfile(contextInfo.getactivityId(), 
				new AsyncCallback<StudentProfile>() {
			
			@Override
			public void onSuccess(StudentProfile profile) {
				view.setProfile(profile);
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				Logger.getLogger("").log(Level.SEVERE, "Couldn't get profile! " + 
						arg0.getMessage());
			}
		});
	}

}
