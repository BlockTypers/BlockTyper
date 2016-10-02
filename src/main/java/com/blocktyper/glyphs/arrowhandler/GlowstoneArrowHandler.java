package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GlowstoneArrowHandler implements IArrowHandler{
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		Block block = event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation());
		if(block != null){
			block.setType(Material.GLOWSTONE);
		}
	}

	
}
