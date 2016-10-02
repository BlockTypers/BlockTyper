package com.blocktyper.teams;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;

public class TeamsCompassClickListener extends AbstractListener {

	TeamsHelper teamsHelper;

	public TeamsCompassClickListener(JavaPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
		this.teamsHelper = new TeamsHelper(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void InventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = ((Player)event.getWhoClicked());
		if (!event.getCurrentItem().getType().equals(Material.COMPASS)) {
			return;
		}
		if (event.getCurrentItem().getItemMeta() == null
				|| event.getCurrentItem().getItemMeta().getDisplayName() == null) {
			if(player.getBedSpawnLocation() != null){
				player.sendMessage("Target set to spawn point.");
				player.setCompassTarget(player.getBedSpawnLocation());
			}
			return;
		}
		
		String compassName = event.getCurrentItem().getItemMeta().getDisplayName();
		String teamName = teamsHelper.getTeamNameFromCompassName(compassName);
		
		try {
			if(teamName == null){
				throw new Exception("Not a team compass.  Target set to spawn point.");
			}
			
			Team team = teamsHelper.getTeam(player.getWorld(), teamName);
			
			if(team == null || team.getBases() == null || team.getBases().isEmpty()){
				throw new Exception("Team not found.  Target set to spawn point.");
			}
			
			if(team.getBases() == null || team.getBases().isEmpty()){
				throw new Exception("Team has no bases.  Target set to spawn point.");
			}
			
			String baseName = teamsHelper.getBaseNameFromCompassName(event.getCurrentItem().getItemMeta().getDisplayName());
			
			Location compassTargetLocation = null;
			TeamBase targetBase = null;
			for(TeamBase base : team.getBases()){
				if(base == null){
					continue;
				}
				
				if(baseName != null){
					if(baseName.equals(base.getName())){
						Block baseBlock = baseName.equals(base.getName()) ? player.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ()) : null;
						if(baseBlock == null){
							throw new Exception("Target base block was null.  Target set to spawn point.");
						}
						targetBase = base;
						compassTargetLocation = baseBlock.getLocation();
						break;
					}
					
				}else{
					Block baseBlock = player.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ());
					if(compassTargetLocation == null){
						targetBase = base;
						compassTargetLocation = baseBlock != null ? baseBlock.getLocation() : null;
						continue;
					}
					double distanceFromCurrentTarget = teamsHelper.getDistance(player.getLocation(), compassTargetLocation);
					double distanceFromCurrentBase = teamsHelper.getDistance(player.getLocation(), baseBlock.getLocation());
					
					if(distanceFromCurrentBase < distanceFromCurrentTarget){
						targetBase = base;
						compassTargetLocation = baseBlock.getLocation();
					}
				}
			}
			
			if(compassTargetLocation == null){
				throw new Exception("Could not determine target base.  Target set to spawn point.");
			}
			player.setCompassTarget(compassTargetLocation);
			player.sendMessage("Compass target updated. Now tracking " + teamName);
			player.sendMessage("Base: " + (targetBase != null ? targetBase.getName() : "UNKNOWN!"));
		} catch (Exception e) {
			if(player.getBedSpawnLocation() != null){
				player.sendMessage("Target set to spawn point.");
			}
			player.sendMessage(compassName + ". " + e.getMessage());
		}
	}
}
