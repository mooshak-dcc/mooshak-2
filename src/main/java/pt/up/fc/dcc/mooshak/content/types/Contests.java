package pt.up.fc.dcc.mooshak.content.types;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;

public class Contests  extends PersistentContainer<Contest>  {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute(
			name="Contest",
			type=AttributeType.CONTENT)
	public Void contest;
	

	public Contests() {
	}


	/**
	 * List all domains with state (not) Concluded and (not) Created
	 * 
	 * @param listCreated
	 * @param listConcluded
	 * @return
	 * @throws MooshakException
	 */
	public List<ResultsContestInfo> getDomains(boolean listCreated, 
			boolean listConcluded) throws MooshakException  {
		List<ResultsContestInfo> domains = new ArrayList<>();
		
		try {
			for(Contest contest: getChildren(Contest.class, true)) {
				if(isListable(contest, listCreated, listConcluded))
					domains.add(new ResultsContestInfo(
							contest.getIdName(), 
							contest.getDesignation(), 
							contest.getContestStatus(), 
							contest.isRegister(),
							contest.isPublicScoreboard(),
							contest.isFrozen()));
				
			}
		} catch (MooshakContentException cause) {
			String message = "Geting domains (contests)";
			Logger.getLogger("").log(Level.SEVERE,message,cause);
			throw new MooshakException(message,cause);
		}
		
		return domains;
	}
	
	private boolean isListable(Contest contest, 
			boolean listCreated, boolean listConcluded) {
		switch(contest.getContestStatus()) {
		case CREATED:
			return listCreated;
		case READY:
		case RUNNING:
		case RUNNING_VIRTUALLY:
		case FINISHED:
			return true;
		case CONCLUDED:
			return listConcluded;
		default:
			return false;
		}
	}
}
