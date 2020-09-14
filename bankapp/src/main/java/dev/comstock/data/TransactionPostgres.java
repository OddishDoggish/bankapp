package dev.comstock.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import dev.comstock.beans.Account;
import dev.comstock.beans.Transaction;
import dev.comstock.beans.TransactionType;
import dev.comstock.utils.ConnectionUtil;

public class TransactionPostgres implements TransactionDAO {
	
	private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();

	@Override
	public Set<Transaction> findTransactionsByAccount(Account a) {
		HashSet<Transaction> transactions = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_transaction " +
					  "WHERE account_id = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,a.getId());
			
			ResultSet rs = pstmt.executeQuery();
			
			transactions = new HashSet<>();
			
			while(rs.next()) {
				TransactionType tt = this.findTTypeById(rs.getInt("trans_type"));
				
				Transaction t = new Transaction();
				t.setId(rs.getInt("trans_id"));
				t.setChangeAmount(rs.getDouble("change_amount"));
				t.setTimeStamp(rs.getTimestamp("time_stamp"));
				t.setType(tt);
				t.setAccountId(rs.getInt("account_id"));
								
				transactions.add(t);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return transactions;
	}
	
	
	@Override
	public Transaction openTransaction(Account a, double d) {
		// Note that d is the starting amount to open the account with.
		int id = 0;
		Transaction t = null;
		
		// Only pending accounts should be opened.
		if (!("pending".equals(a.getStatus().getName())))
			return null;

		// Negative amounts are not allowed.
		if (d < 0.0)
			return null;
		
		TransactionType tt = findTTypeByName("open");
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_transaction VALUES "
					+ "(default, ?, ?, ?, ?)";
			String[] keys = {"trans_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setInt(1, tt.getId());
			pstmt.setDouble(2, d);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(4, a.getId());
			
			
			t = new Transaction();
			
			t.setType(tt);
			t.setChangeAmount(d);
			t.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
			t.setAccountId(a.getId());
			
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				id = rs.getInt(1);
				t.setId(id);
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return t;

	}

	@Override
	public Transaction withdrawTransaction(Account a, double d) {
		int id = 0;
		Transaction t = null;
		
		d = Math.abs(d)*(-1.0); // This is a withdrawal so d is negative.
		
		// Only open accounts should allow withdrawal.
		if (!("open".equals(a.getStatus().getName())))
			return null;
		
		// Do not allow balances to go negative.
		// This bank is kind enough not to let accounts get overdrawn.
		// Should this change in the future, here's where to stick the fees!
		if (a.getBalance() + d < 0)
			return null;
		
		TransactionType tt = this.findTTypeByName("withdraw");
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_transaction VALUES "
					+ "(default, ?, ?, ?, ?)";
			String[] keys = {"trans_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setInt(1, tt.getId());
			pstmt.setDouble(2, d);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(4, a.getId());
			
			t = new Transaction();
			
			t.setType(tt);
			t.setChangeAmount(d);
			t.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
			t.setAccountId(a.getId());

			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				id = rs.getInt(1);
				t.setId(id);
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return t;
	}

	@Override
	public Transaction depositTransaction(Account a, double d) {
		int id = 0;
		Transaction t = null;
		
		d = Math.abs(d); // This is a deposit so d is positive.
		
		// Only open accounts should allow withdrawal.
		if (!("open".equals(a.getStatus().getName())))
			return null;
		
		TransactionType tt = this.findTTypeByName("deposit");
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_transaction VALUES "
					+ "(default, ?, ?, ?, ?)";
			String[] keys = {"trans_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setInt(1, tt.getId());
			pstmt.setDouble(2, d);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(4, a.getId());
			
			t = new Transaction();
			
			t.setType(tt);
			t.setChangeAmount(d);
			t.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
			t.setAccountId(a.getId());

			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				id = rs.getInt(1);
				t.setId(id);
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public Transaction[] transferTransaction(Account fromAccount, Account toAccount, double d) {
		// Note that this is actually two transactions, one to an account and the other from.		
		Transaction tFrom = new Transaction();
		Transaction tTo = new Transaction();
		Transaction[] transactions = {null, null};
		
		d = Math.abs(d); // This is a transfer so set d positive and negate it for the other account.
	
		// Only open accounts should allow transfers.
		if (!("open".equals(fromAccount.getStatus().getName()) && "open".equals(toAccount.getStatus().getName())))
			return transactions;
	
		// Is there enough money in the starting account?
		if (fromAccount.getBalance() - d < 0)
			return transactions;
		
		TransactionType tt = this.findTTypeByName("transfer");
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_transaction VALUES "
					+ "(default, ?, ?, ?, ?)," // From account transaction
					+ "(default, ?, ?, ?, ?)";  // To account transaction
			String[] keys = {"trans_id", "trans_id"};
			
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
						
			pstmt.setInt(1, tt.getId());
			pstmt.setDouble(2, d*(-1.0));
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(4, fromAccount.getId());
			
			pstmt.setInt(5, tt.getId());
			pstmt.setDouble(6, d);
			pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(8, toAccount.getId());
			
			tFrom.setType(tt);
			tTo.setType(tt);
			tFrom.setChangeAmount(d*(-1.0));
			tTo.setChangeAmount(d);
			tFrom.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));;
			tTo.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
			tFrom.setAccountId(fromAccount.getId());
			tTo.setAccountId(toAccount.getId());

			pstmt.executeUpdate();

			ResultSet rs = pstmt.getGeneratedKeys();
			
			transactions[0] = tFrom;
			transactions[1] = tTo;
			
			int index = 0;
			while (rs.next()) {
				transactions[index].setId(rs.getInt(1 + index));
				index++;
			}
			if (transactions[0] != null && transactions[1] != null) {
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return transactions;
	}

	@Override
	public Transaction closeTransaction(Account a) {
		int id = 0;
		Transaction t = null;
		
		// Only open accounts should be closed.
		if (!("open".equals(a.getStatus().getName())))
			return null;
		
		TransactionType tt = this.findTTypeByName("closed");
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_transaction VALUES "
					+ "(default, ?, ?, ?, ?)";
			String[] keys = {"trans_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setInt(1, tt.getId());
			pstmt.setDouble(2, a.getBalance());
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(4, a.getId());
			
			t = new Transaction();
			
			t.setType(tt);
			t.setChangeAmount(a.getBalance());
			t.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
			t.setAccountId(a.getId());
			
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				id = rs.getInt(1);
				t.setId(id);
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public Transaction denyTransaction(Account a) {
		int id = 0;
		Transaction t = null;
		
		// Only pending accounts should be denied. Open accounts are closed.
		if (!("pending".equals(a.getStatus().getName())))
			return null;
		
		TransactionType tt = this.findTTypeByName("denied");
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_transaction VALUES "
					+ "(default, ?, ?, ?, ?)";
			String[] keys = {"trans_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setInt(1, tt.getId());
			pstmt.setDouble(2, 0.0);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(4, a.getId());
			
			t = new Transaction();
			
			t.setType(tt);
			t.setChangeAmount(0.0);
			t.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
			t.setAccountId(a.getId());
			
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				id = rs.getInt(1);
				t.setId(id);
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return t;

	}
	
	@Override
	public Set<Transaction> findTransactionsByType(Account a, TransactionType tt) {
		HashSet<Transaction> transactions = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_transaction " +
						"WHERE account_id = ? AND trans_type = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, a.getId());
			pstmt.setInt(2, tt.getId());
			
			ResultSet rs = pstmt.executeQuery();
			
			transactions = new HashSet<>();
			
			while(rs.next()) {
				Transaction t = new Transaction();
				t.setId(rs.getInt("trans_id"));
				t.setType(tt);
				t.setChangeAmount(rs.getDouble("change_amount"));
				t.setTimeStamp(rs.getTimestamp("time_stamp"));
				t.setAccountId(a.getId());
								
				transactions.add(t);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		return transactions;
	}

	@Override
	public TransactionType findTTypeByName(String name) {
		TransactionType tt = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from transaction_type where name = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				tt = new TransactionType();
				tt.setId(rs.getInt("id"));
				tt.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tt;
	}
	
	@Override
	public TransactionType findTTypeById(int id) {
		TransactionType tt = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from transaction_type where id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				tt = new TransactionType();
				tt.setId(rs.getInt("id"));
				tt.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tt;
	}

}
