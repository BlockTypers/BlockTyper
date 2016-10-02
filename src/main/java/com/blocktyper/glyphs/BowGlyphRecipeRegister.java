package com.blocktyper.glyphs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class BowGlyphRecipeRegister {
	private JavaPlugin plugin;

	public BowGlyphRecipeRegister(JavaPlugin plugin) {
		super();
		this.plugin = plugin;

	}

	public void registerRecipes() {

		plugin.getLogger().warning("registerRecipes");
		for (BowGlyphEnum bowGlyph : BowGlyphEnum.values()) {
			if (bowGlyph.getCraftShape() == null) {
				plugin.getLogger().warning("null craft shape for " + bowGlyph.getDisplayName());
				continue;
			} else {
				plugin.getLogger().info("craft shape for " + bowGlyph.getDisplayName() + " found");
			}

			ItemStack bow = new ItemStack(Material.BOW, 1);
			ShapedRecipe shapedRecipe = null;

			try {
				shapedRecipe = bowGlyph.getCraftShape().getShapedRecipe(bow, "Bow[" + bowGlyph.getGlyph() + "]");

				if (shapedRecipe != null) {
					plugin.getLogger().info("shapedRecipe found");
					plugin.getLogger().info(shapedRecipe.getShape()[0] + "," + shapedRecipe.getShape()[1] + ","
							+ shapedRecipe.getShape()[2]);
					plugin.getServer().addRecipe(shapedRecipe);
				} else {
					plugin.getLogger().warning("missing shapedRecipe");
				}
			} catch (Exception e) {
				plugin.getLogger().info("shapedRecipe exception: " + e.getMessage());
			}

			ItemStack bow2 = new ItemStack(Material.BOW, 1);
			ShapedRecipe shapedRecipe2 = new ShapedRecipe(bow2);
			shapedRecipe2.shape("BBB", "BDB", "BBB");
			shapedRecipe2.setIngredient('B', Material.BOW);
			shapedRecipe2.setIngredient('D', Material.DIAMOND);
			plugin.getServer().addRecipe(shapedRecipe2);
			plugin.getLogger().info("added compound glyph bow recipe");

		}
	}
}
