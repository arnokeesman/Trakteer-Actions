package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.model.OperationMode;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ModeSetCommand implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String value = StringArgumentType.getString(context, "mode");
        if (Arrays.stream(OperationMode.values()).map(Enum::name).noneMatch(m -> m.equals(value))) {
            context.getSource().sendError(Text.of("Invalid mode!"));
            return 0;
        }

        OperationMode mode = OperationMode.valueOf(value);
        try {
            TrakteerFunctions.CONFIG.setMode(mode);
        } catch (IOException ioe) {
            if (ioe instanceof ConnectException) {
                context.getSource().sendError(Text.of("Unable to connect to this server, disabling interval. Try again later?"));
                try { TrakteerFunctions.CONFIG.setInterval(0); } catch (IOException ignored) {}
            } else {
                context.getSource().sendError(Text.of("Invalid API configuration: " + ioe.getMessage()));
                context.getSource().sendError(Text.of("Removing API key."));
                try { TrakteerFunctions.CONFIG.setApiKey(""); } catch (IOException ignored) {}
            }
            return 0;
        }
        context.getSource().sendFeedback(() -> Text.of(String.format("Set mode to %s", mode.name())), false);
        return 1;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                Arrays.stream(OperationMode.values()).map(Enum::name),
                builder);
    }
}
