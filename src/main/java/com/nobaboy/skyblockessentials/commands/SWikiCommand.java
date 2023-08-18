package com.nobaboy.skyblockessentials.commands;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import static net.minecraft.util.EnumChatFormatting.*;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SWikiCommand extends CommandBase {
    private boolean getWebsiteResponseCode(URL site) {
        try {
            HttpURLConnection conn = (HttpURLConnection) site.openConnection();
            conn.getContent();
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException tout) {
            return false;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "swiki";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: /swiki <search query>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length < 1) {
            sender.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + RED + "Missing search query."));
            return;
        }
        String instantWiki = WordUtils.capitalizeFully(String.join("_", args), '_');

        sender.addChatMessage(new ChatComponentText(SkyblockEssentials.MOD_PREFIX + GREEN + "Opening wiki.hypixel.net with search query '" + String.join(" ", args) + "'."));
        try {
            if(getWebsiteResponseCode(new URL("https://wiki.hypixel.net/" + instantWiki))) {
                Desktop.getDesktop().browse(URI.create("https://wiki.hypixel.net/" + instantWiki));
            } else {
                Desktop.getDesktop().browse(URI.create("https://wiki.hypixel.net/index.php?search=" + String.join("+", args) + "&title=Special%3ASearch&fulltext=search"));
            }
        } catch(Exception ignored) { }
    }
}
