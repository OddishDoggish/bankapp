package dev.comstock.beans;

public class Bean {
	private String name;
	private double weight;
	private int flavor;
	
	public Bean() {
		name = "";
		weight = 1.0;
		flavor = 10;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public int getFlavor() {
		return flavor;
	}
	public void setFlavor(int flavor) {
		this.flavor = flavor;
	}
	
	
}
