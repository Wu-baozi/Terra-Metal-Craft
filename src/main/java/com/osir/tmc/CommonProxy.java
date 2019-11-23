package com.osir.tmc;

import com.osir.tmc.api.TMCLog;
import com.osir.tmc.handler.CapabilityHandler;
import com.osir.tmc.handler.GuiHandler;
import com.osir.tmc.handler.NetworkHandler;
import com.osir.tmc.handler.TEHandler;
import com.osir.tmc.handler.recipe.RecipeHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {
		TMCLog.init(e.getModLog());
		CreativeTabList.register();
		TEHandler.register();
		CapabilityHandler.register();
		NetworkHandler.register();
	}

	public void init(FMLInitializationEvent e) {
		new GuiHandler();
		RecipeHandler.register();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}

	public IThreadListener getThreadListener(MessageContext context) {
		if (context.side.isServer()) {
			return context.getServerHandler().player.getServer();
		}
		throw new RuntimeException("Tried to get the client side IThreadListener from the server side");
	}

	public EntityPlayer getPlayer(MessageContext ctx) {
		if (ctx.side.isServer()) {
			return ctx.getServerHandler().player;
		}
		throw new RuntimeException("Tried to get the client side player from the server side");
	}
}