package com.nobaboy.skyblockessentials.commands;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import com.nobaboy.skyblockessentials.config.SkyblockEssentialsConfig;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Integer.parseInt;

public class PartyCommands {
    static int delay = 0;
    static boolean isWarping = false;
    static boolean cancel = false;

    final Pattern chatPattern = Pattern.compile("^Party > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): !(?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_]+)?");

    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if(!SkyblockEssentialsConfig.partyCommands) return;

        String receivedMessage = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
        Matcher chatMatcher = chatPattern.matcher(receivedMessage);
        if(!chatMatcher.find()) return;
        String command = chatMatcher.group("command");
        String argument = chatMatcher.group("argument");
        String sender = chatMatcher.group("username");

        switch(command.toLowerCase()) {
            case "help":
                if(SkyblockEssentials.PLAYER_IGN.equals(sender)) return; // Party members command
                SkyblockEssentials.sendCommand("pc SkyblockEssentials > !help, !warp [optional: time], !cancel, !info");
                break;
            case "ptme":
                if(SkyblockEssentials.PLAYER_IGN.equals(sender)) return; // Party members command
                SkyblockEssentials.sendCommand("p transfer " + sender);
                break;
            case "warp":
                if(SkyblockEssentials.PLAYER_IGN.equals(sender) || isWarping) return; // Party members command
                warpCommand(argument);
                break;
            case "cancel":
                if(SkyblockEssentials.PLAYER_IGN.equals(sender) || !isWarping) return; // Party members command
                cancel = true;
                break;
            case "info":
                if(SkyblockEssentials.PLAYER_IGN.equals(sender)) return; // Party members command
                SkyblockEssentials.sendCommand("pc " + SkyblockEssentials.MOD_NAME + ", Version: " + SkyblockEssentials.MOD_VERSION + ", made by nobaboy.");
                break;
            default:
                System.out.println("Unexpected value: " + command.toLowerCase());
        }
    }

    public void warpCommand(String time) {
        if(time == null) {
            SkyblockEssentials.sendCommand("p warp");
        } else if(!StringUtils.isNumeric(time)) {
            SkyblockEssentials.sendCommand("pc First argument can either be empty or numbers.");
        } else if(parseInt(time) > 15 || parseInt(time) < 3) {
            SkyblockEssentials.sendCommand("pc Warp delay has a max limit of 15 seconds and a lowest of 3.");
        } else {
            SkyblockEssentials.sendCommand("pc Warping in " + time + " (To cancel type '!cancel')");
            delay = parseInt(time);
            isWarping = true;
            check();
        }
    }

    private static class WarpAfterInterval extends Thread {
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            int secondsPassed = --delay;
            while(true) {
                try {
                    sleep(1000);
                } catch(InterruptedException ignored) {}
                if(cancel) {
                    SkyblockEssentials.sendCommand("pc Warp cancelled...");
                    cancel = false; isWarping = false;
                    break;
                }
                if(secondsPassed == 0) {
                    SkyblockEssentials.sendCommand("p warp");
                    isWarping = false;
                    break;
                }
                SkyblockEssentials.sendCommand("pc " + secondsPassed);
                --secondsPassed;
            }
        }
    }

    public void check() {
        WarpAfterInterval thread = new WarpAfterInterval();
        thread.setName("warp-after-delay");
        thread.start();
    }
}