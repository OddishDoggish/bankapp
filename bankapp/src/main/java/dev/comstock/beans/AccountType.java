package dev.comstock.beans;

public class AccountType {
	// type possibilities: checking, savings
	
	private int id;
	private String name; // not null, unique
	
	public AccountType() {
		id = 0;
		name = "checking"; // or savings
	}
	
	public int getId() {
		return id;
	}
	public void setId(int typeID) {
		this.id = typeID;
	}
	public String getName() {
		return name;
	}
	public void setName(String type) {
		this.name = type;
	}
}

/*
create table type_of_account (
id serial primary key,
name varchar(10) unique not null -- checking, savings
);

insert into type_of_account values
(default, 'checking'),
(default, 'savings');
*/