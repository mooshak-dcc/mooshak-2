package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Team;

/**
 * @author Helder Correia
 *
 */
public class KeyGenerator {
	private Team team;
	private Problem problem;
	public KeyGenerator(Team team, Problem problem) {
		this.team=team;
		this.problem=problem;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((problem == null) ? 0 : problem.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyGenerator other = (KeyGenerator) obj;
		if (problem == null) {
			if (other.problem != null)
				return false;
		} else if (!problem.equals(other.problem))
			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}




	/**
	 * @return the problem
	 */
	public Problem getProblem() {
		return problem;
	}
	/**
	 * @param problem the problem to set
	 */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	/**
	 * @return the team
	 */
	public Team getTeam() {
		return team;
	}
	/**
	 * @param team the team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
	}
	
	

}
