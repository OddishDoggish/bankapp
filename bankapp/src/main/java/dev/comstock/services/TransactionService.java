package dev.comstock.services;

import java.util.Set;

import dev.comstock.beans.Account;
import dev.comstock.beans.AccountStatus;
import dev.comstock.beans.Transaction;
import dev.comstock.beans.TransactionType;
import dev.comstock.data.AccountDAO;
import dev.comstock.data.TransactionDAO;

public class TransactionService {

	private AccountDAO aDAO;
	private TransactionDAO tDAO;
	
	public TransactionService(AccountDAO ad, TransactionDAO td) {
		aDAO = ad;
		tDAO = td;
	}
	
	public void openTrans(Account a, double d) {
		Transaction t = tDAO.openTransaction(a, d);
		if (t != null) {
			AccountStatus as = aDAO.findStatusByName("open");
			a.addTransaction(t);
			a.setStatus(as);
			a.setBalance(a.getBalance() + t.getChangeAmount());
			aDAO.updateAccount(a);
		}	
		
	}
	
	public Transaction withdrawTrans(Account a, double d) {
		Transaction t = tDAO.withdrawTransaction(a, d);
		if (t != null) {
			a.setBalance(a.getBalance() + t.getChangeAmount());
			a.addTransaction(t);
			aDAO.updateAccount(a);
		}
		return t;
	}
	
	public Transaction depositTrans(Account a, double d) {
		Transaction t = tDAO.depositTransaction(a, d);
		if (t != null) {
			a.setBalance(a.getBalance() + t.getChangeAmount());
			a.addTransaction(t);
			aDAO.updateAccount(a);
		}
		return t;
	}
	
	public Transaction[] transferTrans(Account from, Account to, double d) {
		Transaction[] t = tDAO.transferTransaction(from, to, d);
		if (t[0] != null && t[1] != null) {
			from.addTransaction(t[0]);
			from.setBalance(from.getBalance() + t[0].getChangeAmount());
			to.addTransaction(t[1]);
			to.setBalance(to.getBalance() + t[1].getChangeAmount());
			aDAO.updateAccount(from);
			aDAO.updateAccount(to);
		}
		return t;
	}
	
	public void closeTrans(Account a) {
		Transaction t = tDAO.closeTransaction(a);
		if (t != null) {
			AccountStatus as = aDAO.findStatusByName("closed");
			a.addTransaction(t);
			a.setStatus(as);
			aDAO.updateAccount(a);
		}
	}
	
	public void denyTrans(Account a) {
		Transaction t = tDAO.denyTransaction(a);
		if (t != null) {
			AccountStatus as = aDAO.findStatusByName("denied");
			a.setStatus(as);
			a.addTransaction(t);
			aDAO.updateAccount(a);
		}
	}

	public Set<Transaction> getTransByAccount(Account a) {
		return tDAO.findTransactionsByAccount(a);
	}
	
	public Set<Transaction> getTransByType(Account a, TransactionType tt) {
		return tDAO.findTransactionsByType(a, tt);
	}
}
