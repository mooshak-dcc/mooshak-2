package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;
import java.util.Date;

import ltiwrapper.LTIWrapper;

import com.google.gwt.user.client.rpc.IsSerializable;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Session extends PersistentObject implements IsSerializable {
	// Object of this class are stored in HTTP sessions
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(
			name="Contest",
			refType = "Contest",
			complement="/data/contests",
			tip = "Contest where authentication takes place")
	private Path contest = null; 
	
	@MooshakAttribute(
			name="Participant",
			type=AttributeType.PATH,
			tip = "Particiant id, contestant, team or student")
	private Path participant = null; 
	
	@MooshakAttribute(
			name="Profile",
			type=AttributeType.PATH,
			tip = "User profile")
	private Path profile = null;

	@MooshakAttribute(
			name="Entry_Point",
			tip = "Entry point on client")
	private String entryPoint;

	@MooshakAttribute(
			name="Last_Used",
			tip = "Last autorization time for this session")
	private Date lastUsed = new Date();
	
	// fields to ignore, declared for compatibility with version 1.6
	@MooshakAttribute(name = "profile", type = AttributeType.DATA)
    private Void profile_;                                                   
    @MooshakAttribute(name = "style", type = AttributeType.DATA)
    private Void style_;                                                     
    @MooshakAttribute(name = "language", type = AttributeType.DATA)
    private Void language_;                                                  
    @MooshakAttribute(name = "user", type = AttributeType.DATA)
    private Void user_;                                              
    @MooshakAttribute(name = "authorization", type = AttributeType.DATA)
    private Void authorization_;                                             
    @MooshakAttribute(name = "browser", type = AttributeType.DATA)
    private Void browser_;                                                   
    @MooshakAttribute(name = "contest", type = AttributeType.DATA)
    private Void contest_;                                                   
    @MooshakAttribute(name = "command", type = AttributeType.DATA)
    private Void command_;                                                   
    @MooshakAttribute(name = "messages", type = AttributeType.DATA)
    private Void messages;                                                  
    @MooshakAttribute(name = "modified", type = AttributeType.DATA)
    private Void modified_;                                        
    @MooshakAttribute(name = "controller", type = AttributeType.DATA)
    private Void controller_;
    @MooshakAttribute(name = "type", type = AttributeType.DATA)
    private Void type_;                                                      
    @MooshakAttribute(name = "time", type = AttributeType.DATA)
    private Void time_;                                        
    @MooshakAttribute(name = "time_type", type = AttributeType.DATA)
    private Void time_type_;                                        
    @MooshakAttribute(name = "page", type = AttributeType.DATA)
    private Void page_;                                                      
    @MooshakAttribute(name = "lines", type = AttributeType.DATA)
    private Void lines_;                                                     
    @MooshakAttribute(name = "problems", type = AttributeType.DATA)
    private Void problems_;                                                  
    @MooshakAttribute(name = "teams", type = AttributeType.DATA)
    private Void teams_;                                                     
    @MooshakAttribute(name = "time_interval", type = AttributeType.DATA)
    private Void time_interval;                                                      
    @MooshakAttribute(name = "words", type = AttributeType.DATA)
    private Void words_;                                                     
    @MooshakAttribute(name = "flow", type = AttributeType.DATA)
    private Void flow_;                                                      
    @MooshakAttribute(name = "scope", type = AttributeType.DATA)
    private Void scope_;                                                     
    @MooshakAttribute(name = "size", type = AttributeType.DATA)
    private Void size_;
    @MooshakAttribute(name = "tab", type = AttributeType.DATA)
    private Void tab_;                                                       
    @MooshakAttribute(name = "tree", type = AttributeType.DATA)
    private Void tree_;                                                      
    @MooshakAttribute(name = "search", type = AttributeType.DATA)
    private Void search_;                                                    
    @MooshakAttribute(name = "path", type = AttributeType.DATA)
    private Void path_;   
    @MooshakAttribute(name = "archive", type = AttributeType.DATA)
    private Void archive_;                                                   
    @MooshakAttribute(name = "virtual", type = AttributeType.DATA)
    private Void virtual_;                                                   
    @MooshakAttribute(name = "groups", type = AttributeType.DATA)
    private Void groups_;                                                    
    @MooshakAttribute(name = "questions", type = AttributeType.DATA)
    private Void questions_;                                                 
    @MooshakAttribute(name = "choices", type = AttributeType.DATA)
    private Void choices_;                                               
    @MooshakAttribute(name = "target", type = AttributeType.DATA)
    private Void target_;
	// -------------------------------------------------------------
	
	
	private String contestId = null;
	private Authenticable authenticable = null;
	
	private LTIWrapper ltiChannel = null;
	
	public Session() {
		// TODO Auto-generated constructor stub
	}
	
	/*-------------------------------------------------------------------*\
	 * 		            Setters and getters                              *
	\*-------------------------------------------------------------------*/
	
	/**
	 * Get last time this session was used
	 * @return the lastUsed
	 */
	public Date getLastUsed() {
		return lastUsed;
	}


	/**
	 * Set last time this session was used
	 * @param lastUsed the lastUsed to set
	 */
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	/**
	 * Get this session's contest
	 * @return
	 * @throws MooshakContentException
	 */
	public Contest getContest() throws MooshakContentException {
		if(contest == null)
			return null;
		else
			return open(contest);
	}
	
	/**
	 * Set this session's contest
	 * @param contest
	 */
	public void setContest(Contest contest) {
		this.contest = contest.getPath();
		contestId = contest.getIdName();
	}

	/**
	 * Get participant using this session
	 * @return
	 * @throws MooshakContentException
	 */
	public Authenticable getParticipant() throws MooshakContentException {
		
		if(authenticable == null) {
			
			if(participant == null)
				return null;
			else
				return PersistentObject.open(participant);
		} else
			return authenticable;
	}

	/**
	 * Set participant using this session 
	 * @param participant
	 */
	public void setParticipant(Authenticable participant) {
		authenticable = participant;
		this.participant = participant.getPath();
	}

	/**
	 * Get profile of this session
	 * @return
	 * @throws MooshakContentException
	 */
	public Profile getProfile() throws MooshakContentException {
		if(profile == null)
			return null;
		else
			return open(profile);
	}

	/**
	 * Set profile of this session
	 * @param profile
	 */
	public void setProfile(Profile profile) {
		this.profile = profile.getPath();
	}

	/**
	 * Get type of entry point for this session
	 * @return
	 */
	public String getEntryPoint() {
		return entryPoint;
	}
	
	/**
	 * Set type of entry point for this session
	 * @param entryPoint
	 */
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}

	/**
	 * @return the ltiChannel
	 */
	public LTIWrapper getLtiChannel() {
		return ltiChannel;
	}

	/**
	 * @param ltiChannel the ltiChannel to set
	 */
	public void setLtiChannel(LTIWrapper ltiChannel) {
		this.ltiChannel = ltiChannel;
	}

	/**
	 * Get contest id of this session
	 * @return
	 */
	public String getContestId() {
		if(contestId == null) {
			if(contest == null)
				return null;
			else
				return Filenames.getSafeFileName(contest.getFileName());
		} else
			return contestId;
	}	
	
	/**
	 * Disable backup for sessions
	 */
	protected void backup() {}


	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}
