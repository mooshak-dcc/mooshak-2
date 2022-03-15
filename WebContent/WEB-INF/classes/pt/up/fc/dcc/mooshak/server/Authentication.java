package pt.up.fc.dcc.mooshak.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.up.fc.dcc.mooshak.managers.AuthManager;

/**
 * Authentication with localized language using HTTP preferences
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
//@WebServlet("/authentication")
public class Authentication extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}
	
	AuthManager authmanager = AuthManager.getInstance();

	private void process(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException {
		
		try {
			StringBuffer url = req.getRequestURL();			
			String prefered = req.getHeader("Accept-Language");
			String locale = authmanager.matchPreferredLanguage(prefered);
			
			url.append("/../Authentication.html");
			url.append("?locale="+locale);
		
			resp.setStatus(HttpServletResponse.SC_FOUND);
			resp.setHeader("Location",url.toString());
		} catch(Exception cause) {
			throw new ServletException("Redirecting authentication",cause);
		}
	}

	
	
	
}