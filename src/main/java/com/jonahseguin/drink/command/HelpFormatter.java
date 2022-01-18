package com.jonahseguin.drink.command;

import net.md_5.bungee.api.CommandSender;

public interface HelpFormatter {

    void sendHelpFor(CommandSender sender, DrinkCommandContainer container);
}
