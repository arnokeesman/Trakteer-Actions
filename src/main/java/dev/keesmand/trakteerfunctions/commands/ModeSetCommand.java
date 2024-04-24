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

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static dev.keesmand.trakteerfunctions.TrakteerFunctions.OPERATION_CONFIG;

public class ModeSetCommand implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String value = StringArgumentType.getString(context, "mode");
        if (Arrays.stream(OperationMode.values()).map(Enum::name).noneMatch(m -> m.equals(value))) {
            context.getSource().sendError(Text.of("Invalid mode!"));
            return 0;
        }

        OperationMode mode = OperationMode.valueOf(value);

        context.getSource().sendFeedback(() -> Text.of("Changing mode..."), false);

        OPERATION_CONFIG.setMode(mode);

        context.getSource().sendFeedback(() -> Text.of(String.format("%sSet mode to %s", TrakteerFunctions.logPrefix, mode.name())), true);
        return SINGLE_SUCCESS;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                Arrays.stream(OperationMode.values()).map(Enum::name),
                builder);
    }
}
