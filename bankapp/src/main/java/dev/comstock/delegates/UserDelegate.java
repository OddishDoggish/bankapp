package dev.comstock.delegates;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.comstock.beans.User;
import dev.comstock.data.UserPostgres;
import dev.comstock.services.UserService;

public class UserDelegate implements FrontControllerDelegate {

	private UserService uServ = new UserService(new UserPostgres());
	private ObjectMapper om = new ObjectMapper();
	
	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		if (request.getSession(false) == null)
			response.sendError(400, "Invalid credentials.");
		
		String path = (String) request.getAttribute("path");
		User uSession = (User) request.getSession().getAttribute("user");
		
		
		if (path == null || path.equals("")) {
			if ("PUT".equals(request.getMethod())) {

				User uAccount = new User();
				
				Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
				if (jsonMap.containsKey("userid"))
					uAccount = uServ.getUserByID((int) jsonMap.get("userid"));
				else uAccount = uSession;				
				if (isAdmin(uSession) || isSelf(uAccount, uSession)) {
					if (jsonMap.containsKey("password"))
						uAccount.setPassword((String) jsonMap.get("password"));
					if (jsonMap.containsKey("firstName"))
						uAccount.setFirstName((String) jsonMap.get("firstName"));
					if (jsonMap.containsKey("lastName"))
						uAccount.setLastName((String) jsonMap.get("lastName"));
					if (jsonMap.containsKey("email"))
						uAccount.setEmail((String) jsonMap.get("email"));
					
					if (isAdmin(uSession)) {
						if (jsonMap.containsKey("username"))
							uAccount.setUsername((String) jsonMap.get("username"));
						if (jsonMap.containsKey("role")) 
							uAccount.setRole(uServ.getRoleByName((String) jsonMap.get("role")));
					}
					
					uServ.updateUser(uAccount);
					response.getWriter().write(om.writeValueAsString(uAccount));
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					response.sendError(400,"Invalid credentials.");
				}
			} else if ("GET".equals(request.getMethod())) {
				if (isAdmin(uSession) || isEmployee(uSession))
					response.getWriter().write(om.writeValueAsString(uServ.getUsers()));
				else
					response.getWriter().write(om.writeValueAsString(uSession));
			} else {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} else {
			if ("GET".equals(request.getMethod())) {
				
				String info = "";
				int num = getDigitsFromString(path);
				if (path.indexOf("/") != -1)
					info = path.substring(path.indexOf("/")+1);
				if (path.contains("roles") && "".equals(info))
					response.getWriter().write(om.writeValueAsString(uServ.getRoles()));
				else if (info.length() > 0)
					response.getWriter().write(om.writeValueAsString(uServ.getUsersByRole(uServ.getRoleByName(info))));
				else {				
				User uAccount = uServ.getUserByID(num);
				if (uAccount != null && (isAdmin(uSession) || isEmployee(uSession) || isSelf(uAccount, uSession)))
					response.getWriter().write(om.writeValueAsString(uAccount));
				else
					response.sendError(400,"Invalid credentials.");
				}
			} else {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		}
	}

	
	private boolean isAdmin(User u) {
		if (u != null && u.getRole().getName().equals("admin"))
			return true;
		else
			return false;
	}

	private boolean isEmployee(User u) {
		if (u != null && u.getRole().getName().equals("employee"))
			return true;
		else
			return false;
	}
	
	private boolean isSelf(User uAccount, User uSession) {
		if (uAccount != null && uSession != null && uAccount.getUserId() == uSession.getUserId())
			return true;
		else
			return false;
	}
	
	private int getDigitsFromString(String str) {
		str = str.trim();
		String digits = "0"; 
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i)))
				digits = digits + str.charAt(i);
		}
		
		return Integer.valueOf(digits);
	}

}
