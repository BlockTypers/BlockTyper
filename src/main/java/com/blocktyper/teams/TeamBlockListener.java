package com.blocktyper.teams;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;
import com.blocktyper.ExperienceManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.md_5.bungee.api.ChatColor;


public class TeamBlockListener extends AbstractListener {

	protected TeamsHelper teamsHelper;

	public static int COLUMNS_PER_INVENTORY_ROW = 9;

	public static int TELEPORT_EXP_COST_BASE = 50;

	public TeamBlockListener(JavaPlugin plugin) {
		super(plugin);
		teamsHelper = new TeamsHelper(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void inventoryClick(InventoryClickEvent event) {
		try {
			if (event.getInventory().getTitle() == null || !event.getInventory().getTitle().endsWith("(Teleport)")) {
				return;
			}
			event.setCancelled(true);

			if (!(event.getWhoClicked() instanceof Player)) {
				return;
			}

			Player player = (Player) event.getWhoClicked();

			String teamName = event.getInventory().getTitle();
			teamName = teamName.substring(0, teamName.indexOf("("));

			Team team = getTeam(player.getWorld().getName(), teamName);

			if (team == null) {
				warning("Team object not loaded. " + teamName);
				return;
			}

			if (!teamsHelper.playerIsTeamMember(team, player.getName())) {
				player.sendMessage("You are not a member of " + teamName);
				return;
			}

			if (event.getCurrentItem().getItemMeta() == null
					|| event.getCurrentItem().getItemMeta().getDisplayName() == null) {
				return;
			}

			if (team.getBases() != null) {
				for (TeamBase base : team.getBases()) {
					if (event.getCurrentItem().getItemMeta().getDisplayName().equals(base.getName())) {
						Location location = new Location(player.getWorld(), base.getX(), base.getY() + 4, base.getZ());
						Double distance = teamsHelper.getDistance(location, player.getLocation());

						ExperienceManager experienceManager = new ExperienceManager(player);
						int totalExperience = experienceManager.getTotalExperience();
						Double xpCost = distance / TELEPORT_EXP_COST_BASE;
						xpCost = xpCost < 1 ? 1 : (xpCost.intValue() + 0.0);
						xpCost *= xpCost;
						if (xpCost > totalExperience) {
							player.sendMessage(
									"Not enough XP to travel that distance. Distance: " + distance + ". Try hiking it ("
											+ location.getX() + "," + location.getY() + "," + location.getZ() + ")");
							return;
						}

						totalExperience -= xpCost;
						experienceManager.setTotalExperience(totalExperience);

						player.teleport(location);
						player.closeInventory();
						player.sendMessage("distance travelled: " + distance.intValue());
						player.sendMessage("XP cost: " + xpCost);
						return;
					}
				}
			}

		} catch (Exception e) {
			((Player) event.getWhoClicked()).sendMessage("ERROR: " + e.getMessage());
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockExplode(EntityExplodeEvent event) {

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(event.getEntity().getName() + " exploded");
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockExplode(BlockExplodeEvent event) {

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage("Diamond block exploded");
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDestroy(BlockBreakEvent event) {
		if (!event.getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
			return;
		}

		File blockTyperTeamFolder = teamsHelper.getBlockTyperTeamsFolder(event.getBlock());

		Map<TeamBase, Team> teamBaseMap = teamsHelper.getTeamBaseMap(blockTyperTeamFolder,
				event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(),
				event.getBlock().getLocation().getBlockZ());

		if (teamBaseMap == null || teamBaseMap.isEmpty()) {
			return;
		}

		for (File teamFile : blockTyperTeamFolder.listFiles()) {
			try {
				String teamName = teamFile.getName().substring(0, teamFile.getName().indexOf(".json"));

				TeamBase baseToRemove = null;
				Team team = null;

				for (TeamBase teamBase : teamBaseMap.keySet()) {
					baseToRemove = teamBase;
					team = teamBaseMap.get(baseToRemove);

					if (baseToRemove != null && team != null && team.getName().equals(teamName)) {

						team.getBases().remove(baseToRemove);

						PrintWriter writer = new PrintWriter(teamFile.getAbsolutePath(), "UTF-8");
						writer.println(new Gson().toJson(team));
						writer.close();

						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.getWorld().getName().equals(event.getPlayer().getWorld().getName())) {
								p.sendMessage(event.getPlayer() + " has destroyed " + teamName + "'s base: "
										+ baseToRemove.getName());
							}
						}
						return;
					}
				}
			} catch (JsonSyntaxException e) {
				warning("JsonSyntaxException: " + e.getMessage());
			} catch (IOException e) {
				warning("IOException: " + e.getMessage());
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {

		if (!event.getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
			return;
		}

		if (event.getItemInHand().getType().equals(Material.DIAMOND_PICKAXE)
				|| event.getItemInHand().getType().equals(Material.GOLD_PICKAXE)
				|| event.getItemInHand().getType().equals(Material.IRON_PICKAXE)
				|| event.getItemInHand().getType().equals(Material.STONE_PICKAXE)
				|| event.getItemInHand().getType().equals(Material.WOOD_PICKAXE)) {
			return;
		}

		Player player = event.getPlayer();

		Map<TeamBase, Team> teamBaseMap = teamsHelper.getTeamBaseMap(event.getBlock().getWorld(),
				event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(),
				event.getBlock().getLocation().getBlockZ());

		if (teamBaseMap == null || teamBaseMap.isEmpty()) {
			return;
		}

		for (TeamBase currentTeamBase : teamBaseMap.keySet()) {
			Team team = teamBaseMap.get(currentTeamBase);

			if (!teamsHelper.playerIsTeamMember(team, event.getPlayer().getName())) {
				continue;
			}

			File blockTyperTeamsFolder = teamsHelper.getBlockTyperTeamsFolder(event.getBlock());

			File teamFile = teamsHelper.getTeamFile(blockTyperTeamsFolder, team.getName());

			if (teamFile == null) {
				continue;
			}

			Inventory teleportInventory = null;

			if (team.getBases() != null) {

				int rows = (team.getBases().size() / COLUMNS_PER_INVENTORY_ROW) + 1;

				teleportInventory = Bukkit.createInventory(null, rows * COLUMNS_PER_INVENTORY_ROW,
						team.getName() + "(Teleport)");

				int i = -1;
				for (TeamBase base : team.getBases()) {
					i++;

					String baseMaterialName = teamsHelper.getMaterialNameFromBase(base);

					Material baseMaterial = Material.valueOf(baseMaterialName);

					ItemStack baseItemForInventory = null;

					try {
						baseItemForInventory = new ItemStack(baseMaterial);
					} catch (Exception e) {
						player.sendMessage(e.getClass().getName());
						player.sendMessage(e.getMessage());
						baseItemForInventory = new ItemStack(Material.COMMAND);
					}

					ItemMeta itemMeta = baseItemForInventory.getItemMeta();

					baseItemForInventory = itemMeta == null ? new ItemStack(Material.COMMAND) : baseItemForInventory;
					itemMeta = itemMeta == null ? baseItemForInventory.getItemMeta() : itemMeta;

					if (itemMeta != null) {
						itemMeta.setDisplayName(base.getName());
						baseItemForInventory.setItemMeta(itemMeta);
					} else {
						player.sendMessage("item meta was null");
					}

					teleportInventory.setItem(i, baseItemForInventory);
				}
				player.sendMessage(currentTeamBase.getName());
				player.openInventory(teleportInventory);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockPlace(BlockPlaceEvent event) {

		Block teamBlock = getTeamBlock(event.getBlockPlaced());

		if (teamBlock == null) {
			return;
		}

		String reasonMaterialCannotBeUsedForTeam = teamsHelper.getReasonMaterialCannotBeUsed(teamBlock.getType());
		if (reasonMaterialCannotBeUsedForTeam != null) {
			event.getPlayer().sendMessage(ChatColor.RED + teamsHelper.getTeamNameFromBlock(teamBlock)
					+ " cannot be used because the material " + reasonMaterialCannotBeUsedForTeam + ".");
			return;
		}

		File blockTyperTeamsFolder = teamsHelper.getBlockTyperTeamsFolder(event.getBlockPlaced());

		String teamBlockName = teamsHelper.getTeamNameFromBlock(teamBlock);
		File newTeamFile = null;
		if (blockTyperTeamsFolder.listFiles() != null) {
			for (File file : blockTyperTeamsFolder.listFiles()) {
				newTeamFile = !file.isDirectory() && file.getName().endsWith(teamBlockName + ".json") ? file : null;
			}
		}

		if (newTeamFile != null) {

			try {
				for (String line : Files.readAllLines(Paths.get(newTeamFile.getAbsolutePath()))) {
					if (line != null && !line.isEmpty()) {
						Team team = new Gson().fromJson(line, Team.class);

						if (team == null)
							break;

						if (team.getLeaderName() == null || !team.getLeaderName().equals(event.getPlayer().getName())) {
							event.getPlayer().sendMessage("Only the team leader can create bases for " + teamBlockName);
							break;
						}

						if (team.getBases() == null) {
							team.setBases(new ArrayList<TeamBase>());
						}

						Map<String, TeamBase> baseNames = new HashMap<String, TeamBase>();
						for (TeamBase base : team.getBases()) {
							baseNames.put(base.getName(), base);
						}

						Block baseBlock = event.getBlockPlaced().getWorld().getBlockAt(
								event.getBlockPlaced().getLocation().getBlockX(),
								event.getBlockPlaced().getLocation().getBlockY() + 1,
								event.getBlockPlaced().getLocation().getBlockZ());

						String reasonMaterialCannotBeUsedForBase = teamsHelper
								.getReasonMaterialCannotBeUsed(baseBlock.getType());
						if (reasonMaterialCannotBeUsedForBase != null) {
							event.getPlayer()
									.sendMessage(ChatColor.RED + teamsHelper.getBaseNameFromBlock(baseBlock)
											+ " cannot be used because the material "
											+ reasonMaterialCannotBeUsedForBase + ".");
							return;
						}

						String baseName = teamsHelper.getBaseNameFromBlock(baseBlock);

						if (baseNames.containsKey(baseName)) {
							TeamBase existingBase = baseNames.get(baseName);
							if (existingBase.getX() == event.getBlockPlaced().getX()
									&& existingBase.getY() == event.getBlockPlaced().getY()
									&& existingBase.getZ() == event.getBlockPlaced().getZ()) {
								event.getPlayer().sendMessage("'" + baseName + "' has been re-built.");
								return;
							} else {
								event.getPlayer().sendMessage("The base name '" + baseName + "' is taken.");
								event.setCancelled(true);
								return;
							}

						}
						new TeamsCompassRecipeRegister(plugin).registerCompass(teamBlock.getType(),
								baseBlock.getType());

						TeamBase base = new TeamBase(baseName, event.getBlockPlaced().getX(),
								event.getBlockPlaced().getY(), event.getBlockPlaced().getZ());
						team.getBases().add(base);

						PrintWriter writer = new PrintWriter(newTeamFile.getAbsolutePath(), "UTF-8");
						writer.println(new Gson().toJson(team));
						writer.close();
						event.getPlayer().sendMessage(base.getName() + " created for " + teamBlockName);
						break;
					}
				}
			} catch (IOException e) {
				event.getPlayer().sendMessage("failed to create base for " + teamBlockName);
			}
		} else {
			if (event.getPlayer().getInventory().firstEmpty() < 0) {
				event.getPlayer().sendMessage("You must have at least one inventory space free for your team compass.");
				event.setCancelled(true);
				return;
			}
			newTeamFile = new File(blockTyperTeamsFolder, teamBlockName + ".json");
			try {
				List<File> teamFiles = teamsHelper.getTeamFiles(blockTyperTeamsFolder);
				if (teamFiles != null && !teamFiles.isEmpty()) {
					for (File teamFile : teamFiles) {
						for (String line : Files.readAllLines(Paths.get(teamFile.getAbsolutePath()))) {
							if (line != null && !line.isEmpty()) {
								Team team = new Gson().fromJson(line, Team.class);

								if (teamsHelper.playerIsTeamMember(team, event.getPlayer().getName())) {
									event.getPlayer().sendMessage("You are already a member of " + team.getName()
											+ " and cannot start a new team.");
									// return;
								}
								continue;
							}
						}
					}
				}

				TeamsCompassRecipeRegister teamsCompassRecipeRegister = new TeamsCompassRecipeRegister(plugin);
				teamsCompassRecipeRegister.registerCompass(teamBlock.getType(), null);
				teamsCompassRecipeRegister.registerCompass(teamBlock.getType(), teamBlock.getType());

				String baseName = teamsHelper.getBaseNameFromBlock(teamBlock);
				TeamBase base = new TeamBase(baseName, event.getBlockPlaced().getX(), event.getBlockPlaced().getY(),
						event.getBlockPlaced().getZ());
				Team team = new Team(teamBlockName, event.getPlayer().getName(), base);

				PrintWriter writer = new PrintWriter(newTeamFile.getAbsolutePath(), "UTF-8");
				writer.println(new Gson().toJson(team));
				writer.close();
				event.getPlayer().sendMessage(teamBlockName + " created");

				ItemStack compass = new ItemStack(Material.COMPASS);
				ItemMeta itemMeta = compass.getItemMeta();
				String compassName = teamsHelper.getCompassName(teamBlock.getType(), null);
				itemMeta.setDisplayName(compassName);
				compass.setItemMeta(itemMeta);
				event.getPlayer().getInventory().addItem(compass);
				event.getPlayer().setCompassTarget(teamBlock.getLocation());

			} catch (IOException e) {
				event.getPlayer().sendMessage("failed to create" + teamBlockName);
				e.printStackTrace();
			}
		}
	}

	private Team getTeam(String world, String name) throws IOException {
		File blockTyperTeamsFolder = teamsHelper.getBlockTyperTeamsFolder(Bukkit.getWorld(world));
		File newTeamFile = null;

		if (blockTyperTeamsFolder.listFiles() == null) {
			return null;
		}

		for (File file : blockTyperTeamsFolder.listFiles()) {
			newTeamFile = !file.isDirectory() && file.getName().endsWith(name + ".json") ? file : null;

			if (newTeamFile != null) {
				break;
			}
		}

		if (newTeamFile == null) {
			return null;
		}

		for (String line : Files.readAllLines(Paths.get(newTeamFile.getAbsolutePath()))) {
			if (line != null && !line.isEmpty()) {
				Team team = new Gson().fromJson(line, Team.class);
				if (team != null) {
					return team;
				}
			}
		}

		return null;
	}

	private Block getTeamBlock(Block block) {
		if (block.getType().equals(Material.DIAMOND_BLOCK)) {
			Location diamondBlockLocation = block.getLocation();

			if (diamondBlockLocation.getWorld().getBlockAt(diamondBlockLocation.getBlockX(),
					diamondBlockLocation.getBlockY() - 1, diamondBlockLocation.getBlockZ()).getType()
					.equals(Material.OBSIDIAN)) {
				return diamondBlockLocation.getWorld().getBlockAt(diamondBlockLocation.getBlockX(),
						diamondBlockLocation.getBlockY() - 2, diamondBlockLocation.getBlockZ());
			}
		}
		return null;
	}

	
}
