package dev.comstock.services;

import java.util.Set;

import dev.comstock.beans.Role;
import dev.comstock.beans.User;
import dev.comstock.data.UserDAO;

public class UserService {

	private UserDAO userDAO;
	
	public UserService(UserDAO ud) {
		userDAO = ud; 
	}
	
	// Create
	public int registerUser(User u) {
		return userDAO.createUser(u);
	}
	
	// Read
	public User loginUser(String username, String password) {
		User u = userDAO.findUserByUsername(username);
		if (u != null && u.getPassword().equals(password))
			return u;
		else
			return null;
	}
	
	public User getUserByUsername(String username) {
		return userDAO.findUserByUsername(username);
		}
	
	public User getUserByEmail(String email) {
		return userDAO.findUserByEmail(email);
	}
	
	public User getUserByID(int id) {
		return userDAO.findUserByUserId(id);
	}
	
	public Set<User> getUsers() {
		return userDAO.findUsers();
	}
	
	public Set<User> getUsersByRole(Role r) {
		return userDAO.findUsersByRole(r);
	}
	
	public Set<Role> getRoles(){
		return userDAO.findRoles();
	}
	
	public Role getRoleByName(String r) {
		return userDAO.findRoleByName(r);
	}
	
	// Update
	public void updatePassword(User u, String password) {
		u.setPassword(password);
		userDAO.updateUser(u);
	}
	
	public void updateFirstName(User u, String firstName) {
		u.setFirstName(firstName);
		userDAO.updateUser(u);
	}
	
	public void updateLastName(User u, String lastName) {
		u.setLastName(lastName);
		userDAO.updateUser(u);
	}
	
	public void updateEmail(User u, String email) {
		u.setEmail(email);
		userDAO.updateUser(u);
	}
	
	public void updateUserRole(User u, Role r) {
		u.setRole(r);
		userDAO.updateUser(u);
	}
	
	public void updateUser(User u) {
		userDAO.updateUser(u);
	}
	
	
	// Delete
	public void removeUser(User u) {
		if (u.getAccountList().isEmpty())
			userDAO.deleteUser(u);
	}
}
