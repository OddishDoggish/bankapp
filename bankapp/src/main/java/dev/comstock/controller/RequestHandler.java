package dev.comstock.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.comstock.delegates.AccountDelegate;
import dev.comstock.delegates.FrontControllerDelegate;
import dev.comstock.delegates.LoginDelegate;
import dev.comstock.delegates.LogoutDelegate;
import dev.comstock.delegates.RegistrationDelegate;
import dev.comstock.delegates.UserDelegate;

public class RequestHandler {

private Map<String, FrontControllerDelegate> delegateMap;
	
	{
		delegateMap = new HashMap<String, FrontControllerDelegate>();
		
		delegateMap.put("login", new LoginDelegate());
		delegateMap.put("logout", new LogoutDelegate());
		delegateMap.put("register", new RegistrationDelegate());
		delegateMap.put("users", new UserDelegate());
		delegateMap.put("accounts", new AccountDelegate());
	}

	public FrontControllerDelegate handle(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		if ("OPTIONS".equals(request.getMethod()))
			return (r1, r2) -> {};
		
		StringBuilder uriString = new StringBuilder(request.getRequestURI());
		
		uriString.replace(0, request.getContextPath().length()+1, "");
		if (uriString.indexOf("/") != -1) {
			request.setAttribute("path", uriString.substring(uriString.indexOf("/")+1));
			uriString.replace(uriString.indexOf("/"), uriString.length(), "");
		}
		
		return delegateMap.get(uriString.toString());
	}
	
}
