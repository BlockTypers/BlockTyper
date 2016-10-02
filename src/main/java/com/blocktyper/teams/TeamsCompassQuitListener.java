package com.blocktyper.teams;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;

import net.md_5.bungee.api.ChatColor;

public class TeamsCompassQuitListener extends AbstractListener {

	public static String INVITE_SUFFIX = "(INVITE)";

	TeamsHelper teamsHelper;

	public TeamsCompassQuitListener(JavaPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
		this.teamsHelper = new TeamsHelper(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerDropItem(PlayerDropItemEvent event) {

		event.getPlayer().sendMessage("Dropped: " + event.getItemDrop().getItemStack().getType().name());

		if (event.getItemDrop() == null || event.getItemDrop().getItemStack() == null
				|| event.getItemDrop().getItemStack().getType() == null) {
			return;
		}

		ItemStack itemDropped = event.getItemDrop().getItemStack();
		if (!itemDropped.getType().equals(Material.COMPASS)) {
			return;
		}

		if (itemDropped.getItemMeta() == null) {
			return;
		}

		String teamName = teamsHelper.getTeamNameFromCompassName(itemDropped.getItemMeta().getDisplayName());

		if (teamName == null) {
			return;
		}

		String baseName = teamsHelper.getBaseNameFromCompassName(itemDropped.getItemMeta().getDisplayName());
		if (baseName != null) {
			return;
		}

		Player player = event.getPlayer();
		Team team = teamsHelper.getTeam(player.getWorld(), teamName);
		if (!teamsHelper.playerIsTeamMember(team, player.getName())) {
			return;
		}

		String newLeaderName = null;
		if (team.getLeaderName().equals(player.getName())) {
			for (String teamMember : team.getMembers()) {
				if (!teamMember.equals(team.getLeaderName())) {
					teamsHelper.updateTeamLeader(player.getWorld(), teamName, teamMember);
					newLeaderName = teamMember;
				}
			}

			if (newLeaderName == null) {
				if (teamsHelper.deleteTeam(player.getWorld(), teamName)) {
					int numberOfPlayersNotified = 0;
					if (player.getWorld().getPlayers() != null) {
						for (Player playerInWorld : player.getWorld().getPlayers()) {
							numberOfPlayersNotified++;
							playerInWorld
									.sendMessage(ChatColor.YELLOW + player.getName() + " has disbanded " + teamName);
						}
					}
					player.sendMessage(ChatColor.YELLOW + "You has disbanded " + teamName + ". "
							+ numberOfPlayersNotified + " players were online and notified.");
				} else {
					player.sendMessage(ChatColor.RED + "There was an issue disbanding your team!");
				}
				return;
			}
		}

		if (teamsHelper.removeTeamMember(player.getWorld(), teamName, player.getName())) {
			event.getItemDrop().remove();
			if (player.getWorld().getPlayers() != null) {
				for (Player playerInWorld : player.getWorld().getPlayers()) {
					playerInWorld.sendMessage(ChatColor.YELLOW + player.getName() + " has quit " + teamName + ".");
					if (newLeaderName != null) {
						playerInWorld.sendMessage(
								ChatColor.YELLOW + "The new leader of " + teamName + " is now " + newLeaderName + ".");
					}
				}
			}
		}

	}

}
