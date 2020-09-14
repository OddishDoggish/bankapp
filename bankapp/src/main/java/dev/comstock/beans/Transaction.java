package dev.comstock.beans;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction {
	
	private int id;
	private Double changeAmount;
	private Timestamp timeStamp;
	private TransactionType type; // deposit, withdrawal, transfer, open, close
	private int accountId;
	
	public Transaction() {
		id = 0;
		changeAmount = 0.0;
		timeStamp = Timestamp.valueOf(LocalDateTime.now());
		type = new TransactionType();
		accountId = 0;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int transactionId) {
		this.id = transactionId;
	}
	public Double getChangeAmount() {
		return changeAmount;
	}
	public void setChangeAmount(Double changeAmount) {
		this.changeAmount = changeAmount;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Timestamp ts) {
		this.timeStamp = ts;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}


}
