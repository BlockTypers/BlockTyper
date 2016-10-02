package com.blocktyper.glyphs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;

public class BowGlyphProjectileListener extends AbstractListener{
	
	
	public BowGlyphProjectileListener(JavaPlugin plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void entityShootBow(EntityShootBowEvent event) {

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		if (event.getBow().getItemMeta() == null || event.getBow().getItemMeta().getDisplayName() == null) {
			return;
		}

		if (event.getBow().getItemMeta().getDisplayName().contains(BowGlyphEnum.ANTI_GRAVITY.getGlyph())) {
			event.getProjectile().setGravity(false);
		}

		event.getProjectile().setCustomName(event.getBow().getItemMeta().getDisplayName());

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {

		Projectile projectile = event.getEntity();

		if (!(projectile.getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) projectile.getShooter();

		if (projectile.getType().equals(EntityType.SNOWBALL)) {
			projectile.setGravity(false);
			projectile.setGlowing(true);
			shooter.sendMessage("Snowlazer. Speed: " + projectile.getVelocity().getX() + ","
					+ projectile.getVelocity().getY() + "," + projectile.getVelocity().getZ());
		}

		shooter.sendMessage("ticks lived: " + projectile.getTicksLived());

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onProjectileHit(ProjectileHitEvent event) {

		if(!(event.getEntity().getShooter() instanceof Player)){
			return;
		}
		
		String[] glyphsArray = getGlyphsArray(event.getEntity().getCustomName());
		
		if(glyphsArray == null || glyphsArray.length < 1){
			return;
		}
		
		for(String glyph : glyphsArray){
			BowGlyphEnum bowGlyph = BowGlyphEnum.findByGlyph(glyph);
			
			if(bowGlyph == null || bowGlyph.getArrowHandler() == null){
				continue;
			}
			
			bowGlyph.getArrowHandler().handleProjectileHitEvent(event, plugin);
		}

		event.getEntity().setCustomName(null);
	}
	
	public static String[] getGlyphsArray(String name){
		if (name == null || name.indexOf("[") < 0 || name.indexOf("]") < 0) {
			return null;
		}
		
		if (name.indexOf("[") >= name.indexOf("]")) {
			return null;
		}
		
		String glyphsCsv = name.substring(name.indexOf("[") + 1, name.indexOf("]"));

		if(glyphsCsv == null || glyphsCsv.isEmpty()){
			return null;
		}
		
		String[] glyphsArray = null;
		
		if(glyphsCsv.contains(",")){
			glyphsArray = glyphsCsv.split(",");
		}else{
			glyphsArray = new String[]{glyphsCsv};
		}
		
		if(glyphsArray == null || glyphsArray.length < 1){
			return null;
		}
		
		return glyphsArray;
	}
	
}
