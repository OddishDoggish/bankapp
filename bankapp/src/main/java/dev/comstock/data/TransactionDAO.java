package dev.comstock.data;

import java.util.Set;

import dev.comstock.beans.*;

public interface TransactionDAO {

	public Set<Transaction> findTransactionsByAccount(Account a);
	public Transaction openTransaction(Account a, double d);
	public Transaction withdrawTransaction(Account a, double d);
	public Transaction depositTransaction(Account a, double d);
	public Transaction[] transferTransaction(Account fromAccount, Account toAccount, double d);
	public Transaction closeTransaction(Account a);
	public Transaction denyTransaction(Account a);
	public Set<Transaction> findTransactionsByType(Account a, TransactionType tt);

	public TransactionType findTTypeByName(String name);
	public TransactionType findTTypeById(int id);
}
