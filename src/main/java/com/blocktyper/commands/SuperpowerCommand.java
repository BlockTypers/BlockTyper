package com.blocktyper.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.glyphs.BowGlyphEnum;

public class SuperpowerCommand implements CommandExecutor {

	private JavaPlugin mainPlugin;

	public SuperpowerCommand(JavaPlugin mp) {
		this.mainPlugin = mp;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		try {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;

			if (args != null && args.length > 0) {

				boolean somethingWasDone = false;

				for (String arg : args) {
					if (arg.equals("clear")) {
						player.getInventory().clear();
						somethingWasDone = true;
						player.sendMessage(ChatColor.GREEN + "Inventory cleared");
					} else if (arg.equals("fly")) {
						player.setAllowFlight(!player.getAllowFlight());
						somethingWasDone = true;
						player.sendMessage(
								ChatColor.GREEN + (player.getAllowFlight() ? "Granted Flight" : "Flight Revoked"));
					} else if (arg.equals("gear")) {
						gearUp(player);
						somethingWasDone = true;
						player.sendMessage(ChatColor.GREEN + "Geared up");
					} else if (arg.equals("bow")) {
						bow(player, args);
						somethingWasDone = true;
						player.sendMessage(ChatColor.GREEN + "Bow given");
					} else if (arg.equals("hp")) {
						player.setHealth(player.getMaxHealth());
						somethingWasDone = true;
						player.sendMessage(ChatColor.GREEN + "Healed");
					} else if (arg.startsWith("food")) {
						player.setFoodLevel(0);
						somethingWasDone = true;
						player.sendMessage(ChatColor.GREEN + "Fed");
						player.sendMessage(ChatColor.YELLOW + "Saturation: " + player.getSaturation());
						player.sendMessage(ChatColor.YELLOW + "Exhaustion: " + player.getExhaustion());
					}
				}

				if (!somethingWasDone) {
					player.sendMessage(
							ChatColor.RED + "You must provide a valid argument['clear','fly','gear', 'hp', 'food']");
				}

				return somethingWasDone;
			} else {
				player.sendMessage(
						ChatColor.RED + "You must provide at least one argument['clear','fly','gear', 'hp', 'food']");
			}

			return false;
		} catch (Exception e) {
			mainPlugin.getLogger().info("ERRRRRRROOOOOROROROROROR:  " + e.getMessage());
			return false;
		}

	}

	private void bow(Player player, String[] args) {
		ItemStack bow = new ItemStack(Material.BOW);
		
		String glyphs = "";
		if(args != null && args.length > 0){
			for(String arg : args){
				for(BowGlyphEnum bowGlyph : BowGlyphEnum.values()){
					if(arg != null && (arg.equals(bowGlyph.getGlyph()) || arg.equals(bowGlyph.getCode()))){
						if(glyphs.isEmpty()){
							glyphs = "[" + bowGlyph.getGlyph();
						}else{
							glyphs += ","+bowGlyph.getGlyph();
						}
					}
				}
			}
		}
		
		if(!glyphs.isEmpty()){
			glyphs = glyphs + "]";
		}
		
		if(!glyphs.isEmpty()){
			ItemMeta bowMeta = bow.getItemMeta();
			
			if(bowMeta != null){
				bowMeta.setDisplayName("Bow"+glyphs);
				bow.setItemMeta(bowMeta);
			}
		}
		
		bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 100);
		player.getInventory().addItem(new ItemStack(Material.ARROW));
		player.getInventory().addItem(bow);
	}

	private void gearUp(Player player) {
		// create hand-held items
		ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
		diamondSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 100);

		ItemStack silkPickAxe = new ItemStack(Material.DIAMOND_PICKAXE);
		silkPickAxe.addEnchantment(Enchantment.SILK_TOUCH, 1);

		ItemStack durablePickAxe = new ItemStack(Material.DIAMOND_PICKAXE);
		durablePickAxe.addUnsafeEnchantment(Enchantment.DURABILITY, 100);

		ItemStack durableSpade = new ItemStack(Material.DIAMOND_SPADE);
		durableSpade.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
		durableSpade.addUnsafeEnchantment(Enchantment.DIG_SPEED, 100);

		ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
		axe.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
		axe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 100);

		ItemStack hoe = new ItemStack(Material.DIAMOND_HOE);
		hoe.addUnsafeEnchantment(Enchantment.DURABILITY, 100);

		ItemStack bow = new ItemStack(Material.BOW);
		bow.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
		bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 100);
		bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 100);
		bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 100);
		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 100);
		
		// add items hand-held items to inventory
		player.getInventory().addItem(diamondSword);
		player.getInventory().addItem(silkPickAxe);
		player.getInventory().addItem(durablePickAxe);
		player.getInventory().addItem(durableSpade);
		player.getInventory().addItem(axe);
		player.getInventory().addItem(hoe);
		player.getInventory().addItem(bow);
		
		player.getInventory().addItem(new ItemStack(Material.ARROW));

		// create gear
		ItemStack helm = new ItemStack(Material.DIAMOND_HELMET);
		helm.addUnsafeEnchantment(Enchantment.OXYGEN, 100);
		helm.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 100);
		helm.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 100);
		helm.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 100);
		helm.addUnsafeEnchantment(Enchantment.THORNS, 100);

		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		chest.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 100);
		chest.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 100);
		chest.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 100);
		chest.addUnsafeEnchantment(Enchantment.THORNS, 100);

		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		legs.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 100);
		legs.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 100);
		legs.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 100);
		legs.addUnsafeEnchantment(Enchantment.THORNS, 100);

		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 100);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 100);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 100);
		boots.addUnsafeEnchantment(Enchantment.THORNS, 100);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 100);

		// add gear to inventory
		player.getInventory().addItem(helm);
		player.getInventory().addItem(chest);
		player.getInventory().addItem(legs);
		player.getInventory().addItem(boots);

	}

}
