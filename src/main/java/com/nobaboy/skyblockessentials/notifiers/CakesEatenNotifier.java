package com.nobaboy.skyblockessentials.notifiers;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import com.nobaboy.skyblockessentials.config.SkyblockEssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CakesEatenNotifier {
    int cakesEaten = 0;

    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent e) {
        if(!SkyblockEssentialsConfig.cakesEatenNotifier) return;
        String message = e.message.getUnformattedText().toLowerCase();
        if(message.startsWith("yum! you gain")) {
            cakesEaten++;
            if (cakesEaten >= 12) {
                Minecraft.getMinecraft().thePlayer.playSound("note.pling", 1, 2.5f);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + EnumChatFormatting.BLUE + "All cakes eaten!"));
                cakesEaten = 0;
            }
        }
    }
}