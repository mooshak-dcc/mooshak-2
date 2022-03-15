package pt.up.fc.dcc.mooshak.content.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pt.up.fc.dcc.asura.tournamentmanager.models.ResultType;
import pt.up.fc.dcc.asura.tournamentmanager.models.StageFormat;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * Stage is a phase of the tournament which runs under the same tournament format
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Stage extends PersistentContainer<Match> {
	private static final long serialVersionUID = 1L;
	
	private static final String SEP = ";";

	@MooshakAttribute(
			name = "Fatal", 
			type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(
			name = "Warning",
			type = AttributeType.LABEL)
	private String warning;
	
	@MooshakAttribute(
			name = "Format",
			type = AttributeType.MENU, 
			tip = "Format of the stage")
	private StageFormat format;
	
	@MooshakAttribute(
			name = "NumberOfPlayersPerMatch", 
			type = AttributeType.INTEGER,
			tip = "Number of Players per Match")
	private Integer nrOfPlayersPerMatch;
	
	@MooshakAttribute(
			name = "MinimumNumberOfPlayersPerGroup", 
			type = AttributeType.INTEGER,
			tip = "Minimum Number of Players per Group (for pool formats)")
	private Integer minNrOfPlayersPerGroup;
	
	@MooshakAttribute(
			name = "MaximumNumberOfRounds",
			type = AttributeType.INTEGER,
			tip = "Maximum Number of Rounds")
	private Integer maxNrOfRounds;
	
	@MooshakAttribute(
			name = "NumberOfQualifiedPlayers",
			type = AttributeType.INTEGER,
			tip = "Number of Qualified Players to the Next Stage")
	private Integer nrOfQualifiedPlayers;
	
	@MooshakAttribute(
			name = "MatchResultType",
			type = AttributeType.MENU,
			tip = "Type of match result")
	private ResultType resultType;
	
	@MooshakAttribute(
			name = "MatchTieBreakers",
			type = AttributeType.TEXT,
			tip = "Tie breakers for matches")
	private String matchTieBreakers;
	
	@MooshakAttribute(
			name = "RankingPoints",
			type = AttributeType.TEXT,
			tip = "Ranking points awarded by match position")
	private String rankingPoints;
	
	@MooshakAttribute(
			name = "RankingTieBreakers",
			type = AttributeType.TEXT,
			tip = "Tie breakers for rankings")
	private String rankingTieBreakers;
	
	
	/*-------------------------------------------------------------------*\
	 * 		            Setters and getters                              *
	\*-------------------------------------------------------------------*/

	/**
	 * Fatal errors messages of this folder
	 * 
	 * @return the fatal
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;
	}

	/**
	 * Set fatal error messages
	 * 
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * Warning errors messages of this folder
	 * 
	 * @return the warning
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;
	}

	/**
	 * Set warning error messages
	 * 
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * @return the format
	 */
	public StageFormat getFormat() {
		if (format == null)
			return StageFormat.SINGLE_ELIMINATION;
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(StageFormat format) {
		this.format = format;
	}

	/**
	 * @return the nrOfPlayersPerMatch
	 */
	public Integer getNrOfPlayersPerMatch() {
		return nrOfPlayersPerMatch;
	}

	/**
	 * @param nrOfPlayersPerMatch the nrOfPlayersPerMatch to set
	 */
	public void setNrOfPlayersPerMatch(Integer nrOfPlayersPerMatch) {
		this.nrOfPlayersPerMatch = nrOfPlayersPerMatch;
	}

	/**
	 * @return the minNrOfPlayersPerGroup
	 */
	public Integer getMinNrOfPlayersPerGroup() {
		return minNrOfPlayersPerGroup;
	}

	/**
	 * @param minNrOfPlayersPerGroup the minNrOfPlayersPerGroup to set
	 */
	public void setMinNrOfPlayersPerGroup(Integer minNrOfPlayersPerGroup) {
		this.minNrOfPlayersPerGroup = minNrOfPlayersPerGroup;
	}

	/**
	 * @return the nrOfQualifiedPlayers
	 */
	public Integer getNrOfQualifiedPlayers() {
		return nrOfQualifiedPlayers;
	}

	/**
	 * @param nrOfQualifiedPlayers the nrOfQualifiedPlayers to set
	 */
	public void setNrOfQualifiedPlayers(Integer nrOfQualifiedPlayers) {
		this.nrOfQualifiedPlayers = nrOfQualifiedPlayers;
	}

	/**
	 * @return the maxNrOfRounds
	 */
	public Integer getMaxNrOfRounds() {
		return maxNrOfRounds;
	}

	/**
	 * @param maxNrOfRounds the maxNrOfRounds to set
	 */
	public void setMaxNrOfRounds(Integer maxNrOfRounds) {
		this.maxNrOfRounds = maxNrOfRounds;
	}
	
	/**
	 * @param resultType the {@link ResultType} to set
	 */
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return the {@link ResultType} match result type
	 */
	public ResultType getMatchResultType() {
		if (resultType == null)
			return ResultType.WIN_DRAW_LOSS;
		return resultType;
	}
	
	public void setMatchTieBreakers(List<String> matchTieBreakers) {
		
		if (matchTieBreakers == null)
			return;
		
		this.matchTieBreakers = String.join(SEP, matchTieBreakers);
	}
	
	public List<String> getMatchTieBreakers() {
		
		if (matchTieBreakers == null)
			return new ArrayList<>();
		
		return Arrays.asList(matchTieBreakers.split(SEP));
	}
	
	public void setRankingPoints(List<Double> rankingPoints) {
		
		if (rankingPoints == null)
			return;
		
		this.rankingPoints = rankingPoints.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(SEP));
	}
	
	public List<Double> getRankingPoints() {
		
		if (rankingPoints == null)
			return new ArrayList<>();
		
		return Arrays.stream(rankingPoints.split(SEP))
				.map(Double::parseDouble)
				.collect(Collectors.toList());
	}
	
	public void setRankingTieBreakers(List<String> rankingTieBreakers) {
		
		if (rankingTieBreakers == null)
			return;
		
		this.rankingTieBreakers = String.join(SEP, rankingTieBreakers);
	}
	
	public List<String> getRankingTieBreakers() {
		
		if (rankingTieBreakers == null)
			return new ArrayList<>();
		
		return Arrays.asList(rankingTieBreakers.split(SEP));
	}

	/**
	 * Add a match to the stage
	 * 
	 * @param match {@link Match} match to add
	 * @throws MooshakException - if an error occurs while adding the match
	 */
	public void addMatch(String id, String movie)
			throws MooshakException {
		
		Match matchPO = create(id, Match.class);
		
		matchPO.saveMovie(movie);
	}
	
	/**
	 * Build stage {@link JsonObject} 
	 * 
	 * @return {@link JsonObject} stage 
	 * @throws MooshakException 
	 *//*
	public JsonObject buildStageJson() throws MooshakException {
		
		JsonObjectBuilder stageBuilder = Json.createObjectBuilder();

		stageBuilder.add("type", getFormat().getCategory().toString());
		stageBuilder.add("format", getFormat().toString());
		
		JsonArrayBuilder roundsBuilder = Json.createArrayBuilder();
		for (PersistentObject roundPO : getChildren(true)) {
			Round round = (Round) roundPO;
			roundsBuilder.add(round.buildRoundJson());
		}
		
		stageBuilder.add("rounds", roundsBuilder.build());
		
		return stageBuilder.build();
	}
	
	*//**
	 * Build stage rankings {@link JsonArray} 
	 * 
	 * @return {@link JsonArray} stage rankings 
	 * @throws MooshakException 
	 *//*
	public JsonArray buildRankingsJson() throws MooshakException {
		
		JsonArrayBuilder rankingsBuilder = Json.createArrayBuilder();
		
		List<Round> rounds = getChildren(true);
		
		switch (format) {
		case SINGLE_ELIMINATION:
			
			break;

		default:
			break;
		}
		
		Map<Integer, List<Round>> roundsByGroup = rounds.stream()
				.collect(Collectors.groupingBy(Round::getGroupNumber));
		
		for (Integer groupNumber : roundsByGroup.keySet()) {
			
			//Map<String, Double> groupScores = new 
			
			List<Round> groupRounds = roundsByGroup.get(groupNumber);
			for (Round round : groupRounds) {
				
				Map<String, Double> scoresByPlayer = round.getAwardedScoresByPlayer();
				for (String playerId : scoresByPlayer.keySet()) {
					
				}
			}
		}
		
		return rankingsBuilder.build();
	}*/
}
