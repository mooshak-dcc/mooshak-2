package pt.up.fc.dcc.mooshak.rest.auth.security;

import java.io.Serializable;
import java.time.LocalDateTime;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;

/**
 * Model that holds details about the auth token
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class AuthTokenDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Session session;
	private String issuer;
	private LocalDateTime issuedDate;
	private LocalDateTime expirationDate;
	private int refreshCount;
	private int refreshLimit;
	
	public AuthTokenDetails(String id, Session session, String issuer, LocalDateTime issuedDate, LocalDateTime expirationDate,
			int refreshCount, int refreshLimit) {
		super();
		this.id = id;
		this.session = session;
		this.issuer = issuer;
		this.issuedDate = issuedDate;
		this.expirationDate = expirationDate;
		this.refreshCount = refreshCount;
		this.refreshLimit = refreshLimit;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return session.getContestId();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		try {
			return session.getParticipant().getIdName();
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		
		try {
			return session.getProfile().getIdName().toUpperCase();
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return the issuer
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	/**
	 * @return the issuedDate
	 */
	public LocalDateTime getIssuedDate() {
		return issuedDate;
	}

	/**
	 * @param issuedDate the issuedDate to set
	 */
	public void setIssuedDate(LocalDateTime issuedDate) {
		this.issuedDate = issuedDate;
	}

	/**
	 * @return the expirationDate
	 */
	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the refreshCount
	 */
	public int getRefreshCount() {
		return refreshCount;
	}

	/**
	 * @param refreshCount the refreshCount to set
	 */
	public void setRefreshCount(int refreshCount) {
		this.refreshCount = refreshCount;
	}

	/**
	 * @return the refreshLimit
	 */
	public int getRefreshLimit() {
		return refreshLimit;
	}

	/**
	 * @param refreshLimit the refreshLimit to set
	 */
	public void setRefreshLimit(int refreshLimit) {
		this.refreshLimit = refreshLimit;
	}
	
	/**
	 * Can refresh token?
	 * 
	 * @return {@code boolean} {@code true} if can refresh token, {@code false} otherwise
	 */
	public boolean canRefresh() {
		return refreshCount < refreshLimit;
	}
	
}
