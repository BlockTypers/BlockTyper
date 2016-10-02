package com.blocktyper.glyphs;

import org.bukkit.Material;

import com.blocktyper.glyphs.arrowhandler.CreeperArrowHandler;
import com.blocktyper.glyphs.arrowhandler.GlowstoneArrowHandler;
import com.blocktyper.glyphs.arrowhandler.IArrowHandler;
import com.blocktyper.glyphs.arrowhandler.LavaArrowHandler;
import com.blocktyper.glyphs.arrowhandler.LightningArrowHandler;
import com.blocktyper.glyphs.arrowhandler.SuperCreeperArrowHandler;
import com.blocktyper.glyphs.arrowhandler.TeleportArrowHandler;
import com.blocktyper.glyphs.arrowhandler.TorchArrowHandler;
import com.blocktyper.glyphs.arrowhandler.WaterArrowHandler;

public enum BowGlyphEnum {
	ANTI_GRAVITY("ANTI-GRAVITY", "Anti-Gravity", "➟", new CraftShape(new CraftShapeRow(Material.DIAMOND,Material.FEATHER,Material.DIAMOND),new CraftShapeRow(Material.DIAMOND,Material.BOW,Material.DIAMOND),new CraftShapeRow(Material.DIAMOND,Material.FEATHER,Material.DIAMOND)), null), 
	TELEPORT("TELEPORT", "Teleport", "✈", new CraftShape(new CraftShapeRow(Material.ENDER_PEARL,Material.EYE_OF_ENDER,Material.ENDER_PEARL),new CraftShapeRow(Material.ENDER_PEARL,Material.BOW,Material.ENDER_PEARL),new CraftShapeRow(Material.EYE_OF_ENDER,Material.EYE_OF_ENDER,Material.EYE_OF_ENDER)), new TeleportArrowHandler()),
	GLOWSTONE("GLOWSTONE", "Glowstone", "✰", new CraftShape(Material.BOW, Material.GLOWSTONE), new GlowstoneArrowHandler()),
	TORCH("TORCH", "Torch", "☼", new CraftShape(Material.BOW, Material.TORCH), new TorchArrowHandler()),
	LAVA("LAVA", "Lava", "♌", new CraftShape(Material.BOW, Material.LAVA_BUCKET), new LavaArrowHandler()),
	WATER("WATER", "Water", "♒", new CraftShape(Material.BOW, Material.WATER_BUCKET), new WaterArrowHandler()),
	LIGHTNING("LIGHTNING", "Ligntning", "⚡", new CraftShape(new CraftShapeRow(Material.BLAZE_POWDER,Material.TNT,Material.BLAZE_POWDER),new CraftShapeRow(Material.TNT,Material.BOW,Material.TNT),new CraftShapeRow(Material.BLAZE_POWDER,Material.TNT,Material.BLAZE_POWDER)), new LightningArrowHandler()),
	CREEPER("CREEPER", "Creeper", "☢", new CraftShape(Material.BOW, Material.SULPHUR), new CreeperArrowHandler()),
	SUPER_CREEPER("SUPER_CREEPER", "Super Creeper", "☢!", new CraftShape(Material.BOW, Material.TNT), new SuperCreeperArrowHandler()),
	SNOWMAN("SNOWMAN", "Snowman", "☃", new CraftShape(new CraftShapeRow(Material.PUMPKIN,Material.SNOW_BLOCK,Material.PUMPKIN),new CraftShapeRow(Material.SNOW_BLOCK,Material.BOW,Material.SNOW_BLOCK),new CraftShapeRow(Material.SNOW_BLOCK,Material.SNOW_BLOCK,Material.SNOW_BLOCK)), null);

	private String code;
	private String displayName;
	private String glyph;
	private CraftShape craftShape;
	private IArrowHandler arrowHandler;
	
	
	

	private BowGlyphEnum(String code, String displayName, String glyph, CraftShape craftShape, IArrowHandler arrowHandler) {
		this.code = code;
		this.displayName = displayName;
		this.glyph = glyph;
		this.craftShape = craftShape;
		this.arrowHandler = arrowHandler;
	}

	public String getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getGlyph() {
		return glyph;
	}

	public CraftShape getCraftShape() {
		return craftShape;
	}

	

	public IArrowHandler getArrowHandler() {
		return arrowHandler;
	}

	public static BowGlyphEnum findByCode(String code) {
		for (BowGlyphEnum bowGlyph : BowGlyphEnum.values()) {
			if (bowGlyph.getCode().equals(code))
				return bowGlyph;
		}
		return null;
	}

	public static BowGlyphEnum findByGlyph(String glyph) {
		for (BowGlyphEnum bowGlyph : BowGlyphEnum.values()) {
			if (bowGlyph.getGlyph().equals(glyph))
				return bowGlyph;
		}
		return null;
	}

}
