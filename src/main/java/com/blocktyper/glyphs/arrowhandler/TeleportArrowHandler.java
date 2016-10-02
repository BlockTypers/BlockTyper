package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportArrowHandler implements IArrowHandler{
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		try {
			Player p = (Player) event.getEntity().getShooter();
			p.teleport(event.getEntity().getLocation());
		} catch (Exception e) {
			plugin.getLogger().warning("TELEPORT FAILED: " + e.getMessage());
		}
	}

	
}
