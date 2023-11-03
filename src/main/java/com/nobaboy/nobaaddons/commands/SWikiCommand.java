package com.nobaboy.nobaaddons.commands;

import com.nobaboy.nobaaddons.NobaAddons;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static net.minecraft.util.EnumChatFormatting.DARK_AQUA;

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
        return "/swiki <search query> - Allows you to search the Official Hypixel Wiki with any search query (if it exists)";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length < 1) {
            NobaAddons.addMessage(true, DARK_AQUA + "Missing search query.");
            return;
        }
        String instantWiki = WordUtils.capitalizeFully(String.join("_", args), '_');
        NobaAddons.addMessage(true, "Opening wiki.hypixel.net with search query '" + String.join(" ", args) + "'.");
        try {
            if(getWebsiteResponseCode(new URL("https://wiki.hypixel.net/" + instantWiki))) {
                Desktop.getDesktop().browse(URI.create("https://wiki.hypixel.net/" + instantWiki));
            } else {
                Desktop.getDesktop().browse(URI.create("https://wiki.hypixel.net/index.php?search=" + String.join("+", args) + "&title=Special%3ASearch&fulltext=search"));
            }
        } catch(Exception ignored) { }
    }
}
