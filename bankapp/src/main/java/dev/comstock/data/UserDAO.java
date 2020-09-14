package dev.comstock.data;

import java.util.Set;

import dev.comstock.beans.*;

public interface UserDAO {
	
	public int createUser(User u);
	public Set<User> findUsers();
	public Set<User> findUsersByAccount(Account a);
	public Set<User> findUsersByRole(Role r);
	public User findUserByUsername(String username);
	public User findUserByEmail(String email);
	public User findUserByUserId(int userId);
	public void updateUser(User u);
	public void deleteUser(User u);

	public Set<Role> findRoles();
	public Role findRoleById(int id);
	public Role findRoleByName(String name);

}
