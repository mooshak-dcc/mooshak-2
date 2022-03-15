package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.AbstractStage;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Stage of a tournament of double knockout category
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DoubleKnockoutStage extends AbstractStage {
	
	private Map<String, ProcessBuilder> players;
	
	//private 
	
	private LinkedList<String> winners = new LinkedList<>();
	private LinkedList<String> losers = new LinkedList<>();

	public DoubleKnockoutStage(
			int minNrOfPlayersPerMatch, int maxNrOfPlayersPerMatch,
			int nrOfPlayersPerMatch, int minNrOfPlayersPerGroup, 
			int nrOfQualifiedPlayers, int maxNrOfRounds) {
		super(minNrOfPlayersPerMatch, maxNrOfPlayersPerMatch,
				nrOfPlayersPerMatch, minNrOfPlayersPerGroup,
				nrOfQualifiedPlayers, maxNrOfRounds);
	}

	@Override
	public void prepare(Map<String, ProcessBuilder> players) throws MooshakException {
		this.players = players;
	}

	@Override
	public void run() throws MooshakException {

		/*
		 * losersBracket = new HashMap<>(); winnersBracket = new
		 * HashMap<>(players);
		 * 
		 * losersMatches = new HashMap<>(); winnersMatches = new HashMap<>();
		 * 
		 * int roundNr = 0; while (winnersBracket.size() >= nrPlayersPerMatch) {
		 * losersMatches.put(roundNr + 1, new ArrayList<>());
		 * winnersMatches.put(roundNr + 1, new ArrayList<>());
		 * 
		 * if (losersBracket.size() > 0) {
		 * 
		 * List<Match> matches = KnockoutStage.generateMatches(losersBracket,
		 * nrPlayersPerMatch);
		 * 
		 * for (Match match : matches) { try { match.run(); } catch (IOException
		 * e) { throw new MooshakException(e.getMessage()); }
		 * 
		 * List<Player> ranks = match.getMatchRanking(isDescendant()); for (int
		 * i = 1; i < ranks.size(); i++) {
		 * losersBracket.remove(ranks.get(i).getId()); } }
		 * 
		 * losersMatches.get(roundNr).addAll(matches); }
		 * 
		 * List<Match> matches = KnockoutStage.generateMatches(winnersBracket,
		 * nrPlayersPerMatch);
		 * 
		 * for (Match match : matches) { try { match.run(); } catch (IOException
		 * e) { throw new MooshakException(e.getMessage()); }
		 * 
		 * List<Player> ranks = match.getMatchRanking(isDescendant()); for (int
		 * i = 1; i < ranks.size(); i++) {
		 * losersBracket.put(ranks.get(i).getId(),
		 * winnersBracket.remove(ranks.get(i).getId())); } }
		 * 
		 * winnersMatches.get(roundNr++).addAll(matches); }
		 * 
		 * while (losersBracket.size() > nrPlayersPerMatch) {
		 * losersMatches.put(roundNr + 1, new ArrayList<>());
		 * winnersMatches.put(roundNr + 1, new ArrayList<>());
		 * 
		 * List<Match> matches = KnockoutStage.generateMatches(losersBracket,
		 * nrPlayersPerMatch);
		 * 
		 * for (Match match : matches) { try { match.run(); } catch (IOException
		 * e) { throw new MooshakException(e.getMessage()); }
		 * 
		 * List<Player> ranks = match.getMatchRanking(isDescendant()); for (int
		 * i = 1; i < ranks.size(); i++) {
		 * losersBracket.remove(ranks.get(i).getId()); } }
		 * 
		 * losersMatches.get(roundNr++).addAll(matches); }
		 */

		/*
		 * if (losersBracket.size() + winnersBracket.size() >=
		 * minPlayersPerMatch && losersBracket.)
		 */
	}

	@Override
	public List<String> getQualifiedPlayers() throws MooshakException {
		// TODO Auto-generated method stub
		return null;
	}

}
