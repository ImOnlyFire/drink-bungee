package com.jonahseguin.drink.command;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;

@Getter
@Setter
public class DrinkAuthorizer {

    private String noPermissionMessage = ChatColor.RED + "You do not have permission to perform this command.";

    public boolean isAuthorized(@Nonnull CommandSender sender, @Nonnull DrinkCommand command) {
        if (command.getPermission() != null && command.getPermission().length() > 0) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(TextComponent.fromLegacyText(noPermissionMessage));
                return false;
            }
        }
        return true;
    }

}
