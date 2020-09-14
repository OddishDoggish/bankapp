package dev.comstock.beans;

public class AccountStatus {

	// Account status: pending, open, closed, denied
	
	private int id;
	private String name; // not null, unique

	public AccountStatus() {
		id = 0;
		name = "pending";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int statusId) {
		this.id = statusId;
	}
	public String getName() {
		return name;
	}
	public void setName(String status) {
		this.name = status;
	}
	
}

/*
create table status_of_account (
id serial primary key,
name varchar(10) unique not null -- pending, open, closed, denied
);

insert into status_of_account values
(default, 'pending'),
(default, 'open'),
(default, 'closed'),
(default, 'denied');
*/