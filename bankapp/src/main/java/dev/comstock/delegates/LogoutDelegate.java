package dev.comstock.delegates;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.comstock.beans.User;

public class LogoutDelegate implements FrontControllerDelegate {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if ("POST".equals(request.getMethod())) {
			if (request.getSession(false) != null) {
				User u = (User) request.getSession().getAttribute("user");
				response.getWriter().write("You have successfully logged out: " + u.getUsername());
				request.getSession().invalidate();
			} else {
				response.getWriter().write("There was no user logged into this session.");
			}
		} else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
	}
}
