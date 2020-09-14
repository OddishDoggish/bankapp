package dev.comstock.beans;

public class TransactionType {

	private int id;
	private String name; // open, deposit, withdraw, transfer, closed, denied
	
	public TransactionType() {
		id = 0;
		name = "open";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String transType) {
		this.name = transType;
	}
	
	
}

/*
create table transaction_type (
id serial primary key,
name varchar(10) unique not null -- open, closed, deposit, transfer, withdraw, denied
);

insert into transaction_type values
(default, 'open'),
(default, 'closed'),
(default, 'deposit'),
(default, 'transfer'),
(default, 'withdraw'),
(default, 'denied');
*/