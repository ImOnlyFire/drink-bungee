package com.jonahseguin.drink;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.command.DrinkCommandService;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This is the main class of Drink
 * Drink can be shaded or used as a plugin
 * This class provides the plugin functionality
 * As well, this class can be used to get an instance of a
 * {@link CommandService} for your plugin to register commands via.
 */
public class Drink extends Plugin {

    private static final ConcurrentMap<String, CommandService> services = new ConcurrentHashMap<>();

    /**
     * Get a {@link CommandService} instance to register commands via
     * - JavaPlugin specific (one per plugin instance)
     *
     * @param plugin {@link Nonnull} your {@link Plugin} instance
     * @return The {@link CommandService} instance
     */
    public static CommandService get(@Nonnull Plugin plugin) {
        Preconditions.checkNotNull(plugin, "JavaPlugin cannot be null");
        return services.computeIfAbsent(plugin.getDescription().getName(), name -> new DrinkCommandService(plugin));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
