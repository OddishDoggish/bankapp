package dev.comstock.beans;

public class Role {
	
	private int id;
	private String name; // Admin, Employee, Premium, or Standard
	
	public Role() {
		id = 1;
		name = "Standard";
	}

	public int getId() {
		return id;
	}

	public void setId(int roleId) {
		this.id = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
