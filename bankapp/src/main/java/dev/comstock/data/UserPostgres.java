package dev.comstock.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import dev.comstock.beans.Account;
import dev.comstock.beans.Role;
import dev.comstock.beans.User;
import dev.comstock.utils.ConnectionUtil;

public class UserPostgres implements UserDAO {

	private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
	private AccountDAO accountDAO = new AccountPostgres();
	
	public int createUser(User u) {
		Integer id = 0;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "INSERT INTO bank_user VALUES "
					+ "(default, ?, ?, ?, ?, ?, ?)";
			String[] keys = {"user_id"};
			PreparedStatement pstmt = conn.prepareStatement(sql, keys);
			pstmt.setString(1, u.getUsername());
			pstmt.setString(2, u.getPassword());
			pstmt.setString(3, u.getFirstName());
			pstmt.setString(4, u.getLastName());
			pstmt.setString(5, u.getEmail());
			pstmt.setInt(6, u.getRole().getId());
			
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
		
		u.setUserId(id);
		return id;
	}

	public Set<User> findUsers() {
		
		HashSet<User> users = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_user";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			
			users = new HashSet<>();
			
			while(rs.next()) {
				Role r = this.findRoleById(rs.getInt("role_type"));
				
				User u = new User();
				u.setUserId(rs.getInt("user_id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("passwd"));
				u.setFirstName(rs.getString("first_name"));
				u.setLastName(rs.getString("last_name"));
				u.setEmail(rs.getString("email"));
				u.setRole(r);
				
				Set<Account> accounts = new HashSet<>();
				accounts = accountDAO.findAccountsByUser(u);
				u.setAccountList(accounts);
				
				users.add(u);
				
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return users;
	}
	
	@Override
	public Set<User> findUsersByAccount(Account a) {
		HashSet<User> users = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_user AS bu JOIN user_account " +
					  "AS ua ON bu.user_id = ua.account_owner " +
					  "WHERE ua.bank_account = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,a.getId());
			
			ResultSet rs = pstmt.executeQuery();
			
			users = new HashSet<>();
			
			while(rs.next()) {
				Role r = this.findRoleById(rs.getInt("role_type"));
				
				User u = new User();
				u.setUserId(rs.getInt("user_id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("passwd"));
				u.setFirstName(rs.getString("first_name"));
				u.setLastName(rs.getString("last_name"));
				u.setEmail(rs.getString("email"));
				u.setRole(r);
				
				Set<Account> accounts = new HashSet<>();
				accounts = accountDAO.findAccountsByUser(u);
				u.setAccountList(accounts);
				
				users.add(u);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return users;
	}
	
	public Set<User> findUsersByRole(Role r) {
		HashSet<User> users = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_user " +
					"WHERE role_type = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,r.getId());
			
			ResultSet rs = pstmt.executeQuery();
			
			users = new HashSet<>();
			
			while(rs.next()) {
				User u = new User();
				u.setUserId(rs.getInt("user_id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("passwd"));
				u.setFirstName(rs.getString("first_name"));
				u.setLastName(rs.getString("last_name"));
				u.setEmail(rs.getString("email"));
				
				u.setRole(r);
				
				Set<Account> accounts = new HashSet<>();
				accounts = accountDAO.findAccountsByUser(u);
				u.setAccountList(accounts);
				
				users.add(u);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return users;
	}

	public User findUserByUsername(String username) {
		User u = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_user " +
					"WHERE username = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,username);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				u = new User();
				Role r = this.findRoleById(rs.getInt("role_type"));

				u.setUserId(rs.getInt("user_id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("passwd"));
				u.setFirstName(rs.getString("first_name"));
				u.setLastName(rs.getString("last_name"));
				u.setEmail(rs.getString("email"));
				u.setRole(r);
				
				Set<Account> accounts = new HashSet<>();
				accounts = accountDAO.findAccountsByUser(u);
				u.setAccountList(accounts);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return u;
	}
	
	@Override
	public User findUserByEmail(String email) {
		User u = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_user " +
					"WHERE email = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				u = new User();
				Role r = this.findRoleById(rs.getInt("role_type"));

				u.setUserId(rs.getInt("user_id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("passwd"));
				u.setFirstName(rs.getString("first_name"));
				u.setLastName(rs.getString("last_name"));
				u.setEmail(rs.getString("email"));
				u.setRole(r);
				
				Set<Account> accounts = new HashSet<>();
				accounts = accountDAO.findAccountsByUser(u);
				u.setAccountList(accounts);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return u;
	}

	public User findUserByUserId(int userId) {
		User u = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM bank_user " +
					"WHERE user_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,userId);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				u = new User();
				Role r = this.findRoleById(rs.getInt("role_type"));

				u.setUserId(rs.getInt("user_id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("passwd"));
				u.setFirstName(rs.getString("first_name"));
				u.setLastName(rs.getString("last_name"));
				u.setEmail(rs.getString("email"));
				u.setRole(r);
				
				Set<Account> accounts = new HashSet<>();
				accounts = accountDAO.findAccountsByUser(u);
				u.setAccountList(accounts);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return u;
	}

	public void updateUser(User u) {
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "UPDATE bank_user SET username = ?, passwd = ?, " +
					"first_name = ?, last_name = ?, email = ?, role_type = ? " +
					"WHERE user_id = ?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, u.getUsername());
			pstmt.setString(2, u.getPassword());
			pstmt.setString(3, u.getFirstName());
			pstmt.setString(4, u.getLastName());
			pstmt.setString(5, u.getEmail());
			pstmt.setInt(6, u.getRole().getId());
			pstmt.setInt(7, u.getUserId());
			
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

	public void deleteUser(User u) {
		if (u.getAccountList().isEmpty()) {
			try (Connection conn = cu.getConnection()) {
				conn.setAutoCommit(false);
				String sql = "DELETE FROM bank_user " +
						"WHERE user_id = ?";
				
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, u.getUserId());
				
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
		
	}

	@Override
	public Set<Role> findRoles() {
		
		HashSet<Role> roles = null;
		
		try (Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "SELECT * FROM user_role";
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			
			roles = new HashSet<>();
			
			while(rs.next()) {
				Role r = new Role();
				
				r.setId(rs.getInt("id"));
				r.setName(rs.getString("name"));
				
				roles.add(r);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
				}
		
		return roles;
	}
	
	
	@Override
	public Role findRoleByName(String name) {
		Role role = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from user_role where name = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				role = new Role();
				role.setId(rs.getInt("id"));
				role.setName(rs.getString("name"));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return role;
	}
	
	@Override
	public Role findRoleById(int id) {
		Role role = null;
		
		try(Connection conn = cu.getConnection()) {
			String sql = "SELECT * from user_role where id = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				role = new Role();
				role.setId(rs.getInt("id"));
				role.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return role;
	}


}
