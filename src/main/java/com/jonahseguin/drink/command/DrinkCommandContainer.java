package com.jonahseguin.drink.command;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DrinkCommandContainer extends Command implements TabExecutor {

    private final DrinkCommandService commandService;
    private final String name;
    private final Set<String> aliases;
    private final Map<String, DrinkCommand> commands;
    private final DrinkCommand defaultCommand;
    private boolean overrideExistingCommands = true;
    private boolean defaultCommandIsHelp = false;

    public DrinkCommandContainer(DrinkCommandService commandService, String name, Set<String> aliases, Map<String, DrinkCommand> commands) {
        super(name, "", aliases.toArray(new String[0]));

        this.commandService = commandService;
        this.name = name;

        this.aliases = aliases;
        this.commands = commands;
        this.defaultCommand = calculateDefaultCommand();

        setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command.");
    }

    public final DrinkCommandContainer registerSub(@Nonnull Object handler) {
        return commandService.registerSub(this, handler);
    }

    public List<String> getCommandSuggestions(@Nonnull String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix cannot be null");
        final String p = prefix.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        for (DrinkCommand c : commands.values()) {
            for (String alias : c.getAllAliases()) {
                if (alias.length() > 0) {
                    if (p.length() == 0 || alias.toLowerCase().startsWith(p)) {
                        suggestions.add(alias);
                    }
                }
            }
        }
        return suggestions;
    }

    private DrinkCommand calculateDefaultCommand() {
        for (DrinkCommand dc : commands.values()) {
            if (dc.getName().length() == 0 || dc.getName().equals(DrinkCommandService.DEFAULT_KEY)) {
                // assume default!
                return dc;
            }
        }
        return null;
    }

    @Nullable
    public DrinkCommand get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(commandService.getCommandKey(name));
    }

    @Nullable
    public DrinkCommand getByKeyOrAlias(@Nonnull String key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        if (commands.containsKey(key)) {
            return commands.get(key);
        }
        for (DrinkCommand drinkCommand : commands.values()) {
            if (drinkCommand.getAliases().contains(key)) {
                return drinkCommand;
            }
        }
        return null;
    }

    /**
     * Gets a sub-command based on given arguments and also returns the new actual argument values
     * based on the arguments that were consumed for the sub-command key
     *
     * @param args the original arguments passed in
     * @return the DrinkCommand (if present, Nullable) and the new argument array
     */
    @Nullable
    public Map.Entry<DrinkCommand, String[]> getCommand(String[] args) {
        for (int i = (args.length - 1); i >= 0; i--) {
            String key = commandService.getCommandKey(StringUtils.join(Arrays.asList(Arrays.copyOfRange(args, 0, i + 1)), ' '));
            DrinkCommand drinkCommand = getByKeyOrAlias(key);
            if (drinkCommand != null) {
                return new AbstractMap.SimpleEntry<>(drinkCommand, Arrays.copyOfRange(args, i + 1, args.length));
            }
        }
        return new AbstractMap.SimpleEntry<>(getDefaultCommand(), args);
    }

    @Nullable
    public DrinkCommand getDefaultCommand() {
        return defaultCommand;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String permission = defaultCommand == null ? "" : defaultCommand.getPermission();
        if(!permission.isEmpty() && !sender.hasPermission(permission)){
            sender.sendMessage(new TextComponent(getPermissionMessage()));
            return;
        }
        try {
            Map.Entry<DrinkCommand, String[]> data = getCommand(args);
            if (data != null && data.getKey() != null) {
                if (args.length > 0) {
                    if (args[args.length - 1].equalsIgnoreCase("help") && !data.getKey().getName().equalsIgnoreCase("help")) {
                        // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                        commandService.getHelpService().sendHelpFor(sender, this);
                        return;
                    }
                }
                commandService.executeCommand(sender, data.getKey(), getName(), data.getValue());
            } else {
                if (args.length > 0) {
                    if (args[args.length - 1].equalsIgnoreCase("help")) {
                        // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                        commandService.getHelpService().sendHelpFor(sender, this);
                        return;
                    }
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown sub-command: " + args[0] + ".  Use '/" + getName() + " help' for available commands."));
                } else {
                    if (isDefaultCommandIsHelp()) {
                        commandService.getHelpService().sendHelpFor(sender, this);
                    } else {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please choose a sub-command.  Use '/" + getName() + " help' for available commands."));
                    }
                }
            }
        } catch (Exception ex) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An exception occurred while performing this command."));
            ex.printStackTrace();
        }
    }

    public DrinkCommandService getCommandService() {
        return commandService;
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<String> getDrinkAliases() {
        return aliases;
    }

    public Map<String, DrinkCommand> getCommands() {
        return commands;
    }

    public boolean isOverrideExistingCommands() {
        return overrideExistingCommands;
    }

    public DrinkCommandContainer setOverrideExistingCommands(boolean overrideExistingCommands) {
        this.overrideExistingCommands = overrideExistingCommands;
        return this;
    }

    public boolean isDefaultCommandIsHelp() {
        return defaultCommandIsHelp;
    }

    public DrinkCommandContainer setDefaultCommandIsHelp(boolean defaultCommandIsHelp) {
        this.defaultCommandIsHelp = defaultCommandIsHelp;
        return this;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Map.Entry<DrinkCommand, String[]> data = getCommand(args);
        if (data != null && data.getKey() != null) {
            String tabCompleting = "";
            int tabCompletingIndex = 0;
            if (data.getValue().length > 0) {
                tabCompleting = data.getValue()[data.getValue().length - 1];
                tabCompletingIndex = data.getValue().length - 1;
            }
            DrinkCommand drinkCommand = data.getKey();
            if (drinkCommand.getConsumingProviders().length > tabCompletingIndex) {
                List<String> s = drinkCommand.getConsumingProviders()[tabCompletingIndex].getSuggestions(tabCompleting);
                if (s != null) {
                    List<String> suggestions = new ArrayList<>(s);
                    if (args.length == 0 || args.length == 1) {
                        String tC = "";
                        if (args.length > 0) {
                            tC = args[args.length - 1];
                        }
                        suggestions.addAll(getCommandSuggestions(tC));
                    }
                    return suggestions;
                } else {
                    if (args.length == 0 || args.length == 1) {
                        String tC = "";
                        if (args.length > 0) {
                            tC = args[args.length - 1];
                        }
                        return getCommandSuggestions(tC);
                    }
                }
            } else {
                if (args.length == 0 || args.length == 1) {
                    String tC = "";
                    if (args.length > 0) {
                        tC = args[args.length - 1];
                    }
                    return getCommandSuggestions(tC);
                }
            }
        } else {
            if (args.length == 0 || args.length == 1) {
                String tC = "";
                if (args.length > 0) {
                    tC = args[args.length - 1];
                }
                return getCommandSuggestions(tC);
            }
        }

        return Collections.emptyList();
    }
}
