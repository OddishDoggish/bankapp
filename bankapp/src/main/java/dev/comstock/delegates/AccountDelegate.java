package dev.comstock.delegates;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.comstock.beans.Account;
import dev.comstock.beans.AccountStatus;
import dev.comstock.beans.Transaction;
import dev.comstock.beans.User;
import dev.comstock.data.AccountPostgres;
import dev.comstock.data.TransactionPostgres;
import dev.comstock.data.UserPostgres;
import dev.comstock.services.AccountService;
import dev.comstock.services.TransactionService;
import dev.comstock.services.UserService;

public class AccountDelegate implements FrontControllerDelegate {

	private UserService uServ = new UserService(new UserPostgres());
	private AccountService aServ = new AccountService(new UserPostgres(), new AccountPostgres());
	private TransactionService tServ = new TransactionService(new AccountPostgres(), new TransactionPostgres());
	private ObjectMapper om = new ObjectMapper();
	
	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String path = (String) request.getAttribute("path");
		User uSession = (User) request.getSession().getAttribute("user");
		String info = "";
		int num = 0;

		if (path != null) {
			num = getDigitsFromString(path);
			if (path.indexOf("/") != -1)
				info =  path.substring(path.indexOf("/")+1);
		}
				
		if (path == null || path.equals("")) { 
			if ("GET".equals(request.getMethod())) {
				if (isAdmin(uSession) || isEmployee(uSession))
					response.getWriter().write(om.writeValueAsString(aServ.getAccounts()));
				else if (uSession != null) {
					response.getWriter().write(om.writeValueAsString(aServ.getAccountsByUser(uSession)));
				} else {
					response.sendError(400,"Invalid credentials.");
				}
			} else if ("POST".equals(request.getMethod())) {
		
				Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
				int userId;
				String accountType = (String) jsonMap.get("accountType");
				if (jsonMap.containsKey("owner"))
					userId= Integer.valueOf((Integer) jsonMap.get("owner"));
				else
					userId = uSession.getUserId();
	
				if (uSession != null) {
					int accountId = aServ.registerAccount(accountType);
					
					Account targetAccount = aServ.getAccountById(accountId);

					if (!(isAdmin(uSession) || isEmployee(uSession))) {
						aServ.addAccountToUser(uSession, targetAccount);
					} else {
						aServ.addAccountToUser(uServ.getUserByID(userId), targetAccount);
					}
					
					response.getWriter().write(om.writeValueAsString(targetAccount));
					response.setStatus(HttpServletResponse.SC_CREATED);
					
				} else {				
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				}
			} else {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} else {
					
			if (path.contains("status")) {
				doStatus(request, response, uSession, info, num);
			} else if (path.contains("owner")) {
				doOwner(request, response, uSession, info, num);
			} else if (num > 0 ) {

				Account a = aServ.getAccountById(num);
				
				if ("GET".equals(request.getMethod())) {
					if (isAdmin(uSession) || isEmployee(uSession) || isOwner(a, uSession)) {
						response.getWriter().write(om.writeValueAsString(a));
						response.setStatus(HttpServletResponse.SC_OK);
					} else {
						response.sendError(400,"Invalid credentials.");
					}
				} else if ("POST".equals(request.getMethod())) {
				
					if (isAdmin(uSession) || isEmployee(uSession)) {
						Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
						int userId = Integer.valueOf((Integer) jsonMap.get("owner"));
						aServ.addAccountToUser(uServ.getUserByID(userId), a);
						response.getWriter().write(om.writeValueAsString(uServ.getUserByID(userId)));
					} else {
						response.sendError(400,"Invalid credentials.");
					}
				} else {
					response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				}
			} else if ("POST".equals(request.getMethod())) {
				doTransaction(request, response, uSession, path);
			} else {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}	
		}
	}

	
	private void doStatus(HttpServletRequest request, HttpServletResponse response, User uSession, String info, int num) 
			throws IOException {
		if ("GET".equals(request.getMethod())) {
			
			AccountStatus as = null;
			if (num > 0) {
				 as = aServ.getStatusById(num);
			} else if (info.length() > 0 ){
				as = aServ.getStatusByName(info);
			} else {
				as = aServ.getStatusByName("pending");
			}
			
			
			if (isEmployee(uSession) || isAdmin(uSession)) {
				response.getWriter().write(om.writeValueAsString(aServ.getAccountsByStatus(as)));
			} else {
				response.sendError(400,"Invalid credentials.");
			}
			
		} else if ("POST".equals(request.getMethod())) {

			Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
			Account a = aServ.getAccountById(Integer.valueOf((Integer) jsonMap.get("accountId")));
			Double amount = 0.0;
			if (jsonMap.containsKey("amount"))
				amount = Double.valueOf((Double) jsonMap.get("amount"));
			if (amount < 0.0 )
				amount = 0.0;

			if (isEmployee(uSession) || (isAdmin(uSession))) {
				
				if (info.contains("open"))
					tServ.openTrans(a, amount);
				else if (info.contains("closed"))
					tServ.closeTrans(a);
				else if (info.contains("denied"))
					tServ.denyTrans(a);
				else
					response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
				
				response.getWriter().write(om.writeValueAsString(a));
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(400,"Invalid credentials.");
			}
		} else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
	}
	
