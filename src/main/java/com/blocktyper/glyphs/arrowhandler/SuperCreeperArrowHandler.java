package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperCreeperArrowHandler implements IArrowHandler{
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		Creeper creeper = (Creeper)event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.CREEPER);
		creeper.setPowered(true);
	}

	
}
