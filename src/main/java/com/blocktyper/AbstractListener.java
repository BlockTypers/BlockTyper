package com.blocktyper;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractListener implements Listener{
	protected JavaPlugin plugin;

	public AbstractListener(JavaPlugin mainPlugin) {
		this.plugin = mainPlugin;
		mainPlugin.getServer().getPluginManager().registerEvents(this, mainPlugin);
	}

	protected void warning(String msg){
		plugin.getLogger().warning(msg);
	}
	
	protected void info(String msg){
		plugin.getLogger().info(msg);
	}
	
	

}
