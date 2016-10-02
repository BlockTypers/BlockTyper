package com.blocktyper.glyphs;

import org.bukkit.Material;

public class CraftShapeRow {
	private Material one;
	private Material two;
	private Material three;

	public CraftShapeRow(Material one, Material two, Material three) {
		super();
		this.one = one;
		this.two = two;
		this.three = three;
	}

	public Material getOne() {
		return one;
	}

	public Material getTwo() {
		return two;
	}

	public Material getThree() {
		return three;
	}

}
