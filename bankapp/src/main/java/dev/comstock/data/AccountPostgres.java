package dev.comstock.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import dev.comstock.beans.Account;
import dev.comstock.beans.AccountStatus;
import dev.comstock.beans.AccountType;
import dev.comstock.beans.Transaction;
import dev.comstock.beans.User;
import dev.comstock.utils.ConnectionUtil;

public class AccountPostgres implements AccountDAO {

	private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
	private TransactionDAO tDAO = new TransactionPostgres();
	
	@Override
	public AccountStatus findStatusByName(String name) {
		AccountStatus status = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from status_of_account where name = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				status = new AccountStatus();
				status.setId(rs.getInt("id"));
				status.setName(rs.getString("name"));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	@Override
	public AccountStatus findStatusById(int id) {
		AccountStatus status = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from status_of_account where id = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				status = new AccountStatus();
				status.setId(rs.getInt("id"));
				status.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}
	
	@Override
	public AccountType findTypeByName(String name) {
		AccountType t = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from type_of_account where name = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				t = new AccountType();
				t.setId(rs.getInt("id"));
				t.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}
	
	@Override
	public AccountType findTypeById(int id) {
		AccountType t = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from type_of_account where id = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				t = new AccountType();
				t.setId(rs.getInt("id"));
				t.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}
	
	@Override
	public int createAccount(String type) {
		Integer id = 0;
		AccountType at = findTypeByName(type);
		AccountStatus as = findStatusByName("pending");
		Account a = new Account();
		a.setStatus(as);
		a.setType(at);
		a.setBalance(0.0);
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO account VALUES "
					+ "(default, ?, ?, ?)";
			String[] keys = {"account_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setInt(1, a.getType().getId());
			pstmt.setInt(2, a.getStatus().getId());
			pstmt.setDouble(3, a.getBalance());
			
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				id = rs.getInt(1);
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}

	@Override
	public Set<Account> findAccounts() {
		HashSet<Account> accounts = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM account";
			
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			
			accounts = new HashSet<>();
			
			while(rs.next()) {
				AccountStatus as = this.findStatusById(rs.getInt("account_status"));
				AccountType at = this.findTypeById(rs.getInt("account_type"));
				
				Account a = new Account();
				a.setId(rs.getInt("account_id"));
				a.setBalance(rs.getDouble("balance"));
				a.setStatus(as);
				a.setType(at);
				a.setTransactions(tDAO.findTransactionsByAccount(a));
				
				accounts.add(a);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return accounts;
	}

	@Override
	public Account findAccountById(int id) {

		Account a = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM account WHERE " +
					"account_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				AccountStatus as = this.findStatusById(rs.getInt("account_status"));
				AccountType at = this.findTypeById(rs.getInt("account_type"));
				
				a = new Account();
				a.setId(id);
				a.setBalance(rs.getDouble("balance"));
				a.setStatus(as);
				a.setType(at);
				a.setTransactions(tDAO.findTransactionsByAccount(a));
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return a;
	}

	@Override
	public Set<Account> findAccountsByUser(User u) {
		HashSet<Account> accounts = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM account AS acc JOIN user_account " +
					  "AS ua ON acc.account_id = ua.bank_account " +
					  "WHERE ua.account_owner = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,u.getUserId());
			
			ResultSet rs = pstmt.executeQuery();
			
			accounts = new HashSet<>();
			
			while(rs.next()) {
				AccountStatus as = this.findStatusById(rs.getInt("account_status"));
				AccountType at = this.findTypeById(rs.getInt("account_type"));
				
				Account a = new Account();
				a.setId(rs.getInt("account_id"));
				a.setBalance(rs.getDouble("balance"));
				a.setStatus(as);
				a.setType(at);
				
				a.setTransactions(tDAO.findTransactionsByAccount(a));
								
				accounts.add(a);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return accounts;
	}
	@Override
	public void linkAccountToUser(User u, Account a) {
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			
			String sql = "INSERT INTO user_account VALUES "
					+ "(?, ?)";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, u.getUserId());
			pstmt.setInt(2, a.getId());
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void unlinkAccountFromUser(User u, Account a) {
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "DELETE FROM user_account WHERE "
					+ "account_owner = ? AND bank_account = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, u.getUserId());
			pstmt.setInt(2, a.getId());
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<Account> findAccountsByStatus(AccountStatus as) {
		HashSet<Account> accounts = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM account " +
					  "WHERE account_status = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,as.getId());
			
			ResultSet rs = pstmt.executeQuery();
			
			accounts = new HashSet<>();
			
			while(rs.next()) {
				AccountType at = this.findTypeById(rs.getInt("account_type"));
				
				Account a = new Account();
				a.setId(rs.getInt("account_id"));
				a.setBalance(rs.getDouble("balance"));
				a.setStatus(as);
				a.setType(at);
				
				a.setTransactions(tDAO.findTransactionsByAccount(a));
								
				accounts.add(a);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return accounts;
	}

	@Override
	public Set<Account> findAccountsByType(AccountType at) {
		HashSet<Account> accounts = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM account " +
					  "WHERE account_type = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,at.getId());
			
			ResultSet rs = pstmt.executeQuery();
			
			accounts = new HashSet<>();
			
			while(rs.next()) {
				AccountStatus as = this.findStatusById(rs.getInt("account_status"));
				
				Account a = new Account();
				a.setId(rs.getInt("account_id"));
				a.setBalance(rs.getDouble("balance"));
				a.setStatus(as);
				a.setType(at);
				
				a.setTransactions(tDAO.findTransactionsByAccount(a));
								
				accounts.add(a);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return accounts;
	}

	@Override
	public void updateAccount(Account a) {
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "UPDATE account SET account_type = ?, account_status = ?, " +
					"balance = ? " +
					"WHERE account_id = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, a.getType().getId());
			pstmt.setInt(2, a.getStatus().getId());
			pstmt.setDouble(3, a.getBalance());
			pstmt.setInt(4, a.getId());
			
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void closeAccount(Account a) {
		Transaction t = tDAO.closeTransaction(a);
		
		AccountStatus as = new AccountStatus();
		as = findStatusByName("close");
		
		a.setStatus(as);
		a.setBalance(0);
		a.addTransaction(t);
		updateAccount(a);
		
	}

}
