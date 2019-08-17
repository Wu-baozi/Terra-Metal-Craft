package com.osir.tmc.api.metal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.osir.tmc.api.heat.HeatMaterial;

import net.minecraft.item.Item;

public class MetalRegistry {
	private static Map<String, Metal> metalList = new HashMap<String, Metal>();

	public static void addMetal(Metal metal) {
		String name = metal.getName();
		if (metalList.containsKey(name)) {
			return;
		}
		metalList.put(name, metal);
	}

	public static Metal getMetal(Item item) {
		Iterator<Metal> iterator = metalList.values().iterator();
		Metal metal;
		while (iterator.hasNext()) {
			metal = iterator.next();
			if (metal.getIngot() == item) {
				return metal;
			}
		}
		return null;
	}

	public static Metal getMetal(HeatMaterial material) {
		Iterator<Metal> iterator = metalList.values().iterator();
		Metal metal;
		while (iterator.hasNext()) {
			metal = iterator.next();
			if (metal.getMaterial() == material) {
				return metal;
			}
		}
		return null;
	}

	public static Metal getMetal(String name) {
		if (metalList.containsKey(name)) {
			return metalList.get(name);
		}
		return null;
	}
}