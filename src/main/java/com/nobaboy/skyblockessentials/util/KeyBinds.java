package com.nobaboy.skyblockessentials.util;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBinds {
    public static KeyBinding KEY_PETS;
    public static KeyBinding KEY_WARDROBE;
    public static KeyBinding KEY_ENDER_CHEST;
    public static KeyBinding KEY_STORAGE;
    public static KeyBinding KEY_EQUIPMENT;

    public static void register() {
        KEY_PETS = new KeyBinding("key.pets.sbes", Keyboard.KEY_V, "key.categories.sbes");
        KEY_WARDROBE = new KeyBinding("key.wardrobe.sbes", Keyboard.KEY_LMENU, "key.categories.sbes");
        KEY_ENDER_CHEST = new KeyBinding("key.enderchest.sbes",  Keyboard.KEY_NONE, "key.categories.sbes");
        KEY_STORAGE = new KeyBinding("key.storage.sbes", Keyboard.KEY_NONE, "key.categories.sbes");
        KEY_EQUIPMENT = new KeyBinding("key.equipment.sbes", Keyboard.KEY_H, "key.categories.sbes");

        ClientRegistry.registerKeyBinding(KeyBinds.KEY_PETS);
        ClientRegistry.registerKeyBinding(KeyBinds.KEY_WARDROBE);
        ClientRegistry.registerKeyBinding(KeyBinds.KEY_ENDER_CHEST);
        ClientRegistry.registerKeyBinding(KeyBinds.KEY_STORAGE);
        ClientRegistry.registerKeyBinding(KeyBinds.KEY_EQUIPMENT);
    }
}