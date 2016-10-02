package com.blocktyper.glyphs;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;

public class BowGlyphCraftListener extends AbstractListener {

	public BowGlyphCraftListener(JavaPlugin plugin) {
		super(plugin);
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void PrepareItemCraft(PrepareItemCraftEvent event) {

		ItemStack itemCreated = event.getInventory().getResult();
		
		if (!itemCreated.getType().equals(Material.BOW)) {
			return;
		}

		String glyphedItemDisplayName1 = null;
		String glyphedItemDisplayName2 = null;
		for (ItemStack usedItem : event.getInventory().getContents()) {
			if (usedItem.getItemMeta() != null && usedItem.getItemMeta().getDisplayName() != null
					&& !usedItem.getItemMeta().getDisplayName().trim().isEmpty()) {
				if (glyphedItemDisplayName1 == null) {
					glyphedItemDisplayName1 = usedItem.getItemMeta().getDisplayName();
				} else {
					glyphedItemDisplayName2 = usedItem.getItemMeta().getDisplayName();
				}
			}
		}


		if (glyphedItemDisplayName1 != null && glyphedItemDisplayName2 != null) {
			String[] glyphsArray1 = BowGlyphProjectileListener.getGlyphsArray(glyphedItemDisplayName1);
			String[] glyphsArray2 = BowGlyphProjectileListener.getGlyphsArray(glyphedItemDisplayName2);
			
			String glyphsCsv = "";
			
			for(String glyph : glyphsArray1){
				if(glyphsCsv.isEmpty()){
					glyphsCsv = "[" + glyph;
				}else{
					glyphsCsv = glyphsCsv + "," + glyph;
				}
			}
			
			for(String glyph : glyphsArray2){
				if(glyphsCsv.isEmpty()){
					glyphsCsv = "[" + glyph;
				}else{
					glyphsCsv = glyphsCsv + "," + glyph;
				}
			}


			if(!glyphsCsv.isEmpty()){
				glyphsCsv = glyphsCsv + "]";
				ItemMeta itemMeta = itemCreated.getItemMeta();
				itemMeta.setDisplayName("Bow" + glyphsCsv);
				itemCreated.setItemMeta(itemMeta);
			}else{
			}
		}
	}

}
