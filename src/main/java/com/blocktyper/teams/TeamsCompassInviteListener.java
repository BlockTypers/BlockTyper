package com.blocktyper.teams;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;

public class TeamsCompassInviteListener extends AbstractListener {

	public static String INVITE_SUFFIX = "(INVITE)";

	TeamsHelper teamsHelper;

	public TeamsCompassInviteListener(JavaPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
		this.teamsHelper = new TeamsHelper(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void entityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player damager = (Player) event.getDamager();
		Player victim = (Player) event.getEntity();

		ItemStack itemInMainHand = damager.getEquipment().getItemInMainHand();

		if (itemInMainHand == null || !itemInMainHand.getType().equals(Material.COMPASS)) {
			return;
		}

		// stop damage to any player hit with a compass
		event.setCancelled(true);
		if (itemInMainHand.getItemMeta() == null || itemInMainHand.getItemMeta().getDisplayName() == null) {
			return;
		}

		String compassName = itemInMainHand.getItemMeta().getDisplayName();

		String teamName = teamsHelper.getTeamNameFromCompassName(compassName);

		if (teamName == null) {
			return;
		}

		String baseName = teamsHelper.getBaseNameFromCompassName(compassName);

		if (baseName != null) {
			damager.sendMessage(ChatColor.RED + "Only a team compass can be used to invite players.");
			return;
		}

		Team team = teamsHelper.getTeam(damager.getWorld(), teamName);

		if (team == null) {
			damager.sendMessage(ChatColor.RED + "Team not recognized: " + teamName);
		}

		boolean isInviteCompass = compassName.contains(INVITE_SUFFIX);
		if (!isInviteCompass && !team.getLeaderName().equals(damager.getName())) {
			damager.sendMessage(ChatColor.RED +
					"Only the team leader (" + team.getLeaderName() + ") can invite without using an invite compass.");
			return;
		}

		boolean inviterFound = false;
		boolean canidateFound = false;
		if (team.getMembers() != null && !team.getMembers().isEmpty()) {
			for (String teamMember : team.getMembers()) {
				if (teamMember.equals(damager.getName())) {
					inviterFound = true;
				}
				if (teamMember.equals(victim.getName())) {
					canidateFound = true;
				}
			}
		}

		if (!inviterFound) {
			damager.sendMessage(ChatColor.RED + "Only a member of the team can send invites.");
			damager.getInventory().remove(itemInMainHand);
			return;
		}

		if (canidateFound) {
			damager.sendMessage(ChatColor.RED + victim.getName() + " is already a member of the team.");
			return;
		}

		boolean candidateHasCompass = victim.getInventory().contains(Material.COMPASS);

		if (candidateHasCompass) {
			victim.sendMessage(ChatColor.RED
					+ "You have a compass in your inventory and must drop it before you can accept the inite from "
					+ teamName + ".");
			damager.sendMessage(ChatColor.RED + victim.getName()
					+ " has a compass in their inventory and must drop it before they can be invited.");
			return;
		}
		
		if(victim.getInventory().firstEmpty() < 0){
			victim.sendMessage(ChatColor.RED
					+ "You have have no space in your inventory and must make room before you can accept the inite from "
					+ teamName + ".");
			damager.sendMessage(ChatColor.RED + victim.getName()
					+ " has no space in their inventory and must make room before they can be invited.");
			return;
		}

		List<Team> teams = teamsHelper.getTeams(damager.getWorld());

		Team candidatesOtherTeam = null;
		if (teams != null && !teams.isEmpty()) {
			for (Team otherTeam : teams) {
				if (otherTeam == null) {
					continue;
				}
				if (otherTeam.getName().equals(teamName)) {
					continue;
				}
				if (otherTeam.getMembers() != null && !otherTeam.getMembers().isEmpty()) {
					for (String teamMember : team.getMembers()) {
						if (teamMember.equals(victim.getName())) {
							candidatesOtherTeam = otherTeam;
						}
					}
				}
			}
		}

		if (candidatesOtherTeam != null) {
			victim.sendMessage(ChatColor.RED + victim.getName() + " is a member of " + candidatesOtherTeam.getName()
					+ " and must quit that team before you can invite them.");
			damager.sendMessage(ChatColor.RED + "You are a member of " + candidatesOtherTeam.getName()
					+ " and must quit that team before you can accept the invite from " + teamName + ".");
			if (!candidateHasCompass) {
				victim.sendMessage(
						ChatColor.RED + "You somehow lost your team compass and must aquire a new one in order to quit "
								+ candidatesOtherTeam.getName() + ".");
				damager.sendMessage(ChatColor.RED + victim.getName()
						+ " somehow lost their team compass and must aquire a new one in order to quit "
						+ candidatesOtherTeam.getName() + ".");
			}
			return;
		}

		boolean added = teamsHelper.addTeamMember(damager.getWorld(), teamName, victim.getName());

		if (!added) {
			victim.sendMessage(ChatColor.RED + "Unknown issue adding you to the " + teamName + "!");
			victim.sendMessage(ChatColor.RED + "Unknown issue adding " + victim.getName() + " to the team!");
			return;
		}

		if (isInviteCompass) {
			damager.getInventory().remove(itemInMainHand);
		}

		ItemStack teamCompassForNewPlayer = new ItemStack(Material.COMPASS);
		ItemMeta teamCompassItemMetaForNewPlayer = teamCompassForNewPlayer.getItemMeta();
		teamCompassItemMetaForNewPlayer.setDisplayName(teamName);
		teamCompassForNewPlayer.setItemMeta(teamCompassItemMetaForNewPlayer);
		victim.getInventory().addItem(teamCompassForNewPlayer);

		for (Player player : damager.getWorld().getPlayers()) {
			player.sendMessage(ChatColor.GREEN + victim.getName() + " is now a member of " + teamName + ".");
		}

	}
}
