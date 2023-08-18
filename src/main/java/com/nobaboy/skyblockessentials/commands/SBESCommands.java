package com.nobaboy.skyblockessentials.commands;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import gg.essential.api.utils.GuiUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import static net.minecraft.util.EnumChatFormatting.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SBESCommands extends CommandBase {
    final String LINE = GRAY + "-------------------------------";

    public String partyHelpMessage() {
        HashMap<String, String> partyCommands = new HashMap<>();
        partyCommands.put("!help", "Sends all usable commands.");
        partyCommands.put("!warp [optional: time]", "Requests party warp with an optional warp delay.");
        partyCommands.put("!cancel", "Stop the current delayed warp.");
        partyCommands.put("!info", "Sends info about SkyblockEssentials.");
        StringBuilder out = new StringBuilder(LINE + "\n");
        for (String name : partyCommands.keySet()) {
            out.append(BLUE).append(name).append(DARK_GRAY).append(" > ").append(YELLOW).append(partyCommands.get(name)).append("\n");
        }
        out.append(LINE);
        return out.toString();
    }

    public String guildHelpMessage() {
        HashMap<String, String> guildCommands = new HashMap<>();
        guildCommands.put("!help", "Sends all usable commands.");
        guildCommands.put("!warpout [username]", "Warp out a player.");
        StringBuilder out = new StringBuilder(LINE + "\n");
        for (String name : guildCommands.keySet()) {
            out.append(BLUE).append(name).append(DARK_GRAY).append(" > ").append(YELLOW).append(guildCommands.get(name)).append("\n");
        }
        out.append(LINE);
        return out.toString();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "sbes";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: /sbes <partycommands, guildcommands>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            GuiUtil.open(SkyblockEssentials.instance.getSkyblockEssentialsConfig().gui());
            return;
        }
        switch(args[0].toLowerCase()) {
            case "partycommands":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + RED + "Missing arguments: help"));
                    return;
                }
                if (args[1].equalsIgnoreCase("help")) {
                    sender.addChatMessage(new ChatComponentText(partyHelpMessage()));
                } else {
                    sender.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + RED + "Unknown argument, Usage: /sbes partycommands help"));
                }
                break;
            case "guildcommands":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + RED + "Missing arguments: help"));
                    return;
                }
                if (args[1].equalsIgnoreCase("help")) {
                    sender.addChatMessage(new ChatComponentText(guildHelpMessage()));
                } else {
                    sender.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + RED + "Unknown argument, Usage: /sbes guildcommands help"));
                }
                break;
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        switch(args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "partycommands", "guildcommands");
            case 2:
                if (args[0].equalsIgnoreCase("partycommands") || args[0].equalsIgnoreCase("guildcommands")) {
                    return getListOfStringsMatchingLastWord(args, "help");
                }
        }
        return Collections.emptyList();
    }
}