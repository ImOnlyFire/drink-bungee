package com.jonahseguin.drink.exception;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandExitMessage extends Exception {

    public CommandExitMessage(String message) {
        super(message);
    }

    public void print(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + getMessage()));
    }
}
