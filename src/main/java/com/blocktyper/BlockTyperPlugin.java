package com.blocktyper;

import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.commands.SuperpowerCommand;
import com.blocktyper.glyphs.BowGlyphCraftListener;
import com.blocktyper.glyphs.BowGlyphProjectileListener;
import com.blocktyper.glyphs.BowGlyphRecipeRegister;
import com.blocktyper.teams.TeamBlockListener;
import com.blocktyper.teams.TeamsCompassClickListener;
import com.blocktyper.teams.TeamsCompassCraftListener;
import com.blocktyper.teams.TeamsCompassInviteListener;
import com.blocktyper.teams.TeamsCompassQuitListener;
import com.blocktyper.teams.TeamsCompassRecipeRegister;

public class BlockTyperPlugin extends JavaPlugin {

	public void onEnable() {
		super.onEnable();
		registerCommands();
		registerListeners();
		registerRecipes();
	}

	private void registerRecipes() {
		new BowGlyphRecipeRegister(this).registerRecipes();
		new TeamsCompassRecipeRegister(this).registerAllTeamCompasses();
	}

	private void registerListeners() {
		new BowGlyphProjectileListener(this);
		new TeamBlockListener(this);
		new BowGlyphCraftListener(this);
		new TeamsCompassCraftListener(this);
		new TeamsCompassClickListener(this);
		new TeamsCompassInviteListener(this);
		new TeamsCompassQuitListener(this);
	}

	private void registerCommands() {
		SuperpowerCommand superpowerCommand = new SuperpowerCommand(this);
		this.getCommand("sp").setExecutor(superpowerCommand);
		this.getCommand("superpower").setExecutor(superpowerCommand);
		getLogger().info("'/superpower' registered to SuperpowerCommand");
	}

}
