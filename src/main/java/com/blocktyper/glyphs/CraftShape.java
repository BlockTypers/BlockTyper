package com.blocktyper.glyphs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftShape {
	private CraftShapeRow top;
	private CraftShapeRow middle;
	private CraftShapeRow bottom;

	public CraftShape(CraftShapeRow top, CraftShapeRow middle, CraftShapeRow bottom) {
		super();
		this.top = top;
		this.middle = middle;
		this.bottom = bottom;
	}
	
	public CraftShape(Material center, Material surrounding) {
		super();
		
		this.top = new CraftShapeRow(surrounding, surrounding, surrounding);
		this.middle = new CraftShapeRow(surrounding, center, surrounding);;
		this.bottom = top;
	}

	public CraftShapeRow getTop() {
		return top;
	}

	public void setTop(CraftShapeRow top) {
		this.top = top;
	}

	public CraftShapeRow getMiddle() {
		return middle;
	}

	public void setMiddle(CraftShapeRow middle) {
		this.middle = middle;
	}

	public CraftShapeRow getBottom() {
		return bottom;
	}

	public void setBottom(CraftShapeRow bottom) {
		this.bottom = bottom;
	}
	
	public ShapedRecipe getShapedRecipe(ItemStack item, String name) throws Exception{
		if(item == null){
			throw new Exception("Item was null");
		}
		
		if(name != null && !name.trim().isEmpty() && item.getItemMeta() != null){
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(name);
			item.setItemMeta(itemMeta);
		}
		
		Map<Material,Character> materialMap = new HashMap<Material,Character>();
		int conflict = 0;
		
		String topRowString = "";
		String middleRowString = "";
		String bottomRowString = "";
		
		for(int i = 0; i < 3; i++){
			CraftShapeRow row = null;
			if(i == 0){
				row = top;
			}else if(i == 1){
				row = middle;
			}else if(i == 2){
				row = bottom;
			}
			
			if(row == null){
				continue;
			}
			
			String currentRowString = "";
			
			for(int j = 0; j < 3; j++){
				Material material = null;
				if(j == 0){
					material = row.getOne();
				}else if(j == 1){
					material = row.getTwo();
				}else if(j == 2){
					material = row.getThree();
				}
				
				if(material == null){
					currentRowString += " ";
					continue;
				}
				
				if(!materialMap.containsKey(material)){
					Character symbol = material.name().charAt(0);
					if(materialMap.values().contains(symbol)){
						
						if(conflict > 9 || conflict < 0){
							throw new Exception("conflict: " + conflict);
						}
						symbol = (char)conflict;
						conflict++;
					}
					materialMap.put(material, symbol);
				}
				
				currentRowString += materialMap.get(material);
				
			}
			
			if(i == 0){
				topRowString = currentRowString;
			}else if(i == 1){
				middleRowString = currentRowString;
			}else if(i == 2){
				bottomRowString = currentRowString;
			}
		}
		
		topRowString = topRowString.isEmpty() ? "   " : topRowString;
		middleRowString = middleRowString.isEmpty() ? "   " :middleRowString;
		bottomRowString = bottomRowString.isEmpty() ? "   " : bottomRowString;
		
		ShapedRecipe shapedRecipe = null;
		if(materialMap != null && !materialMap.isEmpty()){
			shapedRecipe = new ShapedRecipe(item);
			shapedRecipe.shape(topRowString, middleRowString, bottomRowString);
			for(Material material : materialMap.keySet()){
				shapedRecipe.setIngredient(materialMap.get(material), material);
			}
		}else{
			throw new Exception("materialMap was null or empty");
		}
		
		return shapedRecipe;
	}

}
