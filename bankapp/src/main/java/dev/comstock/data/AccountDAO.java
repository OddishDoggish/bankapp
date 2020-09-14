package dev.comstock.data;

import java.util.Set;

import dev.comstock.beans.*;

public interface AccountDAO {

	public int createAccount(String type);
	public void linkAccountToUser(User u, Account a);
	public void unlinkAccountFromUser(User u, Account a);
	public Set<Account> findAccounts();
	public Account findAccountById(int id);
	public Set<Account> findAccountsByUser(User u);
	public Set<Account> findAccountsByStatus(AccountStatus as);
	public Set<Account> findAccountsByType(AccountType at);
	public void updateAccount(Account a);
	public void closeAccount(Account a);
	
	public AccountStatus findStatusByName(String name);
	public AccountStatus findStatusById(int id);
	public AccountType findTypeByName(String name);
	public AccountType findTypeById(int id);

}
