package com.nobaboy.nobaaddons.features.misc;

import com.nobaboy.nobaaddons.NobaAddons;
import com.nobaboy.nobaaddons.util.Utils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.List;

public class PrimalFearPrompt {
    static String lastPhobiaCommand = "/spookysignpaper placeholder";
    static long lastPhobiaTime = 0;
    static boolean canOpen = false;

    /**
     * Referenced from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
     */
    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if(!NobaAddons.config.pressToAccept || !Utils.inSkyblock) return;
        String receivedMessage = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if(receivedMessage.startsWith("Click HERE to sign the")) {
            List<IChatComponent> listOfSiblings = event.message.getSiblings();
            for(IChatComponent sibling : listOfSiblings) {
                if(sibling.getUnformattedText().contains("HERE")) {
                    lastPhobiaCommand = sibling.getChatStyle().getChatClickEvent().getValue();
                    lastPhobiaTime = System.currentTimeMillis() / 1000;
                }
            }
            canOpen = true;
            NobaAddons.addMessage(true, "Open chat then click anywhere on screen to accept commitment phobia contract.");
        }
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
     */
    @SubscribeEvent
    public void onMouseInputPost(GuiScreenEvent.MouseInputEvent.Post event) {
        if(!NobaAddons.config.pressToAccept || !Utils.inSkyblock) return;
        if(canOpen && Mouse.isButtonDown(0) && event.gui instanceof GuiChat) {
            if(System.currentTimeMillis() / 1000 - lastPhobiaTime < 10) {
                NobaAddons.sendCommand(lastPhobiaCommand);
                canOpen = false;
            }
        }
    }

    public static void onKey() {
        if(!NobaAddons.config.pressToAccept || !Utils.inSkyblock || System.currentTimeMillis() / 1000 - lastPhobiaTime > 10) return;
        NobaAddons.sendCommand(lastPhobiaCommand);
        canOpen = false;
    }
}
