package com.jonahseguin.drink.command;

import com.jonahseguin.drink.exception.CommandRegistrationException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Map;

public class DrinkBungeeRegistry {

    private final Plugin plugin;

    public DrinkBungeeRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(@Nonnull DrinkCommandContainer container, boolean unregisterExisting) throws CommandRegistrationException {
        if (unregisterExisting) {
            for (Map.Entry<String, Command> command : ProxyServer.getInstance().getPluginManager().getCommands()) {
                if (command.getKey().contains(container.getName().toLowerCase())) {
                    ProxyServer.getInstance().getPluginManager().unregisterCommand(command.getValue());
                }
                // TODO FIND A WAY TO DO THIS
//                for (String s : container.getDrinkAliases()) {
//                    if (command.getKey().contains(s.toLowerCase())) {
//                        knownCommands.remove(s).unregister(commandMap);
//                    }
//                }
            }
        }

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, container);
    }

}
