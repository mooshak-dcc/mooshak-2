package pt.up.fc.dcc.mooshak.client.widgets;

public interface HasFileContent {

	public void clearObservations();
	
	public void onChangeProgramExtension(String extension,
			boolean askSkeletonUse);
}

