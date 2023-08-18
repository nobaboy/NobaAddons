package com.nobaboy.skyblockessentials.commands;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import com.nobaboy.skyblockessentials.config.SkyblockEssentialsConfig;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildCommands {
    boolean isWarpingOut = false;
    static boolean playedJoined = false;
    static String player;

    Pattern chatPattern = Pattern.compile("^Guild > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+)(?<grank> \\[[A-z0-9 ]+])?: !(?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_]+)?");

    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if (!SkyblockEssentialsConfig.guildCommands) return;
        String receivedMessage = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());

        if(isWarpingOut) {
            if (receivedMessage.toLowerCase().contains(player + " joined the party")) {
                playedJoined = true;
            }
        }

        Matcher chatMatcher = chatPattern.matcher(receivedMessage);
        if(!chatMatcher.find()) return;
        String command = chatMatcher.group("command");
        String argument = chatMatcher.group("argument");
        String sender = chatMatcher.group("username");

        switch (command.toLowerCase()) {
            case "help":
                if (SkyblockEssentials.PLAYER_IGN.equals(sender)) return; // Guild members command
                SkyblockEssentials.sendCommand("gc SkyblockEssentials > !help, !warpout");
                break;
            case "warpout":
                if (SkyblockEssentials.PLAYER_IGN.equals(sender)) return; // Guild members command
                warpOutCommand(argument);
                break;
            default:
                System.out.println("Unexpected value: " + command.toLowerCase());
        }
    }

    public void warpOutCommand(String username) {
        if(isWarpingOut) {
            SkyblockEssentials.sendCommand("gc Warp out is on cooldown, try again later!");
            return;
        }
        if(username == null) {
            SkyblockEssentials.sendCommand("gc Please provide a username.");
            return;
        }
        SkyblockEssentials.sendCommand("p " + username);
        player = username;
        isWarpingOut = true;
        check();
    }

    @SubscribeEvent
    public void onPlayerJoinParty(final ClientChatReceivedEvent event) {
        if(!SkyblockEssentialsConfig.guildCommands) return;

        String receivedMessage = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());

        if(isWarpingOut) {
            if (receivedMessage.toLowerCase().contains(player + " joined the party")) {
                playedJoined = true;
            }
        }
    }

    private static class CheckIntervalThread extends Thread {
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            int secondsPassed = 0;
            while(true) {
                if(secondsPassed++ >= 60) {
                    if(!playedJoined) {
                        SkyblockEssentials.sendCommand("gc Warp out failed, " + player + " did not join party.");
                    }
                    break;
                }
                if(playedJoined) {
                    SkyblockEssentials.sendCommand("p warp");
                    try {
                        sleep(1000);
                    } catch (InterruptedException ignored) {}
                    SkyblockEssentials.sendCommand("p disband");
                    try {
                        sleep(1000);
                    } catch (InterruptedException ignored) {}
                    SkyblockEssentials.sendCommand("gc Warp out successful.");
                    playedJoined = false;
                    break;
                }
                try {
                    sleep(1000);
                } catch(InterruptedException ignored) {}
            }
        }
    }

    public void check() {
        CheckIntervalThread thread = new CheckIntervalThread();
        thread.setName("warp-out-" + player);
        thread.start();
    }
}
