package com.blocktyper.teams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class TeamsHelper {

	public static String TEAM_SUFFIX = "-team";
	public static String BASE_SUFFIX = "-base";
	public static String COMPASS_NAME_DELIMITER = " | ";

	JavaPlugin plugin;

	public TeamsHelper(JavaPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	public File getBlockTyperTeamsFolder(World world) {
		File blockTyperTeamsFolder = null;
		if (world.getWorldFolder().listFiles() != null) {
			for (File file : world.getWorldFolder().listFiles()) {
				blockTyperTeamsFolder = file.isDirectory() && file.getName().equals("blockTyperTeams") ? file : null;
			}
		}

		if (blockTyperTeamsFolder == null) {
			blockTyperTeamsFolder = new File(world.getWorldFolder(), "blockTyperTeams");
			blockTyperTeamsFolder.mkdir();
		}

		return blockTyperTeamsFolder;
	}

	public File getBlockTyperTeamsFolder(Block block) {
		return getBlockTyperTeamsFolder(block.getWorld());
	}

	public File getTeamFile(File blockTyperTeamsFolder, String teamName) {
		File teamFile = null;
		if (blockTyperTeamsFolder.listFiles() != null) {
			for (File file : blockTyperTeamsFolder.listFiles()) {
				teamFile = !file.isDirectory() && file.getName().endsWith(teamName + ".json") ? file : null;
				if (teamFile != null) {
					break;
				}
			}
		}
		return teamFile;
	}

	public List<File> getTeamFiles(File blockTyperTeamsFolder) {
		List<File> teamFiles = null;
		if (blockTyperTeamsFolder.listFiles() != null) {
			teamFiles = new ArrayList<File>();
			for (File file : blockTyperTeamsFolder.listFiles()) {
				if (!file.isDirectory() && file.getName().endsWith(".json")) {
					teamFiles.add(file);
				}
			}
		}
		return teamFiles;
	}

	public List<Team> getTeams(World world) {
		List<Team> teams = null;
		List<File> teamFiles = getTeamFiles(getBlockTyperTeamsFolder(world));

		if (teamFiles != null && !teamFiles.isEmpty()) {
			for (File teamFile : teamFiles) {
				if (teamFile == null) {
					continue;
				}
				Team team = getTeam(world, teamFile.getName().substring(0, teamFile.getName().indexOf(".json")));
				if (team == null) {
					continue;
				}
				if (teams == null) {
					teams = new ArrayList<Team>();
				}
				;
				teams.add(team);
			}
		}

		return teams;
	}

	public Team getTeam(World world, String teamName) {

		File teamFile = getTeamFile(getBlockTyperTeamsFolder(world), teamName);
		if (teamFile == null) {
			return null;
		}

		try {
			for (String line : Files.readAllLines(Paths.get(teamFile.getAbsolutePath()))) {
				if (line != null && !line.isEmpty()) {
					Team team = new Gson().fromJson(line, Team.class);
					return team;
				}
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Map<TeamBase, Team> getTeamBaseMap(World world) {
		return getTeamBaseMap(world, null, null, null);
	}

	public Map<TeamBase, Team> getTeamBaseMap(World world, Integer x, Integer y, Integer z) {
		return getTeamBaseMap(getBlockTyperTeamsFolder(world), x, y, z);
	}

	public Map<TeamBase, Team> getTeamBaseMap(File blockTyperTeamFolder, Integer x, Integer y, Integer z) {
		Map<TeamBase, Team> teamBaseMap = new HashMap<TeamBase, Team>();
		if (blockTyperTeamFolder == null || blockTyperTeamFolder.listFiles() == null
				|| blockTyperTeamFolder.listFiles().length < 1) {
			return null;
		}

		for (File teamFile : blockTyperTeamFolder.listFiles()) {
			try {
				for (String line : Files.readAllLines(Paths.get(teamFile.getAbsolutePath()))) {
					if (line != null && !line.isEmpty()) {
						Team team = new Gson().fromJson(line, Team.class);
						if (team == null) {
							continue;
						}

						if (team.getBases() == null || team.getBases().isEmpty()) {
							continue;
						}

						for (TeamBase base : team.getBases()) {
							if (x == null || y == null || z == null) {
								teamBaseMap.put(base, team);
							} else {
								if (x == base.getX() && y == base.getY() && z == base.getZ()) {
									teamBaseMap.put(base, team);
								}
							}
						}
					}
				}
			} catch (JsonSyntaxException e) {
				plugin.getLogger().warning("JsonSyntaxException: " + e.getMessage());
			} catch (IOException e) {
				plugin.getLogger().warning("IOException: " + e.getMessage());
			}
		}
		return teamBaseMap;
	}

	public String getMaterialNameFromBase(TeamBase base) {
		return removeSuffix(base != null ? base.getName() : null, TeamsHelper.BASE_SUFFIX);
	}

	public String getBaseNameFromBlock(Block block) {
		return getBaseNameFromMaterial(block != null ? block.getType() : null);
	}

	public String getBaseNameFromMaterial(Material material) {
		return material != null && material.name() != null ? material.name() + BASE_SUFFIX : null;
	}

	public String getMaterialNameFromTeam(Team team) {
		return removeSuffix(team != null ? team.getName() : null, TeamsHelper.TEAM_SUFFIX);
	}

	public String getTeamNameFromBlock(Block block) {
		return getTeamNameFromMaterial(block != null ? block.getType() : null);
	}

	public String getTeamNameFromMaterial(Material material) {
		return material != null && material.name() != null ? material.name() + TEAM_SUFFIX : null;
	}

	public String removeSuffix(String input, String suffix) {
		String baseMaterialName = input != null ? (suffix != null ? input.substring(0, input.indexOf(suffix)) : input)
				: null;
		return baseMaterialName;
	}

	public String getReasonMaterialCannotBeUsed(Material material) {
		if (material == null) {
			return "was null";
		}
		if (material.hasGravity()) {
			return "is affected by gravity";
		}
		if (!material.isSolid()) {
			return "is not solid";
		}
		if (!material.isBlock()) {
			return "is not considered a block";
		}
		if (material.isEdible()) {
			return "is editable";
		}
		return null;
	}

	public String getCompassName(Material teamMaterial, Material baseMaterial) {
		String compassName = teamMaterial == null ? null
				: (baseMaterial == null ? getTeamNameFromMaterial(teamMaterial)
						: getTeamNameFromMaterial(teamMaterial) + COMPASS_NAME_DELIMITER
								+ getBaseNameFromMaterial(baseMaterial));
		return compassName;
	}

	public String getTeamNameFromCompassName(String compassName) {

		if (compassName == null || !compassName.contains(TEAM_SUFFIX)) {
			return null;
		}

		String teamName = compassName.substring(0, compassName.indexOf(TEAM_SUFFIX));
		return teamName + TEAM_SUFFIX;
	}

	public String getBaseNameFromCompassName(String compassName) {

		String teamName = getTeamNameFromCompassName(compassName);
		if (teamName == null || !compassName.contains(COMPASS_NAME_DELIMITER) || !compassName.contains(BASE_SUFFIX)) {
			return null;
		}

		int startIndex = teamName.length() + COMPASS_NAME_DELIMITER.length();
		String baseName = compassName.substring(startIndex);
		return baseName;
	}

	public double getDistance(Location location1, Location location2) {
		Double distance = Math.sqrt(
				Math.pow((location1.getX() - location2.getX()), 2) + Math.pow((location1.getY() - location2.getY()), 2)
						+ Math.pow((location1.getZ() - location2.getZ()), 2));
		return distance;
	}
	
	public boolean playerIsTeamMember(Team team, String playerName) {
		return team != null && team.getMembers() != null && team.getMembers().contains(playerName);
	}

	public boolean addTeamMember(World world, String teamName, String playerName) {
		Team team = getTeam(world, teamName);
		if (team == null || playerIsTeamMember(team, playerName)) {
			return false;
		}

		File teamFile = getTeamFile(getBlockTyperTeamsFolder(world), teamName);
		
		if(teamFile == null){
			return false;
		}

		team.getMembers().add(playerName);
		try {
			updateTeam(team, teamFile);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean removeTeamMember(World world, String teamName, String playerName) {
		Team team = getTeam(world, teamName);
		if (team == null || !playerIsTeamMember(team, playerName)) {
			return false;
		}

		File teamFile = getTeamFile(getBlockTyperTeamsFolder(world), teamName);
		if(teamFile == null){
			return false;
		}

		team.getMembers().remove(playerName);
		try {
			updateTeam(team, teamFile);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateTeamLeader(World world, String teamName, String leaderName){
		Team team = getTeam(world, teamName);
		if (team == null || !playerIsTeamMember(team, leaderName)) {
			return false;
		}

		File teamFile = getTeamFile(getBlockTyperTeamsFolder(world), teamName);
		if(teamFile == null){
			return false;
		}
		
		team.setLeaderName(leaderName);
		try {
			updateTeam(team, teamFile);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteTeam(World world, String teamName){
		File teamFile = getTeamFile(getBlockTyperTeamsFolder(world), teamName);
		return teamFile.delete();
	}
	
	private void updateTeam(Team team, File teamFile) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(teamFile.getAbsolutePath(), "UTF-8");
		writer.println(new Gson().toJson(team));
		writer.close();
	}

}
