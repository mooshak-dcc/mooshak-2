package pt.up.fc.dcc.mooshak.shared.asura;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TournamentMovie implements IsSerializable {

	private String playerId;
	private String json;

	public TournamentMovie() {
		super();
	}

	/**
	 * @return the playerId
	 */
	public String getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId
	 *            the playerId to set
	 */
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	/**
	 * @return the json
	 */
	public String getJson() {
		return json;
	}

	/**
	 * @param json
	 *            the json to set
	 */
	public void setJson(String json) {
		this.json = json;
	}

}
