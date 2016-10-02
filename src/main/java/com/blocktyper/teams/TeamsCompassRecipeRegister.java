package com.blocktyper.teams;

import java.io.File;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamsCompassRecipeRegister {
	private JavaPlugin plugin;
	TeamsHelper teamsHelper;

	public TeamsCompassRecipeRegister(JavaPlugin plugin) {
		super();
		this.plugin = plugin;
		this.teamsHelper = new TeamsHelper(plugin);

	}

	public void registerCompass(Material teamMaterial, Material baseMaterial) {

		if (teamMaterial == null || baseMaterial == null) {
			return;
		}

		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ShapedRecipe shapedRecipe = new ShapedRecipe(compass);

		shapedRecipe.shape("TB ", "C  ", "   ");
		shapedRecipe.setIngredient('T', teamMaterial);
		shapedRecipe.setIngredient('B', baseMaterial);
		shapedRecipe.setIngredient('C', Material.COMPASS);
		
		plugin.getServer().addRecipe(shapedRecipe);
		plugin.getLogger().warning("compass registered:");
		plugin.getLogger().warning(teamsHelper.getCompassName(teamMaterial, baseMaterial));
	}

	public void registerAllTeamCompasses() {
		plugin.getLogger().info("registering all compasses");
		if (plugin.getServer().getWorlds() == null || plugin.getServer().getWorlds().isEmpty()) {
			plugin.getLogger().warning("no worlds detected");
		}

		for (World world : plugin.getServer().getWorlds()) {
			registerTeamCompasses(world);
		}
	}

	public void registerTeamCompasses(World world) {
		if (world == null) {
			plugin.getLogger().warning("null world!");
			return;
		}
		plugin.getLogger().info("registering compasses for world: " + world.getName());

		File blockTyperTeamsFolder = teamsHelper.getBlockTyperTeamsFolder(world);

		if (blockTyperTeamsFolder == null) {
			plugin.getLogger().info("no teams exist in this world yet.");
			return;
		}

		Map<TeamBase, Team> teamBaseMap = teamsHelper.getTeamBaseMap(world);

		if (teamBaseMap == null || teamBaseMap.isEmpty()) {
			plugin.getLogger().info("no teams exist in this world.");
			return;
		}

		for (TeamBase base : teamBaseMap.keySet()) {
			String baseMaterialName = teamsHelper.getMaterialNameFromBase(base);
			if (baseMaterialName == null) {
				plugin.getLogger().info("material name not recognized for base: " + base.getName());
			}
			Material baseMaterial = Material.valueOf(baseMaterialName);
			if (baseMaterialName == null) {
				plugin.getLogger().info("material recognized for base: " + base.getName());
			}

			String teamMaterialName = teamsHelper.getMaterialNameFromTeam(teamBaseMap.get(base));
			if (teamMaterialName == null) {
				plugin.getLogger().info("material not recognized for team: " + teamBaseMap.get(base).getName());
			}
			Material teamMaterial = Material.valueOf(teamMaterialName);
			if (teamMaterial == null) {
				plugin.getLogger().info("material recognized for team: " + teamBaseMap.get(base).getName());
			}

			registerCompass(teamMaterial, baseMaterial);
		}

	}
}
