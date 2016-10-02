package com.blocktyper.glyphs.arrowhandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.Torch;
import org.bukkit.plugin.java.JavaPlugin;

public class TorchArrowHandler implements IArrowHandler {
	
	public void handleProjectileHitEvent(ProjectileHitEvent event, JavaPlugin plugin) {
		placeTorch(event.getEntity().getLocation());
	}
	
	private void placeTorch(Location loc) { //Attempt to place a torch on any side available
	    if (loc.getBlock().getType() == Material.AIR) { //Make sure this is an empty block first
	        for (BlockFace face : BlockFace.values()) {
	            switch(face) {
	                case EAST: case NORTH: case SOUTH: case WEST: {
	                    if (loc.getBlock().getRelative(face).getType().isOccluding()
	                            && loc.getBlock().getRelative(face).getType().isSolid()) { //Make sure this side supports a torch
	                        placeTorch(loc, face);
	                        return; //Attachable side found, no need to keep looping
	                    }
	                    else continue;
	                }
	                default: continue;
	            }
	        }
	        placeTorch(loc, BlockFace.DOWN); //No attachable sides found, place it on the ground
	    }
	}

	private void placeTorch(Location loc, BlockFace face) { //Place a torch on a specified side
	    if (loc.getBlock().getType() == Material.AIR) { //Make sure this is an empty block first
	        if (loc.getBlock().getRelative(face).getType().isOccluding()
	                && loc.getBlock().getRelative(face).getType().isSolid()) {
	            loc.getBlock().setType(Material.TORCH);
	            if(loc.getBlock().getState() != null && loc.getBlock().getState().getData() instanceof Torch){
	            	Torch torch = (Torch) loc.getBlock().getState().getData();
		            torch.setFacingDirection(face);
	            }
	        }
	    }
	}

}
