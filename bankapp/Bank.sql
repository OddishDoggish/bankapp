create table status_of_account (
	id serial primary key,
	name varchar(10) unique not null -- pending, open, closed, denied
);

insert into status_of_account values
	(default, 'pending'),
	(default, 'open'),
	(default, 'closed'),
	(default, 'denied');

create table type_of_account (
	id serial primary key,
	name varchar(10) unique not null -- checking, savings
);

insert into type_of_account values
	(default, 'checking'),
	(default, 'savings');

create table account (
	account_id serial primary key,
	account_type int references type_of_account,
	account_status int references status_of_account,
	balance real not null
);

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

create table bank_transaction (
	trans_id serial primary key,
	trans_type int references transaction_type,
	change_amount real not null,
	time_stamp timestamp,
	account_id int references account
);

create table user_role (
	id serial primary key,
	name varchar(10) unique not null -- admin, employee, premium, standard
);

insert into user_role values
	(default, 'admin'),
	(default, 'employee'),
	(default, 'premium'),
	(default, 'standard');

-- Account holders, Employees, and Administrators
create table bank_user (
	user_id serial primary key,
	username varchar(25) unique not null,
	passwd varchar(25) not null,
	first_name varchar(30) not null,
	last_name varchar(40) not null,
	email varchar(40) unique not null,
	role_type int references user_role
);

-- Person-Account Link Table:
create table user_account (
	account_owner integer references bank_user,
	bank_account integer references account
);

INSERT INTO bank_user VALUES
	(default, 'god', 'b4nk3r', 'Elaina', 'Comstock', 'ecomstock@thebank.com', 1);
/*
drop table user_role, bank_user, status_of_account, transaction_type,
	bank_transaction, account, type_of_account, user_account;
*/

