package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

public class ScoreDatabase {
	private static ScoreDatabase instance;
	 
    public static ScoreDatabase getInstance() {
        if (instance == null) {
            instance = new ScoreDatabase();
        }
        return instance;
    }
 
    private ListDataProvider<ScoreRow> dataProvider = 
    		new ListDataProvider<ScoreRow>(ScoreRow.KEY_PROVIDER);
 
    private ScoreDatabase() {
    }
 
    public void addScore(ScoreRow score) {
        List<ScoreRow> scores = dataProvider.getList();
        scores.remove(score);
        scores.add(score);
    }
 
    public void addDataDisplay(HasData<ScoreRow> display) {
        dataProvider.addDataDisplay(display);
    }
 
    public ListDataProvider<ScoreRow> getDataProvider() {
        return dataProvider;
    }
 
    public void refreshDisplays() {
        dataProvider.refresh();
    }
 
}
