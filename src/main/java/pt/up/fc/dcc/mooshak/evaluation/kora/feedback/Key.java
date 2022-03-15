/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.feedback;

import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Problem;

/**
 * @author hcorreia
 *
 */
public class Key {
	Authenticable team;
	Problem problem;
	public Key(Authenticable team, Problem problem) {
		super();
		this.team = team;
		this.problem = problem;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((problem == null) ? 0 : problem.hashCode());
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
		Key other = (Key) obj;
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

	@Override
	public String toString() {
		return "Key [team=" + team + ", problem=" + problem + "]";
	}
	
	
}
