package com.nobaboy.skyblockessentials.util;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if(KeyBinds.KEY_PETS.isPressed()) {
            SkyblockEssentials.sendCommand("pets");
        }
        if(KeyBinds.KEY_WARDROBE.isPressed()) {
            SkyblockEssentials.sendCommand("wardrobe");
        }
        if(KeyBinds.KEY_ENDER_CHEST.isPressed()) {
            SkyblockEssentials.sendCommand("enderchest");
        }
        if(KeyBinds.KEY_STORAGE.isPressed()) {
            SkyblockEssentials.sendCommand("storage");
        }
        if(KeyBinds.KEY_EQUIPMENT.isPressed()) {
            SkyblockEssentials.sendCommand("equipment");
        }
    }
}