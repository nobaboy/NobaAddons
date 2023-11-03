package com.nobaboy.nobaaddons.features.chatcommands;

import com.google.common.collect.Lists;
import com.nobaboy.nobaaddons.NobaAddons;
import com.nobaboy.nobaaddons.util.Utils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildCommands {
    List<String> commands = Lists.newArrayList("help", "warpout");
    static int cooldown = 0;

    static boolean isWarpingOut = false;
    static boolean playedJoined = false;
    static String player;

    Pattern chatPattern = Pattern.compile("^Guild > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+)(?<grank> \\[[A-z0-9 ]+])?: !(?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_]+)?");

    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if (!NobaAddons.config.guildCommands) return;
        String receivedMessage = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());

        if(isWarpingOut) {
            if(receivedMessage.toLowerCase().contains(player + " joined the party")) {
                playedJoined = true;
            }
        }

        Matcher chatMatcher = chatPattern.matcher(receivedMessage);
        if(!chatMatcher.find()) return;
        String command = chatMatcher.group("command");
        String argument = chatMatcher.group("argument");
        String sender = chatMatcher.group("username");

        if(!commands.contains(command.toLowerCase()) || cooldown > 0) return;
        cooldown = 5;
        startGuildCooldown();
        switch (command.toLowerCase()) {
            case "help":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !NobaAddons.config.guildHelpCommand) return; // Guild members command
                NobaAddons.sendCommand("gc NobaAddons > !help, !warpout");
                break;
            case "warpout":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !NobaAddons.config.warpOutCommand) return; // Guild members command
                warpOutCommand(argument);
                break;
            default:
                System.out.println("Unexpected value: " + command.toLowerCase());
        }
    }

    public void warpOutCommand(String username) {
        if(isWarpingOut) {
            NobaAddons.sendCommand("gc Warp out is on cooldown, try again later!");
        } else if(username == null) {
            NobaAddons.sendCommand("gc Please provide a username.");
        } else {
            NobaAddons.sendCommand("p " + username);
            player = username;
            isWarpingOut = true;
            warpOutPlayer();
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
                        NobaAddons.sendCommand("gc Warp out failed, " + player + " did not join party.");
                        isWarpingOut = false;
                    }
                    break;
                }
                if(playedJoined) {
                    NobaAddons.sendCommand("p warp");
                    try {
                        sleep(1000);
                    } catch (InterruptedException ignored) {}
                    NobaAddons.sendCommand("p disband");
                    try {
                        sleep(1000);
                    } catch (InterruptedException ignored) {}
                    NobaAddons.sendCommand("gc Warp out successful.");
                    playedJoined = false;
                    isWarpingOut = false;
                    break;
                }
                try {
                    sleep(1000);
                } catch(InterruptedException ignored) {}
            }
        }
    }

    public void warpOutPlayer() {
        CheckIntervalThread thread = new CheckIntervalThread();
        thread.setName("warp-out-" + player);
        thread.start();
    }

    private static class GuildCommandsCooldown extends Thread {
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            do {
                try {
                    sleep(1000);
                } catch (InterruptedException ignored) {
                }
            } while (--cooldown != 0);
        }
    }

    public void startGuildCooldown() {
        GuildCommandsCooldown thread = new GuildCommandsCooldown();
        thread.setName("guild-command-cooldown");
        thread.start();
    }
}
