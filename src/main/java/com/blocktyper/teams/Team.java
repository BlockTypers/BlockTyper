package com.blocktyper.teams;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private String name;
	private String leaderName;
	private List<String> members;
	private List<TeamBase> bases;

	public Team(String name, String leaderName, TeamBase base) {
		super();
		this.name = name;
		this.leaderName = leaderName;
		this.members = new ArrayList<String>();
		this.members.add(leaderName);
		this.bases = new ArrayList<TeamBase>();
		this.bases.add(base);
	}
	
	

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	public List<TeamBase> getBases() {
		return bases;
	}

	public void setBases(List<TeamBase> bases) {
		this.bases = bases;
	}

}
