package pt.up.fc.dcc.mooshak.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ltiwrapper.LTIWrapper;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.managers.EnkiManager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Authentication through LTI
 * 
 * @author josepaiva
 */
@WebServlet("/lti-auth")
public class LTIAuthentication extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getSession().getAttribute("session") == null) {
			super.doGet(req, resp);
			return;
		}

		Session session = (Session) req.getSession().getAttribute("session");
		log(session.getEntryPoint());
		try {
			String encodedUrl = resp.encodeRedirectURL(req.getContextPath()
					+ "/" + session.getEntryPoint() + ".html");
			resp.sendRedirect(encodedUrl);
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setAttribute("redirect_url", req.getRequestURL());

		LTIWrapper ltiwrapper = new LTIWrapper(req, resp);
		if (ltiwrapper.isRequestValid()) {
			String name = ltiwrapper.getUserName();
			String userId = ltiwrapper.getUserId();
			String contest = ltiwrapper.getCustomParameter("custom_contest");

			Session session;
			try {
				session = AuthManager.getInstance().authenticateLtiUser(
						contest, userId, name);
				session.setLtiChannel(ltiwrapper);

				HttpSession httpSession = req.getSession();
				httpSession.setAttribute("session", session);
			} catch (MooshakException e) {
				throw new ServletException(name + " : " + e.getMessage());
			}

			try {
				EnkiManager.getInstance().registerPlayerAtGS(session);
			} catch (MooshakException e) {
				Logger.getLogger("").log(Level.SEVERE, e.getMessage());
			}
			
			try {
				EnkiManager.getInstance().registerStudentAtSequenciationService(session);
			} catch (MooshakException e) {
				Logger.getLogger("").log(Level.SEVERE, e.getMessage());
			}
		} else {
			String error = ltiwrapper.getErrorDescription();
			System.out.println("Error: " + error);
		}
	}

}
