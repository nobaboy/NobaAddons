package com.nobaboy.nobaaddons.commands;

import com.google.common.collect.Lists;
import com.nobaboy.nobaaddons.NobaAddons;
import gg.essential.api.utils.GuiUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class NobaCommand extends CommandBase {
    final String LINE = GRAY + "-------------------------------";

    public String getHelpMessage(String type) {
        HashMap<String, String> helpMessage = new HashMap<>();
        switch(type.toLowerCase()) {
            case "dmcommands":
                helpMessage.put("!warpme", "Warp user to your lobby.");
                helpMessage.put("!partyme or !pme", "Invite user to party.");
                break;
            case "partycommands":
                helpMessage.put("!help", "Sends all usable commands.");
                helpMessage.put("!ptme or !transfer", "Transfer party to the player who ran the command.");
                helpMessage.put("!allinvite or !allinv", "Turns on all invite party setting.");
                helpMessage.put("!warp [optional: seconds]", "Requests party warp with an optional warp delay.");
                helpMessage.put("!cancel", "Stop the current delayed warp.");
                helpMessage.put("!warpme", "A private message command that warps said user then kicks.");
                helpMessage.put("!coords", "Sends current location of user.");
                break;
            case "guildcommands":
                helpMessage.put("!help", "Sends all usable commands.");
                helpMessage.put("!warpout [username]", "Warp out a player.");
                break;
        }
        StringBuilder out = new StringBuilder(LINE + "\n");
        for (String name : helpMessage.keySet()) {
            out.append(BLUE).append(name).append(DARK_BLUE).append(" > ").append(AQUA).append(helpMessage.get(name)).append("\n");
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
        return "nobaaddons";
    }

    @Override
    public List<String> getCommandAliases() {
        return Lists.newArrayList("noba");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/noba <dmcommands, partycommands, guildcommands> - Shows the usable commands for dm, party and guild chat.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            GuiUtil.open(NobaAddons.config.gui());
            return;
        }
        switch(args[0].toLowerCase()) {
            case "dmcommands":
            case "guildcommands":
            case "partycommands":
                NobaAddons.addMessage(false, getHelpMessage(args[0]));
                break;
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "dmcommands", "partycommands", "guildcommands") : null;
    }
}