package com.nobaboy.nobaaddons;

import com.nobaboy.nobaaddons.commands.NobaCommand;
import com.nobaboy.nobaaddons.commands.SWikiCommand;
import com.nobaboy.nobaaddons.core.Config;
import com.nobaboy.nobaaddons.features.chatcommands.DMCommands;
import com.nobaboy.nobaaddons.features.chatcommands.GuildCommands;
import com.nobaboy.nobaaddons.features.chatcommands.PartyCommands;
import com.nobaboy.nobaaddons.features.dungeons.DungeonFeatures;
import com.nobaboy.nobaaddons.features.misc.DisableMouse;
import com.nobaboy.nobaaddons.features.notifiers.QOLNotifiers;
import com.nobaboy.nobaaddons.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

@Mod(modid = NobaAddons.MOD_ID, name = NobaAddons.MOD_NAME, version = NobaAddons.MOD_VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class NobaAddons {
    public static final String MOD_ID = "nobaaddons";
    public static final String MOD_NAME = "NobaAddons";
    public static final String MOD_VERSION = "0.8.4";
    public static final String MOD_PREFIX = BLUE + "NobaAddons " + DARK_BLUE + "> " + AQUA;
    public static final String PLAYER_IGN = Minecraft.getMinecraft().getSession().getUsername();

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static int ticks = 0;

    public static Config config = new Config();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START || !Utils.isOnHypixel()) return;
        if(ticks % 20 == 0) {
            if (mc.thePlayer != null) {
                Utils.checkForSkyblock();
                Utils.checkForDungeons();
            }
            ticks = 0;
        }
        ticks++;
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event){
        config.initialize();
        MinecraftForge.EVENT_BUS.register(this);
        // Commands
        MinecraftForge.EVENT_BUS.register(new PartyCommands());
        MinecraftForge.EVENT_BUS.register(new GuildCommands());
        MinecraftForge.EVENT_BUS.register(new DMCommands());
        // Dungeons
        MinecraftForge.EVENT_BUS.register(new DungeonFeatures());
        // Misc
        MinecraftForge.EVENT_BUS.register(new QOLNotifiers());
        keyBinds.forEach(ClientRegistry::registerKeyBinding);
    }

    @Mod.EventHandler
    public void loadComplete(final FMLLoadCompleteEvent event) {
        ClientCommandHandler chh = ClientCommandHandler.instance;
        chh.registerCommand(new NobaCommand());
        chh.registerCommand(new SWikiCommand());
    }

    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if(keyBinds.get(0).isPressed()) NobaAddons.sendCommand("pets");
        if(keyBinds.get(1).isPressed()) NobaAddons.sendCommand("wardrobe");
        if(keyBinds.get(2).isPressed()) NobaAddons.sendCommand("equipment");
        if(keyBinds.get(3).isPressed()) NobaAddons.sendCommand("enderchest");
        if(keyBinds.get(4).isPressed()) NobaAddons.sendCommand("storage");
        if(keyBinds.get(5).isPressed()) DisableMouse.onDisableMouse();
    }

    List<KeyBinding> keyBinds = Arrays.asList(
            new KeyBinding("Pets Menu", Keyboard.KEY_V, MOD_NAME),
            new KeyBinding("Wardrobe", Keyboard.KEY_LMENU, MOD_NAME),
            new KeyBinding("Equipment Menu", Keyboard.KEY_H, MOD_NAME),
            new KeyBinding("Enderchest", Keyboard.KEY_NONE, MOD_NAME),
            new KeyBinding("Storage Menu", Keyboard.KEY_NONE, MOD_NAME),
            new KeyBinding("Disable Mouse", Keyboard.KEY_J, MOD_NAME)
    );

    public static void sendCommand(String command) {
        command = (!NobaAddons.config.debugMode ? "/" : "") + command;
        mc.thePlayer.sendChatMessage(command);
    }

    public static void addMessage(boolean prefix, String message) {
        if(prefix) {
            mc.thePlayer.addChatMessage(new ChatComponentText(MOD_PREFIX + message));
        } else {
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }
}