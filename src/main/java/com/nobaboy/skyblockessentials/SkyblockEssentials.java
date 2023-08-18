package com.nobaboy.skyblockessentials;

import com.nobaboy.skyblockessentials.commands.GuildCommands;
import com.nobaboy.skyblockessentials.commands.PartyCommands;
import com.nobaboy.skyblockessentials.commands.SBESCommands;
import com.nobaboy.skyblockessentials.commands.SWikiCommand;
import com.nobaboy.skyblockessentials.config.SkyblockEssentialsConfig;
import com.nobaboy.skyblockessentials.notifiers.CakesEatenNotifier;
import com.nobaboy.skyblockessentials.util.KeyBinds;
import com.nobaboy.skyblockessentials.util.KeyInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.RESET;

@Mod(modid = "skyblockessentials", name = "SkyblockEssentials", version = "0.7.1", acceptedMinecraftVersions = "[1.8.9]")
public class SkyblockEssentials {
    public static final String MOD_ID = "skyblockessentials";
    public static final String MOD_NAME = "SkyblockEssentials";
    public static final String MOD_VERSION = "0.7.1";
    public static final String MOD_PREFIX = GRAY + "[SBES] " + RESET;
    public static final String PLAYER_IGN = Minecraft.getMinecraft().getSession().getUsername();

    @Mod.Instance("skyblockessentials")
    public static SkyblockEssentials instance;

    private SkyblockEssentialsConfig skyblockEssentialsConfig;

    public SkyblockEssentialsConfig getSkyblockEssentialsConfig() {
        return skyblockEssentialsConfig;
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SBESCommands());
        ClientCommandHandler.instance.registerCommand(new SWikiCommand());
        skyblockEssentialsConfig = SkyblockEssentialsConfig.INSTANCE;
        MinecraftForge.EVENT_BUS.register(new CakesEatenNotifier());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new PartyCommands());
        MinecraftForge.EVENT_BUS.register(new GuildCommands());
        KeyBinds.register();
    }

    public static void sendCommand(String command) {
        command = (!SkyblockEssentialsConfig.debugMode ? "/" : "") + command;
        Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
    }
}