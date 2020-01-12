package com.osir.tmc.handler;

import java.util.List;

import com.osir.tmc.Main;
import com.osir.tmc.api.capability.CapabilityList;
import com.osir.tmc.api.capability.IHeatable;
import com.osir.tmc.api.capability.ILiquidContainer;
import com.osir.tmc.api.util.DividedInfoBuilder;
import com.osir.tmc.api.util.InfoBuf;
import com.osir.tmc.block.BlockAnvil;
import com.osir.tmc.block.BlockOriginalForge;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = Main.MODID)
@SideOnly(Side.CLIENT)
public class EventHandlerClient {
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelColor(ColorHandlerEvent.Item e) {
		ItemColors itemColors = e.getItemColors();
		BlockColors blockColors = e.getBlockColors();
		itemColors.registerItemColorHandler((stack, idx) -> {
			if (stack.getItem() instanceof ItemBlock) {
				Block block = ((ItemBlock) stack.getItem()).getBlock();
				if (block instanceof BlockAnvil) {
					return ((BlockAnvil) block).getAnvilMaterial().getColor();
				}
			}
			return 0xffffff;
		}, BlockHandler.ANVIL);
		blockColors.registerBlockColorHandler((state, world, pos, idx) -> {
			Block block = state.getBlock();
			if (block instanceof BlockAnvil) {
				return ((BlockAnvil) block).getAnvilMaterial().getColor();
			}
			return 0xffffff;
		}, BlockHandler.ANVIL);
		itemColors.registerItemColorHandler((stack, idx) -> {
			return 0x202020;
		}, ItemHandler.ITEM_ORIGINAL_FORGE);
		blockColors.registerBlockColorHandler((state, world, pos, idx) -> {
			Block block = state.getBlock();
			if (block instanceof BlockOriginalForge && idx == 1) {
				return state.getValue(BlockOriginalForge.BURN) ? 0x820a0a : 0x202020;
			}
			return 0xffffff;
		}, BlockHandler.ORIGINAL_FORGE);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onHeatableItemTooltip(ItemTooltipEvent e) {
		ItemStack stack = e.getItemStack();
		if (!stack.hasCapability(CapabilityList.HEATABLE, null)) {
			return;
		}
		IHeatable cap = stack.getCapability(CapabilityList.HEATABLE, null);
		List<String> tooltip = e.getToolTip();
		DividedInfoBuilder builder = new DividedInfoBuilder();
		builder.addInfo(
				new InfoBuf("m", cap.getMaterial().getMeltTemp(), TextFormatting.RED, "K", TextFormatting.GREEN));
		builder.addInfo(new InfoBuf("c", cap.getMaterial().getSpecificHeat(), TextFormatting.AQUA, "J/(L��K)",
				TextFormatting.GREEN).setAccuracy(2));
		tooltip.add(builder.build());
		// IHeatable cap = stack.getCapability(CapabilityList.HEATABLE, null);
		// List tooltip = e.getToolTip();
		// long time = 0;
		// if (e.getEntityPlayer() != null && e.getEntityPlayer().getEntityWorld() !=
		// null) {
		// time = e.getEntityPlayer().getEntityWorld().getTotalWorldTime();
		// }
		// tooltip.add(TextFormatting.AQUA + I18n.format("item.heatable.tip.title"));
		// tooltip.add(TextFormatting.AQUA + "--" +
		// I18n.format("item.heatable.tip.heating"));
		// ScalableRecipe recipe = (ScalableRecipe) ModRecipeMap.MAP_HEAT.findRecipe(0,
		// Arrays.asList(stack),
		// new ArrayList(), 0);
		// // if (recipe != null && recipe.getOutputs().isEmpty()) {
		// // tooltip.add(TextFormatting.AQUA + "--" +
		// // I18n.format("item.heatable.tip.melting"));
		// // } else {
		// // tooltip.add(
		// // TextFormatting.AQUA + "--" + I18n.format("item.heatable.tip.making") + " "
		// +
		// // TextFormatting.YELLOW
		// // + I18n.format(recipe.getOutputs().get(0).getItem().getUnlocalizedName() +
		// // ".name"));
		// // }
		// tooltip.add(TextFormatting.BLUE +
		// I18n.format("item.heatable.material.meltPoint") + " " + TextFormatting.GOLD
		// + cap.getMaterial().getMeltTemp() + TextFormatting.GREEN +
		// I18n.format("item.unit.temperature"));
		// tooltip.add(TextFormatting.BLUE +
		// I18n.format("item.heatable.material.specificHeat") + " " +
		// TextFormatting.GOLD
		// + cap.getMaterial().getSpecificHeat() + TextFormatting.GREEN +
		// I18n.format("item.unit.specificHeat"));
		// tooltip.add(TextFormatting.BLUE + I18n.format("item.heatable.state.volume") +
		// " " + TextFormatting.GOLD
		// + cap.getUnit() + TextFormatting.GREEN + I18n.format("item.unit.volume"));
		// int temp = cap.getTemp();
		// tooltip.add(TextFormatting.BLUE +
		// I18n.format("item.heatable.state.temperature") + " " + TextFormatting.GOLD
		// + temp + TextFormatting.GREEN + I18n.format("item.unit.temperature"));
		// if (cap.getProgress() > 0) {
		// tooltip.add(TextFormatting.WHITE +
		// I18n.format("item.heatable.state.melting"));
		// } else {
		// String info = "";
		// if (cap.isDanger()) {
		// info += (time % 10 < 5 ? TextFormatting.RED : TextFormatting.WHITE)
		// + I18n.format("item.heatable.state.danger") + TextFormatting.WHITE + " | ";
		// }
		// if (cap.isWeldable()) {
		// info += TextFormatting.WHITE + I18n.format("item.heatable.state.weldable") +
		// " | ";
		// }
		// if (cap.isWorkable()) {
		// info += TextFormatting.WHITE + I18n.format("item.heatable.state.workable");
		// }
		// if (!info.equals("")) {
		// tooltip.add(info);
		// }
		// }
		// if (cap.getTemp() != 20) {
		// tooltip.add(cap.getColor());
		// }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onLiquidContainerTooltip(ItemTooltipEvent e) {
		ItemStack stack = e.getItemStack();
		if (!stack.hasCapability(CapabilityList.LIQUID_CONTAINER, null)) {
			return;
		}
		ILiquidContainer cap = stack.getCapability(CapabilityList.LIQUID_CONTAINER, null);
		List tooltip = e.getToolTip();
		tooltip.add(TextFormatting.BLUE + I18n.format("item.liquidContainer.state.capacity") + " " + TextFormatting.GOLD
				+ cap.getCapacity() + TextFormatting.GREEN + I18n.format("item.unit.volume"));
	}
}