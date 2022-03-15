package pt.up.fc.dcc.mooshak.evaluation.game.tournament;

import javax.json.JsonValue;

/**
 * Interface for objects that can be transformed into JSON
 * 
 * @author josepaiva
 */
public interface Jsonifiable {

	/**
	 * Convert Java object to JsonValue
	 * 
	 * @return JsonValue of the Java object
	 */
	JsonValue toJson();

}
