package com.blocktyper.teams;

public class TeamBase {
	private String name;

	private int x;
	private int y;
	private int z;

	public TeamBase(String name, int x, int y, int z) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

}
