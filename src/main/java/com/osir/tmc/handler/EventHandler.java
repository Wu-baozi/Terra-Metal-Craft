package com.osir.tmc.handler;

import java.util.Random;

import com.github.zi_jing.cuckoolib.util.NBTAdapter;
import com.osir.tmc.Main;
import com.osir.tmc.api.capability.CapabilityHeat;
import com.osir.tmc.api.capability.CapabilityLiquidContainer;
import com.osir.tmc.api.capability.CapabilityWork;
import com.osir.tmc.api.capability.IHeatable;
import com.osir.tmc.api.capability.ILiquidContainer;
import com.osir.tmc.api.capability.ModCapabilities;
import com.osir.tmc.api.container.ContainerListenerCapability;
import com.osir.tmc.api.heat.HeatMaterialList;
import com.osir.tmc.api.heat.MaterialStack;
import com.osir.tmc.api.recipe.ScalableRecipe;
import com.osir.tmc.api.util.CapabilityUtil;
import com.osir.tmc.handler.recipe.HeatRecipeHandler;
import com.osir.tmc.handler.recipe.OrePrefixRecipeHandler;
import com.osir.tmc.item.ItemMould;
import com.osir.tmc.item.MetaItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@EventBusSubscriber(modid = Main.MODID)
public class EventHandler {
	@SubscribeEvent
	public static void onStoneHarvestBlock(HarvestDropsEvent e) {
		if (e.getHarvester() != null) {
			EntityPlayer player = e.getHarvester();
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			if (MetaItems.grindedFlint.isItemEqual(stack)) {
				Block block = e.getState().getBlock();
				Random rand = new Random();
				if (block == Blocks.LEAVES || block == Blocks.LEAVES2) {
					if (rand.nextFloat() < 0.3) {
						e.getDrops().add(new ItemStack(Items.STICK));
					}
				}
				NBTTagCompound nbtStack = NBTAdapter.getItemStackCompound(stack);
				NBTTagCompound nbtDamage = NBTAdapter.getTagCompound(nbtStack, "tmc.damage", new NBTTagCompound());
				int maxDamage = NBTAdapter.getInteger(nbtDamage, "maxDamage", 16000);
				int damage = NBTAdapter.getInteger(nbtDamage, "damage", 0);
				if (damage + 100 >= maxDamage) {
					player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				} else {
					nbtDamage.setInteger("damage", damage + 100);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onStoneWork(RightClickBlock e) {
		if (e.getWorld().isRemote) {
			return;
		}
		if (e.getFace() != EnumFacing.UP) {
			return;
		}
		BlockPos pos = e.getPos();
		World world = e.getWorld();
		IBlockState state = world.getBlockState(pos);
		if (!(state.isFullBlock() && state.getMaterial() == Material.ROCK)) {
			return;
		}
		ItemStack stack = e.getItemStack();
		if (stack.getItem() != Items.FLINT) {
			return;
		}
		EntityPlayer player = e.getEntityPlayer();
		Vec3d vec = e.getHitVec();
		vec = vec.subtract(pos.getX(), pos.getY(), pos.getZ());
		stack.shrink(1);
		ItemStack result;
		if (vec.x >= 0.25 && vec.x <= 0.75 && vec.z >= 0.25 && vec.z <= 0.75) {
			result = MetaItems.grindedFlint.getItemStack();
		} else {
			result = MetaItems.chippedFlint.getItemStack();
		}
		player.inventory.addItemStackToInventory(result);
		if (!result.isEmpty()) {
			InventoryHelper.spawnItemStack(e.getWorld(), pos.getX(), pos.getY(), pos.getZ(), result);
		}
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		HeatRecipeHandler.register();
		OrePrefixRecipeHandler.register();
	}

	@SubscribeEvent
	public static void onAttachHeat(AttachCapabilitiesEvent<ItemStack> e) {
		ItemStack stack = e.getObject();
		if (stack.hasCapability(ModCapabilities.HEATABLE, null)) {
			return;
		}
		MaterialStack mat = HeatMaterialList.findMaterial(stack);
		ScalableRecipe recipe = HeatMaterialList.findRecipe(stack);
		if (mat != null && recipe != null) {
			e.addCapability(CapabilityHeat.KEY,
					new CapabilityHeat(mat.getMaterial(), mat.getAmount(), (int) recipe.getValue("temp")));
			e.addCapability(CapabilityWork.KEY, new CapabilityWork());
		}
	}

	@SubscribeEvent
	public static void onAttachLiquidContainer(AttachCapabilitiesEvent<ItemStack> e) {
		ItemStack stack = e.getObject();
		if (stack.hasCapability(ModCapabilities.LIQUID_CONTAINER, null)) {
			return;
		}
		if (stack.getItem() instanceof ItemMould) {
			e.addCapability(CapabilityLiquidContainer.KEY, new CapabilityLiquidContainer(144));
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent e) {
		if (e.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) e.player;
			player.inventoryContainer.addListener(new ContainerListenerCapability(player));
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent e) {
		if (e.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) e.player;
			player.inventoryContainer.addListener(new ContainerListenerCapability(player));
		}
	}

	@SubscribeEvent
	public static void onContainerOpen(Open e) {
		if (e.getEntityPlayer() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) e.getEntityPlayer();
			e.getContainer().addListener(new ContainerListenerCapability(player));
		}
	}

	@SubscribeEvent
	public static void onTempUpdate(LivingUpdateEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			InventoryPlayer inv = player.inventory;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.hasCapability(ModCapabilities.HEATABLE, null)) {
					IHeatable cap = stack.getCapability(ModCapabilities.HEATABLE, null);
					CapabilityUtil.heatExchange(cap, 20, 200);
				}
				if (stack.hasCapability(ModCapabilities.LIQUID_CONTAINER, null)) {
					ILiquidContainer liquid = stack.getCapability(ModCapabilities.LIQUID_CONTAINER, null);
					for (IHeatable cap : liquid.getMaterial()) {
						CapabilityUtil.heatExchange(cap, 20, 400);
					}
				}
			}
		}
	}
}