	private void doOwner(HttpServletRequest request, HttpServletResponse response, User uSession, String info, int num)
			throws IOException {
		
		User owner = new User();
		if (num == 0)
			owner = uSession;
		else
			owner = uServ.getUserByID(num);
		
		if ("GET".equals(request.getMethod())) {
			
			if (isEmployee(uSession) || isAdmin(uSession) || isSelf(owner, uSession)) {
				response.getWriter().write(om.writeValueAsString(aServ.getAccountsByUser(owner)));
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(400,"Invalid credentials.");
			}
			
		} else if ("POST".equals(request.getMethod())) {
			
			if (isEmployee(uSession) || isAdmin(uSession)) {
				
				Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
				int accountNum = Integer.valueOf((Integer) jsonMap.get("accountId"));
				Account a = aServ.getAccountById(accountNum);
				
				aServ.addAccountToUser(owner, a);
				uServ.updateUser(owner);
				
				response.getWriter().write(om.writeValueAsString(owner));
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(400,"Invalid credentials.");
			}
		} else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
	}

	private void doTransaction(HttpServletRequest request, HttpServletResponse response, User uSession, String info) throws IOException {
			
		if (info.contains("withdraw")) {

			Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
			Account a = aServ.getAccountById(Integer.valueOf((Integer) jsonMap.get("accountId")));
			double amount = Double.valueOf((Double) jsonMap.get("amount"));
		
			if (isAdmin(uSession) || isOwner(a, uSession)) {
				Transaction t = tServ.withdrawTrans(a, amount);
				if (t == null)
					response.getWriter().write("Bad account transaction");
				else
					response.getWriter().write("$" + amount + " withdrawn from account #" + a.getId());
			} else {
				response.sendError(400,"Invalid credentials.");
			}
			
		} else if (info.contains("deposit")) {

			Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
			Account a = aServ.getAccountById(Integer.valueOf((Integer) jsonMap.get("accountId")));
			double amount = Double.valueOf((Double) jsonMap.get("amount"));
			if (isAdmin(uSession) || isOwner(a, uSession)) {
				Transaction t = tServ.depositTrans(a, amount);
				if (t == null)
					response.getWriter().write("Bad account transaction");
				else
					response.getWriter().write("$" + amount + " deposited to account #" + a.getId());
			} else {
				response.sendError(400,"Invalid credentials.");
			}
			
		} else if (info.contains("transfer")) {

			Map<String, Object> jsonMap = om.readValue(request.getInputStream(), Map.class);
			Account fromAccount = aServ.getAccountById(Integer.valueOf((Integer) jsonMap.get("sourceAccountId")));
			Account toAccount = aServ.getAccountById(Integer.valueOf((Integer) jsonMap.get("targetAccountId")));
			double amount = Double.valueOf((Double) jsonMap.get("amount"));
		
			if (isAdmin(uSession) || isOwner(fromAccount, uSession)) {
				Transaction[] tt = new Transaction[2];
				tt = tServ.transferTrans(fromAccount, toAccount, amount);
				if (tt[0] == null)
					response.getWriter().write("Bad account transaction");
				else
					response.getWriter().write("$" + amount + " transferred from account #" + fromAccount.getId() + " to account #" + toAccount.getId());
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(400,"Invalid credentials.");
			}
			
		} else {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
	}
	
	private boolean isOwner(Account a, User u) {
		if (u != null ) {
			for (Account acc : aServ.getAccountsByUser(u))
				if (acc.getId() == a.getId())
					return true;
		}
		return false;
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
