package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.entity.Snowman;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowmanArrowHandler implements IArrowHandler{
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Snowman.class);
	}

	
}
