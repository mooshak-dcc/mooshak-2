package pt.up.fc.dcc.mooshak.client.gadgets.mydata;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public interface MyDataView extends View {

	public interface Presenter {
		String getStudentId();
		void updateProfile();
	}
	
	void setPresenter(Presenter presenter);

	void setProfile(StudentProfile profile);

	void setStudentId(String studentId);

}
