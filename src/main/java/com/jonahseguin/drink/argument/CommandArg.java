package com.jonahseguin.drink.argument;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandArg {

    private final CommandSender sender;
    private final String value;
    private final String label;
    private final CommandArgs args;

    public CommandArg(CommandSender sender, String value, CommandArgs args) {
        this.sender = sender;
        this.value = value;
        this.label = args.getLabel();
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String get() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSenderPlayer() {
        return sender instanceof ProxiedPlayer;
    }

    public ProxiedPlayer getSenderAsPlayer() {
        return (ProxiedPlayer) sender;
    }

    public CommandArgs getArgs() {
        return args;
    }
}
