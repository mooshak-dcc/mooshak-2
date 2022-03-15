package pt.up.fc.dcc.mooshak.client.gadgets;

import java.util.Date;

public interface HasLearningTime {

	
	public void setLearningTime(Date date);
	
	public void notifySeenResource();
	
	public boolean hasExceededLearningTime();
	
	public void stopTime();
}
