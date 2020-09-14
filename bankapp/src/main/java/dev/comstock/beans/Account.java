package dev.comstock.beans;

import java.util.HashSet;
import java.util.Set;

public class Account {

	private int accountId; // account number -- primary key
	private AccountType type; // checking or savings
	private AccountStatus status; // pending, open, closed, denied
	private double balance; // current amount -- not null
	private Set<Transaction> transactions; // account transactions
	
	public Account() {
		accountId = 0;
		type = new AccountType();
		status = new AccountStatus();
		balance = 0;
		transactions = new HashSet<Transaction>();
	}
	
	public int getId() {
		return accountId;
	}
	public void setId(int accountId) {
		this.accountId = accountId;
	}
	public AccountType getType() {
		return type;
	}
	public void setType(AccountType type) {
		this.type = type;
	}
	public AccountStatus getStatus() {
		return status;
	}
	public void setStatus(AccountStatus status) {
		this.status = status;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public Set<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}
	public void addTransaction(Transaction t) {
		this.transactions.add(t);
	}
	public void removeTransaction(Transaction t) {
		this.transactions.remove(t);
	}
	
}
