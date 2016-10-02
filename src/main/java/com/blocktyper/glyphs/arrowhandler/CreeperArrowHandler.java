package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.entity.Creeper;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CreeperArrowHandler implements IArrowHandler{
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Creeper.class);
	}

	
}
