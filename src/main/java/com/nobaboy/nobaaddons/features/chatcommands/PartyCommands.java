package com.nobaboy.nobaaddons.features.chatcommands;

import com.google.common.collect.Lists;
import com.nobaboy.nobaaddons.NobaAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class PartyCommands {
    List<String> commands = Lists.newArrayList("help", "ptme", "transfer", "allinvite", "allinv", "warp", "cancel", "coords");
    static int cooldown = 0;

    // Warp Command
    static int delay = 0;
    static boolean isWarping = false;
    static boolean cancel = false;

    // Warp in User
    static boolean isWarpingUser = false;
    static boolean playedJoined = false;
    static String player;

    final Pattern chatPattern = Pattern.compile("^Party > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): !(?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_]+)?");

    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if(!NobaAddons.config.partyCommands) return;
        String receivedMessage = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if(isWarpingUser) {
            if(receivedMessage.toLowerCase().contains(player + " is already in the party.")) {
                isWarpingUser = false;
                return;
            } else if(receivedMessage.toLowerCase().contains(player + " joined the party.")) {
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
        startPartyCooldown();
        switch(command.toLowerCase()) {
            case "help":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !NobaAddons.config.partyHelpCommand) return; // Party members command
                NobaAddons.sendCommand("pc NobaAddons > !help, !ptme [Alias: !transfer], !allinvite [Alias: !allinv], !warp [optional: seconds], !cancel, !warpme (command for DMs) !coords, !info");
                break;
            case "ptme":
            case "transfer":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !NobaAddons.config.transferCommand) return; // Party members command
                NobaAddons.sendCommand("p transfer " + sender);
                break;
            case "allinvite":
            case "allinv":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !NobaAddons.config.allInviteCommand) return; // Party members command
                NobaAddons.sendCommand("p settings allinvite");
                break;
            case "warp":
                if(NobaAddons.PLAYER_IGN.equals(sender) || isWarping || !NobaAddons.config.warpCommand) return; // Party members command
                warpCommand(argument);
                break;
            case "cancel":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !isWarping || !NobaAddons.config.warpCommand) return; // Party members command
                cancel = true;
                break;
            case "coords":
                if(NobaAddons.PLAYER_IGN.equals(sender) || !NobaAddons.config.coordsCommand) return; // Party members command
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                NobaAddons.sendCommand("pc x: " + (int) player.posX + ", y: " + (int) player.posY + ", z: " + (int) player.posZ);
                break;
            default:
                System.out.println("Unexpected value: " + command.toLowerCase());
        }
    }

    public void warpCommand(String time) {
        if(time == null) {
            NobaAddons.sendCommand("p warp");
        } else if(!org.apache.commons.lang3.StringUtils.isNumeric(time)) {
            NobaAddons.sendCommand("pc First argument can either be empty or numbers.");
        } else if(parseInt(time) > 15 || parseInt(time) < 3) {
            NobaAddons.sendCommand("pc Warp delay has a max limit of 15 seconds and a lowest of 3.");
        } else {
            NobaAddons.sendCommand("pc Warping in " + time + " (To cancel type '!cancel')");
            delay = parseInt(time);
            isWarping = true;
            startTimedWarp();
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
                    NobaAddons.sendCommand("pc Warp cancelled...");
                    cancel = false; isWarping = false;
                    break;
                }
                NobaAddons.sendCommand("pc " + secondsPassed);
                if(--secondsPassed == 0) {
                    NobaAddons.sendCommand("p warp");
                    isWarping = false;
                    break;
                }
            }
        }
    }

    public void startTimedWarp() {
        WarpAfterInterval thread = new WarpAfterInterval();
        thread.setName("warp-after-delay");
        thread.start();
    }

    private static class PartyCommandsCooldown extends Thread {
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

    public void startPartyCooldown() {
        PartyCommandsCooldown thread = new PartyCommandsCooldown();
        thread.setName("party-command-cooldown");
        thread.start();
    }
}