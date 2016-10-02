package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LightningArrowHandler implements IArrowHandler{
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
	}

	
}
