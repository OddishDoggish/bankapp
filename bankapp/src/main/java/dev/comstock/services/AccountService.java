package dev.comstock.services;

import java.util.Set;

import dev.comstock.beans.Account;
import dev.comstock.beans.AccountStatus;
import dev.comstock.beans.AccountType;
import dev.comstock.beans.User;
import dev.comstock.data.AccountDAO;
import dev.comstock.data.UserDAO;

public class AccountService {

	private UserDAO uDAO;
	private AccountDAO aDAO;
	
	public AccountService(UserDAO ud, AccountDAO ad) {
		uDAO = ud;
		aDAO = ad;
	}
	
	// Create
	public int registerAccount(String type) {
		return aDAO.createAccount(type);
	}
	
	// Read
	
	public AccountStatus getStatusByName(String name) {
		return aDAO.findStatusByName(name);
	}
	
	public AccountStatus getStatusById(int id) {
		return aDAO.findStatusById(id);
	}
	
	public Set<Account> getAccounts() {
		return aDAO.findAccounts();
	}

	public Account getAccountById(int id) {
		return aDAO.findAccountById(id);
	}
	
	public Set<Account> getAccountsByStatus(AccountStatus as) {
		return aDAO.findAccountsByStatus(as);
	}
	
	public Set<Account> getAccountsByType(AccountType at) {
		return aDAO.findAccountsByType(at);
	}
	
	public Set<Account> getAccountsByUser(User u) {
		return aDAO.findAccountsByUser(u);
	}
	
	public Set<User> getUsersByAccount(Account a) {
		return uDAO.findUsersByAccount(a);
	}
	
	// Update
	public void changeAccountStatus(Account a, AccountStatus as) {
		a.setStatus(as);
		aDAO.updateAccount(a);
	}
	
	public void addAccountToUser(User u, Account a) {
		u.addAccount(a);
		aDAO.linkAccountToUser(u,a);
	}
	
	// Delete
	public void removeAccountFromUser(User u, Account a) {
		u.removeAccount(a);
		aDAO.unlinkAccountFromUser(u,a);
	}
	
	public void closeAccount(Account a) {
		aDAO.closeAccount(a);
	}
	
}

