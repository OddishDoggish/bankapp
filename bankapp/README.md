# BankApp
This is my Revature 4-week Session Project.

It has the following functionality:

## /login
```
[POST] log in a user
```
## /logout
```
[POST] log out a user
```
## /register   
```
[POST] (admin only) creates a new user
```
## /users
```
[PUT] (admin or self) updates the following user fields:
{
    "userid" [identifies account if not self]
    "password"
    "firstName"
    "lastName"
    "email" [unique]
    "username" [unique, changeable by admin only]
    "role" [changeable by admin only, must be: "admin", "employee", "premium", or "standard"]
}

[GET] retrieves user information (self); retrieves user information for all users (admin or employee)
```
## /users/#    
```
[GET] retrieves user information for userid # (self, employee, admin)
```
## /accounts   
```
[GET] retrieves account information (self); retrieves account information for all users (employee, admin)

[POST] creates a new account (self); creates a new account and assigns an owner (employee, admin)
{
   "accountType" ["checking" or "savings"]
   "owner" [userid to link account by employee or admin]
}
```
### /accounts/# 
```
[GET] retrieves user information about account # (self, employee, admin)
[POST] adds a userid to account # (employee, admin)
{
    "owner" [userid to link account]
}
```
### /accounts/status
```
[GET] lists all pending accounts (employee or admin)
```
#### /accounts/status/pending    
```
[GET] lists pending accounts (employee or admin)
```
#### /accounts/status/open
```
[GET] lists open accounts (employee or admin)
[POST] opens a pending account (employee or admin)
{
    "accountId"
    "amount" [opening balance]
}
```
#### /accounts/status/denied
```
[GET] lists denied accounts (employee or admin)
[POST] denies a pending account (employee or admin)
{
    "accountId"
}
```
#### /accounts/status/closed     
```
[GET] lists closed accounts (employee or admin)
[POST] closes an open account (employee or admin)
```
### /accounts/owner     
```
[GET] lists all accounts for the current session (self)
```
### /accounts/owner/#   
```
[GET] lists all accounts for userid # (employee or admin)
[POST] adds an account to userid # (employee or admin)
{
    "accountId"
}
```
### /accounts/withdraw  
```
[POST] withdraws an amount from an account (self or admin)
{
    "accountId"
    "amount"
}
```
### /accounts/deposit   
```
[POST] deposits an amount to an account (self or admin)
{
    "accountId"
    "amount"
}
```
### /accounts/transfer
```
[POST] withdraws an amount from the source account to deposit in the target account (source/self or admin)
{
    "sourceAccountId"
    "targetAccountId"
    "amount"
}
```
