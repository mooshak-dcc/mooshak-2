package pt.up.fc.dcc.mooshak.client.gadgets.mydata;

import org.gwtbootstrap3.client.ui.DescriptionData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class MyDataViewImpl extends ResizeComposite implements MyDataView {
	
	private static MyDataUiBinder uiBinder = GWT
			.create(MyDataUiBinder.class);

	@UiTemplate("MyDataView.ui.xml")
	interface MyDataUiBinder extends
			UiBinder<Widget, MyDataViewImpl> {
	}
	
	private Presenter presenter = null;
	
	@UiField
	DescriptionData name;
	
	@UiField
	DescriptionData register;
	
	@UiField
	DescriptionData solved;
	
	/*@UiField
	Label unsolved;*/
	
	@UiField
	DescriptionData seenVideos;
	
	@UiField
	DescriptionData seenStatic;
	
	@UiField
	DescriptionData submissions;
	
	@UiField
	DescriptionData acceptedSubmissions;
	
	@UiField
	DescriptionData part;
	
	
	public MyDataViewImpl() {

		initWidget(uiBinder.createAndBindUi(this));
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setProfile(StudentProfile profile) {
		name.setText(profile.getStudentName());
		
		if (profile.getRegistrationDate() != null)
			register.setText(DateTimeFormat.getFormat("dd/MM/yyyy").format(
					profile.getRegistrationDate()));
		
		solved.setText(String.valueOf(profile.getSolvedExercises()));
		//unsolved.setText(profile.getUnsolvedExercises().toString());
		seenVideos.setText(String.valueOf(profile.getVideoResourcesSeen()));
		seenStatic.setText(String.valueOf(profile.getStaticResourcesSeen()));
		submissions.setText(String.valueOf(profile.getSubmissions()));
		acceptedSubmissions.setText(String.valueOf(profile.getAcceptedSubmissions()));
		part.setText(profile.getCurrentPart());
	}

	@Override
	public void setStudentId(String studentId) {
		name.setText(studentId);
	}

}
