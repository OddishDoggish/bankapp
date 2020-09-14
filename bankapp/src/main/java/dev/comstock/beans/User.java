package dev.comstock.beans;

import java.util.HashSet;
import java.util.Set;

public class User {

	private int userId; // primary key
	private String username; // not null, unique
	private String password; // not null
	private String firstName; // not null
	private String lastName; // not null
	private String email; // not null
	private Role role;
	private Set<Account> accountList;
	
	public User() {
		userId = 0;
		username = "";
		password = "";
		firstName = "";
		lastName = "";
		email = "";
		role = new Role();
		accountList = new HashSet<Account>();
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<Account> getAccountList() {
		return accountList;
	}
	public void setAccountList(Set<Account> accountList) {
		this.accountList = accountList;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public void addAccount(Account a) {
		this.accountList.add(a);
	}
	public void removeAccount(Account a) {
		this.accountList.remove(a);
	}
	
}
