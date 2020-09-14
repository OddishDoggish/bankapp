package dev.comstock.delegates;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.comstock.beans.Role;
import dev.comstock.beans.User;
import dev.comstock.data.UserPostgres;
import dev.comstock.services.UserService;

public class RegistrationDelegate implements FrontControllerDelegate {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if ("POST".equals(request.getMethod())) {
			if (isAdmin((User) request.getSession().getAttribute("user"))) {
				register(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

	}

	private void register(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		User u = null;
		UserService uServ = new UserService(new UserPostgres());
		ObjectMapper om = new ObjectMapper();
		
		u = om.readValue(request.getInputStream(), User.class);
		Role r = new Role();
		r = uServ.getRoleByName("standard");
		u.setRole(r);
		
		if (isUnique(u.getUsername(), u.getEmail())) {
			int uId = uServ.registerUser(u);
			u.setUserId(uId);
			response.getWriter().write(om.writeValueAsString(u));
		} else {
			response.sendError(400, "Invalid fields");
		}
	}
	
	private boolean isAdmin(User u) {
		if (u != null && u.getRole().getName().equals("admin"))
			return true;
		else
			return false;
	}
	
	private boolean isUnique(String username, String email) {
		UserService uServ = new UserService(new UserPostgres());
		
		if (uServ.getUserByUsername(username) != null)
			return false;
		else if (uServ.getUserByEmail(email) != null)
			return false;
		else
			return true;
	}
}
