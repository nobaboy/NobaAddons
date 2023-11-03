package com.nobaboy.nobaaddons.features.dungeons;

import com.nobaboy.nobaaddons.NobaAddons;
import com.nobaboy.nobaaddons.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonFeatures {
    static boolean announcing = false;
    static AnnounceCampWarning announceCampWarning = new AnnounceCampWarning();

    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if(!NobaAddons.config.bloodCampAfterTime || !Utils.inDungeons) return;
        String receivedMessage = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if(announcing) return;
        if(receivedMessage.startsWith("A shiver runs down your spine...") || receivedMessage.startsWith("The BLOOD DOOR has been opened!")) {
            announcing = true;
            startThread();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        announceCampWarning.interrupt();
    }

    public static class AnnounceCampWarning extends Thread {
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            while(announcing) {
                announcing = false;
                try {
                    sleep(1000L * NobaAddons.config.timeTilBloodCamp);
                } catch(InterruptedException ignored) { break; }
                NobaAddons.addMessage(true, "Go camp blood!");
                for(int i = 0; i < 10; i++) {
                    Minecraft.getMinecraft().thePlayer.playSound("note.pling", 1, 2.0f);
                    try {
                        sleep(100);
                    } catch(InterruptedException ignored) { break; }
                }
            }
        }
    }

    public void startThread() {
        AnnounceCampWarning thread = new AnnounceCampWarning();
        thread.setName("blood-camp-warning");
        thread.start();
    }
}
