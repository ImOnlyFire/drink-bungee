package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringProvider extends DrinkProvider<String> {

    public static final StringProvider INSTANCE = new StringProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return true;
    }

    @Nullable
    @Override
    public String defaultNullValue() {
        return null;
    }

    @Override
    public String provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return arg.get();
    }

    @Override
    public String argumentDescription() {
        return "string";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        final String finalPrefix = prefix.toLowerCase();
        return ProxyServer.getInstance().getPlayers().stream().map(CommandSender::getName).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
}
