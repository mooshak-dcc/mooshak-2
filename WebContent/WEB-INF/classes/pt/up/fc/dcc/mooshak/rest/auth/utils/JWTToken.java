package pt.up.fc.dcc.mooshak.rest.auth.utils;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Sessions;
import pt.up.fc.dcc.mooshak.rest.auth.security.AuthTokenDetails;
import pt.up.fc.dcc.mooshak.rest.exception.ForbiddenException;
import pt.up.fc.dcc.mooshak.rest.exception.UnauthenticatedException;

/**
 * Utilities to deal with JWT Tokens
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class JWTToken {
	public static final String TOKEN_TYPE = "Bearer";
	public static final long EXPIRATION_TIME_MINUTES = 60L;
	public static final int REFRESH_LIMIT = 5;
	public static final String CLAIM_SESSION_ID = "sessionId";
	public static final String CLAIM_REFRESH_COUNT = "refreshCount";
	public static final String CLAIM_REFRESH_LIMIT = "refreshLimit";

	private static final Logger LOGGER = Logger.getLogger(JWTToken.class.getName());

	private static JWTToken instance = null;

	private SecretKey secretKey;

	private Sessions sessions;

	private JWTToken() throws NoSuchAlgorithmException, MooshakContentException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		secretKey = keyGenerator.generateKey();
		sessions = PersistentObject.openPath("data/configs/sessions");
	}

	public static JWTToken getInstance() throws NoSuchAlgorithmException, MooshakContentException {
		if (instance == null)
			instance = new JWTToken();

		return instance;
	}

	/**
	 * Issue a token for a established session
	 * 
	 * @param issuer
	 *            {@code String} Issuer of the token
	 * @param session
	 *            {@code Session} Session to which the token is being created
	 * @return {@code String} JWT Token
	 * @throws MooshakContentException
	 */
	public String issue(String issuer, Session session) throws MooshakContentException {

		String jwtToken = Jwts.builder().setId(generateTokenIdentifier())
				.setSubject(session.getParticipant().getIdName())
				.setIssuer(issuer)
				.claim(CLAIM_SESSION_ID, session.getIdName())
				.claim(CLAIM_REFRESH_COUNT, 0)
				.claim(CLAIM_REFRESH_LIMIT, REFRESH_LIMIT)
				.setIssuedAt(new Date())
				.setExpiration(toDate(LocalDateTime.now().plusMinutes(EXPIRATION_TIME_MINUTES)))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
		LOGGER.info("#### generating token for a key : " + jwtToken + " - " + secretKey);
		return jwtToken;
	}

	/**
	 * Refresh a JWT Token
	 * 
	 * @param tokenDetails
	 *            {@link AuthTokenDetails} details of the token
	 * @return {@link String} JWT Token
	 */
	public String refresh(AuthTokenDetails tokenDetails) {

		if (!tokenDetails.canRefresh())
			throw new ForbiddenException("This token has exceeded the refresh limit.");

		String jwtToken = Jwts.builder().setId(tokenDetails.getId()).setSubject(tokenDetails.getUsername())
				.setIssuer(tokenDetails.getIssuer())
				.claim(CLAIM_SESSION_ID, tokenDetails.getSession().getIdName())
				.claim(CLAIM_REFRESH_COUNT, tokenDetails.getRefreshCount() + 1)
				.claim(CLAIM_REFRESH_LIMIT, tokenDetails.getRefreshLimit())
				.setIssuedAt(new Date())
				.setExpiration(toDate(LocalDateTime.now().plusMinutes(EXPIRATION_TIME_MINUTES)))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
		LOGGER.info("#### refreshing token for a key : " + jwtToken + " - " + secretKey);
		return jwtToken;
	}

	/**
	 * Parse a JWT Token
	 * 
	 * @param token
	 *            {@link String} JWT Token
	 * @return {@link AuthTokenDetails} details of the token
	 */
	public AuthTokenDetails parse(String token) {

		AuthTokenDetails tokenDetails = null;
		try {

			Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

			String id = (String) claims.get(Claims.ID);
			Session session = sessions.open((String) claims.get(CLAIM_SESSION_ID));
			if (session == null)
				throw new UnauthenticatedException();

			String issuer = (String) claims.get(Claims.ISSUER);
			LocalDateTime issuedDate = LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(),
					ZoneId.systemDefault());
			LocalDateTime expirationDate = LocalDateTime.ofInstant(claims.getExpiration().toInstant(),
					ZoneId.systemDefault());
			int refreshLimit = (int) claims.getOrDefault(CLAIM_REFRESH_LIMIT, REFRESH_LIMIT);
			int refreshCount = (int) claims.getOrDefault(CLAIM_REFRESH_COUNT, 0);

			tokenDetails = new AuthTokenDetails(id, session, issuer, issuedDate, expirationDate, refreshCount,
					refreshLimit);
		} catch (ExpiredJwtException e) {
			throw new UnauthenticatedException("The token that you have provided has expired.", e);
		} catch (InvalidClaimException e) {
			throw new UnauthenticatedException(
					"The token that you have provided has an invalid value for claim \"" + e.getClaimName() 
					+ "\".", e);
		} catch (Exception e) {
			throw new UnauthenticatedException("The token that you have provided is not valid.", e);
		}

		return tokenDetails;
	}

	/**
	 * Generate a token identifier
	 *
	 * @return {@link String} token identifier
	 */
	private String generateTokenIdentifier() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Convert a {@link LocalDateTime} to a {@link Date}
	 * 
	 * @param localDateTime
	 *            {@link LocalDateTime}
	 * @return {@link Date}
	 */
	private Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}
