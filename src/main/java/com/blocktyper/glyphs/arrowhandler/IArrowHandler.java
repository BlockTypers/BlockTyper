package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public interface IArrowHandler {
	void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin);
}
