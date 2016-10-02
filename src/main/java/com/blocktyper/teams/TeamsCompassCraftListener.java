package com.blocktyper.teams;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.AbstractListener;

public class TeamsCompassCraftListener extends AbstractListener {

	TeamsHelper teamsHelper;

	public TeamsCompassCraftListener(JavaPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
		this.teamsHelper = new TeamsHelper(plugin);

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void PrepareItemCraft(PlayerChangedMainHandEvent event) {
		plugin.getLogger().info("PlayerChangedMainHandEvent");
		plugin.getLogger().info(event.getMainHand().name());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void PrepareItemCraft(PrepareItemCraftEvent event) {

		ItemStack itemCreated = event.getInventory().getResult();

		if (itemCreated == null || !itemCreated.getType().equals(Material.COMPASS)) {
			return;
		}

		int i = -1;
		Map<Integer, Material> itemTypeMap = new HashMap<Integer, Material>();
		for (ItemStack item : event.getInventory().getMatrix()) {
			i++;
			itemTypeMap.put(i, item != null ? item.getType() : null);
		}

		if (itemTypeMap.get(3) == null || !itemTypeMap.get(3).equals(Material.COMPASS)) {
			return;
		}

		Material teamMaterial = itemTypeMap.get(0);
		Material baseMaterial = itemTypeMap.get(1);

		if (baseMaterial == null || teamMaterial == null) {
			return;
		}

		String compassName = teamsHelper.getCompassName(teamMaterial, baseMaterial);

		ItemMeta itemMeta = itemCreated.getItemMeta();
		itemMeta.setDisplayName(compassName);
		itemCreated.setItemMeta(itemMeta);
	}

}